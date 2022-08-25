package com.shaikot.notebook.google;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public abstract class GoogleSignInActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN_REQUEST = 1010;

    protected static GoogleSignInOptions getGoogleSignInOptions() {
        return null;
    }

    protected abstract void onGoogleSignedInSuccess(final GoogleSignInAccount signInAccount);

    protected abstract void onGoogleSignedInFailed(final ApiException exception);

    public void startGoogleSignIn() {
        assert getGoogleSignInOptions() != null;
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            onGoogleSignedInSuccess(account);
        } catch (ApiException e) {
            onGoogleSignedInFailed(e);
        }
    }
}
