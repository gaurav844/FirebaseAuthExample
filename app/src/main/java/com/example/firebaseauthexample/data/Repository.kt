package com.example.firebaseauthexample.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class Repository @Inject constructor(private val fireBaseSource: FirebaseSource) {
    fun signUpUser(email: String, password: String, fullName: String) = fireBaseSource.signUpUser(email, password, fullName)

    fun signInUser(email: String, password: String) = fireBaseSource.signInUser(email, password)

    fun signInWithGoogle(acct: GoogleSignInAccount) = fireBaseSource.signInWithGoogle(acct)

    fun saveUser(email: String, name: String) = fireBaseSource.saveUser(email, name)

    fun fetchUser() = fireBaseSource.fetchUser()

    fun sendForgotPassword(email: String)=fireBaseSource.sendForgotPassword(email)

}