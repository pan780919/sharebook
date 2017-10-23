package com.jackpan.libs.mfirebaselib;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JackPan on 2017/10/21.
 */

public class MfiebaselibsClass {
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    private String userUID;
    private FirebaseUser userpassword;
    private String DELETESUCCESS = "成功刪除資料";
    private String DELETEFAIL = "刪除資料失敗";
    private String SETDBSUCCESS = "成功寫入資料";
    private Context mContext;
    private MfirebaeCallback callback;
    private ProgressDialog progressDialog;

    public MfiebaselibsClass(Context context, MfirebaeCallback mfirebaeCallback) {
        this.mContext = context;
        this.callback = mfirebaeCallback;
        auth = FirebaseAuth.getInstance();

    }

    public void userLogin(final String email, final String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    callback.useLognState(false);

                } else {
                    callback.useLognState(true);

                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

            }
        });


    }

    public void createUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                boolean message =
                                        task.isSuccessful() ? true : false;
                                // task.isComplete() ? "註冊成功" : "註冊失敗"; (感謝jiaping網友提醒)
                                callback.createUserState(message);
                            }
                        });
    }

    public void userLoginCheck() {

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(
                    @NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userUID = user.getUid();
                    callback.getuseLoginId(userUID);
                } else {
                    callback.getuseLoginId("");
                }
            }
        };

    }

    public void setFireBaseDB(String url, String key, HashMap<String, String> newPost) {
        Firebase mFirebaseRef = new Firebase(url);
        Firebase newPostRef = mFirebaseRef.child("posts").push();
        Map updatedUserData = new HashMap();
        updatedUserData.put(key, newPost);
        mFirebaseRef.updateChildren(updatedUserData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    callback.getFireBaseDBState(false, firebaseError.getMessage());
                } else {
                    callback.getFireBaseDBState(true, SETDBSUCCESS);
                }
            }
        });
    }

    public void getFirebaseDatabase(String url, String orderByChildStr) {
        Firebase.setAndroidContext(mContext);
        Firebase mFirebaseRef = new Firebase(url);
        mFirebaseRef.orderByChild(orderByChildStr).addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                callback.getDatabaseData(dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }

    public void searchFirebaseDatabase(String url, String orderByChildStr, String query) {
        Firebase.setAndroidContext(mContext);
        Firebase mFirebaseRef = new Firebase(url);
        mFirebaseRef.orderByChild(orderByChildStr).equalTo(query).addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                callback.getDatabaseData(dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }
    /**
     * url db路徑
     * pathString 結點位置
     * item  要更新的欄位
     * **/
    public void upLoadDB(String url ,String pathString ,String item,final  Object value){

        Firebase mFirebaseRef = new Firebase(url);

        Firebase countRef = mFirebaseRef.child(pathString).child(item);
        countRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData currentData) {

                if(currentData.getValue() == null) {
                    currentData.setValue(value);
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
    /**
    * url 路徑
     * pathString 節點的id
    * */
    public void deleteData(String url, String pathString) {
        Firebase myFirebaseRef = new Firebase(url);
        final Firebase userRef = myFirebaseRef.child(pathString);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userRef.removeValue();
                    callback.getDeleteState(true, DELETESUCCESS);
                } else {
                    callback.getDeleteState(false, DELETEFAIL);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                callback.getDeleteState(false, firebaseError.getMessage());
            }
        });
    }

    public void userDeleteData(String url, String pathString) {

        Firebase myFirebaseRef = new Firebase(url);
        final Firebase userRef = myFirebaseRef.child(pathString);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userRef.removeValue();
                    callback.getDeleteState(true, DELETESUCCESS);
                } else {
                    callback.getDeleteState(false, DELETEFAIL);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                callback.getDeleteState(false, firebaseError.getMessage());
            }
        });
    }
    public void resetPassWord(String oldpassword, final String newpassword) {
        userpassword = FirebaseAuth.getInstance().getCurrentUser();
        final String email = userpassword.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpassword);

        userpassword.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userpassword.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                callback.resetPassWordState(false);

                            } else {
                                callback.resetPassWordState(true);
                            }
                        }
                    });
                } else {
                    callback.resetPassWordState(false);
                }
            }
        });
    }
    public  void sendPasswordResetEmail(String emailAddress){
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.getsSndPasswordResetEmailState(true);
                        }else {
                            callback.getsSndPasswordResetEmailState(false);

                        }
                    }

                });

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    /**
     * datauri  照片路徑uri
     * url   firebase url
     **/
    public void setFirebaseStorageForPhoto(Uri datauri,String url) {
        final boolean after44 = Build.VERSION.SDK_INT >= 19;
        String filePath = "";
        if (after44) {
            String wholeID = DocumentsContract.getDocumentId(datauri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = mContext.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        } else {

            try {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(datauri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url + "");

        StorageReference mountainsRef = storageRef.child(filePath);


        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        UploadTask mUploadTask = mountainsRef.putStream(stream);
        progressDialog = new ProgressDialog(mContext);


        mUploadTask.addOnFailureListener(new OnFailureListener() {


            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.getFirebaseStorageState(false);
                progressDialog.dismiss();
                Toast.makeText(mContext, "上傳失敗", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.getFirebaseStorageState(true);
                callback.getFirebaseStorageType(taskSnapshot.getDownloadUrl().toString(), taskSnapshot.getMetadata().getName());
                progressDialog.dismiss();
                Toast.makeText(mContext, "上傳成功", Toast.LENGTH_SHORT).show();

            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                progressDialog.setTitle("提示訊息");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("上傳中！！");
                progressDialog.show();
            }

        });
    }

    private void setFirebaseStorageForFile(String url, Uri path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url + "file_upload 上傳檔案");
        Uri file = path;
        StorageReference imageRef = storageRef.child(file.getLastPathSegment());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .setCustomMetadata("country", "x")
                .build();
        UploadTask uploadTask = imageRef.putFile(file, metadata);

        progressDialog = new ProgressDialog(mContext);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.getFirebaseStorageState(false);
                progressDialog.dismiss();
                Toast.makeText(mContext, "上傳失敗", Toast.LENGTH_SHORT).show();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.getFirebaseStorageState(true);
                callback.getFirebaseStorageType(taskSnapshot.getDownloadUrl().toString(), taskSnapshot.getMetadata().getName());
                Toast.makeText(mContext, "上傳成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

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

    public void setAuthListener() {
        if (authListener != null) {
            auth.addAuthStateListener(authListener);
        }


    }

    public void removeAuthListener() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

    }
}
