package lads.contancsharing.www.fragments


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.goodiebag.pinview.Pinview.PinViewEventListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.FragmentVerifyOtpBinding
import java.util.concurrent.TimeUnit


class VerifyOtpFragment(
    var verificationId: String,
    var phoneNumber: String,
    var token: PhoneAuthProvider.ForceResendingToken
) : BaseFragment() {

    lateinit var mBinding: FragmentVerifyOtpBinding
    private var vCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    //Progress Dialog

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentVerifyOtpBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        mBinding.tvTitle.text = "Login using OTP sent to ${phoneNumber}"
        forceResendingToken = token
        mBinding.btnDone.setOnClickListener {

            if (mBinding.pinview.value.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Enter OTP sent", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
            verifyPhoneNumberThroughCode(verificationId, mBinding.pinview.value)
            mBinding.pinview.setPinViewEventListener(PinViewEventListener { pinview, fromUser -> //Make api calls here or what not
                printLog(fromUser.toString())
            })
        }

        mBinding.ivBack.setOnClickListener {
            changeFragment(LoginFragment.newInstance(0), false)
        }
        firebaseAuth = FirebaseAuth.getInstance()

        vCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.d(TAG, "onVerificationFailed: ${e.message}")
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: $verificationId")
                this@VerifyOtpFragment.verificationId = verificationId
                forceResendingToken = token
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }

                Toast.makeText(requireContext(), "Verification Code Sent", Toast.LENGTH_SHORT)
                    .show()

            }

        }

        mBinding.btnResendOtp.setOnClickListener {

            forceResendingToken?.let {
                resendVerificationCode(phoneNumber, it)
            }


        }

        return mBinding.root
    }


    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {

        ThreadUtils.runOnUiThread {
            showLoading()
        }

        Log.d(TAG, "resendVerificationCode: $phoneNumber")

        val phoneOptions = vCallBacks?.let {
            token?.let { it1 ->
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(50L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(it)
                    .setForceResendingToken(it1)
                    .build()
            }
        }

        PhoneAuthProvider.verifyPhoneNumber(phoneOptions!!)
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(
            index: Int,
            verificationId: String,
            phoneNumber: String,
            token: PhoneAuthProvider.ForceResendingToken
        ): VerifyOtpFragment {
            val fragment = VerifyOtpFragment(verificationId, phoneNumber, token)
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }


    private fun verifyPhoneNumberThroughCode(verificationId: String?, code: String) {

        // showLoading()
        ThreadUtils.runOnUiThread {
            showLoading()
        }
        val credential = PhoneAuthProvider.getCredential(verificationId.toString(), code)
        signInWithPhoneAuthCredential(credential)

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success

                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val idToken = task.result?.token

                            val phoneNumber = firebaseAuth.currentUser?.phoneNumber


                            sendOpenIDToAWS(idToken.toString())
                        }
                    }

            }
            .addOnFailureListener { e ->
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }


    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }


    private fun sendOpenIDToAWS(idToken: String) {
        printLog(idToken)
        val mobileClient =
            Amplify.Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?

        mobileClient?.federatedSignIn(
            // aws provider URL
            "securetoken.google.com/contactssharing-d144b",
            idToken,
            object : com.amazonaws.mobile.client.Callback<UserStateDetails?> {

                override fun onResult(userStateDetails: UserStateDetails?) {


                    printLog("id= ${mobileClient.identityId}")
                    printLog("userDetails= ${userStateDetails?.details}")
                    changeFragment(ProfileInfoFragment.newInstance(0), true);
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                    }
                }

                override fun onError(e: Exception?) {
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                    }
                    if (e != null) {
                        printLog("Error aws:: " + e.message)
                    }
                }
            }
        )
    }
}