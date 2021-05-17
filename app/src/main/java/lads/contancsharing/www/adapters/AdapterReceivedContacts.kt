package lads.contancsharing.www.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.ContactSharingWith
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.RowAlphabetsBinding
import lads.contancsharing.www.databinding.RowContactsBinding
import lads.contancsharing.www.databinding.RowReceiveBinding
import lads.contancsharing.www.databinding.RowReceiveContactsSaveBinding
import lads.contancsharing.www.models.ContactsInfo


class AdapterReceivedContacts(var mContext: Context, var dataList: List<ContactSharingWith>) :
    RecyclerView.Adapter<AdapterReceivedContacts.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(private val binding: RowReceiveContactsSaveBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
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
            RowReceiveContactsSaveBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        with(holder) {
//            with(dataList[position]) {
////                binding.displayName.text = name
//
////                binding.vChecked.visibility = if (isChecked) View.VISIBLE else View.GONE
//            }
//        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}