package com.example.firebaseauthexample.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebaseauthexample.R
import com.example.firebaseauthexample.databinding.FragmentHomeBinding
import com.example.firebaseauthexample.ui.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {


    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    @Inject
    lateinit var fAuth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fAuth.currentUser?.photoUrl != null) {
            binding?.iv?.load(fAuth.currentUser?.photoUrl)
            {
                transformations(CircleCropTransformation())
                crossfade(true)
            }
        } else {
            binding?.iv?.load(R.drawable.ic_account_circle_24) {
                transformations(CircleCropTransformation())
                crossfade(true)
            }
        }

        val args : HomeFragmentArgs by navArgs()
        binding?.signTv?.text = String.format(
            resources.getString(R.string.user_greet),
            args.name
        )

        binding.btnLogout.setOnClickListener {
            fAuth.signOut()
            findNavController().navigateUp()
        }
    }

}