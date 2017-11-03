package com.jackpan.TaiwanpetadoptionApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adlocus.PushAd;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.jackpan.Brokethenews.R;
import com.jackpan.libs.mfirebaselib.MfiebaselibsClass;
import com.jackpan.libs.mfirebaselib.MfirebaeCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Appkey.MyAdKey;
import bolts.AppLinks;

public class MainActivity extends Activity implements MfirebaeCallback {
    private ListView petlist;
    private ArrayList<ResultData> mAllData = new ArrayList<ResultData>();
    private TextView numtext;
    MyAdapter mydapter = null;
    private boolean isCencel = false;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    HashMap<String, ArrayList<ResultData>> mKind;
    HashMap<String, ArrayList<String>> mCity;
    private MyAdapter mAdapter;
    private ArrayAdapter<String> mAdapter2 = null;
    private DatabaseReference mDatabase;
    private Button mInviteBtn;
    ImageView imageView;
    private static final String TAG = "MainActivity";
    private ArrayList<FirebaseData> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    private String userUID;
    private ExpandableListView mExpandableListView;
    private  boolean deleteBool = false;
    private String[] items;

    private List<String> mExpandableListTitle;
    private ExpandableListAdapter mExpandableListAdapter;

    private Map<String, List<String>> mExpandableListData;
    MfiebaselibsClass m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m = new MfiebaselibsClass(this, MainActivity.this);
        m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook", "data");
        m.userLoginCheck();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mInviteBtn = (Button) findViewById(R.id.inviteBtn);
        mInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userUID.equals("")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("尚未登入")
                            .setMessage("還沒登入會員,不能發文喔！由左向右滑可以看到選單喔！")
                            .setPositiveButton("確定", null).show();
                    return;
                } else {
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, ForIdeaAndShareActivity.class);
                    startActivity(i);
                }

            }
        });

        Uri targetUrl =
                AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        } else {
            AppLinkData.fetchDeferredAppLinkData(
                    getApplication(),
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //process applink data
                        }
                    });
        }
        progressDialog = ProgressDialog.show(MainActivity.this, "讀取中", "目前資料量比較龐大，請耐心等候！！", false, false, new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                //
                isCencel = true;
                finish();
            }
        });
        boolean isbuy = MySharedPrefernces.getIsBuyed(this);
        if (isbuy) {
            Intent promotionIntent = new Intent(this, MainActivity.class);
            PushAd.enablePush(this, MyAdKey.AdLoucskey, promotionIntent);
        } else {
            PushAd.disablePush(MainActivity.this);
        }

        petlist = (ListView) findViewById(R.id.listView1);
        petlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent petint = new Intent(MainActivity.this, TwoActivity.class);
                petint.putExtra("json", new Gson().toJson(mAdapter.mDatas.get(position)));

                startActivity(petint);


            }
        });
        petlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userid = mAdapter.mDatas.get(i).id;
                String pathString = userid + mAdapter.mDatas.get(i).date;
                deleteData(pathString,userid);
                return true;
            }
        });

        mAdapter = new MyAdapter(list);

        petlist.setAdapter(mAdapter);
        items = getResources().getStringArray(R.array.film_genre);
        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());

        initdrawlatout();
//        QuMiConnect.ConnectQuMi(this, "5dd972580405c9d2", "6345c4cc61b91055");
//        AdView adView = (AdView) findViewById(R.id.adView);
//        adView.setSwitchStatus(true);//隐藏关闭按钮
//        QuMiConnect.getQumiConnectInstance(this).initAdView(adView);

