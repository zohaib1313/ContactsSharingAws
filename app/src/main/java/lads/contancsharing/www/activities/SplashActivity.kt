package lads.contancsharing.www.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import lads.contancsharing.www.api_calls.SmsUtils
import lads.contancsharing.www.databinding.ActivitySplashBinding
import lads.contancsharing.www.fragments.ProfileInfoFragment

import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper


class SplashActivity : BaseActivity() {

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


        if (sessionManager.user != null) {
            Helper.sessionRefresh()

        }
        Handler().postDelayed(Runnable {
            val mainIntent = Intent(this@SplashActivity, SignUpActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()

        }, AppConstant.SPLASH_SLEEP_TIME.toLong())
    }


}