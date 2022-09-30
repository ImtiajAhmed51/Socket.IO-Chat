package com.example.chatsocketio;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingScreen extends AppCompatActivity {
    private ImageView imgSplashLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        imgSplashLogo=findViewById(R.id.imgSplashLogo);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.darkGreen));
        window.setNavigationBarColor(this.getResources().getColor(R.color.darkGreen));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        imgSplashLogo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in));
        Handler handler;
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSplashLogo.startAnimation(
                        AnimationUtils.loadAnimation(LoadingScreen.this,
                                R.anim.splash_out

                        ));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgSplashLogo.setVisibility(View.GONE);
                        startActivity(new Intent(LoadingScreen.this,AddUserActivity.class));
                        LoadingScreen.this.finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                },500);
            }
        },900);
    }
}