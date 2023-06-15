package com.camerba.mypetowapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.camerba.mypetowapp.Fragments.HeartFragment
import com.camerba.mypetowapp.Fragments.HomeFragment
import com.camerba.mypetowapp.Fragments.ProfileFragment
import com.camerba.mypetowapp.Fragments.SearchFragment




class MainActivity : AppCompatActivity() {




    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true


            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true

            }
            R.id.nav_add_post -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_hearth -> {
                moveToFragment(HeartFragment())
                return@OnNavigationItemSelectedListener true

            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true

            }
        }

       false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())




    }

    private fun moveToFragment(fragment: Fragment){
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()
    }
}
