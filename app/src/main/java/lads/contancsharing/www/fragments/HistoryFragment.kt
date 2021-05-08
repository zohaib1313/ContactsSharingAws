package lads.contancsharing.www.fragments

import android.content.Context
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
    lateinit var tabSelected: TextView
    lateinit var tabNotSelected: TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHistoryBinding.inflate(layoutInflater)

        tabSelected =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
        tabSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tabSelected.background = (ContextCompat.getDrawable(
            requireContext(),
            R.drawable.selected
        ));

        tabNotSelected =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null) as TextView
        tabNotSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        tabNotSelected.background = (ContextCompat.getDrawable(
            requireContext(),
            R.drawable.not_selected
        ));

        setupViewPager()
        setSelectedTab(0)

        return mBinding.root

    }


    private fun setSelectedTab(selectedTab: Int) {
        if (selectedTab == 0) {
            mBinding.tabLayout.getTabAt(0)?.customView = tabSelected
            mBinding.tabLayout.getTabAt(1)?.customView = tabNotSelected
            tabSelected.text="Received Contacts"
            tabNotSelected.text="Shared Contacts"

        } else {
            mBinding.tabLayout.getTabAt(1)?.customView = tabSelected
            mBinding.tabLayout.getTabAt(0)?.customView = tabNotSelected
        }

    }


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
//
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("taaag", "tabselected")
                if (tab != null) {

                    if (tab.position == 0) {
                        tabSelected.text = "Received Contacts"
                        tabNotSelected.text = "Shared Contacts"

                    } else {
                        tabSelected.text = "Shared Contacts"
                        tabNotSelected.text = "Received Contacts"
                    }
                    tab.customView = tabSelected
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

                if (tab != null) {
//                    if(tab.position==0){
//                        tabSelected.text="Received Contacts"
//                    }else{
//                        tabSelected.text="Shared Contacts"
//                    }
                    tab.customView = tabNotSelected

                }

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