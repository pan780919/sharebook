package com.jackpan.libs.mfirebaselib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by JackPan on 2017/10/21.
 */

public class MfiebaselibsClass {
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    private String userUID;
    private FirebaseUser userpassword;
    private  String DELETESUCCESS = "成功刪除資料";
    private  String DELETEFAIL = "刪除資料失敗";
    private Context mContext;
    private  MfirebaeCallback callback;
    public MfiebaselibsClass(Context context,MfirebaeCallback mfirebaeCallback){
        this.mContext = context;
        this.callback = mfirebaeCallback;
        auth = FirebaseAuth.getInstance();

    }
    public  void userLogin(final  String email ,final  String password){

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    callback.useLognState(false);

                }else {
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
     public void userLoginCheck(){

         authListener = new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(
                     @NonNull FirebaseAuth firebaseAuth) {
                 FirebaseUser user = firebaseAuth.getCurrentUser();
                 if (user != null) {
                     userUID = user.getUid();
                 } else {

                 }
             }
         };

     }

    public void getFirebaseDatabase(String url, String orderByChildStr){
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
    public void searchFirebaseDatabase(String url, String orderByChildStr,String query){
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
    public   void resetPassWord(String oldpassword, final  String newpassword){
        userpassword = FirebaseAuth.getInstance().getCurrentUser();
        final String email = userpassword.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldpassword);

        userpassword.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userpassword.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                callback.resetPassWordState(false);

                            }else {
                                callback.resetPassWordState(true);
                            }
                        }
                    });
                }else {
                    callback.resetPassWordState(false);
                }
            }
        });
    }

    public  void setAuthListener(){
        if(authListener!=null){
            auth.addAuthStateListener(authListener);
        }


    }
    public  void removeAuthListener(){
        if(authListener!=null){
            auth.removeAuthStateListener(authListener);
        }



    }
}
