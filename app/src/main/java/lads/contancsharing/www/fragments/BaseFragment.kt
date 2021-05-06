package lads.contancsharing.www.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import lads.contancsharing.www.utils.SessionManager


abstract class BaseFragment : Fragment(), View.OnClickListener {

    open val TAG: String = "BaseFragment"
    open var isAttached = false

    lateinit var sessionManager: SessionManager
    var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        sessionManager = SessionManager.getInstance(activity)
     //   isLoggedIn = sessionManager.isLoggedIn
    }


    override fun onClick(v: View) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }


    var loadingLayout: RelativeLayout? = null
    fun showLoading() {
        loadingLayout?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingLayout?.visibility = View.GONE
    }

    var noDataFoundLayout: RelativeLayout? = null
    fun showNoDataLayout() {
        noDataFoundLayout?.visibility = View.VISIBLE
    }

    fun hideNoDataLayout() {
        noDataFoundLayout?.visibility = View.GONE
    }
//
//    fun logout(user: User) {
//        sessionManager.clearSession()
//        //api call
//        Helper.startActivity(requireContext() as Activity, Intent(requireContext(), SignInActivity::class.java), true)
//    }


}
