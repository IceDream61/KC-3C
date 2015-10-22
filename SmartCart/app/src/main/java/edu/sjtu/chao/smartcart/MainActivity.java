package edu.sjtu.chao.smartcart;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import java.lang.Math;

public class MainActivity extends Activity implements View.OnTouchListener {

    private ViewFlipper viewFlipper;
    private Button IPconfirm, bgNextButton;
    private ImageView base, stick, background;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private int bgRotate[]={R.drawable.bg0, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9};
    private int bgChoose=0;
    private int bgNumber=10;

    // 左右滑动时手指按下的X坐标
    private float touchDownX;
    // 左右滑动时手指松开的X坐标
    private float touchUpX;

    private View.OnTouchListener stickMotion;
    private View.OnClickListener ipconfirmClick, bgNextClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        IPconfirm = (Button) findViewById(R.id.IPconfirm);
        base = (ImageView) findViewById(R.id.base);
        stick = (ImageView) findViewById(R.id.stick);
        background = (ImageView) findViewById(R.id.background);
        bgNextButton = (Button) findViewById(R.id.bgNextButton);


        stickMotion = new View.OnTouchListener() {
            private double rawX, rawY;
            private int X,Y;
            private int centerX, centerY;
            private int width,height;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                centerX=(base.getLeft()+base.getRight())>>1;
                centerY=(base.getTop()+base.getBottom())>>1;
                rawX=event.getRawX();
                rawY=event.getRawY();
                rawX=Xtrans(rawX-((double)centerX), rawY-((double)centerY), 240.0)+((double)centerX);
                rawY=Ytrans(rawX-((double)centerX), rawY-((double)centerY), 240.0)+((double)centerY);
                X= ((int) rawX);
                Y= ((int) rawY);

                width=stick.getWidth();
                height=stick.getHeight();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        stick.setLeft(X-(width>>1));
                        stick.setRight(stick.getLeft()+width);
                        stick.setTop(Y - (height>>1));
                        stick.setBottom(stick.getTop()+height);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        stick.setLeft(X-(width>>1));
                        stick.setRight(stick.getLeft()+width);
                        stick.setTop(Y - (height>>1));
                        stick.setBottom(stick.getTop()+height);
                        return true;
                    case MotionEvent.ACTION_UP:
                        stick.setLeft(centerX-(width>>1));
                        stick.setRight(stick.getLeft()+width);
                        stick.setTop(centerY-(height>>1));
                        stick.setBottom(stick.getTop()+height);
                        return true;
                }
                return false;
            }

            private double Xtrans(double x, double y, double R){
                double tmp;
                if((tmp=(x*x+y*y))<=R*R){
                    return x;
                }
                else{
                    tmp=Math.sqrt(tmp);
                    x=x*R/tmp;
                    return x;
                }
            }
            private double Ytrans(double x, double y, double R){
                double tmp;
                if((tmp=(x*x+y*y))<=R*R){
                    return y;
                }
                else{
                    tmp=Math.sqrt(tmp);
                    y=y*R/tmp;
                    return y;
                }
            }
        };

        ipconfirmClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(background.getVisibility()==View.INVISIBLE){
                    background.setVisibility(View.VISIBLE);
                }
                else{background.setVisibility(View.INVISIBLE);}
            }
        };

        bgNextClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgChoose=(bgChoose+1)%bgNumber;
                background.setImageResource(bgRotate[bgChoose]);
            }
        };

        viewFlipper.setOnTouchListener(this);
        IPconfirm.setOnClickListener(ipconfirmClick);
        stick.setOnTouchListener(stickMotion);
        bgNextButton.setOnClickListener(bgNextClick);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 取得左右滑动时手指按下的X坐标
            touchDownX = event.getX();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 取得左右滑动时手指松开的X坐标
            touchUpX = event.getX();
            // 从左往右，看前一个View
            if (touchUpX - touchDownX > 100) {
                // 设置View切换的动画
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.switcher_in_left));
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.switcher_out_right));
                // 显示下一个View
                viewFlipper.showPrevious();
                // 从右往左，看后一个View
            } else if (touchDownX - touchUpX > 100) {
                // 设置View切换的动画
                // 由于Android没有提供slide_out_left和slide_in_right，所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.switcher_in_right));
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.switcher_out_left));
                // 显示前一个View
                viewFlipper.showNext();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
