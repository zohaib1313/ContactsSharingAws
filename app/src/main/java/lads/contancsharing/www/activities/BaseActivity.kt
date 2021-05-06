package lads.contancsharing.www.activities

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.jaeger.library.StatusBarUtil
import lads.contancsharing.www.utils.SPManager.getInstance

import lads.contancsharing.www.utils.SessionManager

open class BaseActivity : AppCompatActivity() {
    var isLoggedIn = false
    lateinit var sessionManager: SessionManager
    lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        sessionManager = SessionManager.getInstance(mContext)
        //   isLoggedIn = sessionManager.isLoggedIn
        setStatusBarMode(true)
        setStatusBarTransparent(this)
    }

    open fun setStatusBarMode(enableDarkMode: Boolean) {
        if (enableDarkMode) {
            StatusBarUtil.setDarkMode(this)
        } else {
            StatusBarUtil.setLightMode(this)
        }
    }


    open fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}