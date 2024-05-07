package com.example.mobileproject;

import java.util.Random;

public class QuestionGenerator {

    private Random rand = new Random();

    public Question generateAdditionQuestion(int difficulty_level) {
        int number1 = rand.nextInt(10 + 2*difficulty_level);
        int number2 = rand.nextInt(10 + 2*difficulty_level);
        int answer = number1 + number2;
        String text = number1 + " + " + number2;
        return new Question(text, answer);
    }

    public Question generateSubtractionQuestion(int difficulty_level) {
        int number1 = rand.nextInt(10 + 2 * difficulty_level);
        int number2 = rand.nextInt(10 + 2 * difficulty_level);
        int answer = number1 - number2;
        if(answer < 0) {
            // Если результат отрицательный, меняем числа местами
            return new Question(number2 + " - " + number1, number2 - number1);
        }
        return new Question(number1 + " - " + number2, answer);
    }

    public Question generateMultiplicationQuestion(int difficulty_level) {
        int number1 = rand.nextInt(10 + difficulty_level);
        int number2 = rand.nextInt(10 + difficulty_level);
        int answer = number1 * number2;
        String text = number1 + " * " + number2;
        return new Question(text, answer);
    }

    public Question generateDivisionQuestion(int difficulty_level) {
        int number1 = 1 + rand.nextInt(5 + difficulty_level); // Избегаем деления на ноль
        int answer = 1 + rand.nextInt(5 + difficulty_level);
        int number2 = number1 * answer; // Для деления число должно делиться без остатка
        String text = number2 + " / " + number1;
        return new Question(text, answer);
    }

    public Question generateThreeNumbersNoBracketsQuestion1(int difficulty_level) {
        Random rand = new Random();
        int number1 = rand.nextInt(5 + difficulty_level) + 1;
        int number2 = rand.nextInt(5 + difficulty_level) + 1;
        int number3 = rand.nextInt(9 + difficulty_level) + 1;
        String text;
        int answer;

        // Случайно выбираем, будет ли первая операция умножением или делением
        boolean isMultiplicationFirst = rand.nextBoolean();

        // Выполняем первую операцию
        int firstOperationResult;
        if (isMultiplicationFirst) {
            firstOperationResult = number1 * number2;
            text = number1 + " * " + number2;
        } else {
            // Если деление, убедимся, что результат будет целым числом
            number1 = number2 * (rand.nextInt(9) + 1); // number1 теперь кратно number2
            firstOperationResult = number1 / number2;
            text = number1 + " / " + number2;
        }

        // Случайно выбираем, будет ли вторая операция сложением или вычитанием
        boolean isAdditionSecond = rand.nextBoolean();

        // Выполняем вторую операцию
        if (isAdditionSecond) {
            answer = firstOperationResult + number3;
            text += " + " + number3;
        } else {
            // Если результат вычитания окажется отрицательным, генерируем number3 заново
            if (firstOperationResult < number3) {
                number3 = firstOperationResult - rand.nextInt(firstOperationResult);
            }
                answer = firstOperationResult - number3;
                text += " - " + number3;
        }

        return new Question(text, answer);
    }





    public Question generateThreeNumbersNoBracketsQuestion2(int difficulty_level) {
        Random rand = new Random();
        int a = rand.nextInt(10 +  difficulty_level) + 1;
        int b = rand.nextInt(5 +  difficulty_level) + 1;
        int c = rand.nextInt(5 +  difficulty_level) + 1;
        String text;
        int answer;

        // Случайно выбираем, будет ли вторая операция умножением или делением
        boolean isMultiplicationSecond = rand.nextBoolean();

        // Выполняем вторую операцию
        int secondOperationResult;
        if (isMultiplicationSecond) {
            secondOperationResult = b * c;
            text = b + " * " + c;
        } else {
            // Если деление, убедимся, что результат будет целым числом
            b = c * (rand.nextInt(9) + 1); // b теперь кратно c
            secondOperationResult = b / c;
            text = b + " / " + c;
        }

        // Случайно выбираем, будет ли первая операция сложением или вычитанием
        boolean isFirstOperationAddition = rand.nextBoolean();

        // Выполняем первую операцию
        if (isFirstOperationAddition) {
            answer = a + secondOperationResult;
            text = a + " + " + text;
        } else {
            // Если результат вычитания окажется отрицательным, заново генерируем a
            if (a < secondOperationResult) {
                a = secondOperationResult + rand.nextInt(10);
            }
            answer = a - secondOperationResult;
            text = a + " - " + text;
        }
        return new Question(text, answer);
    }




    public Question generateThreeNumbersWithBracketsQuestion1(int difficulty_level) {
        Random rand = new Random();
        int number1 = rand.nextInt(10 +  difficulty_level) + 1;
        int number2 = rand.nextInt(10 +  difficulty_level - number1) + 1;
        int number3 = rand.nextInt(5 +  difficulty_level) + 1;
        String text;
        int answer;

        // Случайно выбираем, будет ли операция в скобках сложением или вычитанием
        boolean isAdditionInBrackets = rand.nextBoolean();

        // Случайно выбираем, будет ли операция вне скобки умножением или делением
        boolean isMultiplicationOutside = rand.nextBoolean();

        int bracketResult;
        if (isAdditionInBrackets) {
            bracketResult = number1 + number2;
            text = "(" + number1 + " + " + number2 + ")";
        } else {
            // Гарантируем, что результат вычитания не будет отрицательным
            if (number1 < number2) {
                int temp = number1;
                number1 = number2;
                number2 = temp;
            }
            bracketResult = number1 - number2;
            text = "(" + number1 + " - " + number2 + ")";
        }

        if (isMultiplicationOutside) {
            answer = bracketResult * number3;
            text += " * " + number3;
        } else {
            // Гарантируем, что деление будет без остатка
            if (bracketResult % number3 != 0) {
                number3 = bracketResult; // Устанавливаем c равным результату в скобках, деление на самого себя даст 1
            }
            answer = bracketResult / number3;
            text += " / " + number3;
        }

        return new Question(text, answer);
    }




    public Question generateThreeNumbersWithBracketsQuestion2(int difficulty_level) {
        int number1 = rand.nextInt(5 +  difficulty_level) + 1;
        int number2 = rand.nextInt(10 +  difficulty_level) + 1;
        int number3 = rand.nextInt(10 +  difficulty_level - number2) + 1;
        String text;
        int answer;

        boolean isMultiplicationOutside = rand.nextBoolean();
        boolean isAdditionInBrackets = rand.nextBoolean();

        int intermediateResult;

        if (isAdditionInBrackets) {
            intermediateResult = number2 + number3;
        } else {
            if (number2 < number3) {
                int temp = number2;
                number2 = number3;
                number3 = temp;
            }
            intermediateResult = number2 - number3;
        }

        if (isMultiplicationOutside) {
            text = number1 + " * (" + number2 + (isAdditionInBrackets ? " + " : " - ") + number3 + ")";
            answer = number1 * intermediateResult;
        } else {
            if (intermediateResult == 0) intermediateResult = 1; // Избегаем деления на ноль
            number1 *= intermediateResult; // Умножаем, чтобы деление было без остатка
            text = number1 + " / (" + number2 + (isAdditionInBrackets ? " + " : " - ") + number3 + ")";
            answer = number1 / intermediateResult;
        }

        return new Question(text, answer);
    }



}
