package lads.contancsharing.www.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.FragmentLoginBinding
import java.util.concurrent.TimeUnit

class LoginFragment : BaseFragment() {

    lateinit var mBinding: lads.contancsharing.www.databinding.FragmentLoginBinding
    private var vCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mVerificationId: String? = null

    //Progress Dialog

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    private lateinit var phoneNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAuth = FirebaseAuth.getInstance()
        mBinding = FragmentLoginBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        mBinding.btnNext.setOnClickListener {

            if (validateForm()) {
                printLog(mBinding.etCountryCode.selectedCountryCodeWithPlus)

                phoneNumber =
                    mBinding.etCountryCode.selectedCountryCodeWithPlus + mBinding.etPhoneNumber.text.toString()
                        .trim()
                lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
                phoneNumber?.let {
                    startPhoneNumberVerification(it)
                }
            }

        }

        mBinding.ivBack.setOnClickListener {
            requireFragmentManager().popBackStack()
        }



        vCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                printLog("onVerificationCompletede")
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {

                printLog("Verification failed " + e.localizedMessage)
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                forceResendingToken = token
                hideLoading()
                changeFragment(
                    VerifyOtpFragment.newInstance(
                        0,
                        verificationId,
                        phoneNumber,
                        forceResendingToken!!
                    ), false
                )

            }

        }

        return mBinding.root
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        showLoading()

        val phoneOptions = vCallBacks?.let {
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(it)
                .build()
        }

        PhoneAuthProvider.verifyPhoneNumber(phoneOptions!!)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (mBinding.etPhoneNumber.text.isNullOrEmpty()) {
            mBinding.etPhoneNumber.error =
                getString(R.string.error_form_empty_field_of, "Phone number")
            isValid = false
        } else if (mBinding.etPhoneNumber.text.toString().contains("+")) {
            mBinding.etPhoneNumber.error = getString(R.string.erro_contain_country_code)
            isValid = false
        }
        if (mBinding.etCountryCode.textView_selectedCountry.text.isNullOrEmpty()) {
            mBinding.etPhoneNumber.error =
                getString(R.string.error_form_empty_field_of, "Phone number")
            isValid = false
        }
        return isValid;
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
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
                            printLog("Token=== " + idToken.toString())
                            sendOpenIDToAWS(idToken)
                        }
                    }

//                //start profile activity
//                startActivity(Intent(requireContext(), ContactsActivity::class.java))
//                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendOpenIDToAWS(idToken: String?) {

        val mobileClient =
            Amplify.Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?

        mobileClient?.federatedSignIn(
            // aws provider URL
            "securetoken.google.com/contactssharing-d144b",
            idToken,
            object : com.amazonaws.mobile.client.Callback<UserStateDetails?> {
                override fun onResult(userStateDetails: UserStateDetails?) {
                    printLog("Logged in...aws")
                    val phoneNumber = firebaseAuth.currentUser?.phoneNumber
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                    }


                }

                override fun onError(e: Exception?) {
                    if (e != null) {
                        printLog("Logged in...aws failed " + e.localizedMessage)
                    }

                }
            }
        )
    }

    //////////////////////////////////////////////////////////////////
    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }


}