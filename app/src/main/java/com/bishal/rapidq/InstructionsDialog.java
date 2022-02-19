package com.bishal.rapidq;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class InstructionsDialog extends Dialog {


    TextView instuctions_tv;

    public InstructionsDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_dialog_layout);


        final AppCompatButton continueBtn = findViewById(R.id.continueBtn);
        instuctions_tv= findViewById(R.id.instructions_tv);


        setInstructionPoint(instuctions_tv, "1. Total time is 2 minutes.");
        setInstructionPoint(instuctions_tv, "2. Negative Marking: Enabled.");
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setInstructionPoint(TextView instructions_tv, String instructionPoint) {
        instructions_tv.setText(instructions_tv.getText() + "\n" + instructionPoint);
    }
}

