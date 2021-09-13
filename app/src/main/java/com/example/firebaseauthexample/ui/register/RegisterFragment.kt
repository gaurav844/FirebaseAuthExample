package com.example.firebaseauthexample.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthexample.R
import com.example.firebaseauthexample.databinding.FragmentRegisterBinding
import com.example.firebaseauthexample.ui.login.LoginViewModel
import com.example.firebaseauthexample.utils.Status
import com.example.firebaseauthexample.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

fun View.showsnackBar(message:String){
    Snackbar.make(this,message, Snackbar.LENGTH_LONG).show()
}

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()
    private val lViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnRegister?.setOnClickListener {
            val emailText = binding?.edtEmail?.text?.toString()
            val passwordText =  binding?.edtPassword?.text.toString()
            val fullNameText =  binding?.edtName?.text?.toString()
            viewModel.signUpUser( emailText.toString(), passwordText, fullNameText.toString()).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        viewModel.saveUser( it.data?.email.toString(), it.data?.fullName.toString())
                        view.showsnackBar("User account registered")
                        if (findNavController().currentDestination?.id == R.id.registerFragment) {
                            NavHostFragment.findNavController(this).navigate(
                                RegisterFragmentDirections.actionRegisterFragmentToHomeFragment2()
                            )
                            //Timber.d("display ${fAuth.currentUser?.displayName} ")
                        }
                    }
                    Status.ERROR -> {
                        view.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        view.showsnackBar("...")
                    }
                } })
        }

        binding?.googleSignIn?.setOnClickListener {
            signIn()
        }

        try {
            viewModel.saveUserLiveData.observe(viewLifecycleOwner,  {
                if (it.status == Status.SUCCESS){
                    requireView().showsnackBar("success!")
                    if (findNavController().currentDestination?.id == R.id.registerFragment) {
                        NavHostFragment.findNavController(this).navigate(
                            RegisterFragmentDirections.actionRegisterFragmentToHomeFragment2(it?.data?.fullName!!)
                        )
                        //Timber.d("display ${fAuth.currentUser?.displayName} ")
                    }
                }else if (it.status == Status.ERROR) {
                    requireView().showsnackBar(it.message!!)
                }
            })
        }catch (e:ApiException){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {


            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)


                lViewModel.signInWithGoogle(account!!).observe(viewLifecycleOwner, {
                    if (it.status == Status.SUCCESS) {

                        viewModel.saveUser(
                            auth.currentUser?.email!!,
                            auth.currentUser?.displayName!!
                        )
                    } else if (it.status == Status.ERROR) {
                        requireView().showsnackBar(it.message!!)
                    }
                })
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }
}