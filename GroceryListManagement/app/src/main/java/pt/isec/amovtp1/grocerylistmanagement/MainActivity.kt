package pt.isec.amovtp1.grocerylistmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnProductsLists).setOnClickListener {
            Intent(this, ManageProductListsActivity::class.java).also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.btnShoppingLists).setOnClickListener {
            Intent(this, ManageShoppingListsActivity::class.java).also {
                startActivity(it)
            }
        }
    }

}