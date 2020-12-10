package fh.wfp2.flatlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import fh.wfp2.flatlife.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() /*, NavigationView.OnNavigationItemSelectedListener*/ {

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
        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController)

//die domain namen werden vorgeschlagen, je nachdem wie sie im navigation.xml stehen

        //toplevel destinations sind die destination wo kein up button da ist
        /* val topLevelDestinations = setOf(
             R.id.homeFragment,
             R.id.todoFragment,
             R.id.tasksFragment
         )
 */
        appBarConfiguration = AppBarConfiguration.Builder()
            .build()


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
