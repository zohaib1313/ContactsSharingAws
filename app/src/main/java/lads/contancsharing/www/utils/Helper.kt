package lads.contancsharing.www.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import lads.contancsharing.www.R

import java.io.File
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Helper {

    fun startActivity(mActivity: Activity, mIntent: Intent, isFinish: Boolean) {
        mActivity.startActivity(mIntent)
        runAnimation(mActivity)
        if (isFinish) {
            mActivity.finish()
        }
    }

    fun startActivityReverse(mActivity: Activity, mIntent: Intent, isFinish: Boolean) {
        mActivity.startActivity(mIntent)
        runReverseAnimation(mActivity)
        if (isFinish) {
            mActivity.finish()
        }
    }


    private fun runAnimation(mActivity: Activity) {
        mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun runReverseAnimation(mActivity: Activity) {
        mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        //        if (LocaleHelper.isEnglish(mActivity)) {
        //            mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        //        } else {
        //            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //        }
    }


    fun setAnimation(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 1000
        val animation = AnimationSet(false) //change to false
        animation.addAnimation(fadeIn)
        view.animation = animation
    }

    fun getFormattedDate(
        strDate: String,
        sourceFormat: String,
        destinyFormat: String,
        locale: Locale
    ): String {
        var df = SimpleDateFormat(sourceFormat, locale)
        var date: Date? = null
        try {
            date = df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        df = SimpleDateFormat(destinyFormat, locale)
        return df.format(date)
    }


    fun getStringToDate(strDate: String, sourceFormate: String): Date? {
        var df = SimpleDateFormat(sourceFormate)
        var date: Date? = null
        try {
            date = df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date

    }

    fun getCurrentMYSQLDateTime(): String? {
        return SimpleDateFormat(AppConstant.MYSQL_DATETIME_FORMAT).format(Calendar.getInstance().time)
    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun hideKeyboard(activity: Activity) {
        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideSoftKeybord(mContext: Context, v: View) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }


    fun isValidPassword(password: String): Boolean {
        return Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$")
            .matcher(password).matches()
    }


    fun validateCivilId(civilId: String): Boolean {
//        return (civilId.isNotEmpty() && civilId.length == 12 && civilId.startsWith("25"))
        return (civilId.isNotEmpty() && civilId.length == 12)
    }

    fun generateBitmapDescriptorFromRes(context: Context, resId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, resId)
        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }






    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }


    @JvmStatic
    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {

                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }



    fun isDigitOnly(text: CharSequence): Boolean {
        return text.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    @JvmStatic
    fun roundDecimal(discRs: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(discRs)
    }


    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }



}
