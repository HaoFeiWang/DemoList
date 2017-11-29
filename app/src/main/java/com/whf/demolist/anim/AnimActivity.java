package com.whf.demolist.anim;

import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.whf.demolist.R;

public class AnimActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivAnim;
    private ViewPropertyAnimator anim;

    private Button btnAccelerateDecelerate;
    private Button btnAccelerateDecelerate2;
    private Button btnLine;
    private Button btnAccelerate;
    private Button btnAccelerate2;
    private Button btnDecelerate;
    private Button btnDecelerate2;
    private Button btnAnticipate;
    private Button btnOvershoot;
    private Button btnAnticipateOvershoot;
    private Button btnBounce;
    private Button btnCycle;
    private Button btnSimplePath;
    private Button btnPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        ivAnim = findViewById(R.id.iv_anim);
        anim = ivAnim.animate();

        btnAccelerateDecelerate = findViewById(R.id.btn_accelerate_decelerate);
        btnAccelerateDecelerate2 = findViewById(R.id.btn_accelerate_decelerate2);
        btnLine = findViewById(R.id.btn_line);
        btnAccelerate = findViewById(R.id.btn_accelerate);
        btnAccelerate2 = findViewById(R.id.btn_accelerate2);
        btnDecelerate = findViewById(R.id.btn_decelerate);
        btnDecelerate2 = findViewById(R.id.btn_decelerate2);
        btnAnticipate = findViewById(R.id.btn_anticipate);
        btnOvershoot = findViewById(R.id.btn_overshoot);
        btnAnticipateOvershoot = findViewById(R.id.btn_anticipate_overshoot);
        btnBounce = findViewById(R.id.btn_bounce);
        btnCycle = findViewById(R.id.btn_cycle);
        btnSimplePath = findViewById(R.id.btn_simple_path);
        btnPath = findViewById(R.id.btn_path);

        btnAccelerateDecelerate.setOnClickListener(this);
        btnAccelerateDecelerate2.setOnClickListener(this);
        btnLine.setOnClickListener(this);
        btnAccelerate.setOnClickListener(this);
        btnAccelerate2.setOnClickListener(this);
        btnDecelerate.setOnClickListener(this);
        btnDecelerate2.setOnClickListener(this);
        btnAnticipate.setOnClickListener(this);
        btnOvershoot.setOnClickListener(this);
        btnAnticipateOvershoot.setOnClickListener(this);
        btnBounce.setOnClickListener(this);
        btnCycle.setOnClickListener(this);
        btnSimplePath.setOnClickListener(this);
        btnPath.setOnClickListener(this);
    }

    /**
     * PathInterpolator
     * 传入一个路径，横坐标为时间完成度，纵坐标为动画完成度，最大值都为1
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPathAnim() {
        Path interpolatorPath = new Path();
        //迅速运动0.25
        interpolatorPath.lineTo(0.25f, 0.25f);
        // 然后瞬间跳跃到 150% 的动画完成度
        interpolatorPath.moveTo(0.25f, 1.5f);
        // 再匀速倒车，返回到目标点
        interpolatorPath.lineTo(1, 1);
        anim.translationX(600).setInterpolator(new PathInterpolator(interpolatorPath)).setDuration(1000).start();
    }

    /**
     * 简易的自定义插值器
     * 在 http://cubic-bezier.com/ 网站上选择自己喜欢的需要的动画，会生成四个点，然后传入即可
     */
    @Override
    public void onClick(View v) {
        anim.translationX(0).setDuration(0).start();
        switch (v.getId()) {
            case R.id.btn_accelerate_decelerate:
                anim.translationX(600).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_accelerate_decelerate2:
                anim.translationX(600).setInterpolator(new FastOutSlowInInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_line:
                anim.translationX(600).setInterpolator(new LinearInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_accelerate:
                anim.translationX(600).setInterpolator(new AccelerateInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_accelerate2:
                anim.translationX(600).setInterpolator(new FastOutLinearInInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_decelerate:
                anim.translationX(600).setInterpolator(new DecelerateInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_decelerate2:
                anim.translationX(600).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_anticipate:
                anim.translationX(600).setInterpolator(new AnticipateInterpolator()).setDuration(1000).start();
            case R.id.btn_anticipate_overshoot:
                anim.translationX(600).setInterpolator(new AnticipateOvershootInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_overshoot:
                anim.translationX(600).setInterpolator(new OvershootInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_cycle:
                anim.translationX(300).setDuration(0).start();
                anim.translationX(600).setInterpolator(new CycleInterpolator(2)).setDuration(1000).start();
                break;
            case R.id.btn_bounce:
                anim.translationX(600).setInterpolator(new BounceInterpolator()).setDuration(1000).start();
                break;
            case R.id.btn_simple_path:
                anim.translationX(600).setInterpolator(new SimpleCustomInterpolator(0.05f, -0.92f, 0.75f, 1.78f)).setDuration(1000).start();
                break;
            case R.id.btn_path:
                startPathAnim();
                break;
            default:
                break;
        }
    }
}
