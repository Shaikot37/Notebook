package com.shaikot.notebook.google

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

abstract class GoogleDriveActivity : GoogleSignInActivity() {
    fun startGoogleDriveSignIn() {
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST)
    }

    protected abstract fun onGoogleDriveSignedInSuccess(driveApi: Drive?)
    protected abstract fun onGoogleDriveSignedInFailed(exception: ApiException?)
    override fun onGoogleSignedInSuccess(signInAccount: GoogleSignInAccount?) {
        initializeDriveClient(signInAccount!!)
    }

    override fun onGoogleSignedInFailed(exception: ApiException?) {
        onGoogleDriveSignedInFailed(exception)
    }

    private fun initializeDriveClient(signInAccount: GoogleSignInAccount) {
        val scopes: MutableList<String> = ArrayList()
        scopes.add(DriveScopes.DRIVE_APPDATA)
        val credential = GoogleAccountCredential.usingOAuth2(requireActivity(), scopes)
        credential.selectedAccount = signInAccount.account
        val builder = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
        val appName = "Notebook"
        val driveApi = builder
            .setApplicationName(appName)
            .build()
        onGoogleDriveSignedInSuccess(driveApi)
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST = 1010
        protected val googleSignInOptions: GoogleSignInOptions
            protected get() {
                val scopeDriveAppFolder = Scope(Scopes.DRIVE_APPFOLDER)
                return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(scopeDriveAppFolder)
                    .build()
            }
    }
}
