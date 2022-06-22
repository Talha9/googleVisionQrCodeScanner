package com.example.googlevisionqrcodescanner.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.googlevisionqrcodescanner.*
import com.example.googlevisionqrcodescanner.Fragments.CreateWRFragment.CreateQRFragment
import com.example.googlevisionqrcodescanner.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private var navView: NavigationView? = null
    lateinit var binding: ActivityMainBinding
    private val PERMISSION_REQUEST = 200
    var menuItemList = ArrayList<Int>()
    var fragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarr.toolbar)
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        setupActionBarWithNavController(navController)
        navView!!.setupWithNavController(navController)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST
            )
        }
        listFillers()
        setNavigationViewListener()
        //menuItemColorChanger(R.id.scan)
        onClickListenrs()
    }

    private fun listFillers() {
        menuItemList.add(R.id.scan)
        menuItemList.add(R.id.scanImg)
        menuItemList.add(R.id.fav)
        menuItemList.add(R.id.history)
        menuItemList.add(R.id.myQR)
        menuItemList.add(R.id.createQr)
        menuItemList.add(R.id.settings)
        menuItemList.add(R.id.share)
    }

    private fun onClickListenrs() {
        binding.appBarr.menuBtn.setOnClickListener {
            binding.drawer.open()
        }

    }

    private fun setNavigationViewListener() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }



    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scan -> {
                menuItemColorChanger(R.id.scan)
                loadFragment(HomeFragment())
                binding.appBarr.fragmentTitle.text="Scan"
                binding.drawer.close()
                return true
            }
            R.id.scanImg -> {
                menuItemColorChanger(R.id.scanImg)
                binding.appBarr.fragmentTitle.text="Scan Image"
                binding.drawer.close()
                return true
            }
            R.id.fav -> {
                menuItemColorChanger(R.id.fav)
                binding.appBarr.fragmentTitle.text="Favourites"
                loadFragment(FavFragment())
                binding.drawer.close()
                return true
            }
            R.id.history -> {
                menuItemColorChanger(R.id.history)
                binding.appBarr.fragmentTitle.text="History"
                loadFragment(HistoryFragment())
                binding.drawer.close()
                return true
            }
            R.id.myQR -> {
                menuItemColorChanger(R.id.myQR)
                binding.appBarr.fragmentTitle.text="My QR"
                loadFragment(MyQRFragment())
                binding.drawer.close()
                return true
            }
            R.id.createQr -> {
                menuItemColorChanger(R.id.createQr)
                binding.appBarr.fragmentTitle.text="Create QR"
                loadFragment(CreateQRFragment())
                binding.drawer.close()
                return true
            }
            R.id.settings -> {
                menuItemColorChanger(R.id.settings)
                binding.appBarr.fragmentTitle.text="Settings"
                loadFragment(SettingsFragment())
                binding.drawer.close()
                return true
            }
            R.id.share -> {
                menuItemColorChanger(R.id.share)
                binding.appBarr.fragmentTitle.text="Share"
                binding.drawer.close()
                return true
            }
            else -> {
                return true

            }
        }
    }

    private fun menuItemColorChanger(vClick: Int) {
        for (i in menuItemList) {
            if (vClick == i) {
                val v = findViewById<View>(vClick)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    v.setBackgroundColor(getColor(R.color.blue))
                }
            } else {
                val v = findViewById<View>(i)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    v.setBackgroundColor(getColor(R.color.white))
                }
            }
        }

    }

    private fun loadFragment(fragment: Fragment) {

        try {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commitAllowingStateLoss()


        } catch (e: Exception) {
        }
    }


}