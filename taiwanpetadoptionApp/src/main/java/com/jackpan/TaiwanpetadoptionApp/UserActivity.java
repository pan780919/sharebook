package com.jackpan.TaiwanpetadoptionApp;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jackpan.Brokethenews.R;

public class UserActivity extends Activity{
    private ImageView mUsetImg;
    private TextView mUserName,mUserId,mUserEmail,mUserLv;
    Activity mActivty;
    private  String mUserPicStr,mUserNameStr,mUserIdStr,mUserMailStr;
    boolean mUserLvBoolean;
    private static final String TAG = "UserActivity";
    private DisplayMetrics mPhone;
    private Button mUserLogoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        mActivty = this;
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

}
