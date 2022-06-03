package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView cardViewNotice;
    private CardView cardViewEbook;

    private CardView logout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardViewNotice = (CardView) findViewById(R.id.addNotice);
        cardViewNotice.setOnClickListener(this);

        cardViewEbook = (CardView) findViewById(R.id.addEbook);
        cardViewEbook.setOnClickListener(this);

        logout = (CardView)findViewById(R.id.Logout_btn);
        logout.setOnClickListener(this);

        sharedPreferences = this.getSharedPreferences("login",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString("isLogin","false").equals("false"))
        {
            openLogin();
        }
    }

    private void openLogin() {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNotice:
            Intent i;
            i = new Intent(this, UploadNotice.class);
            startActivity(i);
            break;

            case R.id.Logout_btn:

                editor.putString("isLogin","false");
                editor.commit();
                Intent i2;
                i2 = new Intent(this,LoginActivity.class);
                startActivity(i2);
                break;


            case R.id.addEbook:
                Intent j;
                j = new Intent(this, UploadPdf.class);
                startActivity(j);
                break;



        }
    }
}