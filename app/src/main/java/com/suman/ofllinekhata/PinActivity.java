package com.suman.ofllinekhata;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {
    EditText txt1, txt2, txt3, txt4;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnClear, btnOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        txt1 = findViewById(R.id.t1);
        txt2 = findViewById(R.id.t2);
        txt3 = findViewById(R.id.t3);
        txt4 = findViewById(R.id.t4);
        btn1 = findViewById(R.id.lbtn1);
        btn2 = findViewById(R.id.lbtn2);
        btn3 = findViewById(R.id.lbtn3);
        btn4 = findViewById(R.id.lbtn4);
        btn5 = findViewById(R.id.lbtn5);
        btn6 = findViewById(R.id.lbtn6);
        btn7 = findViewById(R.id.lbtn7);
        btn8 = findViewById(R.id.lbtn8);
        btn9 = findViewById(R.id.lbtn9);
        btn0 = findViewById(R.id.lbtn0);
        btnClear = findViewById(R.id.lbtnclear);
        btnOk = findViewById(R.id.btnok);
        textWatcher();
        clickInit();
    }

    private void clickInit() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn1.getText().toString().trim());
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn2.getText().toString().trim());
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn3.getText().toString().trim());
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn4.getText().toString().trim());
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn5.getText().toString().trim());
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn6.getText().toString().trim());
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn7.getText().toString().trim());
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn8.getText().toString().trim());
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn9.getText().toString().trim());
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPinText(btn0.getText().toString().trim());
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPinText();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt4.getText().toString().trim().isEmpty()){
                finish();}
            }
        });
    }

    private void setPinText(String pinText) {
       if(txt1.getText().toString().trim().length() == 0){
            txt1.setText(pinText);
            return;
        }
        if(txt2.getText().toString().trim().length() == 0){
            txt2.setText(pinText);
            return;
        }
        if(txt3.getText().toString().trim().length() == 0){
            txt3.setText(pinText);
            return;
        }
        if(txt4.getText().toString().trim().length() == 0){
            txt4.setText(pinText);
        }

    }

    private void clearPinText() {
        if(txt4.getText().toString().trim().length() > 0){
            txt4.setText("");
            return;
        }
        if(txt3.getText().toString().trim().length() > 0){
            txt3.setText("");
            return;
        }
        if(txt2.getText().toString().trim().length() > 0){
            txt2.setText("");
            return;
        }
        if(txt1.getText().toString().trim().length() > 0){
            txt1.setText("");
        }
    }

    private void textWatcher() {
        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt1.getText().toString().trim().length() > 1){
                    txt1.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txt1.getText().toString().trim().length() == 1){
                    txt2.requestFocus();
                }

            }
        });
        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt2.getText().toString().trim().length() > 1){
                    txt2.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txt2.getText().toString().trim().length() == 1) {
                    txt3.requestFocus();
                }
            }
        });
        txt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt3.getText().toString().trim().length() > 1){
                    txt3.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txt3.getText().toString().trim().length() == 1){
                    txt4.requestFocus();
                }
            }
        });
        txt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt4.getText().toString().trim().length() > 1){
                    txt4.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txt4.getText().toString().trim().length() == 1){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(txt1.getText().toString().trim());
                    stringBuilder.append(txt2.getText().toString().trim());
                    stringBuilder.append(txt3.getText().toString().trim());
                    stringBuilder.append(txt4.getText().toString().trim());
                    Intent intent = new Intent();
                    intent.putExtra("pin", stringBuilder.toString());
                    setResult(RESULT_OK, intent);
                    /*Toast.makeText(PinActivity.this, stringBuilder, Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
        super.onBackPressed();
    }
}