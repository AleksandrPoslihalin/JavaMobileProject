package com.example.mobileproject;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.google.android.material.snackbar.Snackbar;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.resetHighScoreButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetHighScore();
            }
        });
        Button backButton = findViewById(R.id.backButton); // предполагается, что у вас есть кнопка с id backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Вызываем метод onBackPressed
            }
        });
    }

    private void resetHighScore() {
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("HighScore", 0); // Обнуляем рекорд
        editor.apply();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutRoot), "Рекорд обнулен", Snackbar.LENGTH_SHORT);
        snackbar.show();


    }


}
