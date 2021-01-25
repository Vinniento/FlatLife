package fh.wfp2.flatlife.ui.fragments

import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseFragment(layoutId: Int): Fragment(layoutId) {


    fun showSnackbar(text: String) {
        Snackbar.make(
            requireActivity().rootLayout,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    fun hideBottomNavigation(){
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE

    }
    fun showBottomNavigation(){
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE
    }

}