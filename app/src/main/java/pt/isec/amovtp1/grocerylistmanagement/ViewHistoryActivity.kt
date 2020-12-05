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

class ViewHistoryActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private var listOrder = hashMapOf<String, String?>()

    /**
     * onCreate
     * 1. Sets the view to "activity_view_purchase_history"
     * 2. Connects the context to the Database
     * 3. Sets the order of the list of "previously purchased lists" (Alphabetical Ascendant)
     * 4. Sets an action bar on the top of the screen
     */
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

    /**
     * onSelectingList
     * 1. Clears the list of "previously purchased lists"
     * 2. Closes the Database connection
     * 3. Redirects to "ViewBoughtListDetailsActivity", with "listName" as argument and uses "FLAG_ACTIVITY_NEW_TASK"
     * to indicate that if the new activity is already crated it will be reused instead fo creating another
     */
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

    /**
     * fillRecycleView
     * 1. Gets the list of "previously purchased lists" from the Database
     * 2. If the list is empty, adds to the "clShoppedLists" layout (which is in the "activity_view_purchase_history") an informative textView
     * 3. If the list isn't empty, fill a recycle view with every "previously purchased list"
     * 4. The recycle view re-uses every component that's correspondent to a "previously purchased list"
     * that is scrolled out of the screen to fill with a new "previously purchased list" that is now on the screen
     */
    private fun fillRecycleView() {
        val lists = db.getAllBoughtLists(listOrder)

        if(lists.isEmpty()) {

            val cl = findViewById<ConstraintLayout>(R.id.clShoppedLists)
            // Create a TextView
            val textView = TextView(this)
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            params.leftToLeft = cl.id
            params.rightToRight = cl.id
            textView.layoutParams = params
            textView.tag = "tvShoppedLists"
            textView.text = getString(R.string.you_dont_have_any_purchase_history_text)
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            textView.gravity = Gravity.CENTER

            cl.addView(textView)

        } else {
            repeat(lists.size) {
                val item = ListInfo(lists[it].listName, lists[it].date, lists[it].isBought, db.productsInThisList(lists[it].listName))
                listInfo.add(item)
            }
            rvShoppedList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvShoppedList.adapter = RVListAdapter(listInfo, 2)
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
     * onPrepareOptionsMenu
     * 1. Removes the option "Bought" from the "order_menu"
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.removeItem(R.id.isBought)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar", including the "order_menu" dropdown and the "home" arrow component
     */
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

        outState.putSerializable(Constants.SaveDataConstants.LIST_ORDER_STR, listOrder)
    }

    /**
     * onRestoreInstanceState
     * 1. Restores "listOrder"
     * 2. Clears "listInfo"
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listOrder = savedInstanceState.getSerializable(Constants.SaveDataConstants.LIST_ORDER_STR) as HashMap<String, String?>

        // Clear the information in the list to rebuild later
        listInfo.clear()
    }

}