package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_manage_product_lists.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.ListInformation
import pt.isec.amovtp1.grocerylistmanagement.data.Product
import kotlin.random.Random


class ManageProductListsActivity : AppCompatActivity() {
    var productList: HashMap<ListInformation, ArrayList<Product>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_product_lists)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.manage_product_list_title)


        findViewById<Button>(R.id.addNewList).setOnClickListener {
            Intent(this, CreateNewProductListActivity::class.java).also {
                startActivity(it)
            }

            // TODO: ???? SERÁ NECESSÁRIO "finish()", VER INTENT FLAGS
            finish()
        }

        /*

            TODO: IR À BASE DE DADOS BUSCAR A INFORMAÇÃO DAS LISTAS JÁ EXISTENTES

         */

        // TESTES - REMOVER
        repeat(Random.nextInt(10, 20)) {
            val item = Dados("Titulo ${Random.nextInt(0, 1000)}", /*getStr(50,400),*/listProductsNameTEST())
            data.add(item)
        }

        rvProductList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvProductList.adapter = MyRVAdapter(data)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // TESTES - REMOVER
    data class Dados(val str1: String, val str2: String)
    val data = arrayListOf<Dados>()

    fun getStr(minc: Int, maxc: Int) : String {
        var str = ""
        val nrc = Random.nextInt(minc, maxc)
        repeat(nrc) {
            str += Random.nextInt(65, 90).toChar()
        }
        return str
    }

    fun listProductsNameTEST() : String {
        var str = ""
        val products = arrayOf("Olá", "eu", "sou", "um", "teste")

        for (item in products) {
            str += item
            str += "\n"
        }
        return str
    }

    class MyRVAdapter(val data: ArrayList<Dados>) : RecyclerView.Adapter<MyRVAdapter.MyViewHolder>() {
        class MyViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
            var tv1: TextView = view.findViewById(R.id.listName)
            var tv2: TextView = view.findViewById(R.id.productList)

            fun update(str1: String, str2: String, str3: String) {
                // Add the onLongClickListener
                tv2.setOnLongClickListener{
                    (context as ManageProductListsActivity).createDialogToEditOrDelete()
                    return@setOnLongClickListener true
                }

                tv1.text = str1
                tv2.text = str2
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_rv, parent, false)
            return MyViewHolder(view, parent.context)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.update(data[position].str1, data[position].str2, "")
        }

        override fun getItemCount(): Int = data.size
    }

    fun onViewDetails(view: View) {
        // Obtém o id (BD) do CardView selecionado

        // Muda de atividade
        Intent(this, CreateNewProductListActivity::class.java)
                .putExtra(Constants.IS_LIST_DETAILS, 1)
                .also {
            startActivity(it)
        }
    }

    fun createDialogToEditOrDelete() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_or_delete_list_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val btnDelete = dialog.findViewById(R.id.btnDelete) as Button
        val btnCopyList = dialog.findViewById(R.id.btnCopyList) as Button

        // TODO: ACABAR

        btnDelete.setOnClickListener {
            // TODO: ACABAR
            dialog.dismiss()
        }

        btnCopyList.setOnClickListener {
            dialog.dismiss()
            Intent(this, CreateNewProductListActivity::class.java)
                    .putExtra(Constants.IS_LIST_DETAILS, 2).also {
                startActivity(it)
            }
        }
    }
}