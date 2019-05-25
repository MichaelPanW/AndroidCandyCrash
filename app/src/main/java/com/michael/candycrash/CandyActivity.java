package com.michael.candycrash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CandyActivity extends AppCompatActivity {
    LinearLayout[] line;
    LinearLayout main;
    private int[] image = {R.drawable.candy, R.drawable.photo, android.R.drawable.ic_menu_add
            , R.drawable.list, R.drawable.cookies, android.R.drawable.ic_menu_agenda
            , android.R.drawable.ic_menu_always_landscape_portrait, android.R.drawable.ic_menu_call, android.R.drawable.ic_menu_camera};
    ImageView[][] img;
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    int count = 3;
    TextView brewTimeLabel;
    int time = 30, size = 5, width, height;
    Button arrbut[][];
    int arrsou[][];
    Drawable backImg;
    CountDownTimer timer;
    int finishtime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candy);
        findViewById();
    }

    //滑動事件
    OnTouchListener ontouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.e("motion", String.valueOf(event.getAction()));

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
                view.setBackgroundColor(R.drawable.back);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setBackgroundColor(Color.WHITE);
                Log.e("test", String.valueOf(view.getTag()));
                JSONObject viewTag = (JSONObject) (view.getTag());
                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                try {
                    if (y1 - y2 > 50) {
                        if (viewTag.getInt("y") > 0){
                            Change((ImageView) view, img[viewTag.getInt("x")][viewTag.getInt("y") - 1]);
                            if(!checkLine((ImageView) view) && !checkLine(img[viewTag.getInt("x")][viewTag.getInt("y") - 1])){
                                delayChange((ImageView) view,img[viewTag.getInt("x")][viewTag.getInt("y") - 1]);
                            }

                        }

                        Log.e("moveTag", view.getTag() + "向上滑y-1");
                    } else if (y2 - y1 > 50) {
                        if (viewTag.getInt("y") < size - 1){
                            Change((ImageView) view, img[viewTag.getInt("x")][viewTag.getInt("y") + 1]);
                            if(!checkLine((ImageView) view) && !checkLine(img[viewTag.getInt("x")][viewTag.getInt("y") + 1])){
                                delayChange((ImageView) view,img[viewTag.getInt("x")][viewTag.getInt("y") + 1]);
                            }


                        }
                        Log.e("moveTag", view.getTag() + "向下滑y+1");
                    } else if (x1 - x2 > 50) {
                        if (viewTag.getInt("x") > 0){
                            Change((ImageView) view, img[viewTag.getInt("x") - 1][viewTag.getInt("y")]);
                            delayChange((ImageView) view, img[viewTag.getInt("x") - 1][viewTag.getInt("y")]);
                            if(!checkLine((ImageView) view) && !checkLine(img[viewTag.getInt("x")-1][viewTag.getInt("y")])){
                                delayChange((ImageView) view,img[viewTag.getInt("x")-1][viewTag.getInt("y") ]);
                            }



                        }
                        Log.e("moveTag", view.getTag() + "向左滑x-1");
                    } else if (x2 - x1 > 50) {
                        if (viewTag.getInt("x") < size - 1){
                            Change((ImageView) view, img[viewTag.getInt("x") + 1][viewTag.getInt("y")]);
                            if(!checkLine((ImageView) view) && !checkLine(img[viewTag.getInt("x")+1][viewTag.getInt("y")])){
                                delayChange((ImageView) view,img[viewTag.getInt("x")+1][viewTag.getInt("y") ]);
                            }


                        }
                        Log.e("moveTag", view.getTag() + "向右滑x+1");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };
    void delayChange(final ImageView tmpTag1, final ImageView tmpTag2){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                Change(tmpTag1, tmpTag2);

            }}, 1000);
    }
    void removeView(ImageView tmpTag){
        JSONObject viewTag = (JSONObject) (tmpTag.getTag());
        try {
            for(int i=viewTag.getInt("y");i>0;i--){
                Change(img[i][viewTag.getInt("y")],img[i-1][viewTag.getInt("y")]);
            }
            img[0][viewTag.getInt("y")].setImageResource(image[rangomImg()]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    }
    boolean checkLine(ImageView tmpTag){
        List<ImageView> imgList=new ArrayList<ImageView>();
        JSONObject viewTag = (JSONObject) (tmpTag.getTag());
        boolean returnBool=false;
        try {
            for(int i=viewTag.getInt("x");i>0;i--){
                if(checkEqual(img[i][viewTag.getInt("y")],tmpTag)){
                    imgList.add(img[i][viewTag.getInt("y")]);
                }
            }
            for(int i=viewTag.getInt("x");i<size;i++){
                if(checkEqual(img[i][viewTag.getInt("y")],tmpTag)){
                    imgList.add(img[i][viewTag.getInt("y")]);
                }
            }
            if(imgList.size()>2){
                returnBool=true;
                for(int i=0;i<imgList.size();i++)
                    removeView(imgList.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnBool;
    }
    boolean checkEqual(final ImageView tmpTag1, final ImageView tmpTag2){
        return tmpTag1.getDrawable()==tmpTag2.getDrawable();
    }
    void findViewById() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels / size;
        height = (metrics.heightPixels - 300) / size;
        line = new LinearLayout[size];
        main = (LinearLayout) findViewById(R.id.linemain);
        img = new ImageView[size][size];
        LinearLayout.LayoutParams Linparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams Butparams = new LinearLayout.LayoutParams(width, height);
        Linparams.weight = 1;
        Butparams.weight = 1;
        for (int i = 0; i < size; i++) {
            line[i] = new LinearLayout(this);
            line[i].setLayoutParams(Linparams);
            main.addView(line[i]);
            for (int j = 0; j < size; j++) {
                img[j][i] = new ImageView(this);
                JSONObject holder = new JSONObject();
                int newImg = rangomImg();
                try {
                    holder.put("x", j);
                    holder.put("y", i);
                    holder.put("index", newImg);
                    holder.put("img", image[newImg]);
                    holder.put("status", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                img[j][i].setLayoutParams(Butparams);
                img[j][i].setClickable(true);
                img[j][i].setTag(holder);
                img[j][i].setOnTouchListener(ontouch);
                img[j][i].setImageResource(image[newImg]);
                img[j][i].setScaleType(ImageView.ScaleType.FIT_CENTER);
                line[i].addView(img[j][i]);
            }
        }
        brewTimeLabel = (TextView) findViewById(R.id.brewTimeLabel);
        arrbut = new Button[size][size];
        arrsou = new int[size][size];

        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                brewTimeLabel.setText(time - (millisUntilFinished / 1000) + "秒");
                finishtime = time - (int) (millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                finishGame("失敗");

            }
        };
        timer.start();
    }

    //交換圖片
    void Change(ImageView st, ImageView ed) {
        Drawable myDrawable = st.getDrawable();
        st.setImageDrawable(ed.getDrawable());
        ed.setImageDrawable(myDrawable);
    }

    void finishGame(String title) {
        CandyActivity.this.finish();
        AlertDialog.Builder builder = new AlertDialog.Builder(CandyActivity.this);

        builder.setMessage("您花了" + finishtime + "秒鐘，完成了" + count + "張牌")
                .setTitle(title);
        builder.setPositiveButton("結束", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
        if (!CandyActivity.this.isFinishing()) {
            dialog.show();
        }

    }

    int rangomImg() {
        int imgId = (int) (Math.random() * image.length);
        return imgId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
                /*if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                    y1 = event.getY();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    y2 = event.getY();
                    if(y1 - y2 > 50) {
                        Toast.makeText(this, "向上滑", Toast.LENGTH_SHORT).show();
                    } else if(y2 - y1 > 50) {
                        Toast.makeText(this, "向下滑", Toast.LENGTH_SHORT).show();
                    } else if(x1 - x2 > 50) {
                        Toast.makeText(this, "向左滑", Toast.LENGTH_SHORT).show();
                    } else if(x2 - x1 > 50) {
                        Toast.makeText(this, "向右滑", Toast.LENGTH_SHORT).show();
                    }
                }*/
        return super.onTouchEvent(event);
    }

}
