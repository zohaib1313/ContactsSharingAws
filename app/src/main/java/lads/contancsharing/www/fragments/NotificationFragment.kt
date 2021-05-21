package lads.contancsharing.www.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lads.contancsharing.www.room.viewModels.NotificationViewModel
import lads.contancsharing.www.R
import lads.contancsharing.www.adapters.AdapterNotifications
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.FragmentNotificationsBinding
import lads.contancsharing.www.room.entities.ModelNotification
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper


class NotificationFragment : BaseFragment() {

    private lateinit var mBinding: FragmentNotificationsBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var rvNotifications: RecyclerView
    private lateinit var adapterNotifications: AdapterNotifications
    private var dataListModelNotifications = ArrayList<ModelNotification>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNotificationsBinding.inflate(layoutInflater)
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        loadingLayout = mBinding.loadingLayout.rlLoading
        initRv()
        initViewModel()
//        mBinding.btnNext.setOnClickListener {
//            changeFragment()
//        }
        viewModel.insert(
            ModelNotification(
                Helper.getCurrentTimeStamp(),
                "file path",
                "message.....",
                "form user token",
                "fromUser id",
                "",
                "fromUserName",
                "+92123123123",
                ""
            )
        )



        return mBinding.root
    }

    private fun initRv() {
        rvNotifications = mBinding.rvNotifications
        rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        rvNotifications.setHasFixedSize(true)
        adapterNotifications = AdapterNotifications(requireContext(), dataListModelNotifications)
        rvNotifications.adapter = adapterNotifications
        adapterNotifications.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                when (view.id) {
                    R.id.btnAccept -> {
                        acceptRequest(dataListModelNotifications[position])
                    }
                    R.id.btnReject -> {
                        rejectRequest(dataListModelNotifications[position])
                    }
                    else -> {

                    }
                }
            }
        })


    }

    private fun rejectRequest(modelNotification: ModelNotification) {
        viewModel.update(modelNotification.notificationId, AppConstant.STATUS_REJECTED)


    }

    private fun acceptRequest(modelNotification: ModelNotification) {
        viewModel.update(modelNotification.notificationId, AppConstant.STATUS_ACCEPTED)

    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): NotificationFragment {
            val fragment = NotificationFragment()
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

    private fun initViewModel() {
        dataListModelNotifications.clear()
        viewModel =
            ViewModelProviders.of(requireActivity()).get(NotificationViewModel::class.java)
        val observer = Observer<List<ModelNotification>>() {
            dataListModelNotifications.clear()
            dataListModelNotifications.addAll(it)
            adapterNotifications.notifyDataSetChanged()

            it.forEach { modelNotification ->

                printLog("notification from user : " + modelNotification.fromUserName)
            }

        }
        viewModel.allCartItemsInViewModel.observeForever(observer)
    }
}