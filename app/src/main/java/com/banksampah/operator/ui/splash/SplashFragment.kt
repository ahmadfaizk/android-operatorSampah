package com.banksampah.operator.ui.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.banksampah.operator.R
import com.banksampah.operator.utils.TokenPreference

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = TokenPreference.getInstance(requireContext()).getToken()
        Handler().postDelayed({
            if (token == null) {
                view.findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            } else {
                view.findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                this.activity?.finish()
            }
        }, 3000)
    }
}