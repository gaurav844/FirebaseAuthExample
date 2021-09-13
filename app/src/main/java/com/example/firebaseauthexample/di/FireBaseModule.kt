package com.example.firebaseauthexample.di

import android.content.Context
import com.example.firebaseauthexample.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FireBaseModule {

    @Provides
    fun provideFireBaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideGso(@ApplicationContext context: Context) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleClient(@ApplicationContext context: Context, gso:GoogleSignInOptions)= GoogleSignIn.getClient(context, gso)

    @Provides
    fun provideFirestore()= FirebaseFirestore.getInstance()
}