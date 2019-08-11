package com.satragni.navigationassignment;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.eftimoff.androipathview.PathView;
import com.satragni.navigationassignment.Utils.Params;

import static android.support.design.R.attr.height;

public class SplashActivity extends AppCompatActivity {
    
    LottieAnimationView mLottie;
    private RelativeLayout rLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mLottie = (LottieAnimationView)findViewById(R.id.animation_view);
        rLayout = (RelativeLayout)findViewById(R.id.RL);

        new CountDownTimer(5000,1000){
            @Override
            public void onTick(long l) {
                Log.d("Flash", "onTick: "+l);
            }
            @Override
            public void onFinish() {
                // previously invisible view
                View myView = findViewById(R.id.lottieContainer);

                // get the center for the clipping circle
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is  0)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy,finalRadius,0);



                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                            Start activity transition after animation is finished
                            RelativeLayout relativeLayout=   (RelativeLayout)findViewById(R.id.lottieContainer);
                            relativeLayout.setVisibility(View.GONE);
                            moveToHomeScreen();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                // make the view visible and start the animation
                myView.setVisibility(View.VISIBLE);
                anim.setDuration(800);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
            }
        }.start();

    }

    private void moveToHomeScreen(){
        Intent intent =   new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }






}
