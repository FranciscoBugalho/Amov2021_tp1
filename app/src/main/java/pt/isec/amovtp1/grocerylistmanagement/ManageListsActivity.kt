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
import kotlinx.android.synthetic.main.activity_manage_lists.*
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

class ManageListsActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private var listOrder = hashMapOf<String, String?>()

    /**
     * onCreate
     * 1. Sets the view to "activity_manage_lists"
     * 2. Connects the context to the Database
     * 3. Sets the order of the list of "previously purchased lists" (Alphabetical Ascendant)
     * 4. Sets an action bar on the top of the screen
     * 5. Sets the "Add new product" button's onClickListener that redirects to the "CreateNewListActivity"
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_lists)

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
            Intent(this, CreateNewListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                startActivity(it)
            }
        }
    }

    /**
     * fillRecycleView
     * 1. Gets the list of "previously purchased lists" from the Database
     * 2. If the list is empty, adds to the "clShoppedLists" layout (which is in the "activity_view_purchase_history") an informative textView
     * 3. If the list isn't empty, fill a recycle view with every "previously purchased list"
     * 4. The recycle view re-uses every component that's correspondent to a "previously purchased list"
     * that is scrolled out of the screen to fill with a new "previously purchased list" that is now on the screen
     */
    private fun fillRecycleView() {
        val lists = db.getAllLists(listOrder)

        if(lists.isEmpty()) {
            // Create a TextView
            val cl = findViewById<ConstraintLayout>(R.id.clManageProductLists)
            cl.removeViewAt(0) // Remove lists
            cl.setPadding(10, 0, 10, 0)
            val textView = TextView(this)
            textView.layoutParams =  ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            textView.tag = "tvNoLists"
            textView.text = getString(R.string.you_have_no_lists_created_click_on_the_plus_button)
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            textView.gravity = Gravity.CENTER
            cl.addView(textView)
        } else {
            findViewById<ConstraintLayout>(R.id.clManageProductLists).setPadding(0, 0, 0, 0)
            repeat(lists.size) {
                val item = ListInfo(lists[it].listName, lists[it].date, lists[it].isBought, db.productsInThisList(lists[it].listName))
                listInfo.add(item)
            }

            rvProductList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvProductList.adapter = RVListAdapter(listInfo, 0)
        }
    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar", including the "order_menu" dropdown and the "home" arrow component
     */
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

    /**
     * onCreateOptionsMenu
     * 1. Creates "order_menu"
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_menu, menu)
        return true
    }

    /**
     * onEdit
     * 1. Clears "listInfo", that contains the data of each list of products
     * 2. Closes Database
     * 3. Redirects to the "CreateNewListActivity" passing IS_LIST_DETAILS (flag that defines if it an edit or a create list) and LIST_NAME_TO_EDIT (list's name) as constants
     */
    fun onEdit(listName: String) {
        // Clear the information in the list to rebuild later
        listInfo.clear()
        db.closeDB()
        Intent(this, CreateNewListActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.IntentConstants.IS_LIST_DETAILS, 1)
                .putExtra(Constants.IntentConstants.LIST_NAME_TO_EDIT, listName)
                .also {
            startActivity(it)
        }
    }

    /**
     * createDialogToCopyOrDelete
     * 1. Creates a dialog box and sets it's content view as "edit_or_delete_list_dialog" and other properties
     * 2. Creates a "delete list" button and defines it's "onClickListener"
     * 3. Creates a "copy list" button and defines it's "onClickListener" that redirects to the "CreateNewListActivity"
     * passing IS_LIST_DETAILS (flag that defines if it an edit or a create list) and LIST_NAME_TO_EDIT (list's name) as constants
     */
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
            Intent(this, CreateNewListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Constants.IntentConstants.IS_LIST_DETAILS, 2)
                    .putExtra(Constants.IntentConstants.LIST_NAME_TO_EDIT, listName)
                    .also {
                startActivity(it)
            }
            dialog.dismiss()
        }
    }

    /**
     * onResume
     * 1. Calls "fillRecycleView" method
     */
    override fun onResume() {
        super.onResume()
        // Fill the RecycleView if it's empty
        if(listInfo.isEmpty())
            fillRecycleView()
    }

    /**
     * onPause
     * 1. Clears "listInfo"
     */
    override fun onPause() {
        super.onPause()
        // Fill the RecycleView if it's empty
        if(listInfo.isNotEmpty())
            listInfo.clear()
    }

    /**
     * onSaveInstanceState
     * 1. Saves "listOrder"
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(LIST_ORDER_STR, listOrder)
    }

    /**
     * onRestoreInstanceState
     * 1. Restores "listOrder"
     * 2. Clears "listInfo"
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listOrder = savedInstanceState.getSerializable(LIST_ORDER_STR) as HashMap<String, String?>

        // Clear the information in the list to rebuild later
        listInfo.clear()
    }
}