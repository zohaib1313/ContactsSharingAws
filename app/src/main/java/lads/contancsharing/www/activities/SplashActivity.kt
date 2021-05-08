package lads.contancsharing.www.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import lads.contancsharing.www.databinding.ActivitySplashBinding

import lads.contancsharing.www.utils.AppConstant


class SplashActivity :AppCompatActivity() {

    lateinit var mBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
// Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        setContentView(mBinding.root)

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/


        Handler().postDelayed(Runnable {
            val mainIntent = Intent(this@SplashActivity, SignUpActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, AppConstant.SPLASH_SLEEP_TIME.toLong())
    }


}