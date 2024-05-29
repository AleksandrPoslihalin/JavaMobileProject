package com.example.mobileproject;
import com.example.mobileproject.Question;
import com.example.mobileproject.QuestionGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
import java.util.Random;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.os.CountDownTimer;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {
    private TextView mathQuestion, correctAnswerText, wrongCountText, userAnswerText, currentLevelText, scoreText;
    private Button checkAnswer;
    private QuestionGenerator questionGenerator;
    private int wrongAnswers = 0, currentLevel = 1;
    private String correctAnswer;
    private boolean isTextAnswer;
    private final int MAX_WRONG_ANSWERS = 3;
    private int correctAnswersCount = 0;
    private int score = 0;
    private int highScore;
    private CountDownTimer countDownTimer;
    private int timeForAnswer = 20000;
    private long timeLeftInMillis = timeForAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        super.onCreate(savedInstanceState); // Вызов метода onCreate() из класса AppCompatActivity
        setContentView(R.layout.activity_main);

        mathQuestion = findViewById(R.id.mathQuestion);
        wrongCountText = findViewById(R.id.wrongCountText);
        userAnswerText = findViewById(R.id.userAnswerText);
        currentLevelText = findViewById(R.id.currentLevelText);
        scoreText = findViewById(R.id.scoreText);
        setupNumericKeyboard(); // Установка обработчиков для кнопок клавиатуры
        questionGenerator = new QuestionGenerator();
        generateNewQuestion();
        findViewById(R.id.checkAnswer).setOnClickListener(v -> checkAnswer(false));
        loadHighScore();
    }
    @Override
    public void onBackPressed() {
        // Диалог подтверждения
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение выхода")
                .setMessage("Вы действительно хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> super.onBackPressed())  // Если пользователь выбирает "Выйти", активность завершается
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())  // Если пользователь выбирает "Отмена", диалог закрывается
                .setCancelable(true)  // Разрешить закрывать диалог по клику за его пределами
                .show();  // Показать диалог
    }

    private void setupNumericKeyboard() {
        // Общий слушатель для кнопок с числами
        View.OnClickListener numberListener = v -> userAnswerText.append(((Button) v).getText());

        // Слушатель для кнопок YES и NO, который заменяет текст в userAnswerText
        View.OnClickListener yesNoListener = v -> userAnswerText.setText(((Button) v).getText());

        // Массив ID кнопок с числами
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        // Установка слушателя для кнопок с числами
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        // Установка слушателей для кнопок YES и NO
        findViewById(R.id.buttonYes).setOnClickListener(yesNoListener);
        findViewById(R.id.buttonNo).setOnClickListener(yesNoListener);

        // Установка слушателя для кнопки удаления
        findViewById(R.id.buttonDelete).setOnClickListener(v -> {
            String text = userAnswerText.getText().toString();
            if (!text.isEmpty()) {
                userAnswerText.setText(""); // Очищаем весь текст, а не удаляем по одному символу
            }
        });
    }


    private void updateButtonAccessibility(boolean isTextAnswer) {
        // Получаем ссылки на кнопки цифр и кнопки YES/NO
        int[] numericButtonIds = new int[] {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
        };

        Button buttonYes = findViewById(R.id.buttonYes);
        Button buttonNo = findViewById(R.id.buttonNo);

        // Если ожидается текстовый ответ, блокируем цифровые кнопки и разблокируем YES/NO
        for (int id : numericButtonIds) {
            Button btn = findViewById(id);
            btn.setEnabled(!isTextAnswer);
        }

        buttonYes.setEnabled(isTextAnswer);
        buttonNo.setEnabled(isTextAnswer);
    }


    private void checkAnswer(boolean isTimerExpired) {
        String userAnswer = userAnswerText.getText().toString().trim();  // Получаем ответ пользователя и удаляем пробелы с обеих сторон
        if (!userAnswer.isEmpty()) {
            countDownTimer.cancel(); // Остановить таймер при проверке ответа
        }
        final Snackbar[] snackbar = new Snackbar[1];
        try {

            // Проверяем, пустая ли строка и истекло ли время
            if (userAnswer.isEmpty() && isTimerExpired) {
                // Показываем диалог о том, что время истекло
                wrongAnswers++;
                wrongCountText.setText("Неправильные попытки: " + wrongAnswers);
                if (wrongAnswers >= MAX_WRONG_ANSWERS) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogStyle)
                            .setTitle("Игра окончена")
                            .setMessage("Время вышло. Правильный ответ был: " + correctAnswer + "\nВы допустили максимальное количество ошибок.")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                                intent.putExtra("score", score);
                                intent.putExtra("level", currentLevel);
                                intent.putExtra("highScore", highScore);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .create();
                    dialog.show();
                }
                else {
                    new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogStyle)
                            .setMessage("Время вышло! Правильный ответ был: " + correctAnswer)
                            .setPositiveButton("OK", (dialog, which) -> generateNewQuestion())
                            .setCancelable(false)
                            .show();
                    return;
                }
            }

            // Обычная проверка ответа
            if (userAnswer.isEmpty()) {
                throw new IllegalArgumentException("Введите ответ."); // Выбрасываем исключение, если строка пуста
            } else if (Objects.equals(correctAnswer, userAnswer)) {
                correctAnswersCount++;
                score += currentLevel;
                updateScore(score);
                snackbar[0] = Snackbar.make(findViewById(R.id.layoutRoot), "Правильно!", Snackbar.LENGTH_SHORT);
                snackbar[0].show();

                if (correctAnswersCount % 5 == 0) {
                    currentLevel++;
                    if (timeForAnswer > 5000 && currentLevel > 5) {
                        timeForAnswer -= 1000;
                    }
                }
                currentLevelText.setText("Уровень: " + currentLevel);
                scoreText.setText("Очки: " + score);
                generateNewQuestion();
            } else {
                wrongAnswers++;
                wrongCountText.setText("Неправильные попытки: " + wrongAnswers);
                if (wrongAnswers >= MAX_WRONG_ANSWERS) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogStyle)
                            .setTitle("Игра окончена")
                            .setMessage("Неверно. Правильный ответ: " + correctAnswer + "\nВы допустили максимальное количество ошибок.")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                                intent.putExtra("score", score);
                                intent.putExtra("level", currentLevel);
                                intent.putExtra("highScore", highScore);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .create();
                    dialog.show();
                } else {
                    new AlertDialog.Builder(this, R.style.CustomDialogStyle)
                            .setMessage("Неверно! Правильный ответ был: " + correctAnswer)
                            .setPositiveButton("OK", (dialog, which) -> generateNewQuestion())
                            .setCancelable(false)
                            .show();
                }
            }
        } catch (IllegalArgumentException e) {
            Snackbar.make(findViewById(R.id.layoutRoot), e.getMessage(), Snackbar.LENGTH_SHORT).show();
        } finally {userAnswerText.setText(""); // Очищаем ввод после проверки
        }
    }




    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("HighScore", 0); // 0 - значение по умолчанию
        TextView highScoreText = findViewById(R.id.highScoreText);
        highScoreText.setText("Рекорд: " + highScore);
    }

    private void saveHighScore() {
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("HighScore", highScore);
        editor.apply();
    }

    private void updateScore(int score) {
        if (score > highScore) {
            highScore = score;
            TextView highScoreText = findViewById(R.id.highScoreText);
            highScoreText.setText("Рекорд: " + highScore);
            saveHighScore(); // Сохраняем новый рекорд
        }
    }



    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            public void onFinish() {
                checkAnswer(true);
            }
        }.start();
    }

    private void updateCountDownText() {
        TextView timerTextView = findViewById(R.id.timerTextView);
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = timeForAnswer; // Сбросить время до начального
        startTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadHighScore(); // Перезагружаем рекорд когда активность возобновляется
    }

    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }







    private void generateNewQuestion() {
        resetTimer();
        Random rand = new Random();
        Question question = null; // Инициализируем переменную заранее

        switch (currentLevel) {
            case 1:
                question = questionGenerator.generateAdditionQuestion(1);
                break;
            case 2:
                if (rand.nextBoolean()) {
                    question = questionGenerator.generateAdditionQuestion(1);
                } else {
                    question = questionGenerator.generateSubtractionQuestion(1);
                }
                break;
            case 3:
                int randomNumber = rand.nextInt(100); // Генерация случайного числа от 0 до 99
                if (randomNumber < 70) {
                    question = questionGenerator.generateMultiplicationQuestion(1);
                } else if (randomNumber < 85) {
                    question = questionGenerator.generateAdditionQuestion(1);
                } else {
                    question = questionGenerator.generateSubtractionQuestion(1);
                }
                break;
            case 4:
                randomNumber = rand.nextInt(100);
                if (randomNumber < 35) {
                    question = questionGenerator.generateDivisionQuestion(1);
                } else if (randomNumber < 70) {
                    question = questionGenerator.generateMultiplicationQuestion(1);
                } else if (randomNumber < 85) {
                    question = questionGenerator.generateAdditionQuestion(1);
                } else {
                    question = questionGenerator.generateSubtractionQuestion(1);
                }
                break;
            default:
                randomNumber = rand.nextInt(100);
                if (randomNumber < 12) {
                    question = questionGenerator.generateAdditionQuestion(currentLevel - 4);
                } else if (randomNumber < 24) {
                    question = questionGenerator.generateSubtractionQuestion(currentLevel - 4);
                } else if (randomNumber < 36) {
                    question = questionGenerator.generateMultiplicationQuestion(currentLevel - 4);
                } else if (randomNumber < 48) {
                    question = questionGenerator.generateDivisionQuestion(currentLevel - 4);
                } else if (randomNumber < 60) {
                    question = questionGenerator.generateThreeNumbersNoBracketsQuestion1(currentLevel - 4);
                } else if (randomNumber < 72) {
                    question = questionGenerator.generateThreeNumbersNoBracketsQuestion2(currentLevel - 4);
                } else if (randomNumber < 84) {
                    question = questionGenerator.generateThreeNumbersWithBracketsQuestion1(currentLevel - 4);
                } else {
                    question = questionGenerator.generateThreeNumbersWithBracketsQuestion2(currentLevel - 4);
                }
                break;
        }

        isTextAnswer = question.isTextAnswer();
        updateButtonAccessibility(isTextAnswer);
        if (isTextAnswer) {
            mathQuestion.setText("Верно ли: " + question.getQuestionText());
        }
        else {
            mathQuestion.setText("Решите пример: " + question.getQuestionText() + " = ?");
        }
        correctAnswer = question.getAnswer(); // Обновляем правильный ответ на вопрос

    }



       /* private void generateNewQuestion() {
        Random rand = new Random();
        Question question = null;

        switch (currentLevel) {
            case 1:
                question = questionGenerator.generateDivisionQuestion(currentLevel);
                break;


            default:
                question = questionGenerator.generateDivisionQuestion(currentLevel);
                break;
        }
        isTextAnswer = question.isTextAnswer();
        updateButtonAccessibility(isTextAnswer);
        if (isTextAnswer) {
            mathQuestion.setText("Верно ли: " + question.getQuestionText());
        }
        else {
            mathQuestion.setText("Решите пример: " + question.getQuestionText() + " = ?");
        }
        correctAnswer = question.getAnswer(); // Обновляем правильный ответ на вопрос

        }

*/
}