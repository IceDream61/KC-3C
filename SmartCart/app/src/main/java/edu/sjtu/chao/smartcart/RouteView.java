package edu.sjtu.chao.smartcart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by winger on 10/24/15.
 */
class RouteView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**每50帧刷新一次屏幕*/
    public static final int TIME_IN_FRAME = 50;
    private boolean routeRunning = false;
    private Thread thread;
    private SurfaceHolder routeHolder;
    private Path routePath=null;
    private Paint routePaint=null;
    private Canvas routeCanvas=null;


    public RouteView(Context context) {
        super(context);
        init();
    }

    public RouteView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public RouteView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    private void init() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        routeHolder = this.getHolder();
        routeHolder.setFormat(PixelFormat.TRANSLUCENT);
        routeHolder.addCallback(this);
        routePath = new Path();
        routePaint = new Paint();
        routePaint.setColor(getResources().getColor(R.color.blue));
        routePaint.setAntiAlias(true);
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeWidth(6);
        //routePaint.setAlpha(80);
        thread = new Thread(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        routeRunning = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //routeRunning = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //thread.stop();
        routeRunning = false;
    }

    @Override
    public void run() {
        while (routeRunning) {
            //Log.d("hint", "The thread is running!");
            long startTime = System.currentTimeMillis();
            synchronized (routeHolder) {
                routeCanvas = routeHolder.lockCanvas(null);
                routeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                routeCanvas.drawPath(routePath, routePaint);
                routeHolder.unlockCanvasAndPost(routeCanvas);
            }
            long endTime = System.currentTimeMillis();
            int diffTime = ((int) (endTime - startTime));
            while (diffTime < TIME_IN_FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                Thread.yield();
            }
        }
    }

    float lastX=0f, lastY=0f;
    float X,Y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        X=event.getX();
        Y=event.getY();
        if(outBound((int)X, (int)Y)){
            sendPath();
            return true;
        }

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                routePath.moveTo(X,Y);
                break;
            case MotionEvent.ACTION_MOVE:
                routePath.quadTo(lastX,lastY,X,Y);
                break;
            case MotionEvent.ACTION_UP:
                sendPath();
                break;
            case MotionEvent.ACTION_OUTSIDE:
                sendPath();
                break;
        }
        lastX=X;lastY=Y;
        //Log.d("Path",routePath.toString());
        return true;
    }

    public void sendPath(){
        routePath.reset();
    }

    private boolean outBound(int x, int y){
        if(x<this.getWidth() && x>0 && y>0 && y<this.getHeight())return false;
        return true;
    }
}