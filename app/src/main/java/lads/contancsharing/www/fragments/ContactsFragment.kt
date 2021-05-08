package lads.contancsharing.www.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.alphabetik.Alphabetik.SectionIndexClickListener
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.AlphabetIndexAdapter
import lads.contancsharing.www.adapters.ContactListRecyclerViewAdapter
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.FragmentContactsBinding

import lads.contancsharing.www.models.ContactsInfo


class ContactsFragment : BaseFragment() {

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

        listOfContacts.add(ContactsInfo("1", "a", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "b", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "c", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "d", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "e", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "f", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "g", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "h", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "i", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "j", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "k", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "l", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "m", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "b", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "o", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "p", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "q", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "r", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "s", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "t", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "u", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "v", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "w", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "x", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "y", "03030", "asd", "asdfasd"))
        listOfContacts.add(ContactsInfo("1", "z", "03030", "asd", "asdfasd"))

        listOfContacts.sortedWith(compareBy { it.getDisplayName() })



        adapterContactListRecyclerViewAdapter =
            ContactListRecyclerViewAdapter(requireContext(), listOfContacts)
        mBinding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvContacts.adapter = adapterContactListRecyclerViewAdapter
        mBinding.rvContacts.setHasFixedSize(true)

      //  setUpAlphaBetIndex()

        val alphabetRv = mBinding.alphSectionIndex
        alphabetRv.onSectionIndexClickListener(SectionIndexClickListener { view, position, character ->
            val info = " Position = $position Char = $character"
            Log.d("Tagggg ", "$view,$info")
           mBinding.rvContacts.smoothScrollToPosition(getPositionFromData(character))
        })


        return mBinding.root
    }

    private fun setUpAlphaBetIndex() {
        val listOfAlphabets = ArrayList<String>()
        for (a in 'A'..'Z')
        {
            listOfAlphabets.add(a.toString())
        }
        listOfAlphabets.add("#")
        listOfAlphabets.add("#")
        listOfAlphabets.add("#")
        listOfAlphabets.add("#")


        val alphabetRv = mBinding.alphSectionIndex
        alphabetRv.layoutManager=LinearLayoutManager(requireContext())
        val adapter = AlphabetIndexAdapter(requireContext(), listOfAlphabets)
        alphabetRv.adapter=adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View, position: Int, character: String) {
                Log.d("Tagggg",character.toString())
            }

        })
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
            val letter = contact.getDisplayName()?.get(0)
            if (letter != null) {
                if (letter.equals(character[0], true)) {
                    return position;
                }
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


}