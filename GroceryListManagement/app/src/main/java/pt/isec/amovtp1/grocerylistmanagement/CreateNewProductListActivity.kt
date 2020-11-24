package pt.isec.amovtp1.grocerylistmanagement

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_new_product_list.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Product

class CreateNewProductListActivity : AppCompatActivity() {
    lateinit var listName: String
    var products = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product_list)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.create_new_product_list_title)

        val opt = intent.getIntExtra(Constants.IS_LIST_DETAILS, 0)
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
            if(!etListName.text.isEmpty())
                listName = etListName.text.toString()
            else
                listName = ""

            /*
                TODO = SALVAR OS PRODUTOS JÁ ASSOCIADOS
             */

            /*

            TODO:
                 ENVIAR AS INFORMAÇÕES DA NOVA LISTA PARA A ATIVIDADE SEGUINTE ATRAVÉS DO INTENT

             */

            Intent(this, CreateNewProductActivity::class.java)
                .putExtra(Constants.LIST_NAME, listName)
                .also {
                startActivity(it)
            }
            finish()
        }

        findViewById<Button>(R.id.btnFinishList).setOnClickListener {
            Intent(this, ManageProductListsActivity::class.java).also {
                startActivity(it)
            }

            /*

            TODO:
                 CASO TENHA PELO MENOS 1 PRODUTO GUARDAR A LISTA

             */


            finish()
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

}