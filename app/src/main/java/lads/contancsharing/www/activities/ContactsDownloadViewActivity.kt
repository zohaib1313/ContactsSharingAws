package lads.contancsharing.www.activities

import android.Manifest
import android.content.ContentProviderOperation
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.bumptech.glide.Glide
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_contacts_download_view.*
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.ContactListRecyclerViewAdapter
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.ActivityContactsDownloadViewBinding
import lads.contancsharing.www.models.ContactsInfo

import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class ContactsDownloadViewActivity : BaseActivity() {


    lateinit var layoutBottomSheet: RelativeLayout

    lateinit var mBinding: ActivityContactsDownloadViewBinding
    private var senderUser: ContactSharingWith? = null
    private lateinit var rvContacts: RecyclerView
    lateinit var adapterContactListRecyclerViewAdapter: ContactListRecyclerViewAdapter
    var listOfContacts = ArrayList<ContactsInfo>()
    var listOfAllLocalContacts = ArrayList<ContactsInfo>()

    var listOfSelectedContacts = ArrayList<ContactsInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityContactsDownloadViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initRv()
        layoutBottomSheet = mBinding.bottomSheett
        loadingLayout = mBinding.loadingLayout.rlLoading

        hideBottomSheet()

        mBinding.backButton.setOnClickListener {
            onBackPressed()
        }


//        intent.getStringExtra(AppConstant.KEY_DATA)?.let { jsonObject ->
//            senderUser = Gson().fromJson(jsonObject, ContactSharingWith::class.java)
//
//            senderUser?.let {
//                it.user?.let { otherUser ->
//                    printLog(otherUser.name)
//
//                    otherUser.image?.let { img ->
//
//                    }
//                }
//                //   printLog(it.filePath.toString())
//                it.filePath?.let { filePath ->
//
//                }
//            }
//        }


        intent.getStringExtra("keyName")?.let {
            mBinding.tvTitleContacts.text = it.toString()
        }
        intent.getStringExtra("keyImage")?.let {
            Glide.with(mContext).load(Helper.getImageUrl(it.toString()))
                .placeholder(R.drawable.eclipse)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mBinding.ivUser)
        }
        intent.getStringExtra("keyFilePath")?.let {
            if (alreadyExists(it)) {
                printLog("file already exists")
                readFiles(it)

            } else {
                printLog("file doesnot exists")
                downloadCsv(it)
            }
        }


    }

    private fun initRv() {
        listOfAllLocalContacts.clear()




        rvContacts = mBinding.rvContacts
        rvContacts.layoutManager = LinearLayoutManager(this@ContactsDownloadViewActivity)
        rvContacts.setHasFixedSize(true)
        adapterContactListRecyclerViewAdapter =
            ContactListRecyclerViewAdapter(this@ContactsDownloadViewActivity, listOfContacts, false)
        adapterContactListRecyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                listOfContacts[position].selected = !listOfContacts[position].selected
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
                checkBottomSheet()

            }
        })
        rvContacts.adapter = adapterContactListRecyclerViewAdapter
        adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun checkBottomSheet() {
        listOfSelectedContacts.clear()

        for (contact in listOfContacts) {
            if (contact.selected) {
                listOfSelectedContacts.add(contact)
            }
        }
        if (listOfSelectedContacts.isNotEmpty()) {
            printLog("list in not empty")
            showBottomSheet()

        } else {

            hideBottomSheet()
            printLog("list in  empty")
        }

    }

    private fun readFiles(filePath: String) {
        //  val file = File(Helper.getFileDirectory(filePath))
        val directory: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName: String? = filePath.substringAfterLast("/")
        val file = File(directory, fileName)


        printLog(fileName.toString())
        printLog(file.path)
        csvReader().open(file) {
            val listOfContactsNameNumber = ArrayList<String>()



            readAllAsSequence().forEach { col ->
                listOfContactsNameNumber.add(col.toString())

            }
            listOfContacts.clear()
            adapterContactListRecyclerViewAdapter.notifyDataSetChanged()

            printLog(listOfContactsNameNumber.toString())
            var i = 0
            while (i < listOfContactsNameNumber.size) {

                listOfContacts.add(
                    ContactsInfo(
                        listOfContactsNameNumber[i++].replace("[", "").replace("]", ""),
                        listOfContactsNameNumber[i++].replace("[", "").replace("]", ""),
                        "",
                        false,
                        getRandomDrawable()
                    )
                )
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
            }

            hideLoading()
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

    private fun alreadyExists(filePath: String): Boolean {
        val file = File(Helper.getFileDirectory(filePath))
        return file.exists()
    }

    private fun downloadCsv(filePath: String) {
        showLoading()
        //  val filename: String = filePath.substringAfterLast("/")
        val file = File(Helper.getFileDirectory(filePath))
//        if (!file.exists()) {
//            printLog("folder doesnot exists creating new")
//            file.mkdirs()
//        }
        printLog("file path = ${file.path}")
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            Amplify.Storage.downloadFile(
                filePath,
                file,
                StorageDownloadFileOptions.defaultInstance(),
                { progress ->
                    printLog("Progress= $progress")
                },
                { result ->
                    printLog("result= ${result.file}")
                    readFiles(filePath)
                },
                { error ->
                    printLog("error downloading= ${error.cause}")
                }
            )

            handler.post {

            }
        }
    }


    fun showBottomSheet() {
        layoutBottomSheet.visibility = View.VISIBLE

        layoutBottomSheet.findViewById<TextView>(R.id.title).text =
            "${listOfSelectedContacts.size.toString()} Contacts Selected"

        layoutBottomSheet.findViewById<ImageView>(R.id.btnSave).setOnClickListener {

            showLoading()
            checkPermission()
        }
        layoutBottomSheet.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            listOfContacts.forEach { item ->
                item.selected = false
            }
            adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
            listOfSelectedContacts.clear()
            hideBottomSheet()

        }
    }

    private fun saveToContacts() {

        for (contact in listOfSelectedContacts) {

            if (!checkIfAlreadyPresent(contact)) {
                printLog("number not already preset")
                try {

                    val ops =
                        ArrayList<ContentProviderOperation>()

                    ops.add(
                        ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI
                        )
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build()
                    )

                    ops.add(
                        ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI
                        )
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                            )
                            .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                contact.name.toString()
                            ).build()
                    )

                    ops.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                            )
                            .withValue(
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                contact.number.toString()
                            )
                            .withValue(
                                ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                            )
                            .build()
                    );
                    this@ContactsDownloadViewActivity.contentResolver.applyBatch(
                        ContactsContract.AUTHORITY,
                        ops
                    )


                } catch (e: Exception) {


                }
            } else {
                printLog("number  already present")
                hideLoading()
                Toast.makeText(
                    this@ContactsDownloadViewActivity,
                    "Contacts Saved ",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

        printLog("all contacts saved...")
        hideBottomSheet()
        hideLoading()
        listOfContacts.clear()
        listOfSelectedContacts.clear()
        listOfContacts.addAll(listOfAllLocalContacts)
        adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
        Toast.makeText(
            this,
            "Contacts Saved ",
            Toast.LENGTH_SHORT
        ).show()


    }

    private fun checkIfAlreadyPresent(contact: ContactsInfo): Boolean {
        var result = false
        printLog(contact.toString())
        printLog("\n \n")


        for (localContact in listOfAllLocalContacts) {

            printLog(localContact.toString())
            result = localContact.name == contact.name && localContact.number == contact.number
            if (result) {
                break
            }
        }


        return result
    }

    fun hideBottomSheet() {
        layoutBottomSheet.visibility = View.GONE

    }

    fun checkPermission() {

        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {

                getContactsFromContactsList()
                saveToContacts()

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@ContactsDownloadViewActivity,
                    "Please allow the permissions from Settings to Use this Application",
                    Toast.LENGTH_LONG
                )
                    .show()

            }
        }
        TedPermission.with(this@ContactsDownloadViewActivity)
            .setPermissionListener(permissionListener)
            .setPermissions(
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()

    }

    private fun getContactsFromContactsList() {
        val contacts = this@ContactsDownloadViewActivity.contentResolver.query(
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
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var image =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

                if (image.isNullOrEmpty()) {
                    image = ""
                }
                if (name.isNotEmpty() && number.isNotEmpty()) {
                    val contact = ContactsInfo(name, number, image, false, getRandomDrawable())
                    listOfAllLocalContacts.add(contact)
                }
            }
            contacts.close()

        }
    }
}