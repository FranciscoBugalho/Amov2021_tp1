package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_new_product_list.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.IntentConstants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.LIST_NAME_STR
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase


class CreateNewProductListActivity : AppCompatActivity() {
    lateinit var listName: String
    lateinit var db : GMLDatabase

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
            findViewById<EditText>(R.id.etListName).setText(intent.getStringExtra(IntentConstants.LIST_NAME))
        }

        opt = intent.getIntExtra(IntentConstants.IS_LIST_DETAILS, 0)
        if(opt == 1) {
            // Update title on actionbar and on the textview
            supportActionBar?.title = getString(R.string.edit_product_list_title)
            tvTitle!!.text = getString(R.string.edit_product_list_title)
            completeFields() // TODO: Completa os campos com a informação da lista
        } else if (opt == 2) {
            // Update title on actionbar and on the textview
            supportActionBar?.title = getString(R.string.create_list_from_existing_one_title)
            tvTitle!!.text = getString(R.string.create_list_from_existing_one_title)
            completeFields() // TODO: Completa os campos com a informação da lista
        }

        findViewById<Button>(R.id.btnNewProduct).setOnClickListener {

            // Save the list name if the editBox is not empty
            listName = if(!etListName.text.isEmpty())
                etListName.text.toString()
            else ""

            /*
                TODO = SALVAR OS PRODUTOS JÁ ASSOCIADOS
             */

            /*

            TODO:
                 ENVIAR AS INFORMAÇÕES DA NOVA LISTA PARA A ATIVIDADE SEGUINTE ATRAVÉS DO INTENT

             */

            Intent(this, CreateNewProductActivity::class.java)
                .putExtra(IntentConstants.LIST_NAME, listName)
                .also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.btnFinishList).setOnClickListener {
            Intent(this, ManageProductListsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .also {
                startActivity(it)
            }

            /*

            TODO:
                 CASO TENHA PELO MENOS 1 PRODUTO GUARDAR A LISTA

             */
            return@setOnClickListener
        }

        val etCategory = findViewById<EditText>(R.id.etCategory)
        findViewById<Button>(R.id.btnSearchCategory).setOnClickListener {
            when {
                etCategory.text.isEmpty() -> {
                    addProductsToScrollView()
                    return@setOnClickListener
                }
                etCategory.text.equals(getString(R.string.checked_flag)) -> {
                    showCheckedProductsToScrollView()
                    return@setOnClickListener
                }
                else -> {
                    showProductsByCategoryOnScrollView(etCategory.text.toString())
                    return@setOnClickListener
                }
            }
        }

        addProductsToScrollView()
    }

    fun showProductsByCategoryOnScrollView(category: String) {

    }

    fun showCheckedProductsToScrollView() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun addProductsToScrollView() {
        val llProducts = findViewById<LinearLayout>(R.id.llProductCheckBoxes)

        // If there are no products in the database
        if(db.countDbProducts() == 0) {
            val linearLayout = LinearLayout(this)
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val params = linearLayout.layoutParams as MarginLayoutParams
            params.setMargins(0, 20, 0, 0)

            val textView = TextView(this)
            textView.text = getString(R.string.you_have_to_add_products_first)
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);

            linearLayout.addView(textView)
            llProducts.addView(linearLayout)
            return
        }

        val products = db.getAllProducts()
        for(i in products.indices) {
            // Create the LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.tag = "ll" + products[i].name

            var param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 1.0f

            // Create the Checkbox
            val checkBox = CheckBox(this)
            checkBox.layoutParams = param
            checkBox.tag = "cc" + products[i].name
            checkBox.text = products[i].name
            //------------------------------------

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 0.80f

            // Create the TextView
            val textView = TextView(this)
            textView.layoutParams = param
            textView.tag = "tv" + products[i].name
            textView.text = products[i].category
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            //-----------------------------------1-

            param = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            param.weight = 0.20f

            // Create the Button
            val button = Button(this)
            button.layoutParams = param
            button.tag = "btn" + products[i].name
            button.background = getDrawable(R.drawable.edit_product_btn)
            button.gravity = Gravity.END
            //------------------------------------

            // Add components to the LinearLayout
            linearLayout.addView(checkBox)
            linearLayout.addView(textView)
            linearLayout.addView(button)

            // Add the LinearLayout to the main Layout
            llProducts.addView(linearLayout)
        }
    }

    private fun completeFields() {
        // TODO: ACABAR
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, ManageProductListsActivity::class.java).also {
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


        Log.i("TAG", "onSaveInstanceState: ")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        Log.i("TAG", "onRestoreInstanceState: ")
        // Restore data
        findViewById<EditText>(R.id.etListName).setText(savedInstanceState.getString(LIST_NAME_STR))
        listName = savedInstanceState.getString(LIST_NAME_STR).toString()
    }

}