package lads.contancsharing.www.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import lads.contancsharing.www.databinding.ActivitySplashBinding

import lads.contancsharing.www.utils.AppConstant


class SplashActivity :BaseActivity() {

    lateinit var mBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
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