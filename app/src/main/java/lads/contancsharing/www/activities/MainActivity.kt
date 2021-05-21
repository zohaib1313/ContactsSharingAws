package lads.contancsharing.www.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.ActivityMainBinding


import lads.contancsharing.www.fragments.ContactsFragment
import lads.contancsharing.www.fragments.HistoryFragment
import lads.contancsharing.www.fragments.ProfileFragment
import lads.contancsharing.www.fragments.NotificationFragment
import lads.contancsharing.www.utils.Helper

class MainActivity : BaseActivity() {
    private var mFragmentManager: FragmentManager? = null
    lateinit var mBinding: ActivityMainBinding

    companion object {
        lateinit var bottomNavView: BottomNavigationView
        val TAG: String = MainActivity::class.java.simpleName
        private var pinpointManager: PinpointManager? = null
        fun getPinpointManager(applicationContext: Context?): PinpointManager? {
            if (pinpointManager == null) {
                val awsConfig = AWSConfiguration(applicationContext)
                AWSMobileClient.getInstance()
                    .initialize(
                        applicationContext,
                        awsConfig,
                        object : Callback<UserStateDetails?> {

                            override fun onError(e: Exception?) {
                                Log.e("INIT", "Initialization error.", e)
                            }

                            override fun onResult(result: UserStateDetails?) {
                                Log.i("INIT", result?.userState.toString())
                            }
                        })
                val pinpointConfig = PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig
                )
                pinpointManager = PinpointManager(pinpointConfig)
                val token: String = FirebaseMessaging.getInstance().token.toString()
                pinpointManager!!.notificationClient.registerDeviceToken(token)

            }
            return pinpointManager
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mFragmentManager = supportFragmentManager
        bottomNavView = mBinding.bottomNavigationView
        bottomNavView.setOnNavigationItemSelectedListener { onBottomNavClick(it) }
        changeFragment(ContactsFragment.newInstance(0), false)
        if (sessionManager.user != null) {

            Helper.sessionRefresh()
        }

        bottomNavView.menu.forEach { item ->
            TooltipCompat.setTooltipText(findViewById(item.itemId), null)
        }
    }

    var activeTabId = 0
    private fun onBottomNavClick(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionContacts -> {
                if (activeTabId != R.id.actionContacts) {
                    changeFragment(ContactsFragment.newInstance(0), false)
                }
            }
            R.id.actionReceive -> {
                if (activeTabId != R.id.actionReceive) {
                    changeFragment(NotificationFragment.newInstance(0), false)
                }

            }
            R.id.actionHistory -> {
                if (activeTabId != R.id.actionHistory) {
                    changeFragment(HistoryFragment.newInstance(0), false)

                }
            }
            R.id.actionProfile -> {
                if (activeTabId != R.id.actionProfile) {

                    changeFragment(ProfileFragment.newInstance(0), false)


                }
            }

        }

        activeTabId = item.itemId
        return true
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.mainActivityFragmentContainer, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

}