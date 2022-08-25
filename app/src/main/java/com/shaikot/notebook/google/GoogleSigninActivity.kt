package com.shaikot.notebook.google

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

abstract class GoogleSignInActivity : Fragment() {
    protected abstract fun onGoogleSignedInSuccess(signInAccount: GoogleSignInAccount?)
    protected abstract fun onGoogleSignedInFailed(exception: ApiException?)
    
    fun startGoogleSignIn() {
        assert(googleSignInOptions != null)
        val googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            googleSignInOptions!!
        )
        val signInIntent = googleSignInClient.signInIntent
        requireActivity().startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount?>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            onGoogleSignedInSuccess(account)
        } catch (e: ApiException) {
            onGoogleSignedInFailed(e)
        }
    }

    companion object {
        protected const val GOOGLE_SIGN_IN_REQUEST = 1010
        protected val googleSignInOptions: GoogleSignInOptions?
            protected get() = null
    }
}
