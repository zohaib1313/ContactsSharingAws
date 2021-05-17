package lads.contancsharing.www.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.Helper
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.AdapterReceivedContacts
import lads.contancsharing.www.callBacks.ISearch
import lads.contancsharing.www.databinding.FragmentReceivedContactsBinding


class FragmentReceivedContacts : BaseFragment() {

    lateinit var mBinding: FragmentReceivedContactsBinding
    private lateinit var rvReceivedContacts: RecyclerView
    private lateinit var adapterReceivedContacts: AdapterReceivedContacts
    private var dataListReceivedContacts = ArrayList<ContactSharingWith>()
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
        rvReceivedContacts.layoutManager = LinearLayoutManager(requireContext())
        adapterReceivedContacts =
            AdapterReceivedContacts(requireContext(), dataListReceivedContacts)
        rvReceivedContacts.adapter = adapterReceivedContacts
        adapterReceivedContacts.notifyDataSetChanged()

        lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
        return mBinding.root
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


    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }


}