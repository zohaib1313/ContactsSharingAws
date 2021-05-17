package lads.contancsharing.www.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.UserContactSharing
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.activities.SignUpActivity
import lads.contancsharing.www.databinding.FragmentProfileBinding
import lads.contancsharing.www.utils.Helper
import java.io.File


class ProfileFragment : BaseFragment() {

    lateinit var mBinding: FragmentProfileBinding
    var currentUser: UserContactSharing? = null
    var imageKey: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(layoutInflater)
        loadingLayout = mBinding.pgb.rlLoading
        currentUser = sessionManager.user
        mBinding.btnSignOut.setOnClickListener {
            signOutUser()
        }


        printLog("user " + currentUser.toString())


        currentUser?.let {
            if (!it.image.isNullOrEmpty()) {
                printLog(
                    Helper.getImageUrl(it.image)
                )
                Glide.with(requireContext()).load(Helper.getImageUrl(it.image))
                    .placeholder(R.drawable.eclipse)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mBinding.circleImageView)
            }
            mBinding.textView4.setText(it.name.toString())
            imageKey = it.image

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
                            printLog(file?.path.toString())
                            //You can also get File Path from intent
                            file?.let {
                                printLog("file not null")
                                uploadImage(it)
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
        mBinding.button2.setOnClickListener {
            mBinding.textView4.isEnabled = !mBinding.textView4.isEnabled
            if (mBinding.textView4.isEnabled) {
                mBinding.textView4.requestFocus()
            }

            if (mBinding.button2.text == "save") {
                mBinding.button2.text = "update"

                updateProfile()

            } else {
                hideLoading()
                mBinding.button2.text = "save"
            }


        }



        return mBinding.root
    }

    private fun updateProfile() {


        if (mBinding.textView4.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Name should not be empty", Toast.LENGTH_LONG).show()
            return
        }
        showLoading()



        currentUser?.let { useR ->
            val userContactSharing = UserContactSharing.builder()
                .name(mBinding.textView4.text.toString().trim())
                .phone(useR.phone)
                .deviceToken(useR.deviceToken)
                .countryCode(useR.countryCode)
                .id(useR.id)
                .image(imageKey)
                .build()


            Amplify.API.mutate(
                ModelMutation.update(userContactSharing),
                {
                    printLog("Added  user : ${it}")
                    if (it.hasErrors()) {
                        ThreadUtils.runOnUiThread() {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), "User updation Failed ", Toast
                                    .LENGTH_LONG
                            ).show()
                        }
                    } else {
                        sessionManager.updateUserSession(userContactSharing)
                        ThreadUtils.runOnUiThread() {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), "User updated", Toast
                                    .LENGTH_LONG
                            ).show()
                        }
                    }
                },
                {
                    printLog("User update Failed DataStore: ${it.cause}")
                    ThreadUtils.runOnUiThread() {
                        hideLoading()
                        Toast.makeText(
                            requireContext(), "User Updation Failed ${it.cause}", Toast
                                .LENGTH_LONG
                        ).show()
                    }
                }
            )


        }


        // userContactSharing?.name=


    }

    private fun uploadImage(file: File) {
        showLoading()
        //delete previous
        currentUser?.let { user ->
            Amplify.Storage.remove(user.image,
                {
                    printLog(user.image.toString() +" old picture removed")
                },
                { printLog("remove failuer ${it.cause}") }
            )
        }

        //upload new
        val profileImagePath =
            FirebaseAuth.getInstance().currentUser.phoneNumber + "/profileImage" + "/" + file.name
//        val bucketName = "contactsharing113011-dev."
//        imageS3Url = "https://"+bucketName+"s3.amazonaws.com/"+"public/"+profileImagePath
//        imageS3Url= imageS3Url.replace("+","%2B")
        // Toast.makeText(requireContext(),imageS3Url,Toast.LENGTH_LONG).show()
        printLog(profileImagePath)
        Amplify.Storage.uploadFile(profileImagePath, file,
            {
                printLog("image uploaded key= ${it.key}")

                imageKey = it.key
                hideLoading()
                updateProfile()
            },
            {
                hideLoading()
                printLog("Upload failed=  ${it.cause}")
            }
        )

    }

    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        sessionManager.clearSession()
        Helper.startActivity(
            requireActivity(),
            Intent(requireContext(), SignUpActivity::class.java),
            true
        )


    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
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
}