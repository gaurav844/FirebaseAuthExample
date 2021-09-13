package com.example.firebaseauthexample.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthexample.R
import com.example.firebaseauthexample.databinding.FragmentRegisterBinding
import com.example.firebaseauthexample.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val fragmentScope = CoroutineScope(Dispatchers.Main)

    private var _binding: FragmentSplashBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var fAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        fragmentScope.launch {
            delay(1000)
            if (fAuth.currentUser != null){
                findNavController().navigate(R.id.action_splashFragment2_to_homeFragment2)
            }else findNavController().navigate(R.id.action_splashFragment2_to_loginFragment2)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        fragmentScope.cancel()
        super.onPause()
    }
}