//        test();
        test2();
    }
    private void test(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    Document doc = Jsoup.connect("http://www.cych.org.tw/cych_OA/view.aspx/Dr_view.aspx").get();
                    for (int i = 1; i < doc.select("tbody > tr").size(); i++) {
                        Log.d(TAG, "run: "+  doc.select("tbody > tr").get(i).select("td").text());
                        String array [] =doc.select("tbody > tr").get(i).select("td").text().split(" ");
                        Log.d(TAG, "看診狀態: "+array[0].toString());
                        Log.d(TAG, "科 別: "+array[1].toString());
                        Log.d(TAG, "看診號: "+array[2].toString());
                        Log.d(TAG, "最後掛號號碼: "+array[3].toString());
                        Log.d(TAG, "現場待診人數: "+array[4].toString());
                        Log.d(TAG, "報到狀態: "+array[5].toString());
                        Log.d(TAG, "醫師姓名: "+array[6].toString());
                        Log.d(TAG, "位 置: "+array[7].toString());


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: "+e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

        }.start();
    }
    private  void test2(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    Document doc = Jsoup.connect("http://www.chimei.org.tw/left/register/progress/qprogress.asp?idept=752&host=10").get();
                    Log.d(TAG, "run: "+doc.select("font[size=2]").text());
                    for (Element font : doc.select("font[size=2]")) {
                        Log.d(TAG, "run: "+font.text());
                        Log.d(TAG, "run: "+font.getElementsByTag("a").attr("href").toString());
                    }
                    for (int i = 1; i < doc.select("tr").size(); i++) {
                        Log.d(TAG, "run: "+doc.select("tr").get(i).text());
                        String [] array  = doc.select("tr").get(i).text().split("");
                        Log.d(TAG, "班別: "+array[0].toString());
                        Log.d(TAG, "科別名稱: "+array[1].toString());
                        Log.d(TAG, "醫師姓名: "+array[2].toString());
                        Log.d(TAG, "已掛號人數: "+array[3].toString());
                        Log.d(TAG, "等候看診人數: "+array[4].toString());
                        Log.d(TAG, "現在看到幾號: "+array[5].toString());


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: "+e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

        }.start();
    }
    private void deleteData(final String pathString,final  String userid ) {
        new AlertDialog.Builder(this)
                .setTitle("刪除文章")
                .setMessage("你確定要刪除本篇文章內容嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        m.userDeleteData("https://bookshare-99cb3.firebaseio.com/sharebook", pathString, userid);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void getDatabaseData(Object o) {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(o);
        FirebaseData g = gson.fromJson(jsonInString, FirebaseData.class);
        list.add(0, g);

        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }

    @Override
    public void getDeleteState(boolean b, String s, Object o) {
        if (b) {
            Toast.makeText(this, "刪除成功！", Toast.LENGTH_SHORT).show();
            list.clear();
            m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook", "data");
//            deleteBool  = true;
        } else {
//            if(deleteBool) return;
            Toast.makeText(this, "刪除失敗！你不是該文章的作者", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void createUserState(boolean b) {

    }

    @Override
    public void useLognState(boolean b) {
        if (b) {

        } else {

        }

    }

    @Override
    public void getuseLoginId(String s) {
        if (!s.equals("")) {
            MySharedPrefernces.saveUserId(this, s);
            userUID = s;

        } else {
            MySharedPrefernces.saveUserId(this, "");
            userUID = "";
        }

    }

    @Override
    public void getuserLoginEmail(String s) {
        MySharedPrefernces.saveUserMail(this,s);

    }

    @Override
    public void resetPassWordState(boolean b) {

    }

    @Override
    public void getFireBaseDBState(boolean b, String s) {

    }

    @Override
    public void getFirebaseStorageState(boolean b) {

    }

    @Override
    public void getFirebaseStorageType(String s, String s1) {

    }

    @Override
    public void getsSndPasswordResetEmailState(boolean b) {

    }

    @Override
    public void getUpdateUserName(boolean b) {

    }

    @Override
    public void getUserLogoutState(boolean b) {

    }


    public class MyAdapter extends BaseAdapter {
        private ArrayList<FirebaseData> mDatas;

        public MyAdapter(ArrayList<FirebaseData> datas) {
            mDatas = datas;
        }

        public void updateData(ArrayList<FirebaseData> datas) {
            mDatas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.mylayout, null);
            FirebaseData taipeiZoo = mDatas.get(position);
            TextView textname = (TextView) convertView.findViewById(R.id.name);
            TextView list = (TextView) convertView.findViewById(R.id.txtengname);
            TextView bigtext = (TextView) convertView.findViewById(R.id.bigtext);

            TextView place = (TextView) convertView.findViewById(R.id.palace);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView userview = (TextView) convertView.findViewById(R.id.view);
            TextView userlike = (TextView) convertView.findViewById(R.id.like);
            userview.setVisibility(View.GONE);
            userlike.setVisibility(View.GONE);
            bigtext.setText(taipeiZoo.cat);
            textname.setText(taipeiZoo.getTittle());
            list.setText("賣家:" + taipeiZoo.getName());
            time.setText("發文時間:" + taipeiZoo.getDate());
            place.setVisibility(View.VISIBLE);
            place.setText("ID:" + taipeiZoo.getId());
            place.setVisibility(View.GONE);
            imageView = (ImageView) convertView.findViewById(R.id.photoimg);

            Glide.with(MainActivity.this)
                    .load(taipeiZoo.getPic())
                    .centerCrop()
                    .placeholder(R.drawable.nophoto)
                    .crossFade()
                    .into(imageView);
            return convertView;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵

            MainActivity.this.finish();//關閉activity

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }


    public void ConfirmExit() {

        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this); //創建訊息方塊

        ad.setTitle("離開");

        ad.setMessage("確定要登出帳號?");

        ad.setPositiveButton("登出", new DialogInterface.OnClickListener() { //按"是",則退出應用程式

            public void onClick(DialogInterface dialog, int i) {


                MainActivity.this.finish();//關閉activity
                MySharedPrefernces.saveUserId(MainActivity.this, "");

            }

        });

        ad.setNegativeButton("不用", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作

            public void onClick(DialogInterface dialog, int i) {

                MainActivity.this.finish();//關閉activity
            }

        });

        ad.show();//顯示訊息視窗

    }


    //縮放照片
    private void ScalePic(Bitmap bitmap, int phone) {
        //縮放比例預設為1
        float mScale = 1;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if (bitmap.getWidth() > phone) {
            //判斷縮放比例
            mScale = (float) phone / (float) bitmap.getWidth();

            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
//			mImg.setImageBitmap(mScaleBitmap);
        }
//		else mImg.setImageBitmap(bitmap);
    }

    //儲存圖片
    public Uri savePicture(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        String fname = "temp.jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    private void initdrawlatout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mExpandableListView = (ExpandableListView) findViewById(R.id.navList);

        addDrawerItems();

    }

    private void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (mExpandableListData.get(mExpandableListTitle.get(groupPosition))))
                        .get(childPosition).toString();
                Log.d(TAG, "selectedItem: " + selectedItem);
                if (groupPosition == 1) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        m.setAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m.removeAuthListener();
    }
}