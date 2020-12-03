package pt.isec.amovtp1.grocerylistmanagement

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_bought_list_details.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.IntentConstants.LIST_NAME
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.TOTAL_PRICE
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase

class ViewBoughtListDetailsActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private lateinit var listName: String
    private var totalPrice: Double = 0.0

    /**
     * onCreate
     * 1. Sets the view to "activity_view_bought_list_details"
     * 2. Connects the context to the Database
     * 3. Sets the name of the list to show
     * 4. Sets an action bar on the top of the screen
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bought_list_details)

        // Init database
        db = GMLDatabase(this)

        if(savedInstanceState == null) {
            listName = intent.getStringExtra(Constants.IntentConstants.LIST_NAME_TO_SHOW)!!
            totalPrice = 0.0
        }

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.list_details_title)
    }

    /**
     * getAllProductsInformation
     * 1. Gets the products on the respective list
     * 2. For each product in the list gets it's category, quantity, unit last price and date and adds them to an arrayList of strings
     * 3. Adds the array list to a mutableMapOf<String, ArrayList<String>>, where the key is the product's name and the value is the product's data
     * 4. Returns the MutableMap
     */
    fun getAllProductsInformation(): MutableMap<String, ArrayList<String>> {
        val products = db.productsInThisList(listName)
        val boughtProducts = mutableMapOf<String, ArrayList<String>>()

        var qntPrice = arrayListOf<String>()
        for (i in products.indices) {
            // Get product category
            qntPrice.add(db.getCategoryByProductName(products[i]))

            // Get product quantity and unit
            qntPrice.add(db.getProductQuantityAndUnit(listName, products[i]))

            // Get last prices and its date
            val lastPrice = db.getLastProductPrice(products[i])

            qntPrice.add(lastPrice!!)
            totalPrice += lastPrice.split(" ")[0].toDouble()

            boughtProducts[products[i]] = qntPrice
            qntPrice = arrayListOf()
        }
        return boughtProducts
    }

    /**
     * displayAllProductInfo
     * 1. Gets a "LinearLayout"o f purchased products (id: llAllBoughtProducts)
     * 2. Sets the "tvTitleBoughtList" text to the product list's name (kotlin extensions allows the access to this Text View)
     * 3. Sets the "tvBoughtDate" text to the date of creation of the list (gets this date from de database)
     * 4. For each bought product creates a "linearLayout", sets it's params, creates 4 Text Views ("tvProductName" & "tvCategory" & "tvQuantity" & "tvPrice"), sets it's params
     * and adds them to the "linearLayout"
     * 5. Adds the "linearLayout" to the "llAllBoughtProducts" LinearLayout
     * 6. Creates another LinearLayout, "llPrice" and a Text View, "tvTotalPrice" (word "price" and the total cost of the products), and adds them to the "llAllBoughtProducts" LinearLayout
     */
    fun displayAllProductInfo() {
        val llPurchasedProducts = findViewById<LinearLayout>(R.id.llAllBoughtProducts)

        tvTitleBoughtList.text = listName
        findViewById<TextView>(R.id.tvBoughtDate).text = db.getListDate(listName)

        val boughtProducts = getAllProductsInformation()

        for(key in boughtProducts.keys) {
            // Create the LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "ll$key"
            linearLayout.setPadding(5, 5, 5, 5)

            var param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.70f

            // Create the TextView
            val tvProductName = TextView(this)
            tvProductName.layoutParams = param
            tvProductName.tag = "tv$key"
            tvProductName.text = key
            tvProductName.setTextColor(Color.BLACK)
            tvProductName.gravity = Gravity.START
            tvProductName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvProductName.maxLines = 1
            //-----------------------

            val productInfo = boughtProducts[key]

            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.30f

            // Create the TextView
            val tvCategory = TextView(this)
            tvCategory.layoutParams = param
            tvCategory.tag = "tv$key"
            tvCategory.text = productInfo!![0]
            tvCategory.setTextColor(Color.BLACK)
            tvCategory.gravity = Gravity.START
            tvCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvCategory.maxLines = 1
            //-----------------------

            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.40f

            // Create the TextView
            val tvQuantity = TextView(this)
            tvQuantity.layoutParams = param
            tvQuantity.tag = "tvQnt$key"
            tvQuantity.text = productInfo[1]
            tvQuantity.setTextColor(Color.BLACK)
            tvQuantity.gravity = Gravity.END
            tvQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            //-----------------------

            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.40f

            // Create the TextView
            val tvPrice = TextView(this)
            tvPrice.layoutParams = param
            tvPrice.tag = "tvPrice$key"
            tvPrice.setTextColor(Color.BLACK)
            tvPrice.gravity = Gravity.END
            tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvPrice.text = productInfo[2].split(" ")[0] + getString(R.string.price_sign)

            linearLayout.addView(tvProductName)
            linearLayout.addView(tvCategory)
            linearLayout.addView(tvQuantity)
            linearLayout.addView(tvPrice)
            llPurchasedProducts.addView(linearLayout)
        }

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.END
        param.setMargins(0, 5, 0, 0)

        // Create the LinearLayout
        val llPrice = LinearLayout(this)
        llPrice.layoutParams = param
        llPrice.orientation = LinearLayout.HORIZONTAL
        llPrice.tag = "llPrice"
        llPrice.setPadding(5, 5, 5, 5)

        // Create the TextView
        val tvTotalPrice = TextView(this)
        tvTotalPrice.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvTotalPrice.tag = "tvTotalPrice"
        tvTotalPrice.text = getString(R.string.pp_tv_total_text) + " " + totalPrice + getString(R.string.price_sign)
        tvTotalPrice.setTextColor(Color.BLACK)
        tvTotalPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        tvTotalPrice.maxLines = 1
        //-----------------------
        llPrice.addView(tvTotalPrice)
        llPurchasedProducts.addView(llPrice)
    }

    /**
     * onOptionsItemSelected
     * 1. Verifies if the button clicked was the 'back arrow' (id : home) on the supportActionBar
     * 2. If true, redirects to the "ViewPurchaseHistoryActivity"
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, ViewPurchaseHistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .also {
                    startActivity(it)
                }
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * onResume
     * 1. Calls "displayAllProductInfo" method
     */
    override fun onResume() {
        super.onResume()

        displayAllProductInfo()
    }

    /**
     * onSaveInstanceState
     * 1. Saves "listName" & "totalPrice"
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(LIST_NAME, listName)
        outState.putDouble(TOTAL_PRICE, totalPrice)

    }

    /**
     * onRestoreInstanceState
     * 1. Restores "listName" & "totalPrice"
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listName = savedInstanceState.getString(LIST_NAME)!!
        totalPrice = savedInstanceState.getDouble(TOTAL_PRICE)
    }
}