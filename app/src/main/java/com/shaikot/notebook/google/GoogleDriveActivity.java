package com.shaikot.notebook.google;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.List;

public abstract class GoogleDriveActivity extends GoogleSignInActivity {

    private static final int GOOGLE_SIGN_IN_REQUEST = 1010;

    public void startGoogleDriveSignIn() {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST);


    }

    protected abstract void onGoogleDriveSignedInSuccess(final Drive driveApi);

    protected abstract void onGoogleDriveSignedInFailed(final ApiException exception);

    protected static GoogleSignInOptions getGoogleSignInOptions() {
        Scope scopeDriveAppFolder = new Scope(Scopes.DRIVE_APPFOLDER);
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(scopeDriveAppFolder)
                .build();
    }

    @Override
    protected void onGoogleSignedInSuccess(final GoogleSignInAccount signInAccount) {
        initializeDriveClient(signInAccount);
    }

    @Override
    protected void onGoogleSignedInFailed(final ApiException exception) {
        onGoogleDriveSignedInFailed(exception);
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        List<String> scopes = new ArrayList<>();
        scopes.add(DriveScopes.DRIVE_APPDATA);

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, scopes);
        credential.setSelectedAccount(signInAccount.getAccount());
        Drive.Builder builder = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential
        );
        String appName = "Notebook";
        Drive driveApi = builder
                .setApplicationName(appName)
                .build();
        onGoogleDriveSignedInSuccess(driveApi);
    }
}
