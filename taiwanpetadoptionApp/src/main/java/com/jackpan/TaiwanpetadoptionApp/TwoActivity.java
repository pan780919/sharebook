package com.jackpan.TaiwanpetadoptionApp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ads.AdSize;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.gson.Gson;
import com.jackpan.VideoViewActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import Appkey.MyAdKey;
import com.facebook.ads.*;

//import com.adlocus.AdLocusLayout$ErrorCode;
//import com.google.analytics.tracking.android.EasyTracker;
import com.jackpan.Brokethenews.R;
public class TwoActivity extends Activity implements android.location.LocationListener {
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

		textview = (TextView) findViewById(R.id.textView1);
		textview2 = (TextView) findViewById(R.id.textView2);
		textview3 = (TextView) findViewById(R.id.textView3);
		textview4 = (TextView) findViewById(R.id.textView4);
		textview5 = (TextView) findViewById(R.id.textView5);
		textview6 = (TextView) findViewById(R.id.textView6);
		textview4.setVisibility(View.GONE);
		textview5.setVisibility(View.GONE);
		textview6.setVisibility(View.GONE);
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
		img5= (ImageView) findViewById(R.id.pageimg5);

		textview20= (TextView) findViewById(R.id.textView20);
		textview21= (TextView) findViewById(R.id.textView21);
		img = (ImageView) findViewById(R.id.pageimg);

		mUserLikeBtn = (Button) findViewById(R.id.b_button_like);
		mUserText = (TextView) findViewById(R.id.liketext);
		toUserMsg = (Button) findViewById(R.id.b_button_message);

	}

	private static final String TAG = "TwoActivity";
	private void loadIntent() {
		String json = getIntent().getStringExtra("json");
		final GayPlace data = new Gson().fromJson(json, GayPlace.class);
//		for (GayPlace.ForMsg forMsg : data.formsg) {
//			Log.d(TAG, "loadIntent: "+forMsg.id);
//			Log.d(TAG, "loadIntent: "+forMsg.tomsg);
//		}
		mShareBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ShareDialog.canShow(ShareLinkContent.class)) {
					ShareLinkContent linkContent = new ShareLinkContent.Builder()
							.setContentTitle("我要推薦好文章")
							.setContentDescription(data.getMessage())
							.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.jackpan.Brokethenews"))
							.setImageUrl(Uri.parse(data.getPic()))
							.build();
					shareDialog.show(linkContent);
				}
				MyApi.copyToClipboard(getApplication(),data.getMessage());
			}
		});
		endlat =data.lat;
		endlon =data.lon;
		likeid = data.getId()+data.getDate();
		loadImage(data.getPic(), img);
		textview.setText("標題:"+data.getTittle());
		textview2.setText("內容:"+data.getMessage());
		textview3.setText("作者:"+data.getName());
