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
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.FragmentVerifyOtpBinding
import java.util.concurrent.TimeUnit


class VerifyOtpFragment(var verificationId: String) : BaseFragment() {

    lateinit var mBinding: FragmentVerifyOtpBinding

    //Progress Dialog
    private lateinit var progressDialog: ProgressDialog
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
        mBinding.btnDone.setOnClickListener {

            if (mBinding.pinview.value.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Enter OTP sent", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
            verifyPhoneNumberThroughCode(verificationId, mBinding.pinview.value)

        }

        mBinding.ivBack.setOnClickListener {
            requireFragmentManager().popBackStack()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        return mBinding.root
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int, verificationId: String): VerifyOtpFragment {
            val fragment = VerifyOtpFragment(verificationId)
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }


    private fun verifyPhoneNumberThroughCode(verificationId: String?, code: String) {

        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId.toString(), code)
        signInWithPhoneAuthCredential(credential)

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ")

        progressDialog.setMessage("Logging In")
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
                progressDialog.dismiss()
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
                    progressDialog.dismiss()

                    printLog("id= ${mobileClient.identityId}")
                    printLog("userDetails= ${userStateDetails?.details}")




                    changeFragment(ProfileInfoFragment.newInstance(0), true);

                }

                override fun onError(e: Exception?) {
                    progressDialog.dismiss()
                    if (e != null) {
                        printLog("Error aws:: " + e.message)
                    }
                }
            }
        )
    }
}