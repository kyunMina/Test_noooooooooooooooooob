package com.example.mukai.test_noooooooooooooooooob;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  ここから
        imageView =findViewById(R.id.image_view);

        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAMERA);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RESULT_CAMERA){
            Bitmap bitmap;
            //  cancelしたケース
            if(data.getExtras() == null){
                Log.d("debug","cancel");
                return;
            }
            else{
                bitmap =(Bitmap)data.getExtras().get("data");

                //  画像サイズの計測
                int bmpWidth = bitmap.getWidth();
                int bmpHeight = bitmap.getHeight();
                Log.d("debug", String.format("w=%d", bmpWidth));
                Log.d("debug", String.format("h=%d", bmpHeight));
                //Log.d("debug", String.format("dis_DEGREE = %d", getWindowManager().getDefaultDisplay().getRotation()));

            }


            //  画像の向き修正
            //  https://akira-watson.com/android/matrix.html

            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            //  Matrixインスタンス生成
            Matrix matrix =new Matrix();

            //  画像中心を基点に90度回転
            matrix.setRotate(90,bmpWidth/2,bmpHeight/2);

            //  90度回転したBitmap画像を生成
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bmpWidth,bmpHeight,matrix,true);

            //

            imageView.setImageBitmap(bitmap1);

        }
    }
}