//		if(!data.getUrl().equals(""))textview4.setText("點擊觀看影片");
//		else textview4.setText("無影片可觀看");
//		 textview4.setOnClickListener(new View.OnClickListener() {
//			 @Override
//			 public void onClick(View v) {
//				 if(data.getUrl().equals("")) return;
//				 Intent i = new Intent(TwoActivity.this, VideoViewActivity.class);
//				 Bundle bundle = new Bundle();
//				 bundle.putString("video",data.getUrl());
//				 i.putExtras(bundle);
//				 startActivity(i);
//			 }
//		 });
//		if(!data.url2.equals(""))textview5.setText("點擊觀看影片");
//		else textview5.setText("無影片可觀看");
//		textview5.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(data.url2.equals("")) return;
//				Intent i = new Intent(TwoActivity.this, VideoViewActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("video",data.url2);
//				i.putExtras(bundle);
//				startActivity(i);
//			}
//		});
//		if(!data.url3.equals(""))textview6.setText("點擊觀看影片");
//		else textview6.setText("無影片可觀看");
//		textview6.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(data.url3.equals("")) return;
//				Intent i = new Intent(TwoActivity.this, VideoViewActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("video",data.url3);
//				i.putExtras(bundle);
//				startActivity(i);
//			}
//		});


		mUserLikeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String Time =MySharedPrefernces.getMyCardTime(TwoActivity.this);

				if (Time.equals("")) Time = "0";
				if (System.currentTimeMillis() - Long.valueOf(Time) > 24*60 * 60 * 1000) {
					MySharedPrefernces.saveMyCardTime(TwoActivity.this, System.currentTimeMillis() + "");
					Like();
					Toast.makeText(TwoActivity.this,"已經對他按讚囉！！",Toast.LENGTH_SHORT).show();

				}else{
					Toast.makeText(TwoActivity.this,"你今天已經按過讚囉！明天再按吧",Toast.LENGTH_SHORT).show();
				}

			}
		});
		toUserMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data.tomsg ==null) return;
				Intent i = new Intent(TwoActivity.this, UserMessgeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id",likeid);
				bundle.putString("name",data.getName());
				bundle.putString("msg",data.tomsg);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		if(data.getView()!=0)userView();
		mUserText.setText(data.getLike()+"人");
		textview7.setText("此刻心情:"+data.mood);
		textview8.setText("發生地點:"+data.adds);
//		mBtnGPS.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(mBtnGPS.getText().toString().trim().equals("位置讀取中！！"))return;
//				double startLatitude = lat;
//				double startLongitude = lon;
//
//				double endLatitude = data.lat;
//				double endLongitude = data.lon;
//
//				String saddr = "saddr=" + startLatitude + "," + startLongitude;
//				String daddr = "daddr=" + endLatitude + "," + endLongitude;
//				String uriString = "http://maps.google.com/maps?" + saddr + "&" + daddr;
//
//				Uri uri = Uri.parse(uriString);
//
//				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
//
//				startActivity(intent);
//			}
//		});
//		textview8.setText("分類學_綱:"+data.A_Class);
//		textview9.setText("分類學_目:"+data.A_Order);


//		textview11.setText("保育等級:"+data.A_Conservation);
//		textview12.setText("地理分布:"+data.A_Distribution);
//		textview13.setText("棲地型態:"+data.A_Habitat);
//		textview14.setText("解說:"+data.A_Interpretation);
//		textview15.setText("棲地型態:"+data.A_Habitat );
//		textview17.setText("形態特徵:"+data.A_Feature );
//		textview19.setText("生態習性:"+data.A_Behavior);
//		textview21.setText("食性:"+data.A_Diet);
		loadImage(data.pic2, img2);
//		textview16.setText(data.A_Pic02_ALT);
		loadImage(data.pic3, img3);
//		textview18.setText(data.A_Pic03_ALT);
		loadImage(data.pic4, img4);
		loadImage(data.pic5, img4);
//		textview20.setText(data.A_Pic04_ALT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			Intent i = new Intent();
//			i.setClass(TwoActivity.this,InAppBillingActivity.class);
//			startActivity(i);
//
//			return true;
//		}

		if(id == R.id.action_user){
			startActivity(new Intent(TwoActivity.this,UserActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
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

//			if(!isBuy)interstitial.show();
			interstitialAd.show();

			return true;  

		}  

		return super.onKeyDown(keyCode, event);  

	}
	WebViewClient mWebViewClient = new WebViewClient() {
		  @Override
		  public boolean shouldOverrideUrlLoading(WebView view, String url) {
		   view.loadUrl(url);
		   return true;
		  }
		 };

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	private  void setAd(){
		RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
		adView = new com.facebook.ads.AdView(this, "583698071813390_587400221443175", AdSize.RECTANGLE_HEIGHT_250);
		adViewContainer.addView(adView);
		adView.loadAd();

		RelativeLayout adViewContainer2 = (RelativeLayout) findViewById(R.id.adViewContainer2);
		adView2 = new com.facebook.ads.AdView(this, "583698071813390_587400221443175", AdSize.RECTANGLE_HEIGHT_250);
		adViewContainer2.addView(adView2);
		adView2.loadAd();

		RelativeLayout adViewContainer3 = (RelativeLayout) findViewById(R.id.adViewContainer3);
		adView3 = new com.facebook.ads.AdView(this, "583698071813390_587400221443175", AdSize.RECTANGLE_HEIGHT_250);
		adViewContainer3.addView(adView3);
		adView3.loadAd();

	}
	private void loadInterstitialAd() {
		interstitialAd = new com.facebook.ads.InterstitialAd(this, "583698071813390_680915735424956");
		interstitialAd.setAdListener(new InterstitialAdListener() {
			@Override
			public void onInterstitialDisplayed(Ad ad) {

			}

			@Override
			public void onInterstitialDismissed(Ad ad) {

			}

			@Override
			public void onError(Ad ad, AdError adError) {

			}

			@Override
			public void onAdLoaded(Ad ad) {
				interstitialAd.show();

			}

			@Override
			public void onAdClicked(Ad ad) {

			}

			@Override
			public void onLoggingImpression(Ad ad) {

			}
		});
		interstitialAd.loadAd();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.e("Jack", "onLocationChanged...");
		if (location == null) return;

		String msg = "經度: " + location.getLongitude() + ", 緯度: "
				+ location.getLatitude();
		Log.e("Jack", msg);
		lat = location.getLatitude();
		lon = location.getLongitude();
		try {
			Geocoder gc = new Geocoder(TwoActivity.this, Locale.TRADITIONAL_CHINESE);
			List<Address> lstAddress = null;
			lstAddress = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


		} catch (IOException e) {
			e.printStackTrace();
		}


		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}


		if (lat!=null&&lon!=null){
			float distance = 0;
			Location crntLocation = new Location("");
			crntLocation.setLatitude(lat);
			crntLocation.setLongitude(lon);

			Location newLocation = new Location("");
			newLocation.setLatitude(endlat);
			newLocation.setLongitude(endlon);
			distance = crntLocation.distanceTo(newLocation); // in mdistance = distance / 1000;//km

			String km =  new DecimalFormat("0.0").format(distance/1000);

			textview10.setText("距離" +km+"公里");

		}
		mBtnGPS.setText("導航功能準備完成!!");
		this.locationMgr.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
	@Override
	protected void onResume() {
		super.onResume();
		//  mAdapter.updateData(mAllData);
		// 取得位置提供者，不下條件，讓系統決定最適用者，true 表示生效的 provider
		String provider = this.locationMgr.getBestProvider(new Criteria(), true);
		if (provider == null) {

			return;
		}
		if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, this);
		}
		if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}

			locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("jack", "removeUpdates...");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		this.locationMgr.removeUpdates(this);
	}
}
