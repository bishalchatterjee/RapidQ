package com.bishal.rapidq;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private List<QuestionsList> questionsLists=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        final TextView score_tv=findViewById(R.id.score_tv);
        final TextView totalScore_tv=findViewById(R.id.totalScore_tv);
        final TextView correct_tv=findViewById(R.id.correct_tv);
        final TextView incorrect_tv=findViewById(R.id.incorrect_tv);

        final AppCompatButton shareResultsBtn= findViewById(R.id.shareResultsBtn);
        final AppCompatButton reAttemptBtn= findViewById(R.id.reAttemptBtn);


        questionsLists=(List<QuestionsList>)getIntent().getSerializableExtra("questions");

        totalScore_tv.setText("/"+questionsLists.size());
        score_tv.setText(getCorrectAnswers()+"");
        correct_tv.setText(getCorrectAnswers()+"");
        incorrect_tv.setText(String.valueOf(questionsLists.size()-getCorrectAnswers()));

        shareResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent=new Intent( );
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"My Score = "+score_tv.getText());
                Intent shareIntent= Intent.createChooser(sendIntent,"Share Via");
                startActivity(shareIntent);

            }
        });
        reAttemptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ResultsActivity.this,MainActivity.class));

            }
        });
    }
    private int getCorrectAnswers(){
        int correctAnswers=0;
        for (int i=0;i< questionsLists.size();i++){
            int getUserSelectedOption= questionsLists.get(i).getUserSelectedAnswer();
            int getQuestionAnswer=questionsLists.get(i).getAnswer();

            if(getQuestionAnswer==getUserSelectedOption){
                correctAnswers++;
            }
        }
        return correctAnswers;
    }
}