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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var fragment: Fragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            fragment = when(it.itemId) {
                R.id.bottomNavHomeButton -> HomeFragment()
                R.id.bottomNavBagButton -> CartFragment()
                R.id.bottomNavHistoryButton -> HistoryFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }
}