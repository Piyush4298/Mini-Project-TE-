package com.example.miniquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.SharedPreferencesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";
    public static final String EXTRA_SUBJECT_ID = "extraSubjectID";
    public static final String EXTRA_SUBJECT_NAME = "extraSubjectName";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView textViewHighScore;
    private Spinner spinnerDifficulty;
    private Spinner spinnerSubject;
    private int highscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHighScore = findViewById(R.id.text_view_highscore);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerSubject = findViewById(R.id.spinner_subject);

        loadSubjects();
        loadDifficultyLevels();

        loadHighScore();

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }

    private void startQuiz(){
        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();
        int subjectID = selectedSubject.getId();
        String subjectName = selectedSubject.getName();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_SUBJECT_ID, subjectID);
        intent.putExtra(EXTRA_SUBJECT_NAME, subjectName);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivityForResult(intent,REQUEST_CODE_QUIZ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_QUIZ){
            if(resultCode == RESULT_OK) {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
                if(score > highscore){
                    updateHighScore(score);

                }
            }
        }
    }
    private void loadSubjects(){
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        List<Subject> subjects = dbHelper.getAllSubjects();

        ArrayAdapter<Subject> adapterSubjects = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        adapterSubjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapterSubjects);
    }
    private void loadDifficultyLevels(){
        String[] difficultyLevels = Question.getAllDifficultyLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }
    private void loadHighScore(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighScore.setText("Highscore : "+highscore);
    }

    private void updateHighScore(int new_score){
        highscore = new_score;
        textViewHighScore.setText("Highscore : "+highscore);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
    }
}