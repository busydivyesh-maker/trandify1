package com.example.trandify1

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.trandify1.databinding.ActivityMainBinding
import com.example.trandify1.receiver.AirplaneModeReceiver
import com.example.trandify1.ui.account.AccountFragment
import com.example.trandify1.ui.cart.CartFragment
import com.example.trandify1.ui.categories.CategoriesFragment
import com.example.trandify1.ui.home.HomeFragment
import com.example.trandify1.ui.wishlist.WishlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val wishlistFragment = WishlistFragment()
    private val categoriesFragment = CategoriesFragment()
    private val cartFragment = CartFragment()
    private val accountFragment = AccountFragment()

    private var activeFragment: Fragment = homeFragment

    private val airplaneModeReceiver = AirplaneModeReceiver { isOn ->
        if (isOn) {
            binding.layoutAirplaneBanner.visibility = View.VISIBLE
            binding.layoutAirplaneBanner.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down)
            )
            Toast.makeText(this, "Airplane Mode Enabled - Cart & Checkout Locked", Toast.LENGTH_LONG).show()
        } else {
            binding.layoutAirplaneBanner.visibility = View.GONE
            Toast.makeText(this, "Airplane Mode Disabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments(savedInstanceState == null)
        setupBottomNavigation()
        setupBackPressHandling()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(airplaneModeReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(airplaneModeReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(airplaneModeReceiver)
    }

    private fun setupFragments(isFirstInit: Boolean) {
        val fm = supportFragmentManager
        if (isFirstInit) {
            fm.beginTransaction()
                .add(R.id.nav_host_fragment, accountFragment, "ACCOUNT").hide(accountFragment)
                .add(R.id.nav_host_fragment, cartFragment, "CART").hide(cartFragment)
                .add(R.id.nav_host_fragment, categoriesFragment, "CATEGORIES").hide(categoriesFragment)
                .add(R.id.nav_host_fragment, wishlistFragment, "WISHLIST").hide(wishlistFragment)
                .add(R.id.nav_host_fragment, homeFragment, "HOME")
                .commit()
            activeFragment = homeFragment
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.nav_wishlist -> {
                    switchFragment(wishlistFragment)
                    true
                }
                R.id.nav_categories -> {
                    switchFragment(categoriesFragment)
                    true
                }
                R.id.nav_cart -> {
                    switchFragment(cartFragment)
                    true
                }
                R.id.nav_account -> {
                    switchFragment(accountFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(target: Fragment) {
        if (activeFragment == target) return
        val fm = supportFragmentManager
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .hide(activeFragment)
            .show(target)
            .commit()
        activeFragment = target
    }

    fun navigateToTab(menuItemId: Int) {
        binding.bottomNavigation.selectedItemId = menuItemId
    }

    private fun setupBackPressHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                if (currentFragment != null && currentFragment != homeFragment && supportFragmentManager.backStackEntryCount == 0) {
                    navigateToTab(R.id.nav_home)
                } else if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }
}