package lads.contancsharing.www.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.adapters.ContactListRecyclerViewAdapter
import lads.contancsharing.www.databinding.FragmentContactsBinding
import lads.contancsharing.www.databinding.FragmentVerifyOtpBinding
import lads.contancsharing.www.models.ContactsInfo
import lads.contancsharing.www.utils.Helper


class ContactsFragment : BaseFragment(),
    ContactListRecyclerViewAdapter.ContactListRecyclerViewAdapterListener {

    lateinit var mBinding: FragmentContactsBinding
    lateinit var adapterContactListRecyclerViewAdapter: ContactListRecyclerViewAdapter
    var listOfContacts = ArrayList<ContactsInfo>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactsBinding.inflate(layoutInflater)
//        mBinding.btnNext.setOnClickListener {
//            changeFragment()
//        }
        adapterContactListRecyclerViewAdapter = ContactListRecyclerViewAdapter(listOfContacts, this)
        mBinding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvContacts.adapter = adapterContactListRecyclerViewAdapter
        mBinding.rvContacts.setHasFixedSize(true)






        return mBinding.root
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


    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

    override fun onItemSelected(item: ContactsInfo?) {

    }
}