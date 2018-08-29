package com.example.mukai.test_noooooooooooooooooob;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;

    private ImageView imageView;
    private Uri cameraUri;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  ここから
        imageView =findViewById(R.id.image_view);

        final Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Android6,API23以上でパーミッションの確認
                if(Build.VERSION.SDK_INT >= 23){
                    checkPermission();
                }
                else{
                    cameraIntent();
                }
            }
        });

    }

    private void cameraIntent(){
        Log.d("debug","cameraIntent()");

        //  保存先のフォルダーを作成
        File cameraFolder = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),"IMG_noob");
        cameraFolder.mkdirs();

        //  保存ファイル名
        String fileName = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        filePath = String.format("%s/%s.jpg",cameraFolder.getPath(),fileName);
        Log.d("debug","filepath:"+filePath);

        //  capture画像のファイルパス
        File cameraFile = new File(filePath);


        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
        startActivityForResult(intent,RESULT_CAMERA);


        Log.d("debug","startActivityForResult");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        final Button cameraButton = findViewById(R.id.camera_button);
        final View PaintView = findViewById(R.id.view);

        if(requestCode == RESULT_CAMERA){

            if(resultCode != 0){
                Log.d("debug","resultCode = "+resultCode);
                rotateImage(filePath);
                imageView.setImageURI(cameraUri);
                cameraButton.setVisibility(View.GONE);
                PaintView.setVisibility(View.VISIBLE);
                registerDatabase(filePath);
            }
            else{
                Log.d("debug","cameraUri == null");
            }
        }
    }

    //  Androidのデータベースへ登録
    private void registerDatabase(String file){

        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
    }


    private int getRotateDegree(String filePath){

        int degree = 0;
        try{
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
                degree = 90;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180){
                degree =180;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
                degree = 270;
            }
            if (degree != 0){
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "0");
                exifInterface.saveAttributes();
            }
        } catch (IOException e){
            degree = -1;
            e.printStackTrace();
        }
        return degree;
    }

    public int rotateImage(String filePath){

        //filePath = filePath.replace("file://","");
        int degree = getRotateDegree(filePath);
        Log.d("debug","degree:"+degree);

        if (degree > 0){
            OutputStream out = null;
            Bitmap bitmap = null;
            Bitmap rotatedImage = null;
            try{
                Matrix mat = new Matrix();
                mat.postRotate(degree);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opts);
                /*
                int width = 480;
                int scale = 1;

                if (opts.outWidth > width) {
                    scale = opts.outWidth / width + 2;
                }
                */
                opts.inJustDecodeBounds = false;

                //  サイズ調整
                opts.inSampleSize = 2;
                //
                bitmap = BitmapFactory.decodeFile(filePath, opts);
                rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                out = new FileOutputStream(filePath);
                rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null) try { out.close(); } catch (IOException e) {}
                if (bitmap != null) bitmap.recycle();
                if (rotatedImage != null) rotatedImage.recycle();
            }
        }
        return degree;
    }


    //  Runtime Permission Check
    private void checkPermission(){

        //  すでに許可している
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            cameraIntent();
        }
        //  拒否していた場合
        else{
            requestPermission();
        }
    }

    //  許可を求める
    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }
        else{
            Toast toast = Toast.makeText(this,"許可されないと実行できません",Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},REQUEST_PERMISSION);
        }
    }

    //  結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults){

        Log.d("debug","onRequestPermissionResult()");

        if(requestCode == REQUEST_PERMISSION){
            //  使用が許可された
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                cameraIntent();
            }
            else{
                //  それでも拒否されたとき
                Toast toast = Toast.makeText(this,"これ以上何もできません",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug","onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug","onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug","onDestroy()");
    }

}
