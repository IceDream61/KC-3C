package edu.sjtu.chao.smartcart;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

//import com.iflytek.cloud.RecognizerListener;


public class MainActivity extends Activity implements View.OnTouchListener {

    private ViewFlipper viewFlipper;
    private Button IPconfirm, bgNextButton;
    private ImageView base, stick, background, directionKey, voiceButton;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private RouteView routePad;
    private TextView voiceText;
    private String voiceContext;

    private RecognizerDialogListener voiceDialogListener;
    //private com.iflytek.cloud.SpeechRecognizer voiceRecognizer;

    /**本地语音服务*/
    private InitListener mInitListener;
    private RecognizerDialog voiceDialog;

    private int bgRotate[]={R.drawable.bg0, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4,
            R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9, R.drawable.bg10,
            R.drawable.bg11,R.drawable.bg12};
    private int bgChoose=0;
    private int bgNumber=13;

    // 左右滑动时手指按下的X坐标
    private float touchDownX;
    // 左右滑动时手指松开的X坐标
    private float touchUpX;

    private View.OnTouchListener stickMotion, keyTouch;
    private View.OnClickListener ipconfirmClick, bgNextClick, voiceControlClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=562c4ad2");

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        IPconfirm = (Button) findViewById(R.id.IPconfirm);
        base = (ImageView) findViewById(R.id.base);
        stick = (ImageView) findViewById(R.id.stick);
        directionKey = (ImageView) findViewById(R.id.directionKey);

        background = (ImageView) findViewById(R.id.background);
        bgNextButton = (Button) findViewById(R.id.bgNextButton);

        routePad = (RouteView) findViewById(R.id.routePad);
        routePad.setZOrderOnTop(true);

        voiceButton = (ImageView) findViewById(R.id.voiceButton);
        voiceText = (TextView) findViewById(R.id.voiceText);
        voiceContext = "";

        mInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d("mInitLisener", "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.e("Error","初始化失败,错误码："+code);
                }
            }
        };

        voiceDialog = new RecognizerDialog(this, mInitListener);
        voiceDialog.setParameter(SpeechConstant.DOMAIN,"iat");
        voiceDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        voiceDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        voiceDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);


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

        keyTouch = new View.OnTouchListener() {
            private int X,Y,centerX,centerY;
            private int valueLow=40, valueHigh=100;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                centerX=(directionKey.getLeft()+directionKey.getRight())>>1;
                centerY=(directionKey.getTop()+directionKey.getBottom())>>1;
                X=((int)event.getRawX())-centerX;
                Y=((int)event.getRawY())-centerY;
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        changeKeyPic(X,Y);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        changeKeyPic(X,Y);
                        return true;
                    case MotionEvent.ACTION_OUTSIDE:
                        directionKey.setImageResource(R.drawable.key0);
                        return true;
                    case MotionEvent.ACTION_UP:
                        directionKey.setImageResource(R.drawable.key0);
                }
                return false;
            }

            public void changeKeyPic(int x, int y){
                if(y>-valueLow && y<valueLow){
                    if(x>valueHigh){
                        directionKey.setImageResource(R.drawable.key_right);
                    }
                    if(x<-valueHigh){
                        directionKey.setImageResource(R.drawable.key_left);
                    }
                }
                if(x>-valueLow && x<valueLow){
                    if(y>valueHigh){
                        directionKey.setImageResource(R.drawable.key_down);
                    }
                    if(y<-valueHigh){
                        directionKey.setImageResource(R.drawable.key_up);
                    }
                }
            }
        };

        voiceDialogListener = new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                //将解析后的字符串连在一起
                JsonParser json = new JsonParser();
                voiceContext+=json.parseIatResult(recognizerResult.getResultString());
                if(isLast==true)
                {
                    voiceText.setText(voiceContext.substring(0,voiceContext.length()-1));
                    voiceContext="";
                }
                Log.d("result",recognizerResult.getResultString());
                Log.d("describe", String.valueOf(recognizerResult.describeContents()));
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        };


        voiceControlClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceDialog.setListener(voiceDialogListener);
                voiceDialog.show();
            }
        };

        viewFlipper.setOnTouchListener(this);
        IPconfirm.setOnClickListener(ipconfirmClick);
        stick.setOnTouchListener(stickMotion);
        bgNextButton.setOnClickListener(bgNextClick);
        directionKey.setOnTouchListener(keyTouch);
        voiceButton.setOnClickListener(voiceControlClick);
        //voiceRecognizer.startListening(voiceListener);
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
