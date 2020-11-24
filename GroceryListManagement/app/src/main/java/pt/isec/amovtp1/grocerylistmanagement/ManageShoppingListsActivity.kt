package pt.isec.amovtp1.grocerylistmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

// DEBUG
private const val TAG = "Manage Shopping Lists"
class ManageShoppingListsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_shopping_lists)

        // DEBUG
        Log.i(TAG, "onCreate: $TAG")
    }
}