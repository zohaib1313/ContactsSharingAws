package lads.contancsharing.www.adapters


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import lads.contancsharing.www.R
import lads.contancsharing.www.callBacks.OnItemClickListener
import lads.contancsharing.www.databinding.RowContactsBinding
import lads.contancsharing.www.models.ContactsInfo
import java.util.*


class ContactListRecyclerViewAdapter(var mContext: Context, var dataList: List<ContactsInfo>,var isHideRadioSelection:Boolean) :
    RecyclerView.Adapter<ContactListRecyclerViewAdapter.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowContactsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {



        init {
        if(isHideRadioSelection){
            binding.radioBtnSelect.visibility=View.INVISIBLE
        }
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                dataList[adapterPosition].number.let {
                    mOnItemClickListener?.onItemClick(
                        view, adapterPosition, it
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowContactsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                binding.displayName.text = name

                if (selected) {
                    binding.radioBtnSelect.background =
                        getDrawable(mContext, R.drawable.ic_check_icon)
                } else {
                    binding.radioBtnSelect.background =
                        getDrawable(mContext, R.drawable.checkbox_not_checked)

                }


                try {
                    if (photo == "") {
                        binding.ivContact.visibility = View.GONE
                        binding.tvContact.visibility = View.VISIBLE
                        val str: String = name.toString()
                        val strArray = str.split(" ".toRegex()).toTypedArray()
                        val builder = StringBuilder()
//First name
                        if (strArray.isNotEmpty()) {
                            builder.append(strArray[0], 0, 1)
                        }
//Middle name
                        //Middle name
                        if (strArray.size > 1) {
                            builder.append(strArray[1], 0, 1)
                        }
//Surname
                        //Surname
                        if (strArray.size > 2) {
                            builder.append(strArray[2], 0, 1)
                        }

                        binding.tvContact.background=drawable
                        binding.tvContact.text=builder.toString()

                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(
                                mContext.contentResolver,
                                Uri.parse(photo)
                            )
                        binding.ivContact.visibility = View.VISIBLE
                        binding.tvContact.visibility = View.GONE
                        binding.ivContact.setImageBitmap(bitmap)

                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }



}