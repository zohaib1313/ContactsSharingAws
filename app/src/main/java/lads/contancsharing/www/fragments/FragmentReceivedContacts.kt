package lads.contancsharing.www.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphabetik.Alphabetik
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.amplifyframework.datastore.generated.model.UserContactSharing
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import lads.contancsharing.www.R
import lads.contancsharing.www.activities.ContactsDownloadViewActivity
import lads.contancsharing.www.adapters.AdapterReceivedContacts
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.FragmentReceivedContactsBinding

import lads.contancsharing.www.models.MessageEvent
import lads.contancsharing.www.models.ModelReceivedContacts
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


class FragmentReceivedContacts : BaseFragment() {

    lateinit var mBinding: FragmentReceivedContactsBinding
    private lateinit var rvReceivedContacts: RecyclerView
    private lateinit var adapterReceivedContacts: AdapterReceivedContacts

    private var dataListAdapterItem = ArrayList<ModelReceivedContacts>()
    private var dataListAllContactsWith = ArrayList<ModelReceivedContacts>()
    private var dataListFilteredContacts = ArrayList<ModelReceivedContacts>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReceivedContactsBinding.inflate(layoutInflater)
        rvReceivedContacts = mBinding.rvReceivedContacts
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        rvReceivedContacts.layoutManager = LinearLayoutManager(requireContext())
        dataListAdapterItem.clear()
        dataListAllContactsWith.clear()
        adapterReceivedContacts =
            AdapterReceivedContacts(requireContext(), dataListAdapterItem)
        rvReceivedContacts.adapter = adapterReceivedContacts
        adapterReceivedContacts.notifyDataSetChanged()
        adapterReceivedContacts.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                if (view.id == R.id.btnViewDownload) {
                    printLog("radio selected")


                    val intent = Intent(requireContext(), ContactsDownloadViewActivity::class.java)
                    val item = dataListAdapterItem[position]

                    //   intent.putExtra(AppConstant.KEY_DATA, Gson().toJson(item.contactSharingWith))
                    intent.putExtra("keyName", item.user.name)
                    intent.putExtra("keyImage", item.user.image)
                    intent.putExtra("keyFilePath", item.contactSharingWith.filePath)


                    Helper.startActivity(requireActivity(), intent, false)
                }

            }
        })

        lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())

        getListOfReceivedContacts()

        val alphabetRv = mBinding.alphSectionIndex
        alphabetRv.onSectionIndexClickListener(Alphabetik.SectionIndexClickListener { view, position, character ->
            val info = " Position = $position Char = $character"
            Log.d("Tagggg ", "$view,$info")
            if (dataListAdapterItem.isNotEmpty()) {
                hideNoDataLayout()
                mBinding.rvReceivedContacts.scrollToPosition(getPositionFromData(character))
            } else {
                showNoDataLayout()
                hideLoading()
            }

        })

        return mBinding.root
    }

    private fun getListOfReceivedContacts() {
        ThreadUtils.runOnUiThread {
            showLoading()
            hideNoDataLayout()
        }
        try {
            printLog(sessionManager.user.phone)
            val request = ModelQuery.list(ContactSharingWith::class.java)
            printLog(sessionManager.user.id)
            Amplify.API
                .query(
                    request, { response ->
                        ///received contacts
                        if (response.hasData() && !response.hasErrors()) {
                            var userFound = false
                            printLog("no error in response")
                            response.data.items.forEach { item ->
                                if (item.user.id == sessionManager.user.id) {
                                    //   dataListReceivedContacts.add(item)
                                    printLog("id matched...")
                                    printLog(item.toString())
                                    getFileSizeAndAddToList(item)
                                    userFound = true
                                    return@forEach
                                }
                            }


                            if (!userFound) {
                                ThreadUtils.runOnUiThread() {
                                    printLog("outside thread for each...")
                                    hideLoading()
                                    if (dataListAdapterItem.isNotEmpty()) {
                                        hideNoDataLayout()
                                    } else {
                                        showNoDataLayout()
                                    }
                                    adapterReceivedContacts.notifyDataSetChanged()
                                }
                            }

                        } else {
                            //no data found
                            printLog("no data found for contacts received")
                            ThreadUtils.runOnUiThread() {
                                hideLoading()
                                showNoDataLayout()
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
                            showNoDataLayout()
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
                showNoDataLayout()
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
            folderName = "$folderName/"
            printLog(contactSharingWith.filePath)
            try {
                Amplify.Storage.list(
                    folderName.toString(),
                    { result ->
                        var isFileFound = false
                        result.items.forEach { file ->
                            if (file.key == contactSharingWith.filePath) {
                                printLog("file found...")

                                val request = ModelQuery.list(
                                    UserContactSharing::class.java,
                                    UserContactSharing.ID.eq(contactSharingWith.userId)
                                )
                                Amplify.API.query(request, { response ->
                                    if (response.hasData()) {
                                        response.data.forEach { user ->
                                            dataListAdapterItem.add(
                                                ModelReceivedContacts(
                                                    user,
                                                    file.size.toString(),
                                                    file.lastModified.toString(), item
                                                )
                                            )
                                            dataListAllContactsWith.add(
                                                ModelReceivedContacts(
                                                    user,
                                                    file.size.toString(),
                                                    file.lastModified.toString(), item
                                                )
                                            )
                                            ThreadUtils.runOnUiThread {
                                                printLog("user added to rv")
                                                adapterReceivedContacts.notifyDataSetChanged()

                                            }
                                        }

                                        ThreadUtils.runOnUiThread {
                                            if (dataListAdapterItem.isEmpty()) {
                                                showNoDataLayout()
                                                hideLoading()
                                            } else {
                                                hideLoading()
                                                hideNoDataLayout()
                                            }
                                        }
                                    } else {
                                        ThreadUtils.runOnUiThread {
                                            hideLoading()
                                            showNoDataLayout()
                                        }
                                    }

                                }, {

                                    ThreadUtils.runOnUiThread {
                                        hideLoading()
                                        showNoDataLayout()
                                    }
                                })
                                isFileFound = true
                                return@forEach
                            }

                        }
                        if (!isFileFound) {
                            ThreadUtils.runOnUiThread {
                                hideLoading()
                                showNoDataLayout()
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
            printLog(it.searchString + " receive fragment")
            //search from list

            searchFromList(it.searchString.trim())
        }
    }

    private fun searchFromList(newText: String) {
        showLoading()
        dataListFilteredContacts.clear()
        dataListAllContactsWith.forEach { info ->
            if (info.user.name.toLowerCase(Locale.ROOT).contains(
                    newText.toLowerCase(Locale.ROOT)
                        .toString()
                )
            ) {
                dataListFilteredContacts.add(info)

            }
        }

        hideLoading()
        if (dataListFilteredContacts.isNotEmpty()) {
            hideNoDataLayout()
            dataListAdapterItem.clear()
            dataListAdapterItem.addAll(dataListFilteredContacts)
            adapterReceivedContacts.notifyDataSetChanged()
        } else {

            showNoDataLayout()
        }
    }

    private fun getPositionFromData(character: String): Int {
        var position = 0;
        for (contact in dataListAdapterItem) {
            val letter = contact.user.name[0]
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

    override fun onResume() {
        super.onResume()
        Helper.hideKeyboard(requireActivity())
        dataListFilteredContacts.clear()
        dataListAdapterItem.clear()
        dataListAdapterItem.addAll(dataListAllContactsWith)
        adapterReceivedContacts.notifyDataSetChanged()


    }
}