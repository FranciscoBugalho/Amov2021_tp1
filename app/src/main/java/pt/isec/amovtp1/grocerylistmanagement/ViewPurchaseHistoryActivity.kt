package pt.isec.amovtp1.grocerylistmanagement

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_view_purchase_history.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.ListInfo
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.RVListAdapter
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.listInfo

class ViewPurchaseHistoryActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private var listOrder = hashMapOf<String, String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_purchase_history)

        // Init database
        db = GMLDatabase(this)

        if(savedInstanceState == null)
            listOrder[Constants.ListOrders.ALPHABETICAL_ORDER] = Constants.ListOrders.ORDER_ASC

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.purchase_history_btn_txt)
    }

    fun onSelectingList(listName: String) {
        // Clear the information in the list to rebuild later
        listInfo.clear()
        db.closeDB()
        Intent(this, ViewBoughtListDetailsActivity::class.java)
            .putExtra(Constants.IntentConstants.LIST_NAME_TO_SHOW, listName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .also {
                startActivity(it)
            }
    }

    private fun fillRecycleView() {
        val lists = db.getAllBoughtLists(listOrder)

        if(lists.isEmpty()) {
            // Create a TextView
            val textView = TextView(this)
            textView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            textView.tag = "tvShoppedLists"
            textView.text = getString(R.string.you_dont_have_any_purchase_history_text)
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            textView.gravity = Gravity.CENTER
            findViewById<ConstraintLayout>(R.id.clShoppedLists).addView(textView)
        } else {
            repeat(lists.size) {
                val item = ListInfo(lists[it].listName, lists[it].date, lists[it].isBought, db.productsInThisList(lists[it].listName))
                listInfo.add(item)
            }
            rvShoppedList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvShoppedList.adapter = RVListAdapter(listInfo, 2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.removeItem(R.id.isBought)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                        startActivity(it)
                    }
                finish()
                return true
            }
            R.id.alphabeticalOrder -> {
                if(listOrder.keys.contains(Constants.ListOrders.ALPHABETICAL_ORDER)) {
                    if (listOrder[Constants.ListOrders.ALPHABETICAL_ORDER] == Constants.ListOrders.ORDER_ASC) {
                        listOrder.clear()
                        listOrder[Constants.ListOrders.ALPHABETICAL_ORDER] = Constants.ListOrders.ORDER_DESC
                        item.title = ""
                        item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " (${Constants.ListOrders.ORDER_ASC})"
                    }
                    else {
                        listOrder.clear()
                        listOrder[Constants.ListOrders.ALPHABETICAL_ORDER] =
                            Constants.ListOrders.ORDER_ASC
                        item.title = ""
                        item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " (${Constants.ListOrders.ORDER_DESC})"
                    }
                } else {
                    listOrder.clear()
                    listOrder[Constants.ListOrders.ALPHABETICAL_ORDER] =
                        Constants.ListOrders.ORDER_ASC
                    item.title = ""
                    item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " (${Constants.ListOrders.ORDER_DESC})"
                }
                listInfo.clear()
                fillRecycleView()
                return true
            }
            R.id.createdDate -> {
                if(listOrder.keys.contains(Constants.ListOrders.CREATED_DATE_ORDER)) {
                    if (listOrder[Constants.ListOrders.CREATED_DATE_ORDER] == Constants.ListOrders.ORDER_ASC) {
                        listOrder.clear()
                        listOrder[Constants.ListOrders.CREATED_DATE_ORDER] = Constants.ListOrders.ORDER_DESC
                        item.title = ""
                        item.title = resources.getString(R.string.created_date_menu_text) + " (${Constants.ListOrders.ORDER_ASC})"
                    }
                    else {
                        listOrder.clear()
                        listOrder[Constants.ListOrders.CREATED_DATE_ORDER] =
                            Constants.ListOrders.ORDER_ASC
                        item.title = ""
                        item.title = resources.getString(R.string.created_date_menu_text) + " (${Constants.ListOrders.ORDER_DESC})"
                    }
                } else {
                    listOrder.clear()
                    listOrder[Constants.ListOrders.CREATED_DATE_ORDER] =
                        Constants.ListOrders.ORDER_ASC
                    item.title = ""
                    item.title = resources.getString(R.string.created_date_menu_text) + " (${Constants.ListOrders.ORDER_DESC})"
                }
                listInfo.clear()
                fillRecycleView()
                return true
            }
            else -> return  super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Fill the RecycleView if it's empty
        if(listInfo.isEmpty())
            fillRecycleView()
    }

    override fun onPause() {
        super.onPause()
        // Fill the RecycleView if it's empty
        if(listInfo.isNotEmpty())
            listInfo.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(Constants.SaveDataConstants.LIST_ORDER_STR, listOrder)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listOrder = savedInstanceState.getSerializable(Constants.SaveDataConstants.LIST_ORDER_STR) as HashMap<String, String?>

        // Clear the information in the list to rebuild later
        listInfo.clear()
    }

}