package lads.contancsharing.www.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.alphabetik.Alphabetik.SectionIndexClickListener
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.amplifyframework.datastore.generated.model.UserContactSharing
import com.ferfalk.simplesearchview.SimpleSearchView
import de.hdodenhof.circleimageview.CircleImageView
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.ContactListRecyclerViewAdapter
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.FragmentContactsShareToBinding


import lads.contancsharing.www.models.ContactsInfo
import lads.contancsharing.www.utils.Helper
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ContactsShareToFragment(var listOfContactsToShare: ArrayList<ContactsInfo>) : BaseFragment() {
    private var isSearchOn = false
    private val PERMISSIONS_REQUEST_WRITE_CONTACTS: Int = 14
    lateinit var mBinding: FragmentContactsShareToBinding
    lateinit var adapterContactListRecyclerViewAdapter: ContactListRecyclerViewAdapter
    var listOfContacts = ArrayList<ContactsInfo>()
    var listOfAllContacts = ArrayList<ContactsInfo>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentContactsShareToBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading

        mBinding.btnSearch.setOnClickListener {
            mBinding.searchView.showSearch(true)
        }

        mBinding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {

                searchFromList(newText)
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                hideLoading()
                listOfContacts.clear()
                listOfContacts.addAll(listOfAllContacts)
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {


                return true
            }

        })

        mBinding.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                listOfContacts.clear()
                listOfContacts.addAll(listOfAllContacts)
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onSearchViewClosedAnimation() {

            }

            override fun onSearchViewShown() {

            }

            override fun onSearchViewShownAnimation() {

            }

        })



        adapterContactListRecyclerViewAdapter =
            ContactListRecyclerViewAdapter(requireContext(), listOfContacts, true)
        mBinding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvContacts.adapter = adapterContactListRecyclerViewAdapter
        adapterContactListRecyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {

                showShareDialog(listOfContacts[position])

            }
        })



        mBinding.rvContacts.setHasFixedSize(true)
        val alphabetRv = mBinding.alphSectionIndex
        alphabetRv.onSectionIndexClickListener(SectionIndexClickListener { view, position, character ->
            val info = " Position = $position Char = $character"
            Log.d("Tagggg ", "$view,$info")
            mBinding.rvContacts.scrollToPosition(getPositionFromData(character))
        })

        requestContactPermission()
        listOfContacts.sortedWith(compareBy { it.name })
        adapterContactListRecyclerViewAdapter.notifyDataSetChanged()



        return mBinding.root
    }

    private fun showShareDialog(contactsInfo: ContactsInfo) {
        //custom layout
        val view: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmation, null)

        val yesBtn = view.findViewById<TextView>(R.id.btnYes)
        val noBtn = view.findViewById<TextView>(R.id.btnNo)
        val headerTitle = view.findViewById<TextView>(R.id.titleHeader)
        val rec_name = view.findViewById<TextView>(R.id.tvName)
        val rec_phone = view.findViewById<TextView>(R.id.tvNumber)
        val ivDp = view.findViewById<CircleImageView>(R.id.ivContactDp)
        val tvDp = view.findViewById<TextView>(R.id.tvContact)
        val ivCross = view.findViewById<ImageView>(R.id.ivCross)

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show()

        rec_name.text = contactsInfo.name
        rec_phone.text = contactsInfo.number
        headerTitle.text =
            "Are you sure you want to sent " + listOfContactsToShare.size + " contacts to "
        try {
            if (contactsInfo.photo == "") {
                ivDp.visibility = View.GONE
                tvDp.visibility = View.VISIBLE
                val str: String = contactsInfo.name.toString()
                val strArray = str.split(" ".toRegex()).toTypedArray()
                val builder = StringBuilder()
//First name
                if (strArray.isNotEmpty()) {
                    builder.append(strArray[0], 0, 1)
                }
//Middle name
                //Middle name
                if (strArray.size > 1) {
                    builder.append(strArray[1], 0, 1)
                }
//Surname
                //Surname
                if (strArray.size > 2) {
                    builder.append(strArray[2], 0, 1)
                }

                tvDp.background = contactsInfo.drawable
                tvDp.text = builder.toString()

            } else {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        Uri.parse(contactsInfo.photo)
                    )
                ivDp.visibility = View.VISIBLE
                tvDp.visibility = View.GONE
                ivDp.setImageBitmap(bitmap)

            }
        } catch (e: Exception) {
        }




        yesBtn.setOnClickListener {

            saveFile(
                Helper.exportDataToCSV(listOfContactsToShare),
                contactsInfo.number,
                contactsInfo.name
            )

            dialog.dismiss()
        }

        noBtn.setOnClickListener {

            dialog.dismiss()
        }
        ivCross.setOnClickListener { dialog.dismiss() }

    }

    private fun searchFromList(newText: String) {
        showLoading()
        val listOfFilteredContacts = ArrayList<ContactsInfo>()
        listOfAllContacts.forEach { contactsInfo ->
            if (contactsInfo.name.toLowerCase(Locale.ROOT).contains(
                    newText.toLowerCase(Locale.ROOT)
                        .toString()
                )
            ) {
                listOfFilteredContacts.add(contactsInfo)

            }
        }

        hideLoading()
        if (listOfFilteredContacts.isNotEmpty()) {
            listOfContacts.clear()
            listOfContacts.addAll(listOfFilteredContacts)
            adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun saveFile(
        csvData: String,
        receiverNumber: String,
        receiverName: String
    ) {
        showLoading()
        printLog("upload started .....$csvData")
        val fileName = Date().time.toString() + ".csv"
        val contactFile = File(requireActivity().filesDir, fileName)
        contactFile.writeText(csvData)
        val pathOnBucket = sessionManager.user.phone + "/" + fileName
        Amplify.Storage.uploadFile(pathOnBucket, contactFile,
            {
                printLog("folder path after upload= ${it.key}")
                uploadUserData(receiverNumber, it.key, receiverName)
            },
            {
                printLog("upload failed ${it.cause}")
                hideLoading()
                runOnUiThread {
                    Toast.makeText(requireContext(), "Uploding failed", Toast.LENGTH_LONG).show()
                }

            }
        )


        // Toast.makeText(requireActivity(), "File Exported Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun uploadUserData(
        receiverNumber: String,
        keyOfSharedContacts: String,
        receiverName: String
    ) {

        val request = ModelQuery.list(
            UserContactSharing::class.java,
            UserContactSharing.PHONE.eq(receiverNumber)
        )


        Amplify.API.query(request,
            { response ->
                printLog("api response = ${response.data.toString()}")
                if (response.hasData()) {

                    var shareWith: ContactSharingWith? = null
                    var count = 0
                    for (user in response.data.items) {
                        printLog("user Exists")
                        shareWith = ContactSharingWith.builder()
                            .userId(sessionManager.user.id)
                            .filePath(keyOfSharedContacts)
                            .shareWith(receiverNumber)
                            .fileTime(Date().time.toString())
                            .user(user)
                            .status("no")
                            .build()
                        count++

                    }
                    if (count != 0) {

                        Amplify.API.mutate(
                            ModelMutation.create(shareWith!!),
                            {
                                printLog("file added ")
                                runOnUiThread() {
                                    hideLoading()
                                    requireFragmentManager().popBackStack()
                                    Toast.makeText(
                                        requireContext(), "Upload Successfully ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                            },
                            {
                                printLog("creation failed ${it.cause}")

                                runOnUiThread() {
                                    hideLoading()
                                    Toast.makeText(
                                        requireContext(), "uploading failed ${it.cause} ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }

                            }
                        )
                    } else {
                        printLog("user does nt exists")
                        //user didnot exist
                        val userContactSharing = UserContactSharing.builder()
                            .name(receiverName)
                            .phone(receiverNumber)
                            .deviceToken("null")
                            .countryCode("+92")
                            .image("null")
                            .build()
                        Amplify.API.mutate(
                            ModelMutation.create(userContactSharing),
                            {
                                printLog("user creating....")
                                shareWith = ContactSharingWith.builder()
                                    .userId(sessionManager.user.id)
                                    .filePath(keyOfSharedContacts)
                                    .shareWith(receiverNumber)
                                    .fileTime(Date().time.toString())
                                    .user(userContactSharing)
                                    .status("no")
                                    .build()
                                Amplify.API.mutate(
                                    ModelMutation.create(shareWith!!),
                                    {
                                        printLog("file added ")
                                        runOnUiThread() {
                                            hideLoading()



                                            changeFragment(ContactsFragment(), false)


                                            Toast.makeText(
                                                requireContext(), "Upload Successfully ", Toast
                                                    .LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                    {
                                        printLog("creation failed ${it.cause}")
                                        runOnUiThread() {
                                            hideLoading()
                                            Toast.makeText(
                                                requireContext(),
                                                "uploading failed ${it.cause} ",
                                                Toast
                                                    .LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            },
                            {
                                printLog("creation failed ${it.cause}")
                                runOnUiThread() {
                                    hideLoading()
                                    Toast.makeText(
                                        requireContext(), "uploading failed ${it.cause} ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                            }
                        )


                    }

                }
            },
            { Log.e("MyAmplifyApp", "Query failed", it) }
        )


    }


    companion object {
        private val ARG_DATA = "position"
        fun newInstance(
            index: Int,
            listOfContactsToShare: ArrayList<ContactsInfo>
        ): ContactsShareToFragment {
            val fragment = ContactsShareToFragment(listOfContactsToShare)

            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

    private fun getPositionFromData(character: String): Int {
        var position = 0;
        for (contact in listOfContacts) {
            val letter = contact.name[0]
            if (letter.equals(character[0], true)) {
                return position;
            }
            position++;
        }
        return position
    }

    private fun getContactsFromContactsList() {
        val contacts = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (contacts != null) {
            while (contacts.moveToNext()) {
                val name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).toString().replace("-","").replace(" ","")
                var image =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

                if (image.isNullOrEmpty()) {
                    image = ""
                }
                if (name.isNotEmpty() && number.isNotEmpty()) {
                    val contact = ContactsInfo(name, number, image, false, getRandomDrawable())
                    listOfContacts.add(contact)
                }
            }


            val list = listOfContacts.sortedWith(compareBy { it.name })
            listOfContacts.clear()
            listOfContacts.addAll(list)
            listOfAllContacts.addAll(listOfContacts)

            contacts.close()

        }
    }

    private fun getRandomDrawable(): Drawable {
        val r = Random()
        val red = r.nextInt(255 - 0 + 1) + 0
        val green = r.nextInt(255 - 0 + 1) + 0
        val blue = r.nextInt(255 - 0 + 1) + 0
        val draw = GradientDrawable()
        draw.shape = GradientDrawable.OVAL
        draw.setColor(Color.rgb(red, green, blue))
        return draw
    }

    fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.WRITE_CONTACTS
                    )
                ) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Write Contacts permission")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("Please enable access to contacts.")
                    builder.setOnDismissListener(DialogInterface.OnDismissListener {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.READ_CONTACTS
                            ),
                            PERMISSIONS_REQUEST_WRITE_CONTACTS
                        )
                    })
                    builder.show()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_CONTACTS
                        ),
                        PERMISSIONS_REQUEST_WRITE_CONTACTS
                    )
                }
            } else {
                getContactsFromContactsList()

            }
        } else {
            getContactsFromContactsList()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_CONTACTS -> if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                getContactsFromContactsList()
            } else {
                //not granted
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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