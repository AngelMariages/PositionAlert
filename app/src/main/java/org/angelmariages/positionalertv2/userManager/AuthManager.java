package org.angelmariages.positionalertv2.userManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.angelmariages.positionalertv2.GApiClient;
import org.angelmariages.positionalertv2.MainActivity;
import org.angelmariages.positionalertv2.U;

public class AuthManager implements MainActivity.OnActivityResultListener, FirebaseAuth.AuthStateListener {
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private final FirebaseAuth mFirebaseAuth;
    private boolean mLoggedIn;
    private String mUserUID;
    private OnSuccesfulLoginListener mLoginListener;
    private LogoutListener mLogoutListener;

    public AuthManager(Activity activity) {
        mActivity = activity;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(this);
        mGoogleApiClient = GApiClient.getInstance(mActivity.getApplicationContext());
    }

    public void signIn() {
        Intent signIn = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signIn, 1);
    }

    public void signOut() {
        mFirebaseAuth.signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(signInResult.isSuccess()) {
                GoogleSignInAccount account = signInResult.getSignInAccount();
                signInFirebaseWithGoogle(account);
            } else {
                //failed
            }
        }
    }

    private void signInFirebaseWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                             if(!task.isSuccessful()) {
                                 mLoggedIn = false;
                                 //failed
                             }
                    }
                });

    }

    public void removeListener() {
        if(mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(this);
        }
    }

    public String getUserUID() {
        if(mLoggedIn) {
            return mUserUID;
        } else {
            //wait
        }
        return null;
    }

    public void setSuccesfulLoginListener(OnSuccesfulLoginListener succesfulLoginListener) {
        mLoginListener = succesfulLoginListener;
    }

    public void setOnLogoutListener(LogoutListener logoutListener) {
        mLogoutListener = logoutListener;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mLoggedIn = true;
            mUserUID = user.getUid();
            for (UserInfo userInfo : user.getProviderData()) {
                U.sendLog("INFO: " + userInfo.toString());
            }
            if(mLoginListener != null) {
                mLoginListener.succesfulLogin(user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
            }
            U.sendLog("Signed in");
        } else {
            mLoggedIn = false;
            if(mLogoutListener != null) {
                mLogoutListener.onLogout();
            }
            U.sendLog("Signed out");
        }
    }

    public interface OnSuccesfulLoginListener {
        void succesfulLogin(String username, String email, Uri image);
    }

    public interface LogoutListener {
        void onLogout();
    }
}
