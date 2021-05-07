package lads.contancsharing.www.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.ViewPagerHistoryAdapter
import lads.contancsharing.www.databinding.FragmentHistoryBinding


import lads.contancsharing.www.models.FragmentsTitleFrag


class HistoryFragment : BaseFragment() {

    lateinit var mBinding: FragmentHistoryBinding
    var myViewPager2: ViewPager2? = null
    var viewPagerHistoryAdapter: ViewPagerHistoryAdapter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    var activeTabId = 0;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHistoryBinding.inflate(layoutInflater)

         setupViewPager()
         setSelectedTab(0)
//        mBinding.btnReceivedContacts.setOnClickListener {
//            if (activeTabId != 0) {
//                activeTabId = 0
//                checkActiveTab()
//            }
//        }
//        mBinding.btnSharedContacts.setOnClickListener {
//            if (activeTabId != 1) {
//                activeTabId = 1
//                checkActiveTab()
//            }
//        }
        return mBinding.root

    }

//    private fun checkActiveTab() {
//        if (activeTabId == 0) {
//            mBinding.btnReceivedContacts.background =
//                (ContextCompat.getDrawable(requireContext(), R.drawable.received_contacts_selected))
//            mBinding.btnReceivedContacts.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.white
//                )
//            )
//
//            mBinding.btnSharedContacts.background =
//                (ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.shared_contacts_not_selected
//                ))
//            mBinding.btnSharedContacts.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.black
//                )
//            )
//            changeFragment(FragmentReceivedContacts.newInstance(0), false)
//
//        } else {
//            mBinding.btnReceivedContacts.background =
//                (ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.shared_contacts_not_selected
//                ))
//            mBinding.btnReceivedContacts.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.black
//                )
//            )
//
//            mBinding.btnSharedContacts.background =
//                (ContextCompat.getDrawable(requireContext(), R.drawable.received_contacts_selected))
//            mBinding.btnSharedContacts.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.white
//                )
//            )
//            changeFragment(FragmentSharedContacts.newInstance(0), false)
//        }
//
//    }


    private fun setSelectedTab(selectedTab: Int) {
        if(selectedTab==0){
            val tabOne =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
            tabOne.text = "Received Contacts"
            tabOne.background = (ContextCompat.getDrawable(
                requireContext(),
                R.drawable.received_contacts_selected
            ));
            tabOne.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

            mBinding.tabLayout.getTabAt(0)?.customView = tabOne


            val tabTwo =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
            tabTwo.text = "Shared Contacts"
            tabTwo.setTextColor(ContextCompat.getColor(requireContext(),R.color.black));
            tabTwo.background = (ContextCompat.getDrawable(
                requireContext(),
                R.drawable.shared_contacts_not_selected
            ));
            mBinding.tabLayout.getTabAt(1)?.customView = tabTwo




        }else{
            val tabOne =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
            tabOne.text = "Received Contacts"
            tabOne.background = (ContextCompat.getDrawable(
                requireContext(),
                R.drawable.shared_contacts_not_selected
            ));
            tabOne.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))

            mBinding.tabLayout.getTabAt(0)?.customView = tabOne

      val tabTwo =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
            tabTwo.text = "Shared Contacts"
            tabTwo.setTextColor(ContextCompat.getColor(requireContext(),R.color.white));
            tabTwo.background = (ContextCompat.getDrawable(
                requireContext(),
                R.drawable.received_contacts_selected
            ));
            mBinding.tabLayout.getTabAt(1)?.customView = tabTwo

        }

    }
//

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

    private fun setupViewPager() {


        val listOfFragments = ArrayList<FragmentsTitleFrag>()

        listOfFragments.add(FragmentsTitleFrag("Received Contacts", FragmentReceivedContacts()))

        listOfFragments.add(FragmentsTitleFrag("Shared Contacts", FragmentSharedContacts()))



        myViewPager2 = mBinding.viewPager
        viewPagerHistoryAdapter =
            ViewPagerHistoryAdapter(requireFragmentManager(), lifecycle, listOfFragments)
        myViewPager2!!.adapter = viewPagerHistoryAdapter
        TabLayoutMediator(mBinding.tabLayout, myViewPager2!!,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = listOfFragments[position].titleFrag


            }).attach()
//        myViewPager2!!.isUserInputEnabled = false

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("taaag", "tabselected")

//                tab?.let { setSelectedTab(it.position) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }


        })
    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            requireFragmentManager().beginTransaction()
        mFragmentTransaction.replace(R.id.viewPager, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }
}