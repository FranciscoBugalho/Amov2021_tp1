package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_manage_products.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase

class ManageProductsActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase

    /**
     * onCreate
     * 1. Sets the view to "activity_manage_products"
     * 2. Connects the context to the Database
     * 3. Sets an action bar on the top of the screen
     * 4. If the number of products is 0, calls "presentError" method and outs this method
     * 5. If the number of products is > 0
     * 6. Sets a spinner (a dropdown, sOrderProducts) with the order options of the product's list
     * 7. Gets all the products of a certain category (or every product if one isn't selected - default) from the Database
     * 8. Orders the product's list order according to the option selected on the spinner
     * 9. Displays products
     * 10. Sets "btnAddNewProduct", a button that when is clicked redirects to "CreateNewProductActivity"
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_products)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.manage_all_products_title)

        // Add information to the Spinner
        var orderProducts = arrayListOf(getString(R.string.alphabetical_order_asc_text), getString(R.string.alphabetical_order_desc_text), "-------------------------")
        orderProducts = (orderProducts + db.getAllCategoryNames()) as ArrayList<String>
        sOrderProducts.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orderProducts)
        sOrderProducts.setSelection(2)

        // If there are no products in the database
        if(db.countDbProducts() == 0) {
            presentError(getString(R.string.you_have_to_add_products_first))
            return
        }

        // Get the products to present on the ScrollView
        val products = db.getAllProductsNameCategory()

        sOrderProducts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    position == 0 -> {
                        displayAllProducts(products.toSortedMap(String.CASE_INSENSITIVE_ORDER))
                    }
                    position == 1 -> {
                        displayAllProducts(products.toSortedMap(compareByDescending ( String.CASE_INSENSITIVE_ORDER, { it }) ))
                    }
                    position > 2 -> {
                        displayAllProducts(products.filterValues { it.equals(orderProducts[position], ignoreCase = true) } as MutableMap<String, String>)
                    }
                    else -> {
                        displayAllProducts(products)
                    }
                }
            }

            // Nothing happens
            override fun onNothingSelected(parent: AdapterView<*>?) { }

        }

        displayAllProducts(products)

        val addBtn = findViewById<Button>(R.id.btnAddNewProduct)
        val drawable = getDrawable(R.drawable.add_btn)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable!!.colorFilter = BlendModeColorFilter(resources.getColor(R.color.theme_orange), BlendMode.SRC_IN)
        }
        else{
            drawable!!.setColorFilter(resources.getColor(R.color.theme_orange),PorterDuff.Mode.SRC_IN )
        }
        addBtn.background = drawable
        addBtn.setOnClickListener {
            db.closeDB()
            Intent(this, CreateNewProductActivity::class.java)
                .putExtra(Constants.IntentConstants.IS_VIEW_PRODUCTS, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .also {
                    startActivity(it)
                }
        }
    }

    /**
     * displayAllProducts
     * 1. Gets the LinearLayout of the list of products and clears the layout (important for situations where the screen rotates)
     * 2. For each product, then, creates a LinearLayout and sets it's properties
     * 3. For each LinearLayout, adds 2 TextViews and a Button (product's name, product's category, edit button)
     * 4. Adds to the view's LinearLayout every product's LinearLayout
     */
    private fun displayAllProducts(products: MutableMap<String, String>) {
        val llProducts = findViewById<LinearLayout>(R.id.llShowAllProducts)
        llProducts.removeAllViews()

        for(key in products.keys) {
            // Create the LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "ll$key"
            linearLayout.setOnLongClickListener {
                showProductObservations(key)
                true
            }


            var param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 1.0f

            // Create the TextView
            val tvProductName = TextView(this)
            tvProductName.layoutParams = param
            tvProductName.tag = "tv$key"
            tvProductName.text = key
            tvProductName.setTextColor(Color.BLACK)
            //tvProductName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvProductName.maxLines = 1
            tvProductName.gravity = Gravity.CENTER_VERTICAL
            tvProductName.setOnLongClickListener {
                showProductObservations(key)
                true
            }
            //------------------------------------

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 0.70f

            // Create the TextView
            val textView = TextView(this)
            textView.layoutParams = param
            textView.tag = "tv$key"
            textView.text = products[key]
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.maxLines = 1
            //-------------------------------------

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 0.20f

            // Create the Button
            val button = Button(this)
            button.layoutParams = param
            button.tag = "btn$key"
            button.background = getDrawable(R.drawable.edit_product_btn)
            button.gravity = Gravity.END
            // Edit Product
            button.setOnClickListener {
                db.closeDB()
                Intent(this, CreateNewProductActivity::class.java)
                    .putExtra(Constants.IntentConstants.IS_EDIT_PRODUCT, 1)
                    .putExtra(Constants.IntentConstants.PRODUCT_NAME_TO_EDIT, key)
                    .putExtra(Constants.IntentConstants.IS_VIEW_PRODUCTS, true)
                    .also {
                        startActivity(it)
                    }
            }
            //------------------------------------

            // Add components to the LinearLayout
            linearLayout.addView(tvProductName)
            linearLayout.addView(textView)
            linearLayout.addView(button)

            // Add the LinearLayout to the main Layout
            llProducts.addView(linearLayout)
        }
    }

    /**
     * showProductObservations
     * 1. Sets all the fields on the view that edits a product's information
     */
    @SuppressLint("SetTextI18n")
    private fun showProductObservations(productName: String) {
        // Create the Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_show_product_observations)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog layout parameters
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams

        dialog.show()

        val linearLayoutObservations = dialog.findViewById<LinearLayout>(R.id.llShowObservations)

        val productObservations = db.getAllObservations(productName)

        if(productObservations.isEmpty()) {
            val ivProductImage = dialog.findViewById<ImageView>(R.id.ivProductImage)
            val productFilePath = db.getProductFilePath(productName)
            if(productFilePath == Constants.ASSET_IMAGE_PATH_NO_IMG)
                Utils.setImgFromAsset(ivProductImage, Constants.ASSET_IMAGE_PATH_NO_IMG)
            else
                Utils.setPic(ivProductImage, productFilePath)

            val tvProductName = dialog.findViewById<TextView>(R.id.tvProductName)

            val productBrand = db.getProductBrand(productName)
            if(productBrand == null) {
                // Set the TextView with the product name
                tvProductName.text = productName
            } else {
                // Set the TextView with the product name and its brand
                tvProductName.text = "$productName ($productBrand)"
            }
            //-------------------------------------------------------

            // Create a LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            //-------------------------------------------------------

            // Create the TextView
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.tag = "tvObservationsEmpty"
            textView.text = getString(R.string.no_observations_set_for_this_product_yet)
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            //-------------------------------------
            linearLayout.addView(textView)
            linearLayoutObservations.addView(linearLayout)
            return
        }

        for(i in productObservations.indices) {
            // Set the ImageView
            val ivProductImage = dialog.findViewById<ImageView>(R.id.ivProductImage)
            val productFilePath = db.getProductFilePath(productName)
            if(productFilePath == Constants.ASSET_IMAGE_PATH_NO_IMG)
                Utils.setImgFromAsset(ivProductImage, Constants.ASSET_IMAGE_PATH_NO_IMG)
            else
                Utils.setPic(ivProductImage, productFilePath)

            val tvProductName = dialog.findViewById<TextView>(R.id.tvProductName)

            val productBrand = db.getProductBrand(productName)
            if(productBrand == null) {
                // Set the TextView with the product name
                tvProductName.text = productName
            } else {
                // Set the TextView with the product name and its brand
                tvProductName.text = "$productName ($productBrand)"
            }

            // Create LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "llObservation$i"
            //---------------------------------------

            // Create the TextView
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.tag = "tvObservation${Utils.convertDateToDatetime(productObservations[i].date)}"
            textView.text = "${productObservations[i].observation}. (${Utils.convertDateToDatetime(productObservations[i].date)}) "
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView.gravity = Gravity.START
            //-------------------------------------

            linearLayout.addView(textView)

            linearLayoutObservations.addView(linearLayout)
        }

    }

    /**
     * presentError
     * 1. Creates a linearLayout and sets it's properties
     * 2. Creates a textView and sets it's properties (error textView)
     * 3. Adds the textView to the linearLayout
     * 4. Adds the linearLayout to the "llShowAllProducts" linearLayout
     */
    private fun presentError(error: String) {
        val linearLayout = LinearLayout(this)
        linearLayout.gravity = Gravity.CENTER
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val params = linearLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 20, 0, 0)

        val textView = TextView(this)
        textView.text = error
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

        linearLayout.addView(textView)
        findViewById<LinearLayout>(R.id.llShowAllProducts).addView(linearLayout)
        return
    }

    /**
     * onCreateOptionsMenu
     * 1. Creates the "manage_categories_units_menu"
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manage_categories_units_menu, menu)
        return true
    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar"
     * 2. If the "manageCategories" option is selected, redirects to the "ManageCategoriesUnitsActivity" activity, with the value SHOW_CATEGORIES as true in the intent's constants
     * 3. If the "manageUnits" option is selected, redirects to the "ManageCategoriesUnitsActivity" activity, with the value SHOW_CATEGORIES as false in the intent's constants
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                db.closeDB()
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                        startActivity(it)
                    }
                finish()
                return true
            }
            R.id.manageCategories -> {
                Intent(this, ManageCategoriesUnitsActivity::class.java)
                    .putExtra(Constants.IntentConstants.SHOW_CATEGORIES, true)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                        startActivity(it)
                    }
                return true
            }
            R.id.manageUnits -> {
                Intent(this, ManageCategoriesUnitsActivity::class.java)
                    .putExtra(Constants.IntentConstants.SHOW_CATEGORIES, false)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                        startActivity(it)
                    }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}