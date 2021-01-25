package fh.wfp2.flatlife.ui.fragments.authentication

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.remote.BasicAuthInterceptor
import fh.wfp2.flatlife.databinding.AuthFragmentBinding
import fh.wfp2.flatlife.other.Constants.KEY_LOGGED_IN_USERNAME
import fh.wfp2.flatlife.other.Constants.KEY_PASSWORD
import fh.wfp2.flatlife.other.Constants.NO_PASSWORD
import fh.wfp2.flatlife.other.Constants.NO_USERNAME
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.auth.AuthViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.auth_fragment) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: AuthFragmentBinding

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var curUsername: String? = null
    private var curPassword: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLoggedIn()) {
            authenticateApi(curUsername ?: "", curPassword ?: "")
            redirectLogin()
        }
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        binding = AuthFragmentBinding.bind(view)
        subscribeToObservers()
        binding.apply {

            btnRegister.setOnClickListener {
                val email = etRegisterEmail.text.toString()
                val password = etRegisterPassword.text.toString()
                val confirmedPassword = etRegisterPasswordConfirm.text.toString()
                viewModel.register(email, password, confirmedPassword)
            }
            btnLogin.setOnClickListener {
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                curUsername = email
                curPassword = password
                viewModel.login(email, password)
            }
        }
        setHasOptionsMenu(false)
        hideBottomNavigation()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
    }
    private fun isLoggedIn(): Boolean {
        curUsername = sharedPref.getString(KEY_LOGGED_IN_USERNAME, NO_USERNAME) ?: NO_USERNAME
        curPassword = sharedPref.getString(KEY_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD
        return curUsername != NO_USERNAME && curPassword != NO_PASSWORD
    }

    private fun authenticateApi(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToHomeFragment(),
            navOptions
        )
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.loginProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully logged in")
                        sharedPref.edit().putString(KEY_LOGGED_IN_USERNAME, curUsername).apply()
                        sharedPref.edit().putString(KEY_PASSWORD, curPassword).apply()
                        authenticateApi(curUsername ?: "", curPassword ?: "")
                        Timber.d("CALLED")
                        redirectLogin()
                    }
                    Status.ERROR -> {
                        binding.loginProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occured")
                    }
                    Status.LOADING -> {
                        binding.loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully registered an account")
                    }
                    Status.ERROR -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        binding.registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}