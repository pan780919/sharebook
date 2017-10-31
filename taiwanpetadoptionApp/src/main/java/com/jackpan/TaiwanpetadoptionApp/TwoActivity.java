package com.jackpan.TaiwanpetadoptionApp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Appkey.MyAdKey;

import com.jackpan.Brokethenews.R;
public class TwoActivity extends Activity {
	private TextView textview,textview2,textview3,textview4,
	textview5,textview6,textview7,textview8,textview9,textview10,textview11,
	textview12,textview13,textview14,textview15,textview16,textview17,textview18,textview19,textview20,textview21;
	private ImageView img,img2,img3,img4,img5;
	private Button btn;
	private boolean isCencel = false;
	private ProgressDialog progressDialog;
	private String name;
	private Bitmap bitmap=null;
	private InterstitialAd interstitial;
	private 	AdView mAdView;
	private  Button mShareBtn;
	CallbackManager callbackManager;
	ShareDialog shareDialog;
	private com.facebook.ads.AdView adView,adView2,adView3;
	String likeid;
	private  Button mUserLikeBtn;
	private  TextView mUserText;
	private  Button toUserMsg,mBtnGPS;
	private ImageButton callphone;

	private com.facebook.ads.InterstitialAd interstitialAd;

	Double lat, lon;
	Double endlat,endlon;
	LocationManager locationMgr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 //開啟全螢幕
//		        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,	
//		                             WindowManager.LayoutParams.FLAG_FULLSCREEN);	
//		        //設定隱藏APP標題	
//		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_two);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		MyApi.checkGPS(TwoActivity.this);
//		this.locationMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//		mAdView = (AdView) findViewById(R.id.adView);
//		setAd();
//		checkBuyAd();
		initLayout();
		loadIntent();
//		loadInterstitialAd();
//		Like();
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		shareDialog = new ShareDialog(this);
		// this part is optional
		shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
			@Override
			public void onSuccess(Sharer.Result result) {

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {

			}
		});


	}
	private void checkBuyAd(){

		boolean isBuy = MySharedPrefernces.getIsBuyed(TwoActivity.this);
		if(!isBuy){
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(MyAdKey.AdmobinterstitialBannerId);
			interstitial.loadAd(adRequest);
		}
		else {
			mAdView.setVisibility(View.GONE);
		}

	}
	private  void initLayout(){

//		MyGAManager.sendScreenName(this,"資料頁面");
		mShareBtn = (Button) findViewById(R.id.b_button_share);
		mShareBtn.setVisibility(View.GONE);
		textview = (TextView) findViewById(R.id.textView1);
		textview2 = (TextView) findViewById(R.id.textView2);
		textview3 = (TextView) findViewById(R.id.textView3);
		textview4 = (TextView) findViewById(R.id.textView4);
		textview5 = (TextView) findViewById(R.id.textView5);
		textview6 = (TextView) findViewById(R.id.textView6);
		textview7 = (TextView) findViewById(R.id.textView7);
		textview8= (TextView) findViewById(R.id.textView8);
		mBtnGPS= (Button) findViewById(R.id.buttongps);
		mBtnGPS.setText("位置讀取中！！");
		mBtnGPS.setVisibility(View.GONE);
		textview10= (TextView) findViewById(R.id.textView10);
		textview11= (TextView) findViewById(R.id.textView11);
		textview12= (TextView) findViewById(R.id.textView12);
		textview13= (TextView) findViewById(R.id.textView13);
		textview14= (TextView) findViewById(R.id.textView14);
		textview15= (TextView) findViewById(R.id.textView15);
		textview17= (TextView) findViewById(R.id.textView17);
		textview19= (TextView) findViewById(R.id.textView19);
		img2= (ImageView) findViewById(R.id.pageimg2);
		textview16= (TextView) findViewById(R.id.textView16);
		img3= (ImageView) findViewById(R.id.pageimg3);
		textview18= (TextView) findViewById(R.id.textView18);
		img4= (ImageView) findViewById(R.id.pageimg4);
		img4.setVisibility(View.GONE);
		img5= (ImageView) findViewById(R.id.pageimg5);
		img5.setVisibility(View.GONE);

		textview20= (TextView) findViewById(R.id.textView20);
		textview21= (TextView) findViewById(R.id.textView21);
		img = (ImageView) findViewById(R.id.pageimg);

		mUserLikeBtn = (Button) findViewById(R.id.b_button_like);
		mUserLikeBtn.setVisibility(View.GONE);
		mUserText = (TextView) findViewById(R.id.liketext);
		toUserMsg = (Button) findViewById(R.id.b_button_message);
		toUserMsg.setVisibility(View.GONE);
		callphone =(ImageButton) findViewById(R.id.callphone);


	}

	private static final String TAG = "TwoActivity";
	private void loadIntent() {
		String json = getIntent().getStringExtra("json");
		final FirebaseData data = new Gson().fromJson(json, FirebaseData.class);

		likeid = data.getId()+data.getDate();
		loadImage(data.getPic(), img);
		textview.setText("書本名稱:"+data.getTittle());
		textview2.setText("書本大綱:"+data.getMessage());
		textview3.setText("賣家:"+data.getName());
		textview4.setText("聯絡電話:"+data.phone);
		textview5.setText("價錢:"+data.price);
		textview6.setText("ISBN:"+data.isbn);
		callphone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data.phone)));
			}
		});
