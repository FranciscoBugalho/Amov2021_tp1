package pt.isec.amovtp1.grocerylistmanagement.rcManagement

import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pt.isec.amovtp1.grocerylistmanagement.*
import java.util.*
import kotlin.collections.ArrayList

data class ListInfo(val listName: String, val listDate: Date, val isBought: Boolean, val productInfo: List<String>)
val listInfo = arrayListOf<ListInfo>()

class RVListAdapter(private val listInfo: ArrayList<ListInfo>, private val isShopping: Int) : RecyclerView.Adapter<RVListAdapter.RVViewHolder>() {
    class RVViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        var listName: TextView = view.findViewById(R.id.listName)
        var listDate: TextView = view.findViewById(R.id.tvDate)
        var listTime: TextView = view.findViewById(R.id.tvTime)
        var productListInformation: LinearLayout = view.findViewById(R.id.productList)

        fun update(lnStr: String, ldStr: String, ltStr: String, isBought: Boolean, productNames: List<String>, isShopping: Int) {
            if(isShopping == 0) {
                // Add the onLongClickListener
                productListInformation.setOnLongClickListener{
                    (context as ManageProductListsActivity).createDialogToCopyOrDelete(listName.text.toString())
                    return@setOnLongClickListener true
                }

                if(!isBought)
                    productListInformation.setOnClickListener {
                        (context as ManageProductListsActivity).onEdit(listName.text.toString())
                        return@setOnClickListener
                    }
            } else if(isShopping == 1) {
                productListInformation.setOnClickListener {
                    (context as ManageShoppingListsActivity).onSelectingListElement(listName.text.toString())
                    return@setOnClickListener
                }
            } else {
                productListInformation.setOnClickListener {
                    (context as ViewPurchaseHistoryActivity).onSelectingList(listName.text.toString())
                    return@setOnClickListener
                }
            }


            listName.text = lnStr
            listDate.text = ldStr
            listTime.text = ltStr
            createProductList(productNames)
            if(isBought)
                productListInformation.setBackgroundColor(context.resources.getColor(R.color.dark_yellow)) //todo put in colors
        }

        private fun createProductList(productNames: List<String>) {
            for(i in productNames.indices) {
                val llAux = LinearLayout(context)
                llAux.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llAux.orientation = LinearLayout.HORIZONTAL
                llAux.tag = "llAux$i"

                val textView = TextView(context)
                textView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                textView.tag = "tv$i"
                textView.text = productNames[i]
                textView.setTextColor(Color.BLACK)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                textView.gravity = Gravity.START
                textView.maxLines = 2

                llAux.addView(textView)
                productListInformation.addView(llAux)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_rv, parent, false)
        return RVViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {
        holder.update(listInfo[position].listName,
            Utils.convertDateToStrCard(listInfo[position].listDate),
            Utils.convertTimeToStrCard(listInfo[position].listDate),
            listInfo[position].isBought,
            listInfo[position].productInfo,
            isShopping)
    }

    override fun getItemCount(): Int = listInfo.size
}