package com.jackpan.libs.mfirebaselib;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by JackPan on 2017/10/21.
 */

public interface MfirebaeCallback {

    void getDatabaseData(Object o);

    void getDeleteState(boolean b, String s, Object o);

    void createUserState(boolean b);

    void useLognState(boolean b);

    void getuseLoginId(String s);

    void getuserLoginEmail(String s);

    void resetPassWordState(boolean b);

    void getFireBaseDBState(boolean b, String s);

    void getFirebaseStorageState(boolean b);

    void getFirebaseStorageType(String uri, String name);

    void getsSndPasswordResetEmailState(boolean b);

    void getUpdateUserName(boolean b);

    void getUserLogoutState(boolean b);

}
