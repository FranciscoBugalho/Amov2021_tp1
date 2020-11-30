package pt.isec.amovtp1.grocerylistmanagement

import android.app.Dialog
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PurchaseProductsActivity : AppCompatActivity() {
    private lateinit var listName: String
    private var allProducts = mutableMapOf<String, ArrayList<String>>()
    private var boughtProducts = mutableMapOf<String, ArrayList<String>>()
    private lateinit var db : GMLDatabase

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

        val button = findViewById<Button>(R.id.btnSettings)
        val drawable = getDrawable(R.drawable.settings_btn)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable!!.colorFilter = BlendModeColorFilter(Color.DKGRAY, BlendMode.SRC_IN)
        } else {
            drawable!!.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN)
        }
        button.background = drawable
        button.setOnClickListener {
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
                        allProducts = this@PurchaseProductsActivity.allProducts.toSortedMap()
                        setupScrollViews(this@PurchaseProductsActivity.allProducts, true)
                        boughtProducts = this@PurchaseProductsActivity.boughtProducts.toSortedMap()
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts, false)
                    }
                    position == 1 -> {
                        allProducts = this@PurchaseProductsActivity.allProducts.toSortedMap(compareByDescending { it })
                        setupScrollViews(this@PurchaseProductsActivity.allProducts, true)
                        boughtProducts = this@PurchaseProductsActivity.boughtProducts.toSortedMap(compareByDescending { it })
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts, false)
                    }
                    position > 2 -> {
                        setupScrollViews(this@PurchaseProductsActivity.allProducts.filterValues { it[0] == orderList[position] } as MutableMap<String, ArrayList<String>>, true)
                        setupScrollViews(this@PurchaseProductsActivity.boughtProducts.filterValues { it[0] == orderList[position] } as MutableMap<String, ArrayList<String>>, false)
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
        btnFinishListPurchase.isEnabled = false
        btnFinishListPurchase.setOnClickListener {
            finishPurchase(tvTotal.text.toString())
        }
    }

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
                db.setListAsBought(listName)
            } else {
                val ln = listName + " " + Utils.convertDateToStrCard(Date()) + "_" + Utils.convertTimeToStrCard(Date())
                db.saveList(ln, getProductQuantity(boughtProducts))
                db.setListAsBought(ln)
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

    private fun getProductQuantity(boughtProducts: MutableMap<String, ArrayList<String>>): HashMap<String, String?> {
        val productAndQuantities = hashMapOf<String, String?>()

        for(key in boughtProducts.keys)
            productAndQuantities[key] = boughtProducts[key]!![1]

        return productAndQuantities
    }

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
                    setupScrollViews(allProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, true)
                    setupScrollViews(boughtProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, false)
                } else {
                    setupScrollViews(boughtProducts, false)
                    setupScrollViews(allProducts, true)
                }
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
    }

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
            tvProductName.maxLines = 1
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
                param.weight = 0.30f

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
                button.background = getDrawable(R.drawable.add_btn)
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

                /*
                param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                param.weight = 0.20f

                // Create the Remove Button
                button.layoutParams = param
                button.tag = "btnRemove$key"
                val drawable = getDrawable(R.drawable.remove_btn)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    drawable!!.colorFilter = BlendModeColorFilter(Color.RED, BlendMode.SRC_IN)
                } else {
                    drawable!!.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                }
                button.background = drawable

                // Edit Product
                button.setOnClickListener {
                    removeProduct(key, productInfo)
                }
                */
                linearLayout.addView(tvQuantity)
                linearLayout.addView(tvPrice)
            }

            //linearLayout.addView(button)
            llScrollView.addView(linearLayout)
        }

    }

    private fun purchaseProduct(productName: String, productInfo: ArrayList<String>?, quantity: String, etPrice: EditText) {
        if (etPrice.text.isEmpty() && productInfo!!.size == 2) {
            etPrice.error = getString(R.string.pp_et_price_error)
        } else if (etPrice.text.isEmpty() && productInfo!!.size == 3) {
            productInfo.removeAt(1)
            productInfo.add(1, quantity)
            val price = productInfo[2].split(" ")[0]
            productInfo.removeAt(2)
            productInfo.add(price)
            boughtProducts[productName] = productInfo
            allProducts.remove(productName)
        } else if(etPrice.text.isNotEmpty()) {
            productInfo!!.removeAt(1)
            productInfo.add(1, quantity)
            productInfo.add(etPrice.text.toString())
            boughtProducts[productName] = productInfo
            allProducts.remove(productName)
        }

        val q = quantity.split(" ")
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        tvTotal.text = (tvTotal.text.toString().toDouble() + q[0].toDouble()).toString()

        if(sOrderCategories.selectedItemId > 2) {
            setupScrollViews(allProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, true)
            setupScrollViews(boughtProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, false)
        } else {
            setupScrollViews(boughtProducts, false)
            setupScrollViews(allProducts, true)
        }

        // Enable if the price is bigger than "0.0" and the bought product list is not empty
        btnFinishListPurchase.isEnabled = tvTotal.text.toString() != getString(R.string.total_start_number) && boughtProducts.isNotEmpty()
    }

    private fun removeProduct(productName: String, productInfo: ArrayList<String>) {
        val qnt = productInfo[1].split(" ")
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
        tvTotal.text = (tvTotal.text.toString().toDouble() - qnt[0].toDouble()).toString()
        if(tvTotal.text.toString() < "0")
            tvTotal.text = "0.0"

        allProducts[productName] = productInfo
        boughtProducts.remove(productName)

        if(sOrderCategories.selectedItemId > 2) {
            setupScrollViews(allProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, true)
            setupScrollViews(boughtProducts.filterValues { it[0] == sOrderCategories.selectedItem } as MutableMap<String, ArrayList<String>>, false)
        } else {
            setupScrollViews(boughtProducts, false)
            setupScrollViews(allProducts, true)
        }

        // Enable if the price is bigger than "0.0" and the bought product list is not empty
        btnFinishListPurchase.isEnabled = tvTotal.text.toString() != getString(R.string.total_start_number) && boughtProducts.isNotEmpty()
    }

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
            textView.text = allPrices[i]
            textView.setTextColor(Color.BLACK)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView.gravity = Gravity.START
            //-------------------------------------

            linearLayout.addView(textView)

            linearLayoutPrices.addView(linearLayout)
        }
    }

    private fun getUnitPositionOnSpinner(unitName: String): Int {
        val units = db.getAllUnitsNames()
        for(i in units.indices)
            if(units[i] == unitName)
                return i
        return 0
    }

    fun getAllProductsInformation() {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(ALL_PRODCUTS, allProducts as HashMap<String, ArrayList<String>>)
        outState.putSerializable(PRUCHASED_PRODUCTS, boughtProducts as HashMap<String, ArrayList<String>>)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        allProducts = savedInstanceState.getSerializable(ALL_PRODCUTS) as HashMap<String, ArrayList<String>>
        boughtProducts = savedInstanceState.getSerializable(PRUCHASED_PRODUCTS) as HashMap<String, ArrayList<String>>

        setupScrollViews(boughtProducts, false)
        setupScrollViews(allProducts, true)
    }

    fun <K, V> Map<K, V>.toMutableMap(): MutableMap<K, V> {
        return HashMap(this)
    }

}