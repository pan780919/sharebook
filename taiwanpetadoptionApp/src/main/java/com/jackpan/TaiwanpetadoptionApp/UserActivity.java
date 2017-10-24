package com.jackpan.TaiwanpetadoptionApp;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jackpan.Brokethenews.R;
import com.jackpan.libs.mfirebaselib.MfiebaselibsClass;
import com.jackpan.libs.mfirebaselib.MfirebaeCallback;

public class UserActivity extends Activity implements MfirebaeCallback {
    private ImageView mUsetImg;
    private TextView mUserName,mUserId,mUserEmail,mUserLv;
    Activity mActivty;
    private  String mUserPicStr,mUserNameStr,mUserIdStr,mUserMailStr;
    boolean mUserLvBoolean;
    private static final String TAG = "UserActivity";
    private DisplayMetrics mPhone;
    private Button mUserLogoutBtn;
    MfiebaselibsClass mfiebaselibsClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        mActivty = this;
         mfiebaselibsClass = new MfiebaselibsClass(mActivty,UserActivity.this);

        getUser();
        initLayout();
        setUser();

    }

    private void getUser() {
        mUserPicStr = MySharedPrefernces.getUserPic(mActivty);
        mUserIdStr = MySharedPrefernces.getUserId(mActivty);
        mUserNameStr = MySharedPrefernces.getUserName(mActivty);
        mUserMailStr = MySharedPrefernces.getUserMail(mActivty);
        mUserLvBoolean = MySharedPrefernces.getIsBuyed(mActivty);

    }


    private void initLayout(){

        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        mUsetImg = (ImageView) findViewById(R.id.userimg);
        mUserId = (TextView) findViewById(R.id.userid);
        mUserName = (TextView) findViewById(R.id.username);
        mUserEmail = (TextView) findViewById(R.id.usermail);
        mUserLv = (TextView) findViewById(R.id.userlv);
        mUserLogoutBtn = (Button) findViewById(R.id.userlogoutbtn);
        mUserLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfiebaselibsClass.userLogout(mUserIdStr);
            }
        });

    }
    private void setUser() {
        Glide.with(mActivty)

                .load(mUserPicStr)

                .error(R.drawable.nophoto)//load失敗的Drawable

                .centerCrop()//中心切圖, 會填滿

                .fitCenter()//中心fit, 以原本圖片的長寬為主

                .into(mUsetImg);
        mUserId.setText(mUserIdStr);
        mUserName.setText(mUserNameStr);
        mUserEmail.setText(mUserMailStr);
        if (!mUserLvBoolean) mUserLv.setText("普通會員");
        else  mUserLv.setText("尊榮會員");
    }

    @Override
    public void getDatabaseData(Object o) {

    }

    @Override
    public void getDeleteState(boolean b, String s) {

    }

    @Override
    public void createUserState(boolean b) {

    }

    @Override
    public void useLognState(boolean b) {

    }

    @Override
    public void getuseLoginId(String s) {

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
        if(b){
            Toast.makeText(mActivty,"會員登出成功",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mActivty,LoginActivity.class));
            mActivty.finish();
            MySharedPrefernces.saveUserId(mActivty,"");
        }else {

        }

    }
}
