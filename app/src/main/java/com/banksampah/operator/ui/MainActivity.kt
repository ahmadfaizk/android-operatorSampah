package com.banksampah.operator.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.ui.*
import com.banksampah.operator.R
import com.banksampah.operator.utils.TokenPreference

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_profile,
            R.id.nav_customer,
            R.id.nav_register_customer,
            R.id.nav_complaint
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_logout) {
                showLogoutDialog()
            }
            NavigationUI.onNavDestinationSelected(item, navController)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Apakah anda yakin akan keluar dari aplikasi ini?")
            .setPositiveButton("Ya"
            ) { _, _ -> logout() }
            .setNegativeButton("Tidak"
            ) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun logout() {
        TokenPreference.getInstance(this).removeToken()
        startActivity(Intent(this, StarterActivity::class.java))
        finish()
    }
}