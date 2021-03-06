package com.jackpan.TaiwanpetadoptionApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.AppLinks;



public class MainActivity extends Activity implements MfirebaeCallback {
    private ListView petlist;
    private TextView numtext;
    MyAdapter mydapter = null;
    private boolean isCencel = false;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
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
        // 設定資料庫跟撈取資料庫
        m = new MfiebaselibsClass(this, MainActivity.this);
        m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook", "data");
        //檢查登入狀態
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
        mAdapter = new MyAdapter(list);
        petlist.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }

    @Override
    public void getDeleteState(boolean b, String s, Object o) {
        if (b) {
            Toast.makeText(this, "刪除成功！", Toast.LENGTH_SHORT).show();
            list.clear();
            m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook", "data");
        } else {
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
                if (groupPosition == 1) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else {
                    list.clear();

                    Log.d(TAG, "onChildClick: "+groupPosition);

                    Log.d(TAG, "onChildClick: "+selectedItem);
                    if(selectedItem.equals("全部")){
                        m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook","data");
                    }else {
                        m.searchFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook", "cat",selectedItem);

                    }
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