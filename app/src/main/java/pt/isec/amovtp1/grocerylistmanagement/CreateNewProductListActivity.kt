package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_create_new_product.*
import kotlinx.android.synthetic.main.activity_create_new_product_list.*
import kotlinx.android.synthetic.main.dialog_show_product_observations.*
import kotlinx.android.synthetic.main.product_list_rv.*
import kotlinx.android.synthetic.main.set_quantity_dialog.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ERROR
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.IntentConstants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SHOW_ALL_PRODUCTS
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SHOW_CHECKED_PRODUCTS
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.LIST_ID_EDIT_MODE
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.LIST_NAME_STR
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.SELECTED_PRODUCTS_STR
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase

class CreateNewProductListActivity : AppCompatActivity() {
    private lateinit var listName: String
    private var selectedProducts = hashMapOf<String, String?>()
    private var listId: Long? = null
    private lateinit var db : GMLDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product_list)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.create_new_product_list_title)

        var opt = intent.getIntExtra(IntentConstants.IS_NEW_PRODUCT, 0)
        if(opt == 1) {
            listName = intent.getStringExtra(IntentConstants.LIST_NAME)!!
            findViewById<EditText>(R.id.etListName).setText(listName)
            selectedProducts = intent.getSerializableExtra(IntentConstants.SELECTED_PRODUCTS_LIST) as HashMap<String, String?>

            // Update title on actionbar and on the textview
            val title = intent.getStringExtra(IntentConstants.MANAGE_PRODUCTS_TITLE)!!
            tvTitle!!.text = title

            if(title == getString(R.string.create_new_list_title)) {
                listId = null
                supportActionBar?.title = getString(R.string.create_new_product_list_title)
            } else
                listId = db.getListIdByName(listName)
        }

        opt = intent.getIntExtra(IntentConstants.IS_LIST_DETAILS, 0)
        if(opt == 1) {
            // Update title on actionbar and on the textview
            supportActionBar?.title = getString(R.string.edit_product_list_title)
            tvTitle!!.text = getString(R.string.edit_product_list_title)

            listName = intent.getStringExtra(IntentConstants.LIST_NAME_TO_EDIT)!!
            etListName.setText(listName)

            listId = db.getListIdByName(listName)

            findViewById<Button>(R.id.btnFinishList).text = getString(R.string.edit_list_btn_txt)

            completeFields(opt)
        } else if (opt == 2) {
            // Update title on actionbar and on the textview
            supportActionBar?.title = getString(R.string.create_list_from_existing_one_title)
            tvTitle!!.text = getString(R.string.create_list_from_existing_one_title)

            listName = intent.getStringExtra(IntentConstants.LIST_NAME_TO_EDIT)!!
            etListName.setText(listName)

            listId = db.getListIdByName(listName)

            completeFields(opt)
        }

        // Add all the products to the ScrollView
        addProductsToScrollView(SHOW_ALL_PRODUCTS)

        findViewById<Button>(R.id.btnNewProduct).setOnClickListener {
            // Save the list name if the editBox is not empty
            listName = if(etListName.text.isNotEmpty())
                etListName.text.toString()
            else ""
            Intent(this, CreateNewProductActivity::class.java)
                    .putExtra(IntentConstants.LIST_NAME, listName)
                    .putExtra(IntentConstants.SELECTED_PRODUCTS_LIST, selectedProducts)
                    .putExtra(IntentConstants.MANAGE_PRODUCTS_TITLE, tvTitle.text.toString())
                .also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.btnFinishList).setOnClickListener {
            // Have to have one product selected to create one list
            if(selectedProducts.keys.size <= 0) {
                etCategory.error = getString(R.string.you_have_to_select_at_least_one_product)
                return@setOnClickListener
            }

            // Can't create a list without a name
            if(etListName.text.isEmpty()) {
                etListName.error = getString(R.string.this_field_must_be_filled)
                return@setOnClickListener
            }

            // Edit Mode
            if(listId != null) {
                if(!db.editList(etListName.text.toString(), selectedProducts, listId!!)) {
                    etListName.error = getString(R.string.already_exists_a_list_with_this_name)
                    return@setOnClickListener
                }
            } else {
                // Save the list in the database if List name does not exists
                if (!db.saveList(etListName.text.toString(), selectedProducts)) {
                    etListName.error = getString(R.string.list_name_already_exists)
                    return@setOnClickListener
                }
            }

            db.closeDB()

            Intent(this, ManageProductListsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                startActivity(it)
            }
            finish()
            return@setOnClickListener
        }

        val etCategory = findViewById<EditText>(R.id.etCategory)
        findViewById<Button>(R.id.btnSearchCategory).setOnClickListener {
            when {
                etCategory.text.isEmpty() -> {
                    addProductsToScrollView(SHOW_ALL_PRODUCTS)
                }
                etCategory.text.toString() == getString(R.string.checked_flag) -> {
                    addProductsToScrollView(SHOW_CHECKED_PRODUCTS)
                }
                else -> {
                    addProductsToScrollView(etCategory.text.toString())
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun addProductsToScrollView(toShow: String) {
        val llProducts = findViewById<LinearLayout>(R.id.llProductCheckBoxes)
        llProducts.removeAllViews()

        // If there are no products in the database
        if(db.countDbProducts() == 0) {
            presentError(llProducts, getString(R.string.you_have_to_add_products_first))
            return
        }

        val products: HashMap<String, String>
        // Get the products to present on the ScrollView
        if(toShow == SHOW_ALL_PRODUCTS)
            products = db.getAllProductsNameCategory()
        else if(toShow == SHOW_CHECKED_PRODUCTS)
            products = db.getCheckedProductsNameCategory(selectedProducts.keys)
        else {
            products = db.getProductNameByCategory(toShow)

            // If category does not exist
            if (products.keys.contains(ERROR)) {
                presentError(llProducts, getString(R.string.category_doesnt_exist))
                return
            }
        }

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
            param.weight = 0.20f

            val buttonQuantity = ImageButton(this)
            buttonQuantity.layoutParams = param
            buttonQuantity.tag = "btnQuantity$key"
            buttonQuantity.background = null
            buttonQuantity.setOnClickListener {
                setProductQuantity(key)
                return@setOnClickListener
            }

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 1.0f

            // Create the Checkbox
            val checkBox = CheckBox(this)
            checkBox.layoutParams = param
            checkBox.tag = "cc$key"
            checkBox.text = key
            checkBox.maxLines = 1
            checkBox.setOnCheckedChangeListener { _, _ ->
                if(checkBox.isChecked) {
                    // Create an ImageButton to define the Amount of product
                    if(!selectedProducts.containsKey(key)) {
                        // Every time the CheckBox is checked the amount becomes null
                        selectedProducts[key] = null

                        // Set the image with an empty shopping car
                        Utils.setImgFromAsset(
                            buttonQuantity,
                            Constants.ASSET_IMAGE_PATH_SHOPPING_EMPTY
                        )

                        buttonQuantity.visibility = ViewGroup.VISIBLE
                    } else {
                        // If the product has a Amount already defined
                        if(selectedProducts[key] != null) {
                            val quantity = selectedProducts[key]!!.split(" ")
                            // If the quantity is zero
                            if(quantity[0] == "0.0" || quantity[0] == "0") {
                                // Set the image with an empty shopping car
                                Utils.setImgFromAsset(
                                        buttonQuantity,
                                        Constants.ASSET_IMAGE_PATH_SHOPPING_EMPTY
                                )
                            }
                            else {
                                // Set the image with an empty shopping car
                                Utils.setImgFromAsset(
                                        buttonQuantity,
                                        Constants.ASSET_IMAGE_PATH_SHOPPING_FULL
                                )
                            }
                        }
                        else {
                            // Set the image with an empty shopping car
                            Utils.setImgFromAsset(
                                buttonQuantity,
                                Constants.ASSET_IMAGE_PATH_SHOPPING_EMPTY
                            )
                        }
                    }
                }
                else {
                    // If the CheckBox is no more checked the product is removed from the selected products list
                    if(selectedProducts.containsKey(key)) {
                        selectedProducts.remove(key)

                        buttonQuantity.visibility = ViewGroup.INVISIBLE
                    }
                }
            }
            checkBox.isChecked = selectedProducts.containsKey(key)
            if(checkBox.isChecked)
                buttonQuantity.visibility = ViewGroup.VISIBLE
            else
                buttonQuantity.visibility = ViewGroup.INVISIBLE
            checkBox.setOnLongClickListener {
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
            param.weight = 0.30f

            // Create the Button
            val button = Button(this)
            button.layoutParams = param
            button.tag = "btn$key"
            button.background = getDrawable(R.drawable.edit_product_btn)
            button.gravity = Gravity.END
            // Edit Product
            button.setOnClickListener {
                // Save the list name if the editBox is not empty
                listName = if(etListName.text.isNotEmpty())
                    etListName.text.toString()
                else ""

                Intent(this, CreateNewProductActivity::class.java)
                    .putExtra(IntentConstants.IS_EDIT_PRODUCT, 1)
                    .putExtra(IntentConstants.PRODUCT_NAME_TO_EDIT, key)
                    .putExtra(IntentConstants.LIST_NAME, listName)
                    .putExtra(IntentConstants.SELECTED_PRODUCTS_LIST, selectedProducts)
                    .also {
                    startActivity(it)
                }
            }
            //------------------------------------

            // Add components to the LinearLayout
            linearLayout.addView(checkBox)
            linearLayout.addView(textView)
            linearLayout.addView(button)
            linearLayout.addView(buttonQuantity)

            // Add the LinearLayout to the main Layout
            llProducts.addView(linearLayout)
        }
    }

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

    @SuppressLint("CutPasteId")
    fun setProductQuantity(productName: String) {
        // Create the Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.set_quantity_dialog)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog layout parameters
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams

        dialog.show()

        // Add unit on the Spinner
        addUnitsOnSpinner(dialog)

        val editText = dialog.findViewById<EditText>(R.id.etProductQuantity)

        // Put the quantity on the EditText if the product has it
        if(selectedProducts[productName] != null) {
            val quantity = selectedProducts[productName]!!.split(" ")
            editText.setText(quantity[0])
        }

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnConfirm = dialog.findViewById(R.id.btnConfirm) as Button

        btnConfirm.setOnClickListener {
            // If the user tries to insert a quantity without a unit
            if(editText.text.isNotEmpty() && db.countDbUnits() == 0) {
                editText.error = getString(R.string.should_create_new_units)
                return@setOnClickListener
            } else if(editText.text.isEmpty()) {
                editText.error = getString(R.string.this_field_must_be_filled)
                return@setOnClickListener
            }
            // Save the Product and it's Amount (quantity + " " + unit)
            selectedProducts[productName] = editText.text.toString() + " " + dialog.findViewById<Spinner>(
                R.id.sUnit
            ).selectedItem.toString()

            dialog.dismiss()
            addProductsToScrollView(SHOW_ALL_PRODUCTS)
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        // New unit events
        val textView = dialog.findViewById<TextView>(R.id.addNewUnity).setOnClickListener {
            val et = dialog.findViewById<EditText>(R.id.etNewUnit)
            val btn = dialog.findViewById<Button>(R.id.btnAddUnit)

            // Switch the visibility of the EditText and the Button to add a new unit
            if(et.isVisible && btn.isVisible) {
                et.visibility = View.GONE
                btn.visibility = View.GONE
                btnConfirm.isClickable = true
            } else {
                et.visibility = View.VISIBLE
                btn.visibility = View.VISIBLE
                btnConfirm.isClickable = false // TODO: MUDAR A COR DESTE BOTÃO QUANDO NÃO SE PODE CLICAR NELE
            }
        }

        val btnAdd = dialog.findViewById<Button>(R.id.btnAddUnit).setOnClickListener {
            val et = dialog.findViewById<EditText>(R.id.etNewUnit)
            if(et.text.isEmpty()) {
                et.error = getString(R.string.this_field_must_be_filled)
            } else if(!db.addNewUnit(et.text.toString()))
                et.error = getString(R.string.unit_already_exists_error)
            else {
                addUnitsOnSpinner(dialog)
                et.visibility = View.GONE
                et.text.clear()
                dialog.findViewById<Button>(R.id.btnAddUnit).visibility = View.GONE
                btnConfirm.isClickable = true
            }
        }

    }

    private fun addUnitsOnSpinner(dialog: Dialog) {
        val units = db.getAllUnitsNames()

        if(units.isEmpty())
            dialog.findViewById<Spinner>(R.id.sUnit).adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, listOf(
                    getString(
                        R.string.no_units_created_spinner_info
                    )
                )
            )
        else
            dialog.findViewById<Spinner>(R.id.sUnit).adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                units
            )
    }

    private fun presentError(llProducts: LinearLayout, error: String) {
        val linearLayout = LinearLayout(this)
        linearLayout.gravity = Gravity.CENTER
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val params = linearLayout.layoutParams as MarginLayoutParams
        params.setMargins(0, 20, 0, 0)

        val textView = TextView(this)
        textView.text = error
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

        linearLayout.addView(textView)
        llProducts.addView(linearLayout)
        return
    }

    private fun completeFields(isEdit: Int) {
        selectedProducts = db.getListInformation(listId)
        // If is not edit mode clear de listId
        if(isEdit == 2)
            listId = null

        addProductsToScrollView(SHOW_ALL_PRODUCTS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, ManageProductListsActivity::class.java)
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

        // Save data
        outState.putString(LIST_NAME_STR, etListName.text.toString())
        outState.putSerializable(SELECTED_PRODUCTS_STR, selectedProducts)
        outState.putString(LIST_ID_EDIT_MODE, listId?.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore data
        findViewById<EditText>(R.id.etListName).setText(savedInstanceState.getString(LIST_NAME_STR))
        listName = savedInstanceState.getString(LIST_NAME_STR).toString()
        selectedProducts = savedInstanceState.getSerializable(SELECTED_PRODUCTS_STR) as HashMap<String, String?>
        listId = savedInstanceState.getString(LIST_ID_EDIT_MODE)?.toLong()

        // Add all the products to the ScrollView
        addProductsToScrollView(SHOW_ALL_PRODUCTS)
    }

}