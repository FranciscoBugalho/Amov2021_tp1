package pt.isec.amovtp1.grocerylistmanagement

import android.app.Dialog
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_manage_categories_units.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.IS_CATEGORIES
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase

class ManageCategoriesUnitsActivity : AppCompatActivity() {
    private lateinit var db : GMLDatabase
    private var isCategories: Boolean = false

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories_units)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if(savedInstanceState == null)
            isCategories = intent.getBooleanExtra(Constants.IntentConstants.SHOW_CATEGORIES, false)

        if(isCategories)
            // Define title
            supportActionBar?.title = getString(R.string.manage_categories_title)
        else
            supportActionBar?.title = getString(R.string.manage_units_title)

        // Add information to the Spinner
        val order = arrayListOf("-------------------------", getString(R.string.alphabetical_order_asc_text), getString(R.string.alphabetical_order_desc_text))
        sOrderCategoriesUnits.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, order)
        sOrderCategoriesUnits.setSelection(0)
        sOrderCategoriesUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> addInformationToScrollView(getInformation())
                    1 -> addInformationToScrollView(getInformation().sortedBy { it.toLowerCase() })
                    2 -> addInformationToScrollView(getInformation().sortedByDescending { it.toLowerCase() })
                }
            }

            // Nothing happens
            override fun onNothingSelected(parent: AdapterView<*>?) { }

        }

        // Add all the categories or units to the ScrollView
        addInformationToScrollView(getInformation())

        val addBtn = findViewById<Button>(R.id.btnAddNewCategoryUnit)
        val drawable = getDrawable(R.drawable.add_btn)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable!!.colorFilter = BlendModeColorFilter(resources.getColor(R.color.theme_orange), BlendMode.SRC_IN)
        }
        else{
            drawable!!.setColorFilter(resources.getColor(R.color.theme_orange), PorterDuff.Mode.SRC_IN )
        }
        addBtn.background = drawable
        addBtn.setOnClickListener {
            db.closeDB()

            if(isCategories)
                showDialogCategory(null)
            else
                showDialogUnit(null)

        }
    }

    /**
     * getInformation
     */
    private fun getInformation(): List<String> {
        if(isCategories) {
            if(db.countDbCategories() == 0) {
                presentError(getString(R.string.you_have_to_add_categories_error))
                return arrayListOf()
            }

            return db.getAllCategoryNames()
        }
        else {
            if(db.countDbUnits() == 0) {
                presentError(getString(R.string.you_have_to_add_units_first_error))
                return arrayListOf()
            }

            return db.getAllUnitsNames()
        }
    }

    /**
     * addInformationToScrollView
     */
    private fun addInformationToScrollView(information: List<String>) {
        val llInformation = findViewById<LinearLayout>(R.id.llShowAllCategoriesUnits)
        llInformation.removeAllViews()

        for(i in information.indices) {
            // Create the LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "ll$i"


            var param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 1.0f

            // Create the TextView
            val tvName = TextView(this)
            tvName.layoutParams = param
            tvName.tag = "tv$i"
            tvName.text = information[i]
            tvName.setTextColor(Color.BLACK)
            tvName.maxLines = 1
            //------------------------------------

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 0.15f

            // Create the Button
            val button = Button(this)
            button.layoutParams = param
            button.tag = "btn$i"
            button.background = getDrawable(R.drawable.edit_product_btn)
            button.gravity = Gravity.END
            // Edit Product
            button.setOnClickListener {
                if(isCategories)
                    showDialogCategory(information[i])
                else
                    showDialogUnit(information[i])
            }
            //------------------------------------

            // Add components to the LinearLayout
            linearLayout.addView(tvName)
            linearLayout.addView(button)

            // Add the LinearLayout to the main Layout
            llInformation.addView(linearLayout)
        }
    }

    /**
     * showDialogUnit
     */
    private fun showDialogUnit(unitName: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_category_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.etDialog)
        if(unitName == null)
            editText.hint = getString(R.string.insert_a_unit_prompt)
        else {
            dialog.findViewById<TextView>(R.id.tvDialogTitle).text = getString(R.string.aud_tv_edit_unit_text)
            editText.setText(unitName)
        }

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        btnSave.setOnClickListener {
            if (editText.text.isEmpty()) {
                editText.error = getString(R.string.this_field_must_be_filled)
            } else if(editText.text.isNotEmpty()) {
                if(unitName == null) {
                    if(!db.addNewUnit(editText.text.toString()))
                        editText.error = getString(R.string.category_already_exists_error)
                    else {
                        addInformationToScrollView(getInformation())
                        dialog.dismiss()
                    }
                } else {
                    if(!db.editUnit(editText.text.toString(), db.getUnitIdByName(unitName)))
                        editText.error = getString(R.string.category_already_exists_error)
                    else {
                        addInformationToScrollView(getInformation())
                        dialog.dismiss()
                    }
                }
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    /**
     * showDialogCategory
     */
    private fun showDialogCategory(categoryName: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_category_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.etDialog)
        if(categoryName == null)
            editText.hint = getString(R.string.insert_new_category_et_dialog_placeholder)
        else {
            dialog.findViewById<TextView>(R.id.tvDialogTitle).text = getString(R.string.acd_tv_edit_category_text)
            editText.setText(categoryName)
        }

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        btnSave.setOnClickListener {
            if (editText.text.isEmpty()) {
                editText.error = getString(R.string.this_field_must_be_filled)
            } else if(editText.text.isNotEmpty()) {
                if(categoryName == null) {
                    if(!db.addNewCategory(editText.text.toString()))
                        editText.error = getString(R.string.category_already_exists_error)
                    else {
                        addInformationToScrollView(getInformation())
                        dialog.dismiss()
                    }
                } else {
                    if(!db.editCategory(editText.text.toString(), db.getCategoryId(categoryName)))
                        editText.error = getString(R.string.category_already_exists_error)
                    else {
                        addInformationToScrollView(getInformation())
                        dialog.dismiss()
                    }
                }
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    /**
     * presentError
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
        findViewById<LinearLayout>(R.id.llShowAllCategoriesUnits).addView(linearLayout)
        return
    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar"
     * 2. If the "home" button was selected, redirects to the "ManageProductsActivity"
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            db.closeDB()
            Intent(this, ManageProductsActivity::class.java)
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
     * 1. Saves "isCategories"
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(IS_CATEGORIES, isCategories)
    }

    /**
     * onRestoreInstanceState
     * 1. Restores "isCategories"
     * 2. Calls the "addInformationToScrollView" method passing the return of the "getInformation" mathod
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isCategories = savedInstanceState.getBoolean(IS_CATEGORIES)
        addInformationToScrollView(getInformation())
    }
}