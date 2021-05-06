package lads.contancsharing.www.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import lads.contancsharing.www.databinding.ActivitySplashBinding
import lads.contancsharing.www.utils.AppConstant


class SplashActivity :AppCompatActivity() {

    lateinit var mBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
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