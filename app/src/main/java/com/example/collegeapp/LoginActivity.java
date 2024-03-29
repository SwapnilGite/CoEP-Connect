package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail,userPass;
    private TextView tvShow;
    private RelativeLayout loginBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String email,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("login",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userEmail = findViewById(R.id.user_email);
        userPass = findViewById(R.id.user_password);
        tvShow = findViewById(R.id.txt_show);
        loginBtn = findViewById(R.id.Login_btn);

        if(sharedPreferences.getString("isLogin","false").equals("yes"))
        {
            openDash();
        }
        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userPass.getInputType() == 144) //if pass is in show mode then hide it
                {
                    userPass.setInputType(129); //hide
                    tvShow.setText("Show");

                }
                else
                {
                    userPass.setInputType(144); //show
                    tvShow.setText("Hide");
                }
                userPass.setSelection(userPass.getText().length());
            }

        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDate();   //validate data when login button is pressed
            }
        });
    }

    private void validateDate() {
        email = userEmail.getText().toString();
        pass = userPass.getText().toString();

        if(email.isEmpty())
        {
            userEmail.setError("Required");
            userEmail.requestFocus();
        }
        else if(pass.isEmpty())
        {
            userPass.setError("Required");
            userPass.requestFocus();
        }
        else if(email.equals("admin@gmail.com") && pass.equals("12345"))
        {
            editor.putString("isLogin","yes");
            editor.commit();
            openDash();
        }
        else {
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
        }
    }

    private void openDash() {

        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();

    }
}