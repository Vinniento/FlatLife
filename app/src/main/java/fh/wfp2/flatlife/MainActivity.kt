package fh.wfp2.flatlife

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import fh.wfp2.flatlife.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() /*, NavigationView.OnNavigationItemSelectedListener*/ {

    private lateinit var _drawerLayout: DrawerLayout
    private lateinit var _toolbar: Toolbar
    private lateinit var _toggle: ActionBarDrawerToggle
    private lateinit var _navController: NavController
    private lateinit var _appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate Called")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        _drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        _navController = navHostFragment.navController


//die domain namen werden vorgeschlagen, je nachdem wie sie im navigation.xml stehen
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.tasksFragment,
            R.id.todoFragment
        )
        _appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(_drawerLayout)
            .build()

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(_navController, _appBarConfiguration)
        binding.navigationView.setupWithNavController(_navController)


        _toolbar = findViewById(R.id.toolbar)
        _drawerLayout = findViewById(R.id.drawer_layout)

        _toggle = ActionBarDrawerToggle(
            this,
            _drawerLayout,
            _toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        _drawerLayout.addDrawerListener(_toggle)

        _toggle.syncState()


        //set the fragments that should implement the drawer menu


    }

    override fun onBackPressed() {
        /*if (_drawerLayout.isDrawerOpen(GravityCompat.START))
            _drawerLayout.closeDrawer(GravityCompat.START)
        else*/
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(_navController, _appBarConfiguration)
    }


}
