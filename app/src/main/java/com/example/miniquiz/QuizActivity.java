package com.example.miniquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLISEC = 30000;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewDifficulty;
    private TextView textViewSubject;
    private TextView textViewCountDown;
    private RadioGroup radioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCountDown;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliSec;

    private List<Question> questionList;

    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewDifficulty = findViewById(R.id.text_view_difficulty);
        textViewSubject = findViewById(R.id.text_view_subject);
        textViewCountDown = findViewById(R.id.text_view_countdown);

        radioGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button_1);
        rb2 = findViewById(R.id.radio_button_2);
        rb3 = findViewById(R.id.radio_button_3);
        rb4 = findViewById(R.id.radio_button_4);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCountDown = textViewQuestionCount.getTextColors();

        Intent intent = getIntent();
        int subjectID = intent.getIntExtra(MainActivity.EXTRA_SUBJECT_ID, 0);
        String subjectName = intent.getStringExtra(MainActivity.EXTRA_SUBJECT_NAME);
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        textViewDifficulty.setText("Difficulty: "+ difficulty);
        textViewSubject.setText("Subject: "+subjectName);

        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        questionList = dbHelper.getQuestions(subjectID, difficulty);

        questionCountTotal = questionList.size();
        Collections.shuffle(questionList); // we will get random question every time.

        showNextQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()){
                        checkAnswer();
                    }else{
                        Toast.makeText(QuizActivity.this , "Please select an option", Toast.LENGTH_SHORT);
                    }
                }else {
                    showNextQuestion();
                }
            }
        });



    }
    private void showNextQuestion(){
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        radioGroup.clearCheck();

        if(questionCounter < questionCountTotal){
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());
            questionCounter++;
            textViewQuestionCount.setText("Question: "+ questionCounter + " / "+ questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Confirm");
            timeLeftInMilliSec = COUNTDOWN_IN_MILLISEC;
            startCountDown();
        }else {
            finishQuiz();
        }

    }
    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMilliSec, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliSec = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMilliSec = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }
    private void updateCountDownText(){
        int minutes = (int)(timeLeftInMilliSec / 1000) / 60;
        int seconds = (int)(timeLeftInMilliSec / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountDown.setText(timeFormatted);

        if(timeLeftInMilliSec < 10000){
            textViewCountDown.setTextColor(Color.RED);
        }else {
            textViewCountDown.setTextColor(textColorDefaultCountDown);
        }
    }
    private void checkAnswer(){
        answered = true;

        countDownTimer.cancel();
        RadioButton rbselected = findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNr = radioGroup.indexOfChild(rbselected)+1;
        if (answerNr == currentQuestion.getAnswerNr()){        //Checking for answer correct or not
            score++;
            textViewScore.setText("Score: "+ score);
        }
        showSolution();
    }
    private void showSolution(){
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);
        switch (currentQuestion.getAnswerNr()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                break;
        }

        if(questionCounter < questionCountTotal){
            buttonConfirmNext.setText("Next");
        }else {
            buttonConfirmNext.setText("Finish");
        }

    }

    private void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        }else{
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}