package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_create_new_product.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Product
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

class CreateNewProductActivity : AppCompatActivity() {
    lateinit var filePath: String
    lateinit var db : GMLDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.add_new_product_title)

        // Set image form assets
        Utils.setImgFromAsset(ivPreview, Constants.ASSET_IMAGE_PATH)
        filePath = Constants.ASSET_IMAGE_PATH

        // Add all the categories saved on the database to the category spinner
        addCategoriesOnSpinner()

        // Permissions to access to the camera and gallery
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method =
                    StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
        }
        // ------------------------------------------------------

        findViewById<Button>(R.id.btnAddNewList).setOnClickListener {
            // Product name field can't be empty
            if(etProductName.text.isEmpty()) {
                etProductName.error = getString(R.string.this_field_cant_be_empty)
                return@setOnClickListener
            }

            val product = Product(etProductName.text.toString(),
                    sProductCategory.selectedItem.toString(),
                    if (etProductBrand.text.isEmpty()) null else etProductBrand.text.toString(),
                    filePath,
                    if (etObservations.text.isEmpty()) arrayListOf() else arrayListOf(Product.Observation(etObservations.text.toString(), Date())))
            // Save the product in the database
            /*if(!db.saveProduct(product)) {
                etProductName.error = getString(R.string.product_already_exists)
                return@setOnClickListener
            }*/

            db.closeDB()
            Intent(this, CreateNewProductListActivity::class.java)
                    .putExtra(Constants.IntentConstants.IS_NEW_PRODUCT, 1)
                    .putExtra(Constants.IntentConstants.LIST_NAME, intent.getStringExtra(Constants.IntentConstants.LIST_NAME))
                    .addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                    .also {
                startActivity(it)
            }
            finish()
        }

        findViewById<Button>(R.id.btnGaleria).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY)
        }

        findViewById<Button>(R.id.btnCamara).setOnClickListener {
            dispatchTakePictureIntent()
        }

    }

    private fun addCategoriesOnSpinner() {
        val categories = db.getAllCategoryNames()

        if(categories.isEmpty())
            sProductCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(getString(R.string.no_categories_created_spinner_info)))
        else
            sProductCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
    }

    private fun addUnitsOnSpinner() {

    }

    fun addNewCategory(view: View) {
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.add_category_or_unit_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.findViewById<TextView>(R.id.tvDialogTitle).text = getString(R.string.add_new_category_dialog_titlle)
        val editText = dialog.findViewById<EditText>(R.id.etDialog)
        editText.hint = getString(R.string.insert_new_category_et_dialog_placeholder)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        btnSave.setOnClickListener {
            if(!db.addNewCategory(editText.text.toString()))
                editText.error = getString(R.string.category_already_exists_error)
            else {
                addCategoriesOnSpinner()
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    // REMOVE
    fun addNewUnit(view: View) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_category_or_unit_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.findViewById<TextView>(R.id.tvDialogTitle).text = getString(R.string.add_new_unit_dialog_title)
        dialog.findViewById<EditText>(R.id.etDialog).hint = getString(R.string.insert_new_unit_placeholder)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        // TODO: ACABAR

        btnSave.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat(Constants.DATE_FORMAT_TO_IMG).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir /* directory */
        ).apply {
            // Save a filepath for use with ACTION_VIEW intents
            filePath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        Constants.PACKAGE_NAME + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, info: Intent?) {
        // Gallery
        if (requestCode == Constants.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && info != null) {
            val uri = info.data?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor != null && cursor.moveToFirst())
                    filePath = cursor.getString(0)
            }
            Utils.setPic(ivPreview, filePath!!)
            return
        }
        // Camera
        else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Utils.setPic(ivPreview, filePath!!)
            return
        }
        super.onActivityResult(requestCode, resultCode, info)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Intent(this, CreateNewProductListActivity::class.java).also {
                startActivity(it)
            }
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}