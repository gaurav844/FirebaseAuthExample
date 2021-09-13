package com.example.firebaseauthexample.ui.login

import android.app.AlertDialog
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
import com.example.firebaseauthexample.databinding.FragmentLoginBinding
import com.example.firebaseauthexample.ui.register.showsnackBar
import com.example.firebaseauthexample.utils.Status
import com.example.firebaseauthexample.utils.Constants.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private val viewModel:LoginViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.txtRegister.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.loginFragment2) {
                NavHostFragment.findNavController(this)
                    .navigate(LoginFragmentDirections.actionLoginFragment2ToRegisterFragment())
            }
        }

        binding.btnRegister.setOnClickListener {
            val emailText = binding.edtEmail.text.toString()
            val passwordText = binding.edtPassword.text.toString()
            viewModel.signInUser(emailText, passwordText).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.LOADING -> {
                        view.showsnackBar("...")
                    }

                    Status.SUCCESS -> {
                        view.showsnackBar("Login successful")
                        if (findNavController().currentDestination?.id == R.id.loginFragment2) {
                            NavHostFragment.findNavController(this)
                                .navigate(
                                    LoginFragmentDirections.actionLoginFragment2ToHomeFragment2(
                                        it.data?.fullName!!
                                    )
                                )
                        }
                    }

                    Status.ERROR -> {
                        view.showsnackBar(it.message!!)
                    }
                }
            })
        }

        binding?.googleSignIn?.setOnClickListener {
            signIn()
        }


        //forget password
        val dialog = AlertDialog.Builder(requireContext())
        val inflater = (requireActivity()).layoutInflater
        val v = inflater.inflate(R.layout.forgot_password, null)
        dialog.setView(v)
            .setCancelable(false)
        val d = dialog.create()
        val emailEt = v.findViewById<TextInputEditText>(R.id.emailEt)
        val sendBtn = v.findViewById<MaterialButton>(R.id.sendEmailBtn)
        val dismissBtn = v.findViewById<MaterialButton>(R.id.dismissBtn)


        sendBtn.setOnClickListener {
            viewModel.sendResetPassword(emailEt.text.toString()).observeForever {
                if (it.status == Status.SUCCESS){
                    view.showsnackBar("reset email sent")
                }else{
                    view.showsnackBar(it.message.toString())
                }
            }
        }
        dismissBtn.setOnClickListener {
            d.dismiss()
        }


        binding?.txtForgotPassword?.setOnClickListener {
            d.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                viewModel.signInWithGoogle(account!!).observe(viewLifecycleOwner, {
                    if (it.status == Status.SUCCESS) {
                        if (findNavController().currentDestination?.id == R.id.loginFragment2) {
                            NavHostFragment.findNavController(this).navigate(
                                LoginFragmentDirections.actionLoginFragment2ToHomeFragment2(it?.data?.fullName!!)
                            )
                            // Timber.d("display ${fAuth.currentUser?.displayName} ")
                        }
                    } else if (it.status == Status.ERROR) {
                        requireView().showsnackBar(it.message!!)
                    }
                })
            } catch (e: ApiException) {
                Timber.d(""+e.printStackTrace())
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {

        val signInIntent: Intent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


}