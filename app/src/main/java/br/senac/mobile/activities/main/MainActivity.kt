package br.senac.mobile.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import br.senac.mobile.R
import br.senac.mobile.databinding.ActivityMainBinding
import br.senac.mobile.fragments.CartFragment
import br.senac.mobile.fragments.HistoryFragment
import br.senac.mobile.fragments.HomeFragment
import br.senac.mobile.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow))

        var fragment: Fragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
            .addToBackStack("home").commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            var currentFragment: String
            fragment = when(it.itemId) {
                R.id.bottomNavBagButton -> {
                    currentFragment = "cart"
                    CartFragment()
                }
                R.id.bottomNavHistoryButton -> {
                    currentFragment = "history"
                    HistoryFragment()
                }
                R.id.bottomNavProfileButton -> {
                    currentFragment = "profile"
                    ProfileFragment()
                }
                else -> {
                    currentFragment = "home"
                    HomeFragment()
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack(currentFragment).commit()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        var tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        when(tag) {
            "cart" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavBagButton).setChecked(true)
            "history" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHistoryButton).setChecked(true)
            "profile" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavProfileButton).setChecked(true)
            else -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHomeButton).setChecked(true)
        }
        return true
    }
}