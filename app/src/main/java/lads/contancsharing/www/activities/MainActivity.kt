package lads.contancsharing.www.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.ActivityMainBinding


import lads.contancsharing.www.fragments.ContactsFragment
import lads.contancsharing.www.fragments.HistoryFragment
import lads.contancsharing.www.fragments.ProfileFragment
import lads.contancsharing.www.fragments.ReceiveFragment

class MainActivity : BaseActivity() {
    private var mFragmentManager: FragmentManager? = null
    lateinit var mBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mFragmentManager = supportFragmentManager
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener { onBottomNavClick(it) }
        changeFragment(ContactsFragment.newInstance(0), false)


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
                    changeFragment(ReceiveFragment.newInstance(0), false)
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

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.mainActivityFragmentContainer, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

}