package com.jackpan.TaiwanpetadoptionApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by HYXEN20141227 on 2016/6/24.
 */
public class MyApi {
    private  static  Double latitude,longitude;
    public  static void AddtoLatLon(Context context,String store_add){



        try {
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressLocation = geoCoder.getFromLocationName(store_add, 1);
            if(addressLocation.size()<=0){}else {
             latitude = addressLocation.get(0).getLatitude();
             longitude = addressLocation.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  Double getLatitude() {
        return latitude;
    }

    public static  Double getLongitude() {
        return longitude;
    }

    public static long getTime(String dateTime){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = sdf.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void copyToClipboard(Context context,String str){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(str);
            Log.e("version", "1 version");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label",str);
            clipboard.setPrimaryClip(clip);
            Log.e("version","2 version");
        }
        Toast.makeText(context, "複製內容成功!!可以貼上並分享訊息囉", Toast.LENGTH_SHORT).show();
    }
    public static  void loadImage(final String path,
                                  final ImageView imageView, final Activity activity){

        new Thread(){

            @Override
            public void run() {

                try {
                    URL imageUrl = new URL(path);
                    HttpURLConnection httpCon =
                            (HttpURLConnection) imageUrl.openConnection();
                    InputStream imageStr =  httpCon.getInputStream();
                    final Bitmap bitmap =  BitmapFactory.decodeStream(imageStr);

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            imageView.setImageBitmap(bitmap);
                        }
                    });


                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    Log.e("Howard", "MalformedURLException:" + e);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("Howard", "IOException:"+e);
                }



            }


        }.start();

    }
    public static  void  checkGPS(Context context){
        //取得系統定位服務
        LocationManager status = (LocationManager) (context.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

        {
            Log.e("Jack","有開啟定位服務");
        } else {
            Log.e("Jack","請開啟定位服務");
            Toast.makeText(context, "請開啟定位服務,才能使用定位喔", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
        }


    }
}
