package pt.isec.amovtp1.grocerylistmanagement.rcManagement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isec.amovtp1.grocerylistmanagement.*
import java.util.*

data class ListInfo(val listName: String, val listDate: Date, val productInfo: String)
val listInfo = arrayListOf<ListInfo>()

class RVListAdapter(private val listInfo: ArrayList<ListInfo>, private val isShopping: Boolean) : RecyclerView.Adapter<RVListAdapter.RVViewHolder>() {
    class RVViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        var listName: TextView = view.findViewById(R.id.listName)
        var listDate: TextView = view.findViewById(R.id.tvDate)
        var listTime: TextView = view.findViewById(R.id.tvTime)
        var productListInformation: TextView = view.findViewById(R.id.productList)

        fun update(lnStr: String, ldStr: String, ltStr: String, pliStr: String, isShopping: Boolean) {
            if(!isShopping){
                // Add the onLongClickListener
                productListInformation.setOnLongClickListener{
                    (context as ManageProductListsActivity).createDialogToCopyOrDelete(listName.text.toString())
                    return@setOnLongClickListener true
                }

                productListInformation.setOnClickListener {
                    (context as ManageProductListsActivity).onEdit(listName.text.toString())
                    return@setOnClickListener
                }
            }
            else{
                productListInformation.setOnClickListener {
                    (context as ManageShoppingListsActivity).onSelectingListElement(listName.text.toString())
                    return@setOnClickListener
                }
            }

            listName.text = lnStr
            listDate.text = ldStr
            listTime.text = ltStr
            productListInformation.text = pliStr
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_rv, parent, false)
        return RVViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {
        holder.update(listInfo[position].listName, Utils.convertDateToStrCard(listInfo[position].listDate),
                Utils.convertTimeToStrCard(listInfo[position].listDate),
                listInfo[position].productInfo,
                isShopping)
    }

    override fun getItemCount(): Int = listInfo.size
}