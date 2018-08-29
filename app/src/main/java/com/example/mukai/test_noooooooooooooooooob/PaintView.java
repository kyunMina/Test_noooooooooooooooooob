package com.example.mukai.test_noooooooooooooooooob;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class PaintView extends View {

    int points = 0;
    private Paint paint;
    private Path path;

    //  コンストラクタ
    public PaintView(Context context){
        this(context,null);
    }

    public PaintView(Context context, AttributeSet attrs){
        super(context,attrs);
        path = new Path();
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
    }

    //  画面描画メソッド
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawPath(path,paint);
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_foreground);
        //canvas.drawBitmap(bmp,100,150,paint);
    }

    //  タッチ時の挙動
    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();
        float point_x[] = new float[5];
        float point_y[] = new float[5];


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                path.moveTo(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (points < 4){
                    path.addCircle(x,y,10, Path.Direction.CCW);
                    points++;
                    Log.d("debug","x = "+x);
                    Log.d("debug","y = "+y);
                    Log.d("debug","Points"+points);
                    Points(x,y);

                    //  配列に各点を保存する
                    point_x[points] = x;
                    point_y[points] = y;
                    Log.d("debug","point_x = "+point_x[points]);
                    Log.d("debug","point_y = "+point_y[points]);


                }
                else{
                    Error();
                }
                invalidate();
                break;
        }
        return true;
    }

    //  Toast_Error
    protected void Error(){
        Toast toast = Toast.makeText(this.getContext(),"Error",Toast.LENGTH_SHORT);
        toast.show();
    }

    //  Toast_Points_
    protected void Points(float x,float y){
        Toast toast = Toast.makeText(this.getContext(),""+x+y,Toast.LENGTH_SHORT);
        toast.show();
    }
}
