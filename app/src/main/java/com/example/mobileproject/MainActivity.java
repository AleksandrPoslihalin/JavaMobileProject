package com.example.mobileproject;
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


public class MainActivity extends AppCompatActivity {
    private TextView mathQuestion, correctAnswerText, wrongCountText, userAnswerText, currentLevelText, scoreText;
    private Button checkAnswer; // Если используете Button в коде, нужно добавить импорт import android.widget.Button;
    private QuestionGenerator questionGenerator;
    private int correctAnswer, wrongAnswers = 0, currentLevel = 1;
    private final int MAX_WRONG_ANSWERS = 3;
    private int correctAnswersCount = 0;
    private int score = 0;
    private int highScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void setupNumericKeyboard() {
        View.OnClickListener listener = v -> userAnswerText.append(((TextView) v).getText());
        int[] buttonIds = new int[] {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
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
        final Snackbar[] snackbar = new Snackbar[1]; // Используйте массив для обхода ограничения на fina
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
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Игра окончена")
                            .setMessage("Неверно. Правильный ответ: " + correctAnswer + "\nВы допустили максимальное количество ошибок.")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                                intent.putExtra("score", score);
                                intent.putExtra("level", currentLevel);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false) // Пользователь не может отменить/закрыть диалог без нажатия на кнопку
                            .create();

                    dialog.show();
                }
                else {
                    new AlertDialog.Builder(this)
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


    class Question {
        private String questionText;
        private int correctAnswer;

        public Question(String questionText, int correctAnswer) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestionText() {
            return questionText;
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }
    }




    public class QuestionGenerator {

        private Random rand = new Random();

        public Question generateAdditionQuestion() {
            int number1 = rand.nextInt(10); // Генерируем число от 0 до 9
            int number2 = rand.nextInt(10); // Генерируем второе число от 0 до 9
            int answer = number1 + number2;
            String text = number1 + " + " + number2;
            return new Question(text, answer);
        }

        public Question generateSubtractionQuestion() {
            int number1 = rand.nextInt(10);
            int number2 = rand.nextInt(10);
            int answer = number1 - number2;
            if(answer < 0) {
                // Если результат отрицательный, меняем числа местами
                return new Question(number2 + " - " + number1, number2 - number1);
            }
            return new Question(number1 + " - " + number2, answer);
        }

        public Question generateMultiplicationQuestion() {
            int number1 = rand.nextInt(10);
            int number2 = rand.nextInt(10);
            int answer = number1 * number2;
            String text = number1 + " * " + number2;
            return new Question(text, answer);
        }

        public Question generateDivisionQuestion() {
            int number1 = 1 + rand.nextInt(9); // Избегаем деления на ноль
            int answer = 1 + rand.nextInt(10); // Результат деления будет от 1 до 10
            int number2 = number1 * answer; // Для деления число должно делиться без остатка
            String text = number2 + " / " + number1;
            return new Question(text, answer);
        }

        public Question generateThreeNumbersQuestion() {
            // Генерация примера с тремя числами без скобок
            int number1 = rand.nextInt(10);
            int number2 = rand.nextInt(10);
            int number3 = rand.nextInt(10);
            String text = number1 + " + " + number2 + " - " + number3; // Пример: "1 + 2 - 3"
            int answer = number1 + number2 - number3; // Правильный ответ
            return new Question(text, answer);
        }

        public Question generateThreeNumbersWithBracketsQuestion() {
            // Генерация примера с тремя числами со скобками
            int number1 = rand.nextInt(10);
            int number2 = rand.nextInt(10);
            int number3 = rand.nextInt(10);
            String text = "(" + number1 + " + " + number2 + ") * " + number3; // Пример: "(1 + 2) * 3"
            int answer = (number1 + number2) * number3; // Правильный ответ с учетом приоритета операций
            return new Question(text, answer);
        }
    }







    private void generateNewQuestion() {
        Random rand = new Random();
        int number1, number2;
        Question question; // Переменная для хранения вопроса
        String questionText = "";

        switch (currentLevel) {
            case 1:
                // Вызываем метод для генерации вопроса со сложением
                question = questionGenerator.generateAdditionQuestion();
                break;
            case 2:
                // Вызываем метод для генерации вопроса с вычитанием
                question = questionGenerator.generateSubtractionQuestion();
                break;
            case 3:
                // Вызываем метод для генерации вопроса с умножением
                question = questionGenerator.generateMultiplicationQuestion();
                break;
            default:
                // Вызываем метод для генерации вопроса с делением
                question = questionGenerator.generateDivisionQuestion();
                break;
        }

        // Обновление UI с новым вопросом
        mathQuestion.setText("Решите пример: " + question.getQuestionText() + " = ?");
        correctAnswer = question.getCorrectAnswer(); // Обновляем правильный ответ на вопрос
    }


}