package com.leedian.oviewremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.leedian.oviewremote.view.activity.CameraViewActivity;
import com.leedian.oviewremote.view.activity.MainStateActivity;
import com.leedian.oviewremote.view.activity.UserLoginActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent;

        //AppManager.setUserLogout();
        //intent = new Intent(this,UserLoginActivity.class);
        //intent = new Intent(this, MainStateActivity.class);
       if (AppManager.isUserLogin())
            intent = new Intent(this, MainStateActivity.class);
        else
            intent = new Intent(this,UserLoginActivity.class);
        //intent = new Intent(this, MainStateActivity.class);



        startActivity(intent);
        finish();

    }
}
