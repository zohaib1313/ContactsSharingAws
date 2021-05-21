package lads.contancsharing.www.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_notifications.view.*
import lads.contancsharing.www.R
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.RowNotificationsBinding
import lads.contancsharing.www.databinding.RowReceiveContactsSaveBinding
import lads.contancsharing.www.models.ModelSharingContactWith
import lads.contancsharing.www.room.entities.ModelNotification
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import java.time.LocalDate


class AdapterNotifications(
    var mContext: Context,
    var dataList: List<ModelNotification>
) :
    RecyclerView.Adapter<AdapterNotifications.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.btnAccept.setOnClickListener(this)
            binding.btnReject.setOnClickListener(this)

        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener?.onItemClick(
                    view,
                    adapterPosition,
                    dataList[adapterPosition].toString()
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RowNotificationsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {

                binding.userName.text = fromUserName.toString()

                binding.userPhoneNumber.text = fromUserNumber.toString()

                fromUserImage.let {
                    Log.d("asdf", it)
                    if (it != "") {
                        Glide.with(mContext).load(it).placeholder(R.drawable.eclipse)
                            .into(binding.ivUserImage)
                    }
                }
                binding.notificationMessage.text = message.toString()

                when (staus) {
                    AppConstant.STATUS_ACCEPTED -> {
                        binding.rlFinalStatus.visibility = View.VISIBLE
                        binding.rlStatuses.visibility = View.GONE
                        binding.rlFinalStatus.finalStatus.text = AppConstant.STATUS_ACCEPTED

                    }
                    AppConstant.STATUS_REJECTED -> {
                        binding.rlFinalStatus.visibility = View.VISIBLE
                        binding.rlStatuses.visibility = View.GONE
                        binding.rlFinalStatus.finalStatus.text = AppConstant.STATUS_REJECTED

                    }
                    else -> {
                        binding.rlStatuses.visibility = View.VISIBLE
                        binding.rlFinalStatus.visibility = View.GONE
                    }
                }


            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}