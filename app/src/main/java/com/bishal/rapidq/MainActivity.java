package com.bishal.rapidq;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final List<QuestionsList> questionsLists= new ArrayList<>();


    private TextView quizTimer, currentQuestion_tv, totalQuestion_tv,question_tv;

    private RelativeLayout option1Layout, option2Layout, option3Layout, option4Layout;
    private TextView option1_tv, option2_tv, option3_tv, option4_tv;
    private ImageView option1_select, option2_select, option3_select, option4_select,question_iv;


    private int currentQuestionPosition=0,selectedOption=0;
    //countDown Timer
    private CountDownTimer countDownTimer;
    //Creating Database Reference
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rapidq-26647-default-rtdb.firebaseio.com/");
    AppCompatButton nextQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quizTimer = findViewById(R.id.quizTimer);
        question_tv=findViewById(R.id.question_tv);
        currentQuestion_tv = findViewById(R.id.currentQuestion_tv);
        totalQuestion_tv = findViewById(R.id.totalQuestion_tv);

        option1_tv = findViewById(R.id.option1_tv);
        option2_tv = findViewById(R.id.option2_tv);
        option3_tv = findViewById(R.id.option3_tv);
        option4_tv = findViewById(R.id.option4_tv);

        option1Layout = findViewById(R.id.option1Layout);
        option2Layout = findViewById(R.id.option2Layout);
        option3Layout = findViewById(R.id.option3Layout);
        option4Layout = findViewById(R.id.option4Layout);

        option1_select = findViewById(R.id.option1_select);
        option2_select = findViewById(R.id.option2_select);
        option3_select = findViewById(R.id.option3_select);
        option4_select = findViewById(R.id.option4_select);

        question_iv=findViewById(R.id.question_iv);


        nextQuestionBtn = findViewById(R.id.nextQuestionBtn);


        //show Instructions
        InstructionsDialog instructionsDialog = new InstructionsDialog(MainActivity.this);
        instructionsDialog.setCancelable(false);
        instructionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsDialog.show();

        //getting data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int getQuizTime =Integer.parseInt(snapshot.child("time").getValue(String.class));
                for(DataSnapshot questions : snapshot.child("questions").getChildren()){
                    String getQuestion =questions.child("question").getValue(String.class);
                    String getOption1 =questions.child("option1").getValue(String.class);
                    String getOption2 =questions.child("option2").getValue(String.class);
                    String getOption3 =questions.child("option3").getValue(String.class);
                    String getOption4 =questions.child("option4").getValue(String.class);
                    String getUrl =questions.child("url").getValue(String.class);
                    int getAnswer =Integer.parseInt(questions.child("answer").getValue(String.class));

                    //creating questions list object and add details
                    QuestionsList questionsList= new QuestionsList(getQuestion,getOption1,getOption2,getOption3,getOption4,getUrl,getAnswer);
                    //adding questionsList object into  the list
                    questionsLists.add(questionsList);
                }

                totalQuestion_tv.setText("/"+questionsLists.size());

                //StartTimer
                startQuizTimer(getQuizTime);

                selectQuestion(currentQuestionPosition);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Failed To Retrive Data From Server",Toast.LENGTH_LONG).show();

            }
        });

        option1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption=1;
                selectOption(option1Layout,option1_select);
            }
        });
        option2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption=2;
                selectOption(option2Layout,option2_select);
            }
        });
        option3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption=3;
                selectOption(option3Layout,option3_select);

            }
        });
        option4Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption=4;
                selectOption(option4Layout,option4_select);
            }
        });

        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOption!=0){
                    questionsLists.get(currentQuestionPosition).setUserSelectedAnswer(selectedOption);
                    resetVisiblility();
                    selectedOption=0;
                    currentQuestionPosition++;

                    if(currentQuestionPosition < questionsLists.size()){
                        selectQuestion(currentQuestionPosition);

                    }else{
                        countDownTimer.cancel();
                        finishQuiz();;
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Please Select an Option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void finishQuiz(){
        Intent intent= new Intent(MainActivity.this,ResultsActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("questions",(Serializable) questionsLists);

        intent.putExtras(bundle);
        startActivity(intent);

        finish();
    }
    private void startQuizTimer(int maxTimeInSeconds){
        countDownTimer= new CountDownTimer(maxTimeInSeconds*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long getHour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long getMinute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long getSecond = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                String generateTime= String.format(Locale.getDefault(),"%02d:%02d:%02d",getHour,
                        getMinute-TimeUnit.HOURS.toMinutes(getHour),
                        getSecond -TimeUnit.MINUTES.toSeconds(getMinute));

                quizTimer.setText(generateTime);
            }

            @Override
            public void onFinish() {

                finishQuiz();

            }
        };

        countDownTimer.start();
    }
    private void selectQuestion(int questionListPosition){

        resetOptions();
        Picasso.get().load(questionsLists.get(questionListPosition).getUrl()).resize(800, 400).into(question_iv);
        question_tv.setText(questionsLists.get(questionListPosition).getQuestion());
        option1_tv.setText(questionsLists.get(questionListPosition).getOption1());
        option2_tv.setText(questionsLists.get(questionListPosition).getOption2());
        if((questionsLists.get(questionListPosition).getOption3()==null)) {
            option3Layout.setVisibility(View.GONE);
            option3_select.setVisibility(View.GONE);
        }
        if((questionsLists.get(questionListPosition).getOption4()==null)) {
            option4Layout.setVisibility(View.GONE);
            option4_select.setVisibility(View.GONE);
        }
        option3_tv.setText(questionsLists.get(questionListPosition).getOption3());
        option4_tv.setText(questionsLists.get(questionListPosition).getOption4());
        currentQuestion_tv.setText("Question "+(questionListPosition+1));
    }
    private void resetOptions(){

        option1Layout.setBackgroundResource(R.drawable.thin_border);
        option2Layout.setBackgroundResource(R.drawable.thin_border);
        option3Layout.setBackgroundResource(R.drawable.thin_border);
        option4Layout.setBackgroundResource(R.drawable.thin_border);


        option1_select.setImageResource(R.drawable.round_option_select);
        option2_select.setImageResource(R.drawable.round_option_select);
        option3_select.setImageResource(R.drawable.round_option_select);
        option4_select.setImageResource(R.drawable.round_option_select);
    }

    private void selectOption(RelativeLayout selectedOptionLayout , ImageView selectedOptionIcon){
        resetOptions();

        selectedOptionIcon.setImageResource(R.drawable.check_icon);
        selectedOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option);

    }
    private void resetVisiblility(){
        option3Layout.setVisibility(View.VISIBLE);
        option3_select.setVisibility(View.VISIBLE);
        option4Layout.setVisibility(View.VISIBLE);
        option4_select.setVisibility(View.VISIBLE);
    }
}
