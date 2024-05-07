package com.example.mobileproject;

public class Question {
    private String questionText;
    private String answer;
    private boolean isTextAnswer; // Флаг, указывающий на тип ответа

    public Question(String questionText, int numericAnswer) {
        this.questionText = questionText;
        this.answer = String.valueOf(numericAnswer); // Преобразуем числовой ответ в строку
        this.isTextAnswer = false; // Ответ числовой
    }

    public Question(String questionText, String textAnswer) {
        this.questionText = questionText;
        this.answer = textAnswer;
        this.isTextAnswer = true; // Ответ текстовый
    }


    public String getQuestionText() {
        return questionText;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isTextAnswer() {
        return isTextAnswer;
    }
}
