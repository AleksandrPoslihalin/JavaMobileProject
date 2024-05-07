package com.example.mobileproject;

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