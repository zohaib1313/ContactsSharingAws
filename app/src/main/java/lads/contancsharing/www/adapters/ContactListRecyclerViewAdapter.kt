package lads.contancsharing.www.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import lads.contancsharing.www.R

import lads.contancsharing.www.models.BaseViewHolder
import lads.contancsharing.www.models.ContactsInfo
import java.util.*

/*import com.app.sneakersmap.R;
import com.app.sneakersmap.api.commonApiModel.CartItem;
import com.app.sneakersmap.pagination.BaseViewHolder;
import com.app.sneakersmap.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;*/
/**
 * [RecyclerView.Adapter] that can display a [] and makes a call to the
 * specified [].
 * TODO: Replace the implementation with code for your data type.
 */
class ContactListRecyclerViewAdapter(
    contactList: List<ContactsInfo>,
    listener: ContactListRecyclerViewAdapterListener
) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var contact_List: List<ContactsInfo>
    var contact_List_filtered: List<ContactsInfo>
    var listener: ContactListRecyclerViewAdapterListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_contacts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                contact_List_filtered = if (charString.isEmpty()) {
                    contact_List
                } else {
                    val filteredList: MutableList<ContactsInfo> = ArrayList<ContactsInfo>()
                    for (row in contact_List) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
//                        if (row.getDisplayName1().toLowerCase()
//                                .contains(charString.toLowerCase()) || row.getPhoneNumber1()
//                                .contains(charSequence)
//                        ) {
//                            filteredList.add(row)
//                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contact_List_filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contact_List_filtered = filterResults.values as ArrayList<ContactsInfo>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder internal constructor(val mView: View) :
        BaseViewHolder(mView) {
        @BindView(R.id.displayName)
        var displayName: TextView? = null

//        @BindView(R.id.phoneNumber)
//        var phoneNumber: TextView? = null
        protected override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
//            val contactsInfo: ContactsInfo = contact_List_filtered[position]
//            displayName?.text=(contactsInfo.getDisplayName())
//
//            mView.setOnClickListener { listener.onItemSelected(contact_List_filtered[getAdapterPosition()]) }
        }

        init {
            ButterKnife.bind(this, mView)
        }
    }

    interface ContactListRecyclerViewAdapterListener {
        fun onItemSelected(item: ContactsInfo?)
    }

    init {
        contact_List = contactList
        contact_List_filtered = contactList
        this.listener = listener
    }
}