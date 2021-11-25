package br.senac.mobile.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.fragment.app.Fragment
import br.senac.mobile.R
import br.senac.mobile.databinding.ActivityMainBinding
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import br.senac.mobile.fragments.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        var fragment: Fragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment, "home")
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

            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment, currentFragment)
                .addToBackStack(currentFragment).commit()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        menu.findItem(R.id.scanner).icon.setTint(getColor(R.color.white))

        val myActionMenuItem: MenuItem = menu.findItem(R.id.search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val fragment = StoreCatalogFragment.newInstance("search", 0, query)
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.scanner) {
            val fragment = QrCodeFragment()
            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        } else {
            val scannerIcon = findViewById<View>(R.id.scanner)
            scannerIcon.isVisible = false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        var tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        when(tag) {
            "cart" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavBagButton).isChecked =
                true
            "history" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHistoryButton).isChecked =
                true
            "profile" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavProfileButton).isChecked =
                true
            else -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHomeButton).isChecked =
                true
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val isHome = supportFragmentManager.findFragmentByTag("home")

        if (isHome?.isVisible == false) {
            var tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
            when(tag) {
                "cart" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavBagButton).isChecked =
                    true
                "history" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHistoryButton).isChecked =
                    true
                "profile" -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavProfileButton).isChecked =
                    true
                else -> binding.bottomNavigationView.menu.findItem(R.id.bottomNavHomeButton).isChecked =
                    true
            }
        } else {
            binding.bottomNavigationView.menu.findItem(R.id.bottomNavHomeButton).isChecked =
                true
        }
    }
}