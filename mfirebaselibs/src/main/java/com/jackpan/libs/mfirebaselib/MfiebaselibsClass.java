package com.jackpan.libs.mfirebaselib;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by JackPan on 2017/10/21.
 */

public class MfiebaselibsClass {
    private  String DELETESUCCESS = "成功刪除資料";
    private  String DELETEFAIL = "刪除資料失敗";
    private Context mContext;
    private  MfirebaeCallback callback;
    public MfiebaselibsClass(Context context,MfirebaeCallback mfirebaeCallback){
        this.mContext = context;
        this.callback = mfirebaeCallback;

    }

    public void getFirebaseDatabase(String url, String orderByChildStr){
        Firebase.setAndroidContext(mContext);
//        String url = "https://bookshare-99cb3.firebaseio.com/sharebook";

        Firebase mFirebaseRef = new Firebase(url);
        //.orderByChild("cat").equalTo("醫療保健")
//		if(Firebase.getDefaultConfig().isPersistenceEnabled()==false)mFirebaseRef.getDefaultConfig().setPersistenceEnabled(true);
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
    public  void deleteData(String url , String pathString ){
        Firebase myFirebaseRef = new Firebase(url);
        final Firebase userRef = myFirebaseRef.child(pathString);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    userRef.removeValue();
                    callback.getDeleteState(true,DELETESUCCESS);
                }else {
                    callback.getDeleteState(false,DELETEFAIL);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                callback.getDeleteState(false,firebaseError.getMessage());
            }
        });
    }

}
