package lads.contancsharing.www.fragments


import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.alphabetik.Alphabetik.SectionIndexClickListener
import com.ferfalk.simplesearchview.SimpleSearchView
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.ContactListRecyclerViewAdapter
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.FragmentContactsBinding

import lads.contancsharing.www.models.ContactsInfo
import java.util.*
import kotlin.collections.ArrayList


class ContactsFragment : BaseFragment() {
    private var isSearchOn = false
    private val PERMISSIONS_REQUEST_WRITE_CONTACTS: Int = 13
    lateinit var mBinding: FragmentContactsBinding
    lateinit var adapterContactListRecyclerViewAdapter: ContactListRecyclerViewAdapter
    var listOfContacts = ArrayList<ContactsInfo>()
    var listOfAllContacts = ArrayList<ContactsInfo>()
    var listOfFilteredContacts = ArrayList<ContactsInfo>()
    var selectedItemsCount = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onResume() {
        super.onResume()

        initRv()
        checkSelectedContacts()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactsBinding.inflate(layoutInflater)
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
                checkSelectedContacts()
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onSearchViewClosedAnimation() {

            }

            override fun onSearchViewShown() {

            }

            override fun onSearchViewShownAnimation() {

            }

        })


//
//        initRv()

        mBinding.fab.setOnClickListener {
            if (selectedItemsCount > 0) {
                val list = ArrayList<ContactsInfo>()
                list.clear()
                listOfContacts.forEach {
                    if (it.selected) {
                        list.add(it)
                        printLog(it.toString())
                    }
                }

                shareSelectedContacts(list)
            }

        }

        return mBinding.root
    }

    private fun initRv() {

        listOfContacts.clear()
        listOfAllContacts.clear()
        listOfFilteredContacts.clear()

        printLog(sessionManager.isLoggedIn.toString())
        adapterContactListRecyclerViewAdapter =
            ContactListRecyclerViewAdapter(requireContext(), listOfContacts, false)
        mBinding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvContacts.adapter = adapterContactListRecyclerViewAdapter
        adapterContactListRecyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                listOfContacts[position].selected = !listOfContacts[position].selected
                adapterContactListRecyclerViewAdapter.notifyDataSetChanged()
                checkSelectedContacts()
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
    }

    private fun searchFromList(newText: String) {
        showLoading()
        listOfFilteredContacts.clear()
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

    private fun shareSelectedContacts(contactsToShare: java.util.ArrayList<ContactsInfo>) {


        listOfContacts.forEach {
            it.selected = false
        }
        checkSelectedContacts()
        adapterContactListRecyclerViewAdapter.notifyDataSetChanged()

        selectedItemsCount = 0;
        printLog(contactsToShare.toString())
        changeFragment(ContactsShareToFragment(contactsToShare), true)


    }

    private fun checkSelectedContacts() {
        selectedItemsCount = 0
        listOfContacts.forEach {
            if (it.selected) {
                selectedItemsCount++
            }
        }

        if (selectedItemsCount > 0) {
            mBinding.tvTitleContacts.text = "Selected Contacts $selectedItemsCount"
            mBinding.fab.setImageDrawable(getDrawable(requireContext(), R.drawable.fillfab_icon))
        } else {
            mBinding.fab.setImageDrawable(getDrawable(requireContext(), R.drawable.fab_icon))
            mBinding.tvTitleContacts.text = "Select Contacts"
        }
    }


    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ContactsFragment {
            val fragment = ContactsFragment()
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
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
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
            adapterContactListRecyclerViewAdapter.notifyDataSetChanged()

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
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSIONS_REQUEST_WRITE_CONTACTS
                        )
                    })
                    builder.show()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
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