package com.example.cameraalbumtest2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawActivity extends AppCompatActivity implements View.OnClickListener, PaletteView.Callback,Handler.Callback {


    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private PaletteView mPaletteView;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        HideColor();



        mPaletteView = (PaletteView) findViewById(R.id.palette);
        mPaletteView.setCallback(this);

        mUndoView = findViewById(R.id.undo);
        mRedoView = findViewById(R.id.redo);
        mPenView = findViewById(R.id.pen);
        mPenView.setSelected(true);
        mEraserView = findViewById(R.id.eraser);
        mClearView = findViewById(R.id.clear);

        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mPenView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        mClearView.setOnClickListener(this);

        findViewById(R.id.black).setOnClickListener(this);
        findViewById(R.id.red).setOnClickListener(this);
        findViewById(R.id.yellow).setOnClickListener(this);
        findViewById(R.id.blue).setOnClickListener(this);
        findViewById(R.id.green).setOnClickListener(this);
        findViewById(R.id.brown).setOnClickListener(this);

        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);

        mHandler = new Handler(this);
    }

    @Override
    protected void onDestroy() {
        //调用该方法，用于释放此Activity之前所占用的资源
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg(){
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);//dialog弹出后点击屏幕或物理返回键，dialog不消失
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * 此方法用于初始化菜单，其中menu参数就是即将要显示的Menu实例。 返回true则显示该menu,false 则不显示;
         * (只会在第一次初始化菜单时调用) Inflate the menu; this adds items to the action bar
         * if it is present.
         */
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if(mSaveProgressDlg==null){
                    initSaveProgressDlg();
                }
                mSaveProgressDlg.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm = mPaletteView.buildBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(DrawActivity.this, savedFile);
                            mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                        }else{
                            mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        }
                    }
                }).start();
                break;
        }
        return true;
    }

    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mPaletteView.undo();
                break;
            case R.id.redo:
                mPaletteView.redo();
                break;
            case R.id.pen:
                v.setSelected(true);
                mEraserView.setSelected(false);
                ShowColor();
                mPaletteView.setMode(PaletteView.Mode.DRAW);

                break;
            case R.id.eraser:
                v.setSelected(true);
                mPenView.setSelected(false);
                HideColor();
                mPaletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.clear:
                mPaletteView.clear();
                break;
            case R.id.black:
                mPaletteView.getmPaint().setColor(0XFF000000);
                HideColor();
                break;
            case R.id.red:
                mPaletteView.getmPaint().setColor(0XFFFF0000);
                HideColor();
                break;
            case R.id.yellow:
                 mPaletteView.getmPaint().setColor(0XFFFFFF00);
                 HideColor();
                    break;
            case R.id.blue:
                mPaletteView.getmPaint().setColor(0XFF0000FF);
                HideColor();
                break;
            case R.id.green:
                mPaletteView.getmPaint().setColor(0XFF00FF00);
                HideColor();
                break;
            case R.id.brown:
                mPaletteView.getmPaint().setColor(0XFF663300);
                HideColor();
                break;
        }
    }
    public void HideAnswer(){
        LinearLayout tmp = (LinearLayout) findViewById(R.id.reply);
        tmp.setVisibility(View.GONE);
    }

    public void HideDraw(){
        LinearLayout hd = (LinearLayout) findViewById(R.id.draw);
        hd.setVisibility(View.GONE);
    }

    public void HideColor(){
        LinearLayout hd = (LinearLayout) findViewById(R.id.Color);
        hd.setVisibility(View.GONE);
    }

    public void ShowAnswer(){
        LinearLayout tmp = (LinearLayout) findViewById(R.id.reply);
        tmp.setVisibility(View.VISIBLE);
    }

    public void ShowDraw(){
        LinearLayout hd = (LinearLayout) findViewById(R.id.draw);
        hd.setVisibility(View.VISIBLE);
    }

    public void ShowColor(){
        LinearLayout hd = (LinearLayout) findViewById(R.id.Color);
        hd.setVisibility(View.VISIBLE);
    }
}
