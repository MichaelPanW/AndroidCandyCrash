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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CandyActivity extends AppCompatActivity {
    LinearLayout[] line;
    LinearLayout main;
    ImageView[][] img;
    //圖庫 可以直接增加減少
    private int[] image = {R.drawable.candy, R.drawable.sweet
            , R.drawable.cookies, R.drawable.gummy_bear, R.drawable.lollipop};
    float x1 = 0,x2 = 0,y1 = 0,y2 = 0;
    int count = 0;
    TextView brewTimeLabel;
    //time 關卡時間,size 寬*高,timeDelay 消除時間延遲
    int time = 30, size = 6, width, height,timeDelay=1000;
    CountDownTimer timer;
    int finishtime = 0;
    boolean haveLink=false,startGame=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candy);
        findViewById();
        setTimear();
        checkAllCandy();
        startGame=true;
    }
    //設置初始物件設定
    void findViewById() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //每個物件的寬高預留300px給顯示提示
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
            //每一列的linearlayout
            line[i] = new LinearLayout(this);
            line[i].setLayoutParams(Linparams);
            line[i].setBackgroundColor(Color.WHITE);
            main.addView(line[i]);
            for (int j = 0; j < size; j++) {
                img[j][i] = new ImageView(this);
                setNewRecource(j,i);
                img[j][i].setLayoutParams(Butparams);
                img[j][i].setClickable(true);
                img[j][i].setOnTouchListener(ontouch);
                img[j][i].setScaleType(ImageView.ScaleType.FIT_CENTER);
                line[i].addView(img[j][i]);
            }
        }
        brewTimeLabel = (TextView) findViewById(R.id.brewTimeLabel);
    }
    //倒數與提示
    void setTimear(){
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                brewTimeLabel.setText(time - (millisUntilFinished / 1000) + "秒 得到"+count+"分");
                finishtime = time - (int) (millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                //大於100分則過關;小於則失敗
                if(count>100){
                    finishGame("過關");
                }else{
                    finishGame("失敗");
                }
            }
        };
        timer.start();
    }
    //滑動事件
    OnTouchListener ontouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
                view.setBackgroundColor(R.drawable.back);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setBackgroundColor(Color.WHITE);
                JSONObject viewTag = (JSONObject) (view.getTag());
                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                try {
                    int newX=viewTag.getInt("x"),newY=viewTag.getInt("y");
                    if (y1 - y2 > 50) {
                        if (viewTag.getInt("y") > 0){
                            newY=newY-1;
                            Change((ImageView) view, img[newX][newY]);
                            delayChange((ImageView) view,img[newX][newY]);
                        }
                        Log.e("moveTag", view.getTag() + "向上滑y-1");
                    } else if (y2 - y1 > 50) {
                        if (viewTag.getInt("y") < size - 1){
                            newY=newY+1;
                            Change((ImageView) view, img[newX][newY]);
                            delayChange((ImageView) view,img[newX][newY]);
                        }
                        Log.e("moveTag", view.getTag() + "向下滑y+1");
                    } else if (x1 - x2 > 50) {
                        if (viewTag.getInt("x") > 0){
                            newX=newX-1;
                            Change((ImageView) view, img[newX][newY]);
                            delayChange((ImageView) view,img[newX][newY]);
                        }
                        Log.e("moveTag", view.getTag() + "向左滑x-1");
                    } else if (x2 - x1 > 50) {
                        if (viewTag.getInt("x") < size - 1){
                            newX=newX+1;
                            Change((ImageView) view, img[newX][newY]);
                            delayChange((ImageView) view,img[newX][newY]);
                        }
                        Log.e("moveTag", view.getTag() + "向右滑x+1");
                    }
                    showRemoveAllColor();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };
    //準備如果沒有連線換回來
    void delayChange(final ImageView tmpTag1, final ImageView tmpTag2){
        if(!checkAllCandy()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    Change(tmpTag1, tmpTag2);

                }}, timeDelay);
        }
    }
    //檢查所有的位置是否有連線
    boolean checkAllCandy(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JSONObject viewTag1 = (JSONObject) (img[i][j].getTag());
                try {
                    if (viewTag1.getInt("status") != 2) {
                        checkLine(img[i][j]);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //如果有連線就先上色，幾秒後:背景變白、刪除該物件並補位、清掉設定的status、再次檢查
        if(haveLink){
            //開始遊戲前先清理連線的物件
            if(startGame){
                showRemoveAllColor();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        initAllBackColor();
                        removeAllView();
                        clearAllStatus();
                        checkAllCandy();
                    }}, timeDelay);
            }else{
                removeAllView();
                clearAllStatus();
                checkAllCandy();
            }
        }
        if(haveLink){
            return true;
        }else{
            return false;
        }
    }
    //顯示所有顏色
    void showRemoveAllColor() {
        String Logtext="\n";
        String LogString="";
        //給補位的物件上黃色，因為會互相遮蓋所以黑色黃色分開寫
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++) {
                JSONObject viewTag1 = (JSONObject) (img[i][j].getTag());
                try {
                    if(viewTag1.getInt("status")==2){
                        showRemoveColor(img[i][j]);
                        Logtext+=" 1";
                        LogString+=i+"_"+j+" ";
                    }else{
                        Logtext+=" 0";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Logtext+=" \n";
        }
        //給刪除的物件上黑色
        for(int i=0;i<size;i++) {
            for (int j = 0; j < size; j++) {
                JSONObject viewTag1 = (JSONObject) (img[i][j].getTag());
                try {
                    if(viewTag1.getInt("status")==2){
                        img[i][j].setBackgroundColor(Color.BLACK);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("LogString",LogString);
        Log.e("logmap",Logtext);
    }
    //傳入圖片物件，給所有補位的塗黃色
    void showRemoveColor(ImageView tmpTag){
            JSONObject viewTag = (JSONObject) (tmpTag.getTag());
            try {
                for(int j=viewTag.getInt("y");j>0;j--){
                    img[viewTag.getInt("x")][j-1].setBackgroundColor(Color.YELLOW);
                }
                //補充刪去的圖
                setNewRecource(viewTag.getInt("x"),0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    //刪除物件，並遞補上方掉下來的跟新的物件
    void removeView(ImageView tmpTag){
        if(startGame)
            count++;
        JSONObject viewTag = (JSONObject) (tmpTag.getTag());
        try {
            //遞補前面的上面的物件
            for(int i=viewTag.getInt("y");i>0;i--){
                Change(img[viewTag.getInt("x")][i],img[viewTag.getInt("x")][i-1]);
            }
            //最上面的位置補新的物件
            setNewRecource(viewTag.getInt("x"),0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //給x,y位置放隨機的物件
    void setNewRecource(int x, int y){
        JSONObject holder = new JSONObject();
        int newImg = rangomImg();
        try {
            holder.put("x", x);//x座標
            holder.put("y", y);//y座標
            holder.put("index", newImg);//對應圖號
            holder.put("status", 1);//狀態 1:一般 2:待消
        } catch (JSONException e) {
            e.printStackTrace();
        }
        img[x][y].setImageResource(image[newImg]);
        img[x][y].setTag(holder);
    }
    //檢查欄列是否有連線
    void checkLine(ImageView tmpTag){
        List<ImageView> imgList=new ArrayList<ImageView>();
        JSONObject viewTag = (JSONObject) (tmpTag.getTag());
        try {
            //檢查列
            int tmpY=viewTag.getInt("y");
            //左邊
            for(int i=viewTag.getInt("x")-1;i>=0;i--){
                if(checkEqual(img[i][tmpY],tmpTag)){
                    imgList.add(img[i][tmpY]);
                }else{
                    break;
                }
            }
            //右邊
            for(int i=viewTag.getInt("x")+1;i<size;i++){
                if(checkEqual(img[i][tmpY],tmpTag)){
                    imgList.add(img[i][tmpY]);
                }else{
                    break;
                }
            }
            imgList.add(tmpTag);

            Log.e("llogcount", String.valueOf(imgList.size()));
            //如果有三個以上則判定為連線
            if(imgList.size()>=3){
                String Logtext="";
                for(int i=0;i<imgList.size();i++){
                    JSONObject viewTag1=(JSONObject)imgList.get(i).getTag();
                    Logtext+=viewTag1.getString("x")+"_"+viewTag1.getString("y")+" ";

                }
                Log.e("llogready",Logtext);

                setListStatus(imgList);
            }
            imgList=new ArrayList<ImageView>();
            //檢查欄
            int tmpX=viewTag.getInt("x");
            //上面
            for(int i=viewTag.getInt("y")-1;i>=0;i--){
                if(checkEqual(img[tmpX][i],tmpTag)){
                    imgList.add(img[tmpX][i]);
                    Log.e("lvlogreadyput",tmpX+"_"+i);
                }else{
                    break;
                }
            }
            //下面
            for(int i=viewTag.getInt("y")+1;i<size;i++){
                if(checkEqual(img[tmpX][i],tmpTag)){
                    imgList.add(img[tmpX][i]);
                    Log.e("rvlogreadyput",tmpX+"_"+i);
                }else{
                    break;
                }
            }
            imgList.add(tmpTag);
            Log.e("vlogcount", String.valueOf(imgList.size()));
            //如果有三個以上則判定為連線
            if(imgList.size()>=3){
                String Logtext="";
                for(int i=0;i<imgList.size();i++){
                    JSONObject viewTag1=(JSONObject)imgList.get(i).getTag();
                    Logtext+=viewTag1.getString("x")+"_"+viewTag1.getString("y")+" ";
                }
                Log.e("vlogready",Logtext);
                setListStatus(imgList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //傳入選定的list，給對應的物件tag
    void setListStatus(List<ImageView> imgList){
        String Logtext="";
        haveLink=true;
        for(int i=0;i<imgList.size();i++){
            JSONObject viewTag = (JSONObject) (imgList.get(i).getTag());
            try {
                viewTag.put("status",2);
                Logtext+=viewTag.getString("x")+"_"+viewTag.getString("y")+" ";
                img[viewTag.getInt("x")][viewTag.getInt("y")].setTag(viewTag);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.e("vlogsetstatus",Logtext);
    }

    //把有tag的物件都刪除
    void removeAllView(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++) {
                JSONObject viewTag1 = (JSONObject) (img[i][j].getTag());
                try {
                    if(viewTag1.getInt("status")==2)
                        removeView(img[i][j]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        haveLink=false;
    }
    //檢查兩個物件是否相同
    boolean checkEqual(final ImageView tmpTag1, final ImageView tmpTag2){
        JSONObject viewTag1 = (JSONObject) (tmpTag1.getTag());
        JSONObject viewTag2 = (JSONObject) (tmpTag2.getTag());
        try {
            Log.e("checkEqual",viewTag1.getString("x")+"_"+viewTag1.getString("y")+"="+viewTag2.getString("x")+"_"+viewTag2.getString("y")+" "+viewTag1.getString("index")+"="+viewTag2.getString("index"));
            return viewTag1.getInt("index")==viewTag2.getInt("index");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    //把所有物件填白
    void initAllBackColor(){
        Log.e("initAllBackColor","white");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                img[i][j].setBackgroundColor(Color.WHITE);
            }
        }
    }
    //把所有tag還原
    void clearAllStatus(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JSONObject viewTag = (JSONObject) (img[i][j].getTag());
                try {
                    viewTag.put("status",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                img[i][j].setTag(viewTag);
            }
        }

    }
    //交換圖片與設定
    void Change(ImageView tmpTag1, ImageView tmpTag2) {

        JSONObject viewTag1 = (JSONObject) (tmpTag1.getTag());
        JSONObject viewTag2 = (JSONObject) (tmpTag2.getTag());
        int tmpInt=0;
        try {
            Log.e("ChangeViewLog",viewTag1.getString("x")+"_"+viewTag1.getString("y")+"<=>"+viewTag2.getString("x")+"_"+
                    viewTag2.getString("y"));
            tmpInt=viewTag1.getInt("x");
            viewTag1.put("x",viewTag2.getInt("x"));
            viewTag2.put("x",tmpInt);
            tmpInt=viewTag1.getInt("y");
            viewTag1.put("y",viewTag2.getInt("y"));
            viewTag2.put("y",tmpInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Drawable myDrawable = tmpTag1.getDrawable();
        tmpTag1.setImageDrawable(tmpTag2.getDrawable());
        tmpTag2.setImageDrawable(myDrawable);
        tmpTag1.setTag(viewTag2);
        tmpTag2.setTag(viewTag1);
    }
    //結束遊戲
    void finishGame(String title) {
        //CandyActivity.this.finish();
        AlertDialog.Builder builder = new AlertDialog.Builder(CandyActivity.this
               );
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                CandyActivity.this.finish();

            }
        });
        builder.setMessage("您花了" + finishtime + "秒鐘，得到" + count + "分")
                .setTitle(title);
        builder.setPositiveButton("結束", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                CandyActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        if (!CandyActivity.this.isFinishing()) {
        }
    }
    //隨機取圖片
    int rangomImg() {
        int imgId = (int) (Math.random() * image.length);
        return imgId;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
