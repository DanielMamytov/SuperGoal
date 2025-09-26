package be.buithg.supergoal

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import be.buithg.supergoal.presentation.ui.onboarding.hasSeenOnboarding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val bottom = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom.itemIconTintList = null            // ← иконки остаются цветными
        bottom.itemTextColor = resources.getColorStateList(R.color.bottom_nav_text_selector, theme)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph).apply {
            val startDestination = if (hasSeenOnboarding()) {
                R.id.nav_goals
            } else {
                R.id.splashFragment
            }
            setStartDestination(startDestination)
        }
        navController.setGraph(graph, null)
        bottom.setupWithNavController(navController)

        val mainContainer = findViewById<View>(R.id.main)
        val navHostView = findViewById<View>(R.id.nav_host_fragment)

        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            navHostView.updatePadding(bottom = systemBars.bottom)
            insets
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.onBoardingFragment -> bottom.visibility = View.GONE

                else -> bottom.visibility = View.VISIBLE
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}