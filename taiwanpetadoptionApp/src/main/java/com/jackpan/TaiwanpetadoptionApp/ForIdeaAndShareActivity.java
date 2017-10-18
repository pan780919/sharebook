package com.jackpan.TaiwanpetadoptionApp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackpan.Brokethenews.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jackpan.Brokethenews.R.menu.spinner;

public class ForIdeaAndShareActivity extends Activity implements View.OnClickListener, android.location.LocationListener {
    private EditText mNameEdt, mMailEdt, mTiitleEdt, mMessageEdt;
    private Button mSureBtn, mCancelBtn;
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    private final static int VIDEO = 33;
    private String picUri = "";
    private String picUri2 = "";
    private String picUri3 = "";
    private String picUri4 = "";
    private String picUri5 = "";
    private String videoUri = "";
    private String videoUri2 = "";
    private String videoUri3 = "";
    CharSequence s;
    private Bitmap bitmap;
    private static final int PICKER = 100;
    private static final String TAG = "ForIdeaAndShareActivity";
    ImageView imageView;
    private static final int REQUEST_EXTERNAL_STORAGE = 200;
    private ProgressDialog progressDialog;
    private TextView mPicName, mPicName2, mPicName3, mPicName4, mPicName5, mVideoName, mVideoName2,
            mVideoName3;
    private Button mPicBtn, mVideoBtn;
    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private Spinner mSpinner, mSpinner2;
    private EditText mAddEdt;
    private Button mAddBtn;
    private String mcatStr, mMoodStr;
    String[] cat = {"文學", "文財經企管", "生活風格", "飲食料理", "心理勵志", "醫療保健", "旅遊", "宗教命理","教育/親子教養","童書","漫畫"};
//    String[] mood = {"喜", "怒", "哀", "樂"};
    private int picInt = 0;
    private int videoInt = 0;
    String returnAddress = "";
    Double lat, lon;
    LocationManager locationMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_idea_and_share);
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
//        MyApi.checkGPS(ForIdeaAndShareActivity.this);
//        this.locationMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        initLayout();

        Calendar mCal = Calendar.getInstance();
        s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
        Log.d(TAG, "onCreate: " + s);
    }

    private void initLayout() {
        mPicName = (TextView) findViewById(R.id.picnametext);
        mPicName2 = (TextView) findViewById(R.id.picnametext2);
        mPicName3 = (TextView) findViewById(R.id.picnametext3);
//        mPicName4 = (TextView) findViewById(R.id.picnametext4);
//        mPicName5 = (TextView) findViewById(R.id.picnametext5);
//        mVideoName = (TextView) findViewById(R.id.videonametext);
//        mVideoName2 = (TextView) findViewById(R.id.videonametext2);
//        mVideoName3 = (TextView) findViewById(R.id.videonametext3);
        mNameEdt = (EditText) findViewById(R.id.nameedt);
        mMailEdt = (EditText) findViewById(R.id.phoneedt);
        mTiitleEdt = (EditText) findViewById(R.id.tittle);
        mMessageEdt = (EditText) findViewById(R.id.ideaedt);
        mAddEdt = (EditText) findViewById(R.id.addedt);
        mSureBtn = (Button) findViewById(R.id.btn_sure_send);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel_send);
        mSpinner = (Spinner) findViewById(R.id.spinner1);
//        mSpinner2 = (Spinner) findViewById(R.id.spinner2);
//        mSpinner2.setVisibility(View.GONE);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cat);
        //設定下拉選單的樣式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ForIdeaAndShareActivity.this, "您選擇" + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                mcatStr = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
//        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mood);
//        //設定下拉選單的樣式
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner2.setAdapter(adapter2);
//        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ForIdeaAndShareActivity.this, "您選擇" + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                mMoodStr = parent.getSelectedItem().toString();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        findViewById(R.id.btn_sure_send).setOnClickListener(this);
        findViewById(R.id.btn_cancel_send).setOnClickListener(this);
        findViewById(R.id.picbtn).setOnClickListener(this);
        findViewById(R.id.picbtn2).setOnClickListener(this);
        findViewById(R.id.picbtn3).setOnClickListener(this);