//		if(data.getView()!=0)userView();
		mUserText.setText(data.getLike()+"人");
		mUserText.setVisibility(View.GONE);
		textview8.setText("書籍所在地:"+data.adds);

		loadImage(data.pic2, img2);
		loadImage(data.pic3, img3);

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
	private void Like(){

		String url = "https://sevenpeoplebook.firebaseio.com/Broke";
		Firebase mFirebaseRef = new Firebase(url);

		Firebase countRef = mFirebaseRef.child(likeid).child("like");
		countRef.runTransaction(new Transaction.Handler() {

			@Override
			public Transaction.Result doTransaction(MutableData currentData) {

				if(currentData.getValue() == null) {
					currentData.setValue(0);

				} else {
					currentData.setValue((Long) currentData.getValue() + 1);

				}
				return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
			}

			@Override
			public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
				//This method will be called once with the results of the transaction.
			}
		});

	}
	private void userView(){

		String url = "https://sevenpeoplebook.firebaseio.com/Broke";
		Firebase mFirebaseRef = new Firebase(url);

		Firebase countRef = mFirebaseRef.child(likeid).child("view");
		countRef.runTransaction(new Transaction.Handler() {

			@Override
			public Transaction.Result doTransaction(MutableData currentData) {

				if(currentData.getValue() == null) {
					currentData.setValue(1);
				} else {
					currentData.setValue((Long) currentData.getValue() + 1);
				}
				return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
			}

			@Override
			public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
				//This method will be called once with the results of the transaction.
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (interstitialAd != null) {
			interstitialAd.destroy();
		}


	}
	private void loadImage(final String path,
			final ImageView imageView){


		imageView.setImageResource(R.drawable.nophoto);

		new Thread(){

			@Override
			public void run() {

				try {
					URL imageUrl = new URL(path);
					HttpURLConnection httpCon = 
							(HttpURLConnection) imageUrl.openConnection();
					InputStream imageStr =  httpCon.getInputStream();
					bitmap = decodeSampledBitmapFromResourceMemOpt(imageStr, 640, 640);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(bitmap);

						}
					});


				} catch (Exception e) {
					e.printStackTrace();

				}



			}


		}.start();

	}
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public Bitmap decodeSampledBitmapFromResourceMemOpt(
			InputStream inputStream, int reqWidth, int reqHeight) {

		byte[] byteArr = new byte[0];
		byte[] buffer = new byte[1024];
		int len;
		int count = 0;

		try {
			while ((len = inputStream.read(buffer)) > -1) {
				if (len != 0) {
					if (count + len > byteArr.length) {
						byte[] newbuf = new byte[(count + len) * 2];
						System.arraycopy(byteArr, 0, newbuf, 0, count);
						byteArr = newbuf;
					}

					System.arraycopy(buffer, 0, byteArr, count, len);
					count += len;
				}
			}

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(byteArr, 0, count, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			//            int[] pids = { android.os.Process.myPid() };
			//            MemoryInfo myMemInfo = mAM.getProcessMemoryInfo(pids)[0];
			//            Log.e(TAG, "dalvikPss (decoding) = " + myMemInfo.dalvikPss);

			return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵
			finish();
			boolean isBuy = MySharedPrefernces.getIsBuyed(TwoActivity.this);


			return true;  

		}  

		return super.onKeyDown(keyCode, event);  

	}
}
