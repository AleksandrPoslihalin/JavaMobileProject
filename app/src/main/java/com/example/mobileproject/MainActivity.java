package com.example.mobileproject;
import com.example.mobileproject.Question;
import com.example.mobileproject.QuestionGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import android.view.WindowManager;  // Импортируем WindowManager
import android.view.WindowManager.LayoutParams;  // Импортируем LayoutParams
import android.util.Log;



public class MainActivity extends AppCompatActivity {
    private TextView mathQuestion, correctAnswerText, wrongCountText, userAnswerText, currentLevelText, scoreText;
    private Button checkAnswer;
    private QuestionGenerator questionGenerator;
    private int correctAnswer, wrongAnswers = 0, currentLevel = 1;
    private final int MAX_WRONG_ANSWERS = 3;
    private int correctAnswersCount = 0;
    private int score = 0;
    private int highScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mathQuestion = findViewById(R.id.mathQuestion);
        wrongCountText = findViewById(R.id.wrongCountText);
        userAnswerText = findViewById(R.id.userAnswerText); // TextView для показа введённого пользователем числа
        currentLevelText = findViewById(R.id.currentLevelText);
        scoreText = findViewById(R.id.scoreText);
        setupNumericKeyboard(); // Установка обработчиков для кнопок клавиатуры
        questionGenerator = new QuestionGenerator();
        generateNewQuestion(); // Генерация первого вопроса

        findViewById(R.id.checkAnswer).setOnClickListener(v -> checkAnswer());
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
        View.OnClickListener listener = v -> userAnswerText.append(((TextView) v).getText());
        int[] buttonIds = new int[] {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonYes, R.id.buttonNo
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }

        findViewById(R.id.buttonDelete).setOnClickListener(v -> {
            String text = userAnswerText.getText().toString();
            if (!text.isEmpty()) {
                userAnswerText.setText(text.substring(0, text.length() - 1));
            }
        });
    }

    private void checkAnswer() {
        final Snackbar[] snackbar = new Snackbar[1];
        try {
            int userAnswer = Integer.parseInt(userAnswerText.getText().toString());
            if (userAnswer == correctAnswer) {
                correctAnswersCount++;
                score += currentLevel;
                updateScore(score);
                snackbar[0] = Snackbar.make(findViewById(R.id.layoutRoot), "Правильно!", Snackbar.LENGTH_SHORT);
                snackbar[0].show();
                if (correctAnswersCount % 5 == 0) {
                    currentLevel++;
                }
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
                            .setCancelable(false) // Пользователь не может отменить/закрыть диалог без нажатия на кнопку
                            .create();

                    dialog.show();
                }
                else {
                    new AlertDialog.Builder(this, R.style.CustomDialogStyle)
                            .setMessage("Неверно. Правильный ответ: " + correctAnswer)
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Обработчик нажатия кнопки "OK"
                            })
                            .setCancelable(false) // Диалоговое окно не исчезнет, пока пользователь не нажмет кнопку
                            .show();
                }

            }
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(R.id.layoutRoot), "Введите число.", Snackbar.LENGTH_SHORT).show();
        } finally {
            userAnswerText.setText(""); // Очищаем ввод после проверки
            currentLevelText.setText("Уровень: " + currentLevel);
            scoreText.setText("Очки: " + score);
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













    private void generateNewQuestion() {
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

            mathQuestion.setText("Решите пример: " + question.getQuestionText() + " = ?");
            correctAnswer = question.getCorrectAnswer(); // Обновляем правильный ответ на вопрос

    }


/*
        private void generateNewQuestion() {
        Random rand = new Random();
        Question question = null; // Инициализируем переменную заранее
        int randomNumber;

        switch (currentLevel) {
            case 1:
                question = questionGenerator.generateAdditionQuestion(2);
                break;


            default:
                question = questionGenerator.generateAdditionQuestion(currentLevel);
                break;
        }

        // Обрабатываем случай, если question не инициализирован
        if (question == null) {
            question = questionGenerator.generateAdditionQuestion(currentLevel); // Безопасный вариант
        }

        mathQuestion.setText("Решите пример: " + question.getQuestionText() + " = ?");
        correctAnswer = question.getCorrectAnswer(); // Обновляем правильный ответ на вопрос
    }

*/
}