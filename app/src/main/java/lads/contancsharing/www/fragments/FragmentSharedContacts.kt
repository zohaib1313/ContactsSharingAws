package lads.contancsharing.www.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphabetik.Alphabetik
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.google.gson.Gson
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.ContactsDownloadViewActivity
import lads.contancsharing.www.adapters.AdapterContactsShared
import lads.contancsharing.www.callBacks.OnItemClickListener

import lads.contancsharing.www.databinding.FragmentSharedContactsBinding
import lads.contancsharing.www.models.MessageEvent
import lads.contancsharing.www.models.ModelSharingContactWith
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class FragmentSharedContacts : BaseFragment() {

    lateinit var mBinding: FragmentSharedContactsBinding
    private lateinit var rvReceivedContacts: RecyclerView
    private lateinit var adapterContactsShared: AdapterContactsShared
    private var dataListAdapterItem = ArrayList<ModelSharingContactWith>()
    private var dataListAllContactsWith = ArrayList<ModelSharingContactWith>()
    private var dataListFilteredContacts = ArrayList<ModelSharingContactWith>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSharedContactsBinding.inflate(layoutInflater)
        lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())

        rvReceivedContacts = mBinding.rvContacts
        loadingLayout = mBinding.loadingLayout.rlLoading

        dataListAdapterItem.clear()
        dataListAllContactsWith.clear()
        rvReceivedContacts.layoutManager = LinearLayoutManager(requireContext())
        adapterContactsShared =
            AdapterContactsShared(requireContext(), dataListAdapterItem)
        rvReceivedContacts.adapter = adapterContactsShared
        adapterContactsShared.notifyDataSetChanged()
        adapterContactsShared.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                if (view.id == R.id.btnViewDownload) {
                    printLog("radio selected")


                    val intent = Intent(requireContext(), ContactsDownloadViewActivity::class.java)
                    val item = dataListAdapterItem[position]



                    printLog(item.sharingWithCloudModel.filePath)
                   // intent.putExtra(AppConstant.KEY_DATA, Gson().toJson(item.sharingWithCloudModel))

                    intent.putExtra("keyName",item.sharingWithCloudModel.user.name)
                    intent.putExtra("keyImage",item.sharingWithCloudModel.user.image)
                    intent.putExtra("keyFilePath",item.sharingWithCloudModel.filePath)


                    Helper.startActivity(requireActivity(), intent, false)
                }

            }
        })


        getListOfShredContacts()
        val alphabetRv = mBinding.alphSectionIndex
        alphabetRv.onSectionIndexClickListener(Alphabetik.SectionIndexClickListener { view, position, character ->
            val info = " Position = $position Char = $character"
            Log.d("Tagggg ", "$view,$info")
            mBinding.rvContacts.scrollToPosition(getPositionFromData(character))
        })
        return mBinding.root
    }

    private fun getListOfShredContacts() {
        showLoading()
        try {

            val request = ModelQuery.list(
                ContactSharingWith::class.java,
                ContactSharingWith.USER_ID.eq(sessionManager.user.id)
            )
            printLog(sessionManager.user.id)
            Amplify.API
                .query(
                    request, { response ->

                        ///received contacts
                        if (response.hasData() && !response.hasErrors()) {
                            printLog("no error in response and has data")

                            response.data.items.forEach { item ->

                                if (item.userId == sessionManager.user.id) {
                                    //   dataListReceivedContacts.add(item)
                                    printLog(item.toString())
                                    getFileSizeAndAddToList(item)
                                }
                            }


                        } else {
                            //no data found
                            printLog("no data found for contacts received")
                            ThreadUtils.runOnUiThread() {
                                hideLoading()
                                Toast.makeText(
                                    requireContext(), "No Contacts Received ", Toast
                                        .LENGTH_LONG
                                ).show()
                            }
                        }
                    }, {
                        //no data found
                        printLog("no data found api exception ${it.cause}")
                        ThreadUtils.runOnUiThread() {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), "No Contacts Received ", Toast
                                    .LENGTH_LONG
                            ).show()
                        }
                    }
                )

        } catch (error: ApiException) {
            //no data found
            printLog("no data found api exception ${error.cause}")
            ThreadUtils.runOnUiThread() {
                hideLoading()
                Toast.makeText(
                    requireContext(), "No Contacts Received ", Toast
                        .LENGTH_LONG
                ).show()
            }
        }


    }

    private fun getFileSizeAndAddToList(item: ContactSharingWith?) {

        item?.let { contactSharingWith ->
            var folderName: String? = contactSharingWith.filePath.substringBeforeLast("/")
            folderName = folderName + "/"

            printLog(contactSharingWith.filePath)

            try {

                Amplify.Storage.list(
                    folderName.toString(),
                    { result ->
                        result.items.forEach { file ->
                            if (file.key == contactSharingWith.filePath) {
                                printLog(file.key)
                                dataListAdapterItem.add(
                                    ModelSharingContactWith(
                                        contactSharingWith,
                                        file.size.toString()
                                    )
                                )
                                dataListAllContactsWith.add(
                                    ModelSharingContactWith(
                                        contactSharingWith,
                                        file.size.toString()
                                    )
                                )

                            }
                            ThreadUtils.runOnUiThread() {
                                hideLoading()
                                adapterContactsShared.notifyDataSetChanged()
                            }
                        }


                    },
                    { error -> printLog("error in getting size ${error.cause}") }
                )

            } catch (e: Exception) {
                printLog("error in getting size ${e.cause}")
            }

        }


    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): FragmentReceivedContacts {
            val fragment = FragmentReceivedContacts()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

    private fun getPositionFromData(character: String): Int {
        var position = 0;
        for (contact in dataListAdapterItem) {
            val letter = contact.sharingWithCloudModel.user.name[0]
            if (letter.equals(character[0], true)) {
                return position;
            }
            position++;
        }
        return position
    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        event?.let {
            printLog(it.searchString + " shared fragment")
            //search from list

            searchFromList(it.searchString.trim())
        }
    }

    private fun searchFromList(newText: String) {
        showLoading()
        dataListFilteredContacts.clear()
        dataListAllContactsWith.forEach { info ->
            if (info.sharingWithCloudModel.user.name.toLowerCase(Locale.ROOT).contains(
                    newText.toLowerCase(Locale.ROOT)
                        .toString()
                )
            ) {
                dataListFilteredContacts.add(info)

            }
        }

        hideLoading()
        if (dataListFilteredContacts.isNotEmpty()) {
            dataListAdapterItem.clear()
            dataListAdapterItem.addAll(dataListFilteredContacts)
            adapterContactsShared.notifyDataSetChanged()
        }
    }

}