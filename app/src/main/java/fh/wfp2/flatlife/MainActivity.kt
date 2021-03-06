package fh.wfp2.flatlife

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.databinding.ActivityMainBinding
import fh.wfp2.flatlife.util.OptionsMenuInterface
import timber.log.Timber

@AndroidEntryPoint

class MainActivity : AppCompatActivity(), OptionsMenuInterface {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate Called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        setSupportActionBar(binding.toolbar)
        binding.bottomNavigation.setupWithNavController(navController)
        //connecting action bar (=toolbar bei uns) zu navController
        setupActionBarWithNavController(navController)

        appBarConfiguration = AppBarConfiguration.Builder()
            .build()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun hideOptionsMenu() {
        findViewById<View>(R.id.action_settings).visibility = View.GONE
    }

    override fun showOptionsMenu() {
        findViewById<View>(R.id.action_settings).visibility = View.VISIBLE
    }
}
