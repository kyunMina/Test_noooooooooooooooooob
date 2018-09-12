package com.example.mukai.test_noooooooooooooooooob;

import android.app.AlertDialog;
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
    float point_x[] = new float[5];
    float point_y[] = new float[5];

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
        paint.setColor(0xFFFF0000);
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

    //  点間補完
    public void DrawLine(){
        path.moveTo(point_x[points-1],point_y[points-1]);
        path.lineTo(point_x[points],point_y[points]);
    }

    //  角度計算
    public void math(){
        float A_x[] = new float[2];
        float A_y[] = new float[2];
        float B_x[] = new float[2];
        float B_y[] = new float[2];
        double cos[] = new double[2];

        for (int i = 0;i < 2;i++){
            A_x[i] = point_x[i] - point_x[i+1];
            A_y[i] = point_y[i] - point_y[i+1];
            B_x[i] = point_x[i+2] - point_x[i+1];
            B_y[i] = point_y[i+2] - point_y[i+1];
            cos[i] = (A_x[i] * B_x[i] + A_y[i] * B_y[i]) / (Math.sqrt(A_x[i]*A_x[i] + A_y[i]*A_y[i])*Math.sqrt(B_x[i]*B_x[i] + B_y[i]*B_y[i]));
            //Math.acos(cos[i]);
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("角度");
        alertDialog.setMessage(
                "acos[0] = "+Math.toDegrees(Math.acos(cos[0]))+ "\n"+
                        "cos[0] = "+cos[0]+ "\n"+
                        "acos[1] = "+Math.toDegrees(Math.acos(cos[1]))+ "\n"+
                        "cos[1] = "+cos[1]
        );
        alertDialog.setPositiveButton("完了", null);
        alertDialog.show();


    }

    //  タッチ時の挙動
    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //path.moveTo(x,y);
                //path.addCircle(x,y,10, Path.Direction.CCW);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                //path.addCircle(x,y,10,Path.Direction.CW);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:


                if (points < 5){
                    path.addCircle(x,y,10, Path.Direction.CCW);
                    Log.d("debug","x = "+x);
                    Log.d("debug","y = "+y);
                    Log.d("debug","Points"+points);

                    //  配列に各点を保存する
                    point_x[points] = x;
                    point_y[points] = y;
                    Points(x,y);

                    //  点間補完
                    if(points > 0){
                        DrawLine();
                    }


                    if(points == 4){
                        //  座標値確認アラートダイアログ
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("座標値");
                        alertDialog.setMessage(
                                "x0 = "+point_x[0]+ " , y0 = "+point_y[0]+ "\n"+
                                        "x1 = "+point_x[1]+ " , y1 = "+point_y[1]+ "\n"+
                                        "x2 = "+point_x[2]+ " , y2 = "+point_y[2]+ "\n"+
                                        "x3 = "+point_x[3]+ " , y3 = "+point_y[3]+ "\n");
                        alertDialog.setPositiveButton("完了", null);
                        //alertDialog.show();
                    }
                    points++;

                }
                else{
                    //Error();
                    //  角度計算
                    math();
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
