package pt.isec.amovtp1.grocerylistmanagement.rcManagement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isec.amovtp1.grocerylistmanagement.ManageProductListsActivity
import pt.isec.amovtp1.grocerylistmanagement.R
import pt.isec.amovtp1.grocerylistmanagement.Utils
import java.util.*
import kotlin.collections.ArrayList

data class ListInfo(val listName: String, val listDate: Date, val productInfo: String)
val listInfo = arrayListOf<ListInfo>()

class RVListAdapter(private val listInfo: ArrayList<ListInfo>) : RecyclerView.Adapter<RVListAdapter.RVViewHolder>() {
    class RVViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        var listName: TextView = view.findViewById(R.id.listName)
        var listDate: TextView = view.findViewById(R.id.tvListModificationDate)
        var productListInformation: TextView = view.findViewById(R.id.productList)
        var editTextView: TextView = view.findViewById(R.id.tvEditList)

        fun update(lnStr: String, ldStr: String, pliStr: String) {
            // Add the onLongClickListener
            productListInformation.setOnLongClickListener{
                (context as ManageProductListsActivity).createDialogToCopyOrDelete(listName.text.toString())
                return@setOnLongClickListener true
            }

            editTextView.setOnClickListener {
                (context as ManageProductListsActivity).onEdit(listName.text.toString())
                return@setOnClickListener
            }

            listName.text = lnStr
            listDate.text = ldStr
            productListInformation.text = pliStr
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_rv, parent, false)
        return RVViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {
        holder.update(listInfo[position].listName, Utils.convertDateToStrCard(listInfo[position].listDate), listInfo[position].productInfo)
    }

    override fun getItemCount(): Int = listInfo.size
}