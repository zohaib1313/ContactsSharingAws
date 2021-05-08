package lads.contancsharing.www.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.RowAlphabetsBinding
import lads.contancsharing.www.databinding.RowContactsBinding
import lads.contancsharing.www.models.ContactsInfo


class AlphabetIndexAdapter(var mContext: Context, var dataList: List<String>) :
    RecyclerView.Adapter<AlphabetIndexAdapter.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowAlphabetsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener?.onItemClick(view, adapterPosition,dataList[adapterPosition].toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowAlphabetsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                binding.alphabet.text = this.toString()
//                binding.vChecked.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}