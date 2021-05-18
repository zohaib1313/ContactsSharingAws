package lads.contancsharing.www.activities

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.bumptech.glide.Glide
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import lads.contancsharing.www.R
import lads.contancsharing.www.databinding.ActivityContactsDownloadViewBinding

import lads.contancsharing.www.models.ModelSharingContactWith
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.Executors


class ContactsDownloadViewActivity : BaseActivity() {


    lateinit var layoutBottomSheet: RelativeLayout
    lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>
    lateinit var mBinding: ActivityContactsDownloadViewBinding
    private var senderUser: ContactSharingWith? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityContactsDownloadViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        layoutBottomSheet = mBinding.bottomSheet.bottomSheett
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBinding.backButton.setOnClickListener {
            onBackPressed()
        }


        intent.getStringExtra(AppConstant.KEY_DATA)?.let { jsonObject ->
            senderUser = Gson().fromJson(jsonObject, ContactSharingWith::class.java)

            senderUser?.let {
                it.user?.let { otherUser ->
                    printLog(otherUser.name)
                    mBinding.tvTitleContacts.text = otherUser.name.toString()
                    otherUser.image?.let { img ->
                        Glide.with(mContext).load(Helper.getImageUrl(img.toString()))
                            .placeholder(R.drawable.eclipse)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mBinding.ivUser)
                    }
                }
                printLog(it.filePath.toString())
                it.filePath?.let { filePath ->

                    if (alreadyExists(filePath)) {
                        printLog("file already exists")
                        readFiles(filePath)

                    } else {
                        printLog("file doesnot exists")
                        downloadCsv(filePath)
                    }
                }
            }
        }

















        sheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })


    }

    private fun readFiles(filePath: String) {
      //  val file = File(Helper.getFileDirectory(filePath))
        val directory: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName: String? = filePath.substringAfterLast("/")
        val file = File(directory, fileName)
        var i=0

        printLog(fileName.toString())
        printLog(file.path)
        csvReader().open(file) {

            printLog("file opened ${readAllAsSequence()}")
            readAllAsSequence().forEach { row ->
                printLog(row.toString())

//                while (i<row.size)
//                {
//                    val name=row[i++].replace("[","").replace("]","")
//                    val number=row[i++].replace("[","").replace("]","")
//                  printLog(name+" "+number)
//
//                }

            }
        }
    }

    private fun alreadyExists(filePath: String): Boolean {
        val file = File(Helper.getFileDirectory(filePath))
        return file.exists()
    }

    private fun downloadCsv(filePath: String) {

        //  val filename: String = filePath.substringAfterLast("/")
        val file = File(Helper.getFileDirectory(filePath))
//        if (!file.exists()) {
//            printLog("folder doesnot exists creating new")
//            file.mkdirs()
//        }
        printLog("file path = ${file.path}" )
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
                   // readFiles(filePath)
                },
                { error ->
                    printLog("error downloading= ${error.cause}")
                }
            )

            handler.post {

            }
        }
    }

    fun toggleBottomSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setHideable(false)
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        } else {
            sheetBehavior.setHideable(true)
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            //sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

        }
    }
}