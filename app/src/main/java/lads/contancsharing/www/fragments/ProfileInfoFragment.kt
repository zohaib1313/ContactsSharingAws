package lads.contancsharing.www.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.UserContactSharing
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker

import com.google.firebase.auth.FirebaseAuth

import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity

import lads.contancsharing.www.databinding.FragmentProfileInfoBinding
import lads.contancsharing.www.utils.Helper
import java.io.File


class ProfileInfoFragment : BaseFragment() {

    lateinit var mBinding: FragmentProfileInfoBinding
    lateinit var firebaseAuth: FirebaseAuth
    var filePath: String? = null
    var imagekey: String? = null
    var deviceToken: String? = null
    var userExists = false
    var name: String? = null
    var userContactSharing: UserContactSharing.Builder? = null
    lateinit var userPhoneNumber: String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        printLog("profile info fragment")
        mBinding = FragmentProfileInfoBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        firebaseAuth = FirebaseAuth.getInstance()
        userContactSharing = UserContactSharing.Builder()
        userPhoneNumber =
            firebaseAuth.currentUser.phoneNumber.toString().replace("-", "").replace(" ", "")


        ////saving token in sharedPref
        Helper.refreshFcmToken(requireContext())
        getDeviceToken()
        checkIfUserExists()

        mBinding.btnDone.setOnClickListener {
            if (mBinding.textView3.text.isNullOrEmpty()) {
                mBinding.textView3.error = getString(
                    R.string.error_form_empty_field_of,
                    "User Name"
                )
                return@setOnClickListener
            }
            name = mBinding.textView3.text.toString()
            lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
            showLoading()
            uploadImage()

            printLog("show loading")
        }


        mBinding.imageView.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            //Image Uri will not be null for RESULT_OK
                            val fileUri = data?.data
                            mBinding.circleImageView.setImageURI(fileUri)
                            //You can get File object from intent
                            val file: File? = ImagePicker.getFile(data)
                            //You can also get File Path from intent

