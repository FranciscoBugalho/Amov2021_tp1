package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_purchase_products.*
import kotlinx.android.synthetic.main.set_quantity_dialog.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.IntentConstants.LIST_NAME_TO_EDIT
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.ALL_PRODCUTS
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRUCHASED_PRODUCTS
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase
import java.util.*

class PurchaseProductsActivity : AppCompatActivity() {
    private lateinit var listName: String
    private var allProducts = mutableMapOf<String, ArrayList<String>>()
    private var boughtProducts = mutableMapOf<String, ArrayList<String>>()
    private lateinit var db : GMLDatabase

    /**
     * onCreate
     * 1. Sets the content view to the "activity_purchase_products" LinearLayout
     * 2. Connects to the database
     * 3. Sets a "supportActionBar"
     * 4. Sets the "tvTitleListPurchase" TextView's text as the "listName" passed to this Activity through an Intent
     * 5. If the "savedInstanceState" is null (this activity is created), call the method "setupScrollViews"
     * 6. Sets a "onClickListener" for the "btnSettings" button
     * 7. Gets the spinner (dropdown) "sOrderCategories", defines it's options and "onItemSelectedListener"
     * 8. Sets the "btnFinishListPurchase" onClickListener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_products)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.purchase_products)

        // Set List Name on the TextView
        listName = intent.getStringExtra(LIST_NAME_TO_EDIT).toString()
        findViewById<TextView>(R.id.tvTitleListPurchase).text = listName

        if(savedInstanceState == null) {
            getAllProductsInformation()
            setupScrollViews(allProducts, true)
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            insertNewUnit()
            return@setOnClickListener
        }

        // Add information to the Spinner
        var orderList = arrayListOf(getString(R.string.alphabetical_order_asc_text), getString(R.string.alphabetical_order_desc_text), "-------------------------")
        orderList = (orderList + db.getAllCategoryNames()) as ArrayList<String>
        sOrderCategories.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orderList)
        sOrderCategories.setSelection(2)
        // Create the listener
        sOrderCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    position == 0 -> {
                        allProducts = this@PurchaseProductsActivity.allProducts.toSortedMap(String.CASE_INSENSITIVE_ORDER)
                        setupScrollViews(this@PurchaseProductsActivity.allProducts, true)
                        boughtProducts = this@PurchaseProductsActivity.boughtProducts.toSortedMap(String.CASE_INSENSITIVE_ORDER)
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts, false)
                    }
                    position == 1 -> {
                        allProducts = this@PurchaseProductsActivity.allProducts.toSortedMap(compareByDescending(String.CASE_INSENSITIVE_ORDER, { it }))
                        setupScrollViews(this@PurchaseProductsActivity.allProducts, true)
                        boughtProducts = this@PurchaseProductsActivity.boughtProducts.toSortedMap(compareByDescending(String.CASE_INSENSITIVE_ORDER, { it }))
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts, false)
                    }
                    position > 2 -> {
                        setupScrollViews(this@PurchaseProductsActivity.allProducts.filterValues {
                            it[0].equals(orderList[position], ignoreCase = true) } as MutableMap<String, ArrayList<String>>, true)
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts.filterValues {
                            it[0].equals(orderList[position], ignoreCase = true) } as MutableMap<String, ArrayList<String>>, false)
                    }
                    else -> {
                        setupScrollViews(this@PurchaseProductsActivity.allProducts, true)
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts, false)
                    }
                }
            }

            // Nothing happens
            override fun onNothingSelected(parent: AdapterView<*>?) { }

        }

        // By default the button is disabled
        if(savedInstanceState == null)
            btnFinishListPurchase.isEnabled = false
        btnFinishListPurchase.setOnClickListener {
            finishPurchase(tvTotal.text.toString())
        }
    }

    /**
     * finishPurchase
     * 1. Creates a new "dialog", sets it's content view as the "insert_new_unit_dialog" layout, sets it's properties and shows it
     * 2. Gets the "llPurchasedProducts" LinearLayout and the "tvPurchaseListName" TextView from the dialog
     * 3. For each bought product creates a "linearLayout" and 3 TextView: tvQuantity, tvQuantity and tvPrice, sets their properties and adds the TextViews to the "linearLayout"
     * 4. Adds the "linearLayout" to the "llPurchasedProducts" LinearLayout
     * 5. Creates a LinearLayout, "llPrice", and a TextView, "tvTotalPrice", sets their properties and adds "tvTotalPrice" to "llPrice" and "llPrice" to "llPurchasedProducts"
     * 6. Sets a 'onClickListener' for the "btnFinishPurchase" that operates along the list and it's products and redirects to the "MainActivity" dismissing the "dialog"
     * 7. Sets a 'onClickListener' for the "btnCancel" that dismisses the "dialog"
     */
    @SuppressLint("SetTextI18n")
    private fun finishPurchase(totalPrice: String) {
        // Create the Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.purchase_receipt_dialog)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog layout parameters
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams

        dialog.show()

        val llPurchasedProducts = dialog.findViewById<LinearLayout>(R.id.llPurchasedProducts)

        val tvPurchaseListName = dialog.findViewById<TextView>(R.id.tvPurchaseListName)
        tvPurchaseListName.text = listName

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
            param.weight = 0.50f

            // Create the TextView
            val tvProductName = TextView(this)
            tvProductName.layoutParams = param
            tvProductName.tag = "tv$key"
            tvProductName.text = key
            tvProductName.setTextColor(Color.BLACK)
            tvProductName.gravity = Gravity.START
            tvProductName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvProductName.maxLines = 2
            //-----------------------

            val productInfo = boughtProducts[key]
            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.20f

            // Create the TextView
            val tvQuantity = TextView(this)
            tvQuantity.layoutParams = param
            tvQuantity.tag = "tvQnt$key"
            tvQuantity.text = productInfo!![1]
            tvQuantity.setTextColor(Color.BLACK)
            tvQuantity.gravity = Gravity.END
            tvQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            //-----------------------

            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 0.20f

            // Create the TextView
            val tvPrice = TextView(this)
            tvPrice.layoutParams = param
            tvPrice.tag = "tvPrice$key"
            tvPrice.setTextColor(Color.BLACK)
            tvPrice.gravity = Gravity.END
            tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            if(productInfo.size == 3) {
                tvPrice.text = productInfo[2] + getString(R.string.price_sign)
            } else if(productInfo.size == 4) { // Has new price set
                tvPrice.text = productInfo[3] + getString(R.string.price_sign)
            }

            linearLayout.addView(tvProductName)
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

        val btnCancel = dialog.findViewById(R.id.btnCancelPurchase) as Button
        val btnFinishPurchase = dialog.findViewById(R.id.btnFinishPurchase) as Button

        btnFinishPurchase.setOnClickListener {
            // If the all products list is empty just set the list as bought in the database
            if(allProducts.isEmpty()) {
                db.setListAsBought(listName, getProductQuantity(boughtProducts))
            } else {
                val ln = listName + " " + Utils.convertDateToStrCard(Date()) + "_" + Utils.convertTimeToStrCard(Date())
                db.saveList(ln, getProductQuantity(boughtProducts))
                db.setListAsBought(ln, null)
            }

            // Save the products price
            for(key in boughtProducts.keys) {
                if(boughtProducts[key]!!.size == 4)
                    db.saveProductPrice(db.getProductIdByName(key), boughtProducts[key]!![3])
                else if(boughtProducts[key]!!.size == 3) {
                    if (db.getLastProductPrice(key) == null)
                        db.saveProductPrice(db.getProductIdByName(key), boughtProducts[key]!![2])
                }
            }

            Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                        startActivity(it)
                    }
            finish()

            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    /**
     * getProductQuantity
     * 1. Returns a "hashMap" for all the products and their quantities
     */
    private fun getProductQuantity(boughtProducts: MutableMap<String, ArrayList<String>>): HashMap<String, String?> {
        val productAndQuantities = hashMapOf<String, String?>()

        for(key in boughtProducts.keys)
            productAndQuantities[key] = boughtProducts[key]!![1]

        return productAndQuantities
    }

    /**
     * insertNewUnit
     * 1. Creates a new "dialog", sets it's content view as the "insert_new_unit_dialog" layout, sets it's properties and shows it
     * 2. Gets the buttons "btnCancel", "btnSave" and the edit text "etDialogNewUnit"
     * 3. Sets a 'onClickListener' for the "btnSave" that has multiple behaviours depending on the "etDialogNewUnit" data.
     * 4. When the "dialog" is dismissed by clicking "btnSave", updates the scrollViews calling "setupScrollViews"
     * 5. Sets a 'onClickListener' for the "btnCancel" that dismisses the "dialog"
     */
    private fun insertNewUnit() {
        // Create the Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.insert_new_unit_dialog)
        dialog.setCanceledOnTouchOutside(true)

        dialog.show()

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSaveUnit) as Button
        val etDialogNewUnit = dialog.findViewById(R.id.etDialogNewUnit) as EditText

        btnSave.setOnClickListener {
            if(etDialogNewUnit.text.isEmpty()) {
                etDialogNewUnit.error = getString(R.string.this_field_must_be_filled)
            } else if(!db.addNewUnit(etDialogNewUnit.text.toString()))
                etDialogNewUnit.error = getString(R.string.unit_already_exists_error)
            else {
                dialog.dismiss()
                if(sOrderCategories.selectedItemId > 2) {
                    setupScrollViews(allProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, true)
                    setupScrollViews(boughtProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, false)
                } else {
                    setupScrollViews(boughtProducts, false)
                    setupScrollViews(allProducts, true)
                }
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    /**
     * setupScrollViews
     * 1. Gets either the "llAllProducts" (all products) or "llBoughtProducts" (bought products) LinearLayouts as "llScrollView" and removes all it's views
     * 2. For each  product to add to the "llScrollView" creates a "linearLayout", a "TextView" (tvProductName) and a button (add to list button), and adds these components to the "linearLayout"
     * 3. Adds more data and components to the "llScrollView", but these components ant their data differ if the "llScrollView" is correspondent to a view for all products or only for bought products
     */
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setupScrollViews(productsToView: MutableMap<String, ArrayList<String>>, isAllProductView: Boolean) {
        val llScrollView: LinearLayout = if(isAllProductView)
            findViewById(R.id.llAllProducts)
        else
            findViewById(R.id.llBoughtProducts)
        llScrollView.removeAllViews()

        for(key in productsToView.keys) {
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
            if(isAllProductView)
                param.weight = 0.50f
            else
                param.weight = 0.70f

            // Create the TextView
            val tvProductName = TextView(this)
            tvProductName.layoutParams = param
            tvProductName.tag = "tv$key"
            tvProductName.text = key
            tvProductName.setTextColor(Color.BLACK)
            tvProductName.gravity = Gravity.START
            tvProductName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvProductName.maxLines = 2
            //-----------------------
            linearLayout.addView(tvProductName)

            // Initialize the Button to add or remove the product
            val button = Button(this)

            // If it is the ScrollView for all products
            if(isAllProductView) {
                // Define LinearLayout onLongClickListener to view the prices history
                linearLayout.setOnLongClickListener {
                    showPriceHistory(key)
                    true
                }

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.25f

                // Create the EditText for the product quantity
                val etQuantity = EditText(this)
                etQuantity.layoutParams = param
                etQuantity.tag = "etQuantity$key"
                etQuantity.setTextColor(Color.BLACK)
                etQuantity.gravity = Gravity.END
                etQuantity.inputType = InputType.TYPE_CLASS_NUMBER.or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                etQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.45f

                // Create the Spinner
                val spinner = Spinner(this)
                spinner.tag = "spinner$key"
                spinner.layoutParams = param
                spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, db.getAllUnitsNames())

                val productInfo = productsToView[key]
                // Have at least a quantity
                if (productInfo!!.size > 0) {
                    val amount = productInfo[1].split(" ")

                    // Have quantity and unit
                    if (amount.size > 1) {
                        spinner.setSelection(getUnitPositionOnSpinner(amount[1]))
                        etQuantity.setText(amount[0])
                    } else etQuantity.setText("0.0")

                }

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.30f

                // Create the EditText for the product price
                val etPrice = EditText(this)
                etPrice.layoutParams = param
                etPrice.tag = "etPrice$key"
                etPrice.setTextColor(Color.BLACK)
                etPrice.gravity = Gravity.END
                etPrice.inputType = InputType.TYPE_CLASS_NUMBER.or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                etPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

                if (productInfo.size == 3) {
                    val lastPrice = productInfo[2].split(" ")
                    etPrice.hint = lastPrice[0]
                } else
                    etPrice.hint = getString(R.string.pp_et_price_placeholder)

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.05f

                // Create the TextView with the price sign
                val tvPriceSign = TextView(this)
                tvPriceSign.layoutParams = param
                tvPriceSign.tag = "tvPrice$key"
                tvPriceSign.text = getString(R.string.price_sign)
                tvPriceSign.setTextColor(Color.BLACK)
                tvPriceSign.gravity = Gravity.START
                tvPriceSign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.20f

                // Create the Add Button
                button.layoutParams = param
                button.tag = "btnBuy$key"

                val drawable = getDrawable(R.drawable.add_btn)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    drawable!!.colorFilter = BlendModeColorFilter(resources.getColor(R.color.theme_blue), BlendMode.SRC_IN)
                } else {
                    drawable!!.setColorFilter(resources.getColor(R.color.theme_blue), PorterDuff.Mode.SRC_IN)
                }
                button.background = drawable

                // Edit Product
                button.setOnClickListener {
                    val etQnt = linearLayout.getChildAt(1) as EditText
                    val etPrice = linearLayout.getChildAt(3) as EditText

                    if(etQnt.text.toString() == "0.0" || etQnt.text.toString() == "0")
                        etQnt.error = getString(R.string.pp_et_quantity_error)
                    else if (etPrice.text.isEmpty() && etPrice.hint.toString() == getString(R.string.pp_et_price_placeholder))
                        etPrice.error = getString(R.string.pp_et_price_error)
                    else
                        purchaseProduct(key, productsToView[key], etQnt.text.toString() + " " + spinner.selectedItem.toString(), etPrice)
                }

                linearLayout.addView(etQuantity)
                linearLayout.addView(spinner)
                linearLayout.addView(etPrice)
                linearLayout.addView(tvPriceSign)
                linearLayout.addView(button)
            } else {
                val productInfo = productsToView[key]
                // Define LinearLayout onClickListener to remove the product
                linearLayout.setOnClickListener {
                    removeProduct(key, productInfo!!)
                    return@setOnClickListener
                }

                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.40f

                // Create the TextView
                val tvQuantity = TextView(this)
                tvQuantity.layoutParams = param
                tvQuantity.tag = "tvQnt$key"
                tvQuantity.text = productInfo!![1]
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

                if(productInfo.size == 3) {
                    tvPrice.text = productInfo[2] + getString(R.string.price_sign)
                } else if(productInfo.size == 4) { // Has new price set
                    tvPrice.text = productInfo[3] + getString(R.string.price_sign)
                }

                // Define TextView onLongClickListener to view the prices history
                tvPrice.setOnLongClickListener {
                    showPriceHistory(key)
                    true
                }
                //-----------------------
                linearLayout.addView(tvQuantity)
                linearLayout.addView(tvPrice)
            }

            //linearLayout.addView(button)
            llScrollView.addView(linearLayout)
        }

    }

    /**
     * purchaseProduct
     * 1. If the the price on the 'etPrice' edit text is empty and the 'productInfo' has no price : error
     * 2. If the the price on the 'etPrice' edit text is empty but the 'productInfo' has a price : sets the price as the product's price on 'productInfo'
     * 3. If the the price on the 'etPrice' edit text isn't empty : sets the price as the product's price on 'etPrice'
     * 4. Gets the "tvTotal" TextView and sets the new total price
     * 5. Updates the scrollViews calling "setupScrollViews"
     */
    private fun purchaseProduct(productName: String, productInfo: ArrayList<String>?, quantity: String, etPrice: EditText) {
        var price = 0.0
        if (etPrice.text.isEmpty() && productInfo!!.size == 2) {
            etPrice.error = getString(R.string.pp_et_price_error)
        } else if (etPrice.text.isEmpty() && productInfo!!.size == 3) {
            productInfo.removeAt(1)
            productInfo.add(1, quantity)
            val priceAux = productInfo[2].split(" ")[0]
            productInfo.removeAt(2)
            price = priceAux.toDouble()
            productInfo.add(priceAux)
            boughtProducts[productName] = productInfo
            allProducts.remove(productName)
        } else if(etPrice.text.isNotEmpty()) {
            productInfo!!.removeAt(1)
            productInfo.add(1, quantity)
            productInfo.add(etPrice.text.toString())
            price = etPrice.text.toString().toDouble()
            boughtProducts[productName] = productInfo
            allProducts.remove(productName)
        }

        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        tvTotal.text = (tvTotal.text.toString().toDouble() + price).toString()

        if(sOrderCategories.selectedItemId > 2) {
            setupScrollViews(allProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, true)
            setupScrollViews(boughtProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, false)
        } else {
            setupScrollViews(boughtProducts, false)
            setupScrollViews(allProducts, true)
        }

        // Enable if the price is bigger than "0.0" and the bought product list is not empty
        btnFinishListPurchase.isEnabled = tvTotal.text.toString() != getString(R.string.total_start_number) && boughtProducts.isNotEmpty()
    }

    /**
     * removeProduct
     * 1. If 'productInfo' of a product equals 4 (has more than 1 price associated) or, equals 3 and it's 'LastProductPrice' is null, sets the quantity and removes the last price
     * 2. Gets the "tvTotal" TextView and sets the new total price
     * 3. Updates the product on the "allProducts" global variable and removes the product from the "boughtProducts" global variable
     * 4. Updates the scrollViews calling "setupScrollViews"
     */
    private fun removeProduct(productName: String, productInfo: ArrayList<String>) {
        val price = productInfo[2]
        if(productInfo.size == 4) {
            // Reset the quantity value
            val quantity = db.getProductQuantityAndUnit(listName, productName)
            if(quantity == "N/A") productInfo[1] = "0.0"
            else productInfo[1] = quantity
            // Remove the price
            productInfo.removeLast()
        } else if(productInfo.size == 3 && db.getLastProductPrice(productName) == null) {
            // Reset the quantity value
            val quantity = db.getProductQuantityAndUnit(listName, productName)
            if(quantity == "N/A") productInfo[1] = "0.0"
            else productInfo[1] = quantity
            // Remove the price
            productInfo.removeLast()
        }

        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        tvTotal.text = (tvTotal.text.toString().toDouble() - price.toDouble()).toString()
        if(tvTotal.text.toString() < "0")
            tvTotal.text = "0.0"

        allProducts[productName] = productInfo
        boughtProducts.remove(productName)

        if(sOrderCategories.selectedItemId > 2) {
            setupScrollViews(allProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, true)
            setupScrollViews(boughtProducts.filterValues { it[0].equals(sOrderCategories.selectedItem.toString(), ignoreCase = true) } as MutableMap<String, ArrayList<String>>, false)
        } else {
            setupScrollViews(boughtProducts, false)
            setupScrollViews(allProducts, true)
        }

        // Enable if the price is bigger than "0.0" and the bought product list is not empty
        btnFinishListPurchase.isEnabled = tvTotal.text.toString() != getString(R.string.total_start_number) && boughtProducts.isNotEmpty()
    }

    /**
     * showPriceHistory
     * 1. Creates a "Dialog" to insert the price history & sets it's params and text
     * 2. Gets the "llPriceHistory" LinearLayout
     * 3. Gets the prices of the selected product from the database
     * 4. If the product has no previous prices, creates a "linearLayout" and a "textView" with an indicative text and add the "textView" to the "linearLayout"
     * 5. If the product has precious prices does the same process but for each price
     */
    @SuppressLint("SetTextI18n")
    private fun showPriceHistory(productName: String) {
        // Create the Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.price_history_dialog)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog layout parameters
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams

        dialog.show()

        val linearLayoutPrices = dialog.findViewById<LinearLayout>(R.id.llPriceHistory)

        dialog.findViewById<TextView>(R.id.tvProductNamePriceHistory).text = "$productName ${getString(R.string.ph_dialog_tv_title)}"

        val allPrices = db.getAllProductPrices(productName)
        if(allPrices == null) {
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
            textView.tag = "tvPricesEmpty"
            textView.text = getString(R.string.no_prices_set_for_this_product)
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            //-------------------------------------
            linearLayout.addView(textView)
            linearLayoutPrices.addView(linearLayout)
            return
        }

        for(i in allPrices.indices) {
            // Create LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "llPrices$i"
            //---------------------------------------

            // Create the TextView
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.tag = "tvPrice${allPrices[i]}"
            val txt = allPrices[i].split(" ")
            textView.text = txt[0] + resources.getString(R.string.price_sign) + "     (" + txt[1] + " " + txt[2] + ")"
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView.gravity = Gravity.START
            //-------------------------------------

            linearLayout.addView(textView)

            linearLayoutPrices.addView(linearLayout)
        }
    }

    /**
     * getUnitPositionOnSpinner
     * 1. For each product returns it's index
     */
    private fun getUnitPositionOnSpinner(unitName: String): Int {
        val units = db.getAllUnitsNames()
        for(i in units.indices)
            if(units[i] == unitName)
                return i
        return 0
    }

    /**
     * getAllProductsInformation
     * 1. Gets all products of the list from the database
     * 2. For each product gets it's data into an ArrayList<String> and adds it to the global variable "allProducts" mutableMapOf<String, ArrayList<String>>, being the key the name of the product
     */
    private fun getAllProductsInformation() {
        val products = db.productsInThisList(listName)

        var qntPrice = arrayListOf<String>()
        for (i in products.indices) {
            // Get product category
            qntPrice.add(db.getCategoryByProductName(products[i]))

            // Get product quantity and unit
            qntPrice.add(db.getProductQuantityAndUnit(listName, products[i]))

            // Get last prices and its date
            val lastPrice = db.getLastProductPrice(products[i])

            if(lastPrice != null)
                qntPrice.add(lastPrice)

            allProducts[products[i]] = qntPrice
            qntPrice = arrayListOf()
        }
    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar"
     * 2. If the "home" button was selected, redirects to the "ManageShoppingListsActivity"
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, ManageShoppingListsActivity::class.java)
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
     * onSaveInstanceState
     * 1. Saves "allProducts" & "boughtProducts"
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(ALL_PRODCUTS, allProducts as HashMap<String, ArrayList<String>>)
        outState.putSerializable(PRUCHASED_PRODUCTS, boughtProducts as HashMap<String, ArrayList<String>>)
    }

    /**
     * onRestoreInstanceState
     * 1. Restores "allProducts" & "boughtProducts"
     * 2. Calls the "setupScrollViews" for both refered variables
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        allProducts = savedInstanceState.getSerializable(ALL_PRODCUTS) as HashMap<String, ArrayList<String>>
        boughtProducts = savedInstanceState.getSerializable(PRUCHASED_PRODUCTS) as HashMap<String, ArrayList<String>>

        setupScrollViews(boughtProducts, false)
        setupScrollViews(allProducts, true)
    }

}