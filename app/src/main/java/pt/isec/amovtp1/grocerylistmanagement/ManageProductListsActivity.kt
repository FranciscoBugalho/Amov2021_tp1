package pt.isec.amovtp1.grocerylistmanagement

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_manage_product_lists.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.ALPHABETICAL_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.CREATED_DATE_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.IS_BOUGHT_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.ORDER_ASC
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.ORDER_DESC
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.LIST_ORDER_STR
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.ListInfo
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.RVListAdapter
import pt.isec.amovtp1.grocerylistmanagement.rcManagement.listInfo

class ManageProductListsActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private var listOrder = hashMapOf<String, String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_product_lists)

        if(savedInstanceState == null)
            listOrder[ALPHABETICAL_ORDER] = ORDER_ASC

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.manage_product_list_title)

        findViewById<Button>(R.id.addNewList).setOnClickListener {
            db.closeDB()
            Intent(this, CreateNewProductListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                startActivity(it)
            }
        }
    }

    private fun fillRecycleView() {
        val lists = db.getAllLists(listOrder)

        if(lists.isEmpty()) {
            // Create a TextView
            val textView = TextView(this)
            textView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            textView.tag = "tvNoLists"
            textView.text = getString(R.string.you_have_no_lists_created_click_on_the_plus_button)
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            textView.gravity = Gravity.CENTER
            findViewById<ConstraintLayout>(R.id.clManageProductLists).addView(textView)
        } else {
            repeat(lists.size) {
                val item = ListInfo(lists[it].listName, lists[it].date, Utils.listProductsName(db.productsInThisList(lists[it].listName)))
                listInfo.add(item)
            }

            rvProductList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvProductList.adapter = RVListAdapter(listInfo, false)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // If user clicks on the back menu button
        when (item.itemId) {
            android.R.id.home -> {
                // Clear the information in the list to rebuild later
                listInfo.clear()
                db.closeDB()
                Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .also {
                            startActivity(it)
                        }
                finish()
                return true
            }
            R.id.alphabeticalOrder -> {
                if(listOrder.keys.contains(ALPHABETICAL_ORDER)) {
                    if (listOrder[ALPHABETICAL_ORDER] == ORDER_ASC) {
                        listOrder.clear()
                        listOrder[ALPHABETICAL_ORDER] = ORDER_DESC
                        item.title = ""
                        item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " ($ORDER_ASC)"
                    }
                    else {
                        listOrder.clear()
                        listOrder[ALPHABETICAL_ORDER] = ORDER_ASC
                        item.title = ""
                        item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " ($ORDER_DESC)"
                    }
                } else {
                    listOrder.clear()
                    listOrder[ALPHABETICAL_ORDER] = ORDER_ASC
                    item.title = ""
                    item.title =  resources.getString(R.string.alphabetical_order_menu_text) + " ($ORDER_DESC)"
                }
                listInfo.clear()
                fillRecycleView()
                return true
            }
            R.id.createdDate -> {
                if(listOrder.keys.contains(CREATED_DATE_ORDER)) {
                    if (listOrder[CREATED_DATE_ORDER] == ORDER_ASC) {
                        listOrder.clear()
                        listOrder[CREATED_DATE_ORDER] = ORDER_DESC
                        item.title = ""
                        item.title = resources.getString(R.string.created_date_menu_text) + " ($ORDER_ASC)"
                    }
                    else {
                        listOrder.clear()
                        listOrder[CREATED_DATE_ORDER] = ORDER_ASC
                        item.title = ""
                        item.title = resources.getString(R.string.created_date_menu_text) + " ($ORDER_DESC)"
                    }
                } else {
                    listOrder.clear()
                    listOrder[CREATED_DATE_ORDER] = ORDER_ASC
                    item.title = ""
                    item.title = resources.getString(R.string.created_date_menu_text) + " ($ORDER_DESC)"
                }
                listInfo.clear()
                fillRecycleView()
                return true
            }
            R.id.isBought -> {
                if(listOrder.keys.contains(IS_BOUGHT_ORDER)) {
                    if (listOrder[IS_BOUGHT_ORDER] == ORDER_ASC) {
                        listOrder.clear()
                        listOrder[IS_BOUGHT_ORDER] = ORDER_DESC
                        item.title = ""
                        item.title = resources.getString(R.string.bougth_menu_text) + " ($ORDER_ASC)"
                    } else {
                        listOrder.clear()
                        listOrder[IS_BOUGHT_ORDER] = ORDER_ASC
                        item.title = ""
                        item.title = resources.getString(R.string.bougth_menu_text) + " ($ORDER_DESC)"
                    }
                } else {
                    listOrder.clear()
                    listOrder[IS_BOUGHT_ORDER] = ORDER_ASC
                    item.title = ""
                    item.title = resources.getString(R.string.bougth_menu_text) + " ($ORDER_DESC)"
                }
                listInfo.clear()
                fillRecycleView()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_menu, menu)
        return true
    }

    fun onEdit(listName: String) {
        // Clear the information in the list to rebuild later
        listInfo.clear()
        db.closeDB()
        Intent(this, CreateNewProductListActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.IntentConstants.IS_LIST_DETAILS, 1)
                .putExtra(Constants.IntentConstants.LIST_NAME_TO_EDIT, listName)
                .also {
            startActivity(it)
        }
    }

    fun createDialogToCopyOrDelete(listName: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_or_delete_list_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val btnDelete = dialog.findViewById(R.id.btnDelete) as Button
        val btnCopyList = dialog.findViewById(R.id.btnCopyList) as Button

        btnDelete.setOnClickListener {
            db.deleteListByName(listName)
            listInfo.clear()
            fillRecycleView()
            dialog.dismiss()
        }

        btnCopyList.setOnClickListener {
            // Clear the information in the list to rebuild later
            listInfo.clear()
            db.closeDB()
            Intent(this, CreateNewProductListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Constants.IntentConstants.IS_LIST_DETAILS, 2)
                    .putExtra(Constants.IntentConstants.LIST_NAME_TO_EDIT, listName)
                    .also {
                startActivity(it)
            }
            dialog.dismiss()
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

        outState.putSerializable(LIST_ORDER_STR, listOrder)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listOrder = savedInstanceState.getSerializable(LIST_ORDER_STR) as HashMap<String, String?>

        // Clear the information in the list to rebuild later
        listInfo.clear()
    }
}