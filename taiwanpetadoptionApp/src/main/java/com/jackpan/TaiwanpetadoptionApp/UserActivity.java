package com.jackpan.TaiwanpetadoptionApp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button mUserLogoutBtn,mResetPassword;
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
        mResetPassword = (Button) findViewById(R.id.button2);
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText passwordeditText = new EditText(UserActivity.this);
                new AlertDialog.Builder(UserActivity.this)
                        .setView(passwordeditText)
                        .setTitle("重設密碼")
                        .setMessage("請輸入當初設定舊密碼")
                        .setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String oldPassword = passwordeditText.getText().toString().trim();
                                if (oldPassword.equals("")) {
                                    Toast.makeText(UserActivity.this, "請勿輸入空白", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final EditText passwordeditText = new EditText(UserActivity.this);
                                new AlertDialog.Builder(UserActivity.this)
                                        .setView(passwordeditText)
                                        .setTitle("重設密碼")
                                        .setMessage("請輸入新密碼")
                                        .setPositiveButton("送出", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String newPassword = passwordeditText.getText().toString().trim();
                                                if (newPassword.equals("")) {
                                                    Toast.makeText(UserActivity.this, "請勿輸入空白", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                mfiebaselibsClass.resetPassWord(oldPassword,newPassword);
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                                dialogInterface.dismiss();
                            }
                        }).show();


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
        if(mUserMailStr.equals("")) mUserEmail.setText("無資料");
        else mUserEmail.setText(mUserMailStr);
        if (!mUserLvBoolean) mUserLv.setText("普通會員");
        else  mUserLv.setText("尊榮會員");
    }

    @Override
    public void getDatabaseData(Object o) {

    }

    @Override
    public void getDeleteState(boolean b, String s ,Object o) {

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
    public void getuserLoginEmail(String s) {

    }

    @Override
    public void resetPassWordState(boolean b) {
        if(b){
            Toast.makeText(this, "成功修改密碼！！", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "發生錯誤,查無此帳號", Toast.LENGTH_SHORT).show();
        }

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