                            ImagePicker.getFilePath(data)?.let {
                                filePath = it
                            }

                        }
                        ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                ImagePicker.getError(data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }


        return mBinding.root
    }

    private fun checkIfUserExists() {
        showLoading()
        val request =
            ModelQuery.list(UserContactSharing::class.java)
        Amplify.API.query(request, { response ->
            if (response.hasData()) {
                printLog("response has data and checking user..")
                var userFound = false
                response.data.items.forEach { userO ->
                    if (userO.phone.equals(userPhoneNumber.toString())) {
                        runOnUiThread {
                            printLog("user already exists ${userO.toString()}")
                            hideLoading()
                            userExists = true
                            userContactSharing!!.id(userO.id)
                            userO.name?.let {
                                mBinding.textView3.setText(it.toString())
                            }

                            userO.image?.let {
                                Glide.with(requireContext())
                                    .load(lads.contancsharing.www.utils.Helper.getImageUrl(it.toString()))
                                    .placeholder(R.drawable.eclipse)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(mBinding.circleImageView)
                            }
                        }
                        userFound = true
                        return@forEach
                    } else {
                        printLog("user does not  exists already")
                        runOnUiThread {
                            hideLoading()
                        }
                    }
                }
                if (!userFound) {
                    printLog("No user exists")
                    runOnUiThread {
                        hideLoading()
                    }
                }
            } else {
///error
                printLog("response has no data error")
                runOnUiThread {
                    hideLoading()
                }
            }
        }, {
            printLog("checking user exception ${it.cause}")

            runOnUiThread {
                hideLoading()
            }
        })
    }

    private fun getDeviceToken() {

        deviceToken = if (sessionManager.token != null) {
            sessionManager.token
        } else {
            Helper.refreshFcmToken(requireContext())
            "null"
        }
    }


    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ProfileInfoFragment {
            val fragment = ProfileInfoFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

    private fun uploadImage() {

        if (filePath != null) {
            val myfie = File(filePath.toString())
            if (!myfie.exists()) {
                myfie.mkdir()
            }

            val profileImagePath =
                userPhoneNumber + "/profileImage" + "/" + myfie.name
//        val bucketName = "contactsharing113011-dev."
//        imageS3Url = "https://"+bucketName+"s3.amazonaws.com/"+"public/"+profileImagePath
//        imageS3Url= imageS3Url.replace("+","%2B")
            // Toast.makeText(requireContext(),imageS3Url,Toast.LENGTH_LONG).show()
            Amplify.Storage.uploadFile(profileImagePath, myfie,
                {
                    printLog("image uploaded key= ${it.key}")
                    imagekey = it.key
                    uploadDataToDataStore()
                },
                {
                    printLog("Upload failed=  ${it.cause}")
                }
            )
        } else {
            uploadDataToDataStore()
        }


    }

    private fun uploadDataToDataStore() {

        val countryCode = userPhoneNumber?.substring(0, 3)
        printLog("country code= " + countryCode)

        // userContactSharing!!.id(phoneNumber)
        userContactSharing!!.name(name)
        userContactSharing!!.phone(userPhoneNumber)
        userContactSharing!!.countryCode(countryCode)
        imagekey?.let {
            userContactSharing!!.image(imagekey)
        }

        deviceToken?.let {
            userContactSharing!!.deviceToken(deviceToken)
        }


        val request =
            ModelQuery.list(UserContactSharing::class.java)
        Amplify.API.query(request, { response ->
            if (response.hasData() && !response.hasErrors()) {

                var userExists = false
                response.data.items.forEach { userO ->
                    if (userO.phone.equals(userPhoneNumber)) {
                        printLog("user already exists")
                        userExists = true
                        userContactSharing!!.id(userO.id)
                        return@forEach
                    }
                }


                val user = userContactSharing!!.build()

                if (userExists) {
                    printLog("user exists")
                    Amplify.API.mutate(
                        ModelMutation.update(user),
                        {
                            printLog("Added  user : ${it}")
                            if (it.hasErrors()) {
                                runOnUiThread() {
                                    hideLoading()
                                    Toast.makeText(
                                        requireContext(), "User Updation Failed ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                                return@mutate
                            } else {

                                sessionManager.createUserLoginSession(user)

                                lads.contancsharing.www.utils.Helper.startActivity(
                                    requireActivity(),
                                    Intent(requireContext(), MainActivity::class.java),
                                    true
                                )
                                requireActivity().finishAffinity()
                            }
                        },
                        {
                            printLog("User Add Failed DataStore: ${it.cause}")
                            runOnUiThread() {
                                hideLoading()
                                Toast.makeText(
                                    requireContext(), "User Creation Failed ${it.cause}", Toast
                                        .LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                } else {
                    printLog("user does not exists")
                    Amplify.API.mutate(
                        ModelMutation.create(user),
                        {
                            printLog("Added  with id: ${it.data.toString()}")
                            if (it.hasErrors()) {
                                runOnUiThread() {
                                    Toast.makeText(
                                        requireContext(), "User Creation Failed ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                                return@mutate
                            } else {

                                sessionManager.createUserLoginSession(user)


                                lads.contancsharing.www.utils.Helper.startActivity(
                                    requireActivity(),
                                    Intent(requireContext(), MainActivity::class.java),
                                    true
                                )
                                requireActivity().finishAffinity()
                            }
                        },
                        {
                            printLog("User Add Failed DataStore: ${it.cause}")
                            runOnUiThread() {
                                Toast.makeText(
                                    requireContext(), "User Creation Failed ${it.cause}", Toast
                                        .LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }


            } else {
///error
                runOnUiThread {
                    hideLoading()
                    Toast.makeText(
                        requireContext(), "User Creation Failed", Toast
                            .LENGTH_LONG
                    ).show()
                }
            }
        }, {
            runOnUiThread {
                hideLoading()
                Toast.makeText(
                    requireContext(), "User Creation Failed ${it.cause}", Toast
                        .LENGTH_LONG
                ).show()
            }
        })


    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.mainActivityFragmentContainer, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }
}