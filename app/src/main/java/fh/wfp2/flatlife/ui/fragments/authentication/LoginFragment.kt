package fh.wfp2.flatlife.ui.fragments.authentication

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.LoginFragmentBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.authentication.LoginViewModel
import timber.log.Timber


class LoginFragment : BaseFragment(R.layout.login_fragment) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var _viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)

        _viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate called")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStopCalled")
    }
}