//        findViewById(R.id.picbtn4).setOnClickListener(this);
//        findViewById(R.id.picbtn5).setOnClickListener(this);
//        findViewById(R.id.videobtn).setOnClickListener(this);
//        findViewById(R.id.videobtn2).setOnClickListener(this);
//        findViewById(R.id.videobtn3).setOnClickListener(this);

        findViewById(R.id.addbtn).setOnClickListener(this);
        mAddBtn = (Button) findViewById(R.id.addbtn);
        mAddBtn.setVisibility(View.GONE);
        returnAddress = mAddEdt.getText().toString().trim();
//        MyApi.AddtoLatLon(ForIdeaAndShareActivity.this, returnAddress);
//        lat = MyApi.getLatitude();
//        lon = MyApi.getLongitude();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure_send:
                if (mTiitleEdt.getText().toString().trim().equals("")) {
                    Toast.makeText(ForIdeaAndShareActivity.this, "標題不能空白喔", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mMessageEdt.getText().toString().trim().equals("")) {
                    Toast.makeText(ForIdeaAndShareActivity.this, "內容不能空白喔", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mNameEdt.getText().toString().trim().equals("")) {
                    Toast.makeText(ForIdeaAndShareActivity.this, "名字不能空白喔", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (picUri.equals("")) {
                    Toast.makeText(ForIdeaAndShareActivity.this, "至少放一張相片吧！！", Toast.LENGTH_SHORT).show();
                    return;
                }

                setFireBaseDB(MySharedPrefernces.getUserId(this),
                        mTiitleEdt.getText().toString().trim(),
                        mMessageEdt.getText().toString().trim(),
                        mMailEdt.getText().toString().trim(),
                        mcatStr,
                        mMoodStr,
                        picUri,
                        picUri2,
                        picUri3,
                        mNameEdt.getText().toString().trim(),
                        String.valueOf(s),
                        mAddEdt.getText().toString().trim()

                );
                break;
            case R.id.btn_cancel_send:
                ForIdeaAndShareActivity.this.finish();
                break;
            case R.id.picbtn:
                picInt = 1;
                selectPic();
                break;
            case R.id.picbtn2:
                picInt = 2;
                selectPic();
                break;
            case R.id.picbtn3:
                picInt = 3;
                selectPic();
                break;
//            case R.id.picbtn4:
//                picInt = 4;
//                selectPic();
//                break;
//            case R.id.picbtn5:
//                picInt = 5;
//                selectPic();
//                break;
//
//            case R.id.videobtn:
//                videoInt = 1;
//                selectVideo();
//                break;
//
//            case R.id.videobtn2:
//                videoInt = 2;
//                selectVideo();
//                break;
//
//            case R.id.videobtn3:
//                videoInt = 3;
//                selectVideo();
//                break;
            case R.id.addbtn:

                if (mAddEdt.getText().toString().trim().equals("")) {
                    Toast.makeText(ForIdeaAndShareActivity.this, "帶入地址！！", Toast.LENGTH_SHORT).show();
                    mAddEdt.setText(returnAddress);
                }
                break;
        }


    }

    private void setFireBaseDB(String id, String tittle, String message, String phone,String cat, String mood, String uri, String uri2, String uri3
            ,  String name, String date,
                               String adds
    ) {


        String url = "https://bookshare-99cb3.firebaseio.com/sharebook";
        Firebase mFirebaseRef = new Firebase(url);
//		Firebase userRef = mFirebaseRef.child("user");
//		Map newUserData = new HashMap();
//		newUserData.put("age", 30);
//		newUserData.put("city", "Provo, UT");
        Firebase newPostRef = mFirebaseRef.child("posts").push();
//        String newPostKey = newPostRef.getKey();
//        Log.d(TAG, "setFireBaseDB: " + newPostKey);ㄐ
        String key = id + date;

        Map newPost = new HashMap();
        newPost.put("id", id);
        newPost.put("name", name);
        newPost.put("tittle", tittle);
        newPost.put("message", message);
        newPost.put("phone",phone);
        newPost.put("cat", cat);
//        newPost.put("mood", mood);
        newPost.put("pic", uri);
        newPost.put("pic2", uri2);
        newPost.put("pic3", uri3);
//        newPost.put("pic4", uri4);
//        newPost.put("pic5", uri5);
        newPost.put("adds", adds);
//        newPost.put("lat", lat);
//        newPost.put("lon", lon);
//        newPost.put("url", videoUri);
//        newPost.put("url2", videoUri2);
//        newPost.put("url3", videoUri3);


        newPost.put("date", date);
        newPost.put("like", 1);
        newPost.put("view", 1);
        newPost.put("tomsg", "");
        Map updatedUserData = new HashMap();
//		updatedUserData.put("3/posts/" + newPostKey, true);
        updatedUserData.put(key, newPost);
        mFirebaseRef.updateChildren(updatedUserData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(TAG, "onComplete: " + "Error updating data: " + firebaseError.getMessage());
                } else {
                    Log.d(TAG, "onComplete: " + "is true");
                    new AlertDialog.Builder(ForIdeaAndShareActivity.this)
                            .setTitle("訊息")
                            .setMessage("您已經發布文章成功,將跳回首頁！！")
                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ForIdeaAndShareActivity.this.finish();
                                    dialog.dismiss();


                                }
                            }).show();
                }
            }

        });
    }

    private void upLoad() {

//				sharePicWithUri(uri);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://sevenpeoplebook.appspot.com");
        StorageReference mountainsRef = storageRef.child("file.jpg");
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                 Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private void selectVideo() {
        int permission = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限

            if (ActivityCompat.shouldShowRequestPermissionRationale(ForIdeaAndShareActivity.this,
                    android.Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(ForIdeaAndShareActivity.this)
                        .setMessage("我真的沒有要做壞事, 給我權限吧?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ForIdeaAndShareActivity.this,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        REQUEST_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE

                );
            }

        } else {
            //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
            //為點選相片後返回程式呼叫onActivityResult
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, VIDEO);


        }
    }

    private void selectPic() {
        int permission = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限

            if (ActivityCompat.shouldShowRequestPermissionRationale(ForIdeaAndShareActivity.this,
                    android.Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(ForIdeaAndShareActivity.this)
                        .setMessage("我真的沒有要做壞事, 給我權限吧?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ForIdeaAndShareActivity.this,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        REQUEST_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE

                );
            }

        } else {
            //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
            //為點選相片後返回程式呼叫onActivityResult
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PHOTO);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKER) {
            if (resultCode == Activity.RESULT_OK) {


            }
        }
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
            //取得照片路徑uri
            Uri datauri = data.getData();

            uploadFromPic(datauri);


            super.onActivityResult(requestCode, resultCode, data);
        }
        if ((requestCode == CAMERA || requestCode == VIDEO) && data != null) {
            //取得照片路徑uri
            Uri datauri = data.getData();
            uploadFromFile(datauri);


            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadFromFile(Uri path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://bookshare-99cb3.appspot.com\n" +
                "file_upload 上傳檔案");
        Uri file = path;
        StorageReference imageRef = storageRef.child(file.getLastPathSegment());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .setCustomMetadata("country", "x")
                .build();
        UploadTask uploadTask = imageRef.putFile(file, metadata);

        progressDialog = new ProgressDialog(ForIdeaAndShareActivity.this);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                progressDialog.dismiss();
                Toast.makeText(ForIdeaAndShareActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                switch (videoInt) {
                    case 1:
                        mVideoName.setText(taskSnapshot.getMetadata().getName());
                        videoUri = taskSnapshot.getDownloadUrl().toString();
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();


                        break;
                    case 2:
                        mVideoName2.setText(taskSnapshot.getMetadata().getName());
                        videoUri2 = taskSnapshot.getDownloadUrl().toString();
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();


                        break;

                    case 3:
                        mVideoName3.setText(taskSnapshot.getMetadata().getName());
                        videoUri3 = taskSnapshot.getDownloadUrl().toString();
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();


                        break;

                }


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                progressDialog.setTitle("提示訊息");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("上傳中！！");
                progressDialog.show();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void uploadFromPic(Uri datauri) {
        final boolean after44 = Build.VERSION.SDK_INT >= 19;
        String filePath = "";

        if (after44) {
            String wholeID = DocumentsContract.getDocumentId(datauri);

// Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

// where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
                Log.d(TAG, "onActivityResult: " + filePath);

            }

            cursor.close();
        } else {

            try {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(datauri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                Log.d(TAG, "uploadFromPic:" + filePath);
                cursor.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }

//			sharePicWithUri(uri);
//			if (true) return;
//			String uri = data.getData()+"";
//			ContentResolver cr = this.getContentResolver();
//			String uri = "sdcard/req_images/temp.jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://bookshare-99cb3.appspot.com" +
                "");

        StorageReference mountainsRef = storageRef.child(filePath);


        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        UploadTask mUploadTask = mountainsRef.putStream(stream);
        progressDialog = new ProgressDialog(ForIdeaAndShareActivity.this);


        mUploadTask.addOnFailureListener(new OnFailureListener() {


            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: " + "onFailure: " + exception.getMessage()
                );
                progressDialog.dismiss();
                Toast.makeText(ForIdeaAndShareActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                switch (picInt) {
                    case 1:

                        picUri = taskSnapshot.getDownloadUrl().toString();
                        mPicName.setText(taskSnapshot.getMetadata().getName());
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:

                        picUri2 = taskSnapshot.getDownloadUrl().toString();
                        mPicName2.setText(taskSnapshot.getMetadata().getName());
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:

                        picUri3 = taskSnapshot.getDownloadUrl().toString();
                        mPicName3.setText(taskSnapshot.getMetadata().getName());
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:

                        picUri4 = taskSnapshot.getDownloadUrl().toString();
                        mPicName4.setText(taskSnapshot.getMetadata().getName());
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:

                        picUri5 = taskSnapshot.getDownloadUrl().toString();
                        mPicName5.setText(taskSnapshot.getMetadata().getName());
                        progressDialog.dismiss();
                        Toast.makeText(ForIdeaAndShareActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                        break;


                }


            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                Log.d(TAG, "onProgress: " + taskSnapshot.getBytesTransferred());
                Log.d(TAG, "onProgress: " + taskSnapshot.getTotalByteCount());

                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                Log.d(TAG, "onProgress: " + progress);
                Log.d(TAG, progress + "% done");


                progressDialog.setTitle("提示訊息");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("上傳中！！");
                progressDialog.show();
            }

        });
////			try {
////				//讀取照片，型態為Bitmap
////				bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
////				//判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
////				if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap,
////						mPhone.heightPixels);
////				else ScalePic(bitmap, mPhone.widthPixels);
////			} catch (FileNotFoundException e) {
////			}

    }


    /**
     * 查詢MediaStroe Uri對應的絕對路徑。
     *
     * @param context 傳入Context
     * @param uri     傳入MediaStore Uri
     * @return 傳回絕對路徑
     */
    public static String queryAbsolutePath(final Context context, final Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

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
            Geocoder gc = new Geocoder(ForIdeaAndShareActivity.this, Locale.TRADITIONAL_CHINESE);
            List<Address> lstAddress = null;
            lstAddress = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            returnAddress = lstAddress.get(0).getAddressLine(0);
            Log.d(TAG, "onLocationChanged: " + returnAddress);
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
        mAddBtn.setVisibility(View.VISIBLE);
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
//        String provider = this.locationMgr.getBestProvider(new Criteria(), true);
//        if (provider == null) {
//
//            return;
//        }
//        if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, this);
//        }
//        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//
//            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);
//
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e("jack", "removeUpdates...");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        this.locationMgr.removeUpdates(this);
    }

}
