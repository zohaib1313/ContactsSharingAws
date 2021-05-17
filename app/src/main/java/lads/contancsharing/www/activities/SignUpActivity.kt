package lads.contancsharing.www.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.solver.widgets.Helper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.ActivitySignUpBinding
import lads.contancsharing.www.fragments.LoginFragment

class SignUpActivity : BaseActivity() {

    lateinit var mBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        if (sessionManager.isLoggedIn) {
            lads.contancsharing.www.utils.Helper.startActivity(
                this@SignUpActivity,
                Intent(this@SignUpActivity, MainActivity::class.java),
                true
            )
            finishAffinity()
        }

        changeFragment(LoginFragment.newInstance(0), false)

    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

}