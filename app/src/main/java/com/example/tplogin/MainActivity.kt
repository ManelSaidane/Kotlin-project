package com.example.tplogin


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.tplogin.Login
import com.example.tplogin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var actionToggle: ActionBarDrawerToggle
    lateinit var user: FirebaseUser
    lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration:AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        initMenu(binding)
        initNav(navView,binding)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if (user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } else {
         //   binding.tvDetails.text = user!!.email.toString()
        }
      /*  binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    private fun initNav(navView: NavigationView ,binding: ActivityMainBinding) {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.dashboardFragment, R.id.action_dashboardFragment_to_userListFragment),
            binding.Drawer
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        navView?.setupWithNavController(navController)


        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.users -> {
                    navController.navigate(R.id.userListFragment)
                 drawerLayout.closeDrawer(Gravity.LEFT)
                    true}
                  /*  val intent = Intent(this, UserList::class.java)
                    startActivity(intent)
                    true}*/
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment4)
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }
                R.id.logout -> {

                    auth.signOut()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                    true}
                else -> false

            }
        }

    }

    private fun initMenu(binding: ActivityMainBinding) {

            drawerLayout = binding.Drawer
            navView = binding.navView

            actionToggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, 0, 0)
            drawerLayout.addDrawerListener(actionToggle)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

//make icon menu visible
            actionToggle.syncState()
        }
    }


