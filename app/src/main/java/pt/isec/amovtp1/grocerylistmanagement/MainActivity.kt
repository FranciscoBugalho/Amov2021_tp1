package pt.isec.amovtp1.grocerylistmanagement

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPurchaseHistory).setOnClickListener {
            Intent(this, ViewPurchaseHistoryActivity::class.java).also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.btnViewAllProducts).setOnClickListener {
            Intent(this, ManageProductsActivity::class.java).also {
                startActivity(it)
            }
        }

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