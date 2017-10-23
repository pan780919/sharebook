package com.jackpan.TaiwanpetadoptionApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.MyAPI.VersionChecker;
import com.adlocus.PushAd;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Appkey.MyAdKey;
import bolts.AppLinks;

import com.facebook.ads.*;

import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.jackpan.MyPushService;
import com.jackpan.VideoViewActivity;
import com.jackpan.libs.mfirebaselib.MfiebaselibsClass;
import com.jackpan.libs.mfirebaselib.MfirebaeCallback;
import com.jackpan.video.VideoMainActivity;
import com.jackpan.Brokethenews.R;

public class MainActivity extends Activity implements MfirebaeCallback {
    private ListView petlist;
    private ArrayList<ResultData> mAllData = new ArrayList<ResultData>();
    private TextView numtext;
    MyAdapter mydapter = null;
    private boolean isCencel = false;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;

    private com.facebook.ads.AdView adView, googleads;
    private InterstitialAd interstitial;
    private Spinner mSpinner, mSpinner2;
    HashMap<String, ArrayList<ResultData>> mKind;
    HashMap<String, ArrayList<String>> mCity;
    private MyAdapter mAdapter;
    private ArrayAdapter<String> mAdapter2 = null;
    private DatabaseReference mDatabase;
    private Button mInviteBtn;
    ImageView imageView;
    private static final String TAG = "MainActivity";
    private ArrayList<GayPlace> list = new ArrayList<>();

    private AdView admobAd, admobAd2;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    private String userUID;
    private ExpandableListView mExpandableListView;

    private String[] items;

    private List<String> mExpandableListTitle;
//    private NavigationManager mNavigationManager;
    private ExpandableListAdapter mExpandableListAdapter;

    private Map<String, List<String>> mExpandableListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
////		 //開啟全螢幕
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
////        //設定隱藏APP標題
//        requestWindowFeature(Window.FEATURE_NO_TITLE);


        MfiebaselibsClass m = new MfiebaselibsClass(this,MainActivity.this);
        m.getFirebaseDatabase("https://bookshare-99cb3.firebaseio.com/sharebook","data");
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(
                    @NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("onAuthStateChanged", "登入:" +
                            user.getUid());
                    userUID = user.getUid();
                    MySharedPrefernces.saveUserId(MainActivity.this, userUID);
                    if (firebaseAuth.getCurrentUser().getDisplayName() != null)
                        MySharedPrefernces.saveUserName(MainActivity.this, firebaseAuth.getCurrentUser().getDisplayName());
                    else MySharedPrefernces.saveUserName(MainActivity.this, "沒有暱稱");
                    if (firebaseAuth.getCurrentUser().getEmail() != null)
                        MySharedPrefernces.saveUserMail(MainActivity.this, firebaseAuth.getCurrentUser().getEmail());
                    else MySharedPrefernces.saveUserMail(MainActivity.this, "");
                    if (firebaseAuth.getCurrentUser().getPhotoUrl() != null)
                        MySharedPrefernces.saveUserPic(MainActivity.this, String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl()));
                    else MySharedPrefernces.saveUserPic(MainActivity.this, "");
                } else {
                    Log.d("onAuthStateChanged", "已登出");
                    MySharedPrefernces.saveUserId(MainActivity.this, "");
                }
            }
        };
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		MyGAManager.sendScreenName(this,"搜尋頁面");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mInviteBtn = (Button) findViewById(R.id.inviteBtn);
        mInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent();
//                i.setClass(MainActivity.this, LoginActivity.class);
//                startActivity(i);
//				uploadFromStream();
//				upLoad();
//				setFireBaseDB();
//				String appLinkUrl, previewImageUrl;
//				appLinkUrl = "https://play.google.com/store/apps/details?id=com.jackpan.TaipeiZoo";
//				previewImageUrl = "https://lh3.googleusercontent.com/2TPsyspPyf6WOYUEjduISOrg0HZH_xqtwa0G5LJsclL-knggHE0-KdbisjutLpr7lo8=w300-rw";
//
//				if (AppInviteDialog.canShow()) {
//					AppInviteContent content = new AppInviteContent.Builder()
//							.setApplinkUrl(appLinkUrl)
//							.setPreviewImageUrl(previewImageUrl)
//							.build();
//					AppInviteDialog.show(MainActivity.this, content);
//				}
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


//                userRef.removeValue();
                return true;
            }
        });

        mAdapter = new MyAdapter(list);

        petlist.setAdapter(mAdapter);
        items = getResources().getStringArray(R.array.film_genre);

//        setFireBase();
        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());

        initdrawlatout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushManager.getInstance().initialize(MainActivity.this, MyPushService.class);
        PushManager.getInstance().registerPushIntentService(MainActivity.this, MyIntentService.class);
    }





    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void getDatabaseData(Object o) {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(o);
        GayPlace g = gson.fromJson(jsonInString,GayPlace.class);
        list.add(0, g);


        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }



    public class MyAdapter extends BaseAdapter {
        //		private ArrayList<ResultData> mDatas;
        private ArrayList<GayPlace> mDatas;

        public MyAdapter(ArrayList<GayPlace> datas) {
            mDatas = datas;
        }

        public void updateData(ArrayList<GayPlace> datas) {
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
//			ResultData data = mDatas.get(position);
            GayPlace taipeiZoo = mDatas.get(position);
            TextView textname = (TextView) convertView.findViewById(R.id.name);
            TextView list = (TextView) convertView.findViewById(R.id.txtengname);
            TextView bigtext = (TextView) convertView.findViewById(R.id.bigtext);

            TextView place = (TextView) convertView.findViewById(R.id.palace);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView userview = (TextView) convertView.findViewById(R.id.view);
            TextView userlike = (TextView) convertView.findViewById(R.id.like);
            bigtext.setText(taipeiZoo.cat);
            textname.setText(taipeiZoo.getTittle());
            list.setText("賣家:" + taipeiZoo.getName());
            time.setText("發文時間:" + taipeiZoo.getDate());
//            bigtext.setVisibility(View.GONE);
            place.setVisibility(View.VISIBLE);
            place.setText("ID:" + taipeiZoo.getId());
            if (taipeiZoo.getView() == -1) {
                userview.setText("觀看人數:" + 0);
            } else {
                userview.setText("觀看人數:" + taipeiZoo.view);
            }
            if (taipeiZoo.getLike() == -1) {
                userlike.setText("喜歡人數:" + 0);
            } else {
                userlike.setText("喜歡人數:" + taipeiZoo.like);
            }


//            time.setVisibility(View.GONE);
//			place.setText("英文名:"+taipeiZoo.getAge());
//			time.setText("地理分布:"+data.A_Distribution );
            imageView = (ImageView) convertView.findViewById(R.id.photoimg);
            //			loadImage(data.album_file, img);
            //			Glide.with(MainActivity.this).load(data.album_file).into(imageView);

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

            ConfirmExit(); //呼叫ConfirmExit()函數

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
                auth.signOut();
                MySharedPrefernces.saveUserId(MainActivity.this, "");
//                interstitial.show();

            }

        });

        ad.setNegativeButton("不用", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作

            public void onClick(DialogInterface dialog, int i) {

                MainActivity.this.finish();//關閉activity
//                interstitial.show();
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
                Log.d(TAG, "selectedItem: "+selectedItem);
                Log.d(TAG, "groupPosition: "+groupPosition);
                Log.d(TAG, "childPosition: "+childPosition);
                Log.d(TAG, "id: "+id);
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}