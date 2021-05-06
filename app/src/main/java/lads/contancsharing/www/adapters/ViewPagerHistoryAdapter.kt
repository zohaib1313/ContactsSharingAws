package lads.contancsharing.www.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import lads.contancsharing.www.models.FragmentsTitleFrag

class ViewPagerHistoryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var listOfFragments: ArrayList<FragmentsTitleFrag>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return listOfFragments[position].fragment
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listOfFragments.size
    }

}