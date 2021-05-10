package lads.contancsharing.www.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.Helper
import androidx.fragment.app.FragmentTransaction
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.databinding.FragmentProfileInfoBinding


class ProfileInfoFragment : BaseFragment() {

    lateinit var mBinding: FragmentProfileInfoBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileInfoBinding.inflate(layoutInflater)
        mBinding.btnDone.setOnClickListener {
            sessionManager.isLoggedIn = true





            lads.contancsharing.www.utils.Helper.startActivity(
                requireActivity(),
                Intent(requireContext(), MainActivity::class.java), true
            )
            requireActivity().finishAffinity()

        }
        lads.contancsharing.www.utils.Helper.hideKeyboard(requireActivity())
        return mBinding.root
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ProfileInfoFragment {
            val fragment = ProfileInfoFragment()
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