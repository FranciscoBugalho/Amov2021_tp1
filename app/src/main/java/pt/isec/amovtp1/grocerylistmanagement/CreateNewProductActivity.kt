package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_create_new_product.*
import kotlinx.android.synthetic.main.activity_create_new_product_list.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ASSET_IMAGE_PATH_NO_IMG
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.IS_MANAGE_PRODUCT_ACTIVITY
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_BRAND_STR
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_CATEGORY_NUM
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_ID_EDIT_MODE
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_IMAGE_STR
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_NAME_STR
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.SaveDataConstants.PRODUCT_OBSERVATION_STR
import pt.isec.amovtp1.grocerylistmanagement.data.Product
import pt.isec.amovtp1.grocerylistmanagement.database.GMLDatabase
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

class CreateNewProductActivity : AppCompatActivity() {
    private lateinit var filePath: String
    private lateinit var db : GMLDatabase
    private var productId: Long? = null
    private var isManageProducts: Boolean = false

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product)

        // Init database
        db = GMLDatabase(this)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.add_new_product_title)

        if(savedInstanceState == null) {
            filePath = ASSET_IMAGE_PATH_NO_IMG
            // Set image form assets
            Utils.setImgFromAsset(ivPreview, ASSET_IMAGE_PATH_NO_IMG)
        }

        // Add all the categories saved on the database to the category spinner
        addCategoriesOnSpinner()

        val opt = intent.getIntExtra(Constants.IntentConstants.IS_EDIT_PRODUCT, 0)
        if(opt == 1) {
            completeProductFields(intent.getStringExtra(Constants.IntentConstants.PRODUCT_NAME_TO_EDIT)!!)
            // Get the product id which will be edited
            productId = db.getProductIdByName(intent.getStringExtra(Constants.IntentConstants.PRODUCT_NAME_TO_EDIT)!!)

            isManageProducts = intent.getBooleanExtra(Constants.IntentConstants.IS_VIEW_PRODUCTS, false)
        } else isManageProducts = intent.getBooleanExtra(Constants.IntentConstants.IS_VIEW_PRODUCTS, false)

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

            // Create a product with the info set by the user
            val product = Product(etProductName.text.toString(),
                    sProductCategory.selectedItem.toString(),
                    if (etProductBrand.text.isEmpty()) null else etProductBrand.text.toString(),
                    filePath,
                    if (etObservations.text.isEmpty()) arrayListOf() else arrayListOf(Product.Observation(etObservations.text.toString(), Date())))

            // Edit Mode
            if(productId != null) {
                if(!db.editProduct(product, productId!!)) {
                    etProductName.error = getString(R.string.product_already_exists)
                    return@setOnClickListener
                }
            } else {
                // Save the product in the database
                if(!db.saveProduct(product)) {
                    etProductName.error = getString(R.string.product_already_exists)
                    return@setOnClickListener
                }
            }

            db.closeDB()
            if(!isManageProducts)
                Intent(this, CreateNewListActivity::class.java)
                            .putExtra(Constants.IntentConstants.IS_NEW_PRODUCT, 1)
                            .putExtra(Constants.IntentConstants.LIST_NAME, intent.getStringExtra(Constants.IntentConstants.LIST_NAME))
                            .putExtra(Constants.IntentConstants.MANAGE_PRODUCTS_TITLE, intent.getStringExtra(Constants.IntentConstants.MANAGE_PRODUCTS_TITLE))
                            .putExtra(Constants.IntentConstants.SELECTED_PRODUCTS_LIST, intent.getSerializableExtra(Constants.IntentConstants.SELECTED_PRODUCTS_LIST))
                            .addFlags(FLAG_ACTIVITY_NEW_TASK)
                            .also {
                    startActivity(it)
                }
            else
                Intent(this, ManageProductsActivity::class.java)
                    .addFlags(FLAG_ACTIVITY_NEW_TASK)
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
        canRemoveImage()

        val addBtn = findViewById<Button>(R.id.btnAddNewCategory)
        val drawable = getDrawable(R.drawable.add_btn)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable!!.colorFilter = BlendModeColorFilter(resources.getColor(R.color.theme_orange), BlendMode.SRC_IN)
        }
        else{
            drawable!!.setColorFilter(resources.getColor(R.color.theme_orange), PorterDuff.Mode.SRC_IN )
        }
        addBtn.background = drawable
        addBtn.setOnClickListener {
            addNewCategory()
        }

    }

    /**
     * canRemoveImage
     */
    private fun canRemoveImage() {
        val iv = findViewById<ImageView>(R.id.ivPreview)
        if(filePath != ASSET_IMAGE_PATH_NO_IMG) {
            iv.setOnLongClickListener {
                openDialogDeleteImage(iv)
                return@setOnLongClickListener true
            }
        } else iv.setOnLongClickListener(null)
    }

    /**
     * completeProductFields
     */
    private fun completeProductFields(productName: String) {
        val productInfo = db.getProductInfoByName(productName)

        etProductName.setText(productName)

        if(productInfo[0] != null) {
            etProductBrand.setText(productInfo[0])
        }

        filePath = productInfo[1].toString()
        if(filePath == ASSET_IMAGE_PATH_NO_IMG)
            Utils.setImgFromAsset(ivPreview, filePath)
        else
            Utils.setPic(ivPreview, filePath)

        sProductCategory.setSelection(getCategoryPositionOnSpinner(productInfo[2].toString()))
    }

    /**
     * getCategoryPositionOnSpinner
     */
    private fun getCategoryPositionOnSpinner(productCategory: String): Int {
        val categories = db.getAllCategoryNames()
        for(i in categories.indices)
            if(categories[i] == productCategory)
                return i
        return 0
    }

    /**
     * addCategoriesOnSpinner
     */
    private fun addCategoriesOnSpinner() {
        val categories = db.getAllCategoryNames()

        if(categories.isEmpty())
            sProductCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(getString(R.string.no_categories_created_spinner_info)))
        else
            sProductCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

    }

    /**
     * addNewCategory
     */
    fun addNewCategory() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_category_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.etDialog)
        editText.hint = getString(R.string.insert_new_category_et_dialog_placeholder)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        btnSave.setOnClickListener {
            if (editText.text.isEmpty()) {
                editText.error = getString(R.string.this_field_must_be_filled)
            } else if(!db.addNewCategory(editText.text.toString()))
                editText.error = getString(R.string.category_already_exists_error)
            else {
                addCategoriesOnSpinner()
                sProductCategory.setSelection(getCategoryPositionOnSpinner(editText.text.toString()))
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    /**
     * openDialogDeleteImage
     */
    private fun openDialogDeleteImage(ivPreview: ImageView) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.delete_product_image_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val btnNo = dialog.findViewById(R.id.btnNo) as Button
        val btnYes = dialog.findViewById(R.id.btnYes) as Button

        btnYes.setOnClickListener {
            filePath = ASSET_IMAGE_PATH_NO_IMG
            Utils.setImgFromAsset(ivPreview, filePath)
            dialog.dismiss()
        }
        btnNo.setOnClickListener { dialog.dismiss() }
    }

    /**
     * createImageFile
     */
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

    /**
     * dispatchTakePictureIntent
     */
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

    /**
     * onActivityResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, info: Intent?) {
        // Gallery
        if (requestCode == Constants.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && info != null) {
            val uri = info.data?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor != null && cursor.moveToFirst())
                    filePath = cursor.getString(0)
            }
            Utils.setPic(ivPreview, filePath)
            canRemoveImage()
            return
        }
        // Camera
        else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Utils.setPic(ivPreview, filePath)
            canRemoveImage()
            return
        }
        super.onActivityResult(requestCode, resultCode, info)

    }

    /**
     * onOptionsItemSelected
     * 1. Listens to all the operations on the "supportActionBar"
     * 2. If the "home" button was selected, redirects to the "CreateNewListActivity",
     * passing as arguments the list's name, the title (edit or create) and the selected product list. Also pases a flag to verify if the operation was 'create' or 'edit'
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            if(!isManageProducts)
                Intent(this, CreateNewListActivity::class.java)
                        .putExtra(Constants.IntentConstants.IS_NEW_PRODUCT, 1)
                        .putExtra(Constants.IntentConstants.LIST_NAME, intent.getStringExtra(Constants.IntentConstants.LIST_NAME))
                        .putExtra(Constants.IntentConstants.MANAGE_PRODUCTS_TITLE, intent.getStringExtra(Constants.IntentConstants.MANAGE_PRODUCTS_TITLE))
                        .putExtra(Constants.IntentConstants.SELECTED_PRODUCTS_LIST, intent.getSerializableExtra(Constants.IntentConstants.SELECTED_PRODUCTS_LIST))
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
                        .also {
                    startActivity(it)
                }
            else
                Intent(this, ManageProductsActivity::class.java)
                    .addFlags(FLAG_ACTIVITY_NEW_TASK)
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
     * 1. Saves "filePath", "productId", "isManageProducts", "etProductName" text, "sProductCategory" text, "etProductBrand" text & "etProductBrand" text
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save data
        outState.putString(PRODUCT_NAME_STR, etProductName.text.toString())
        outState.putInt(PRODUCT_CATEGORY_NUM, sProductCategory.selectedItemPosition)
        outState.putString(PRODUCT_BRAND_STR, etProductBrand.text.toString())
        outState.putString(PRODUCT_OBSERVATION_STR, etObservations.text.toString())
        outState.putString(PRODUCT_IMAGE_STR, filePath)
        outState.putString(PRODUCT_ID_EDIT_MODE, productId?.toString())
        outState.putBoolean(IS_MANAGE_PRODUCT_ACTIVITY, isManageProducts)

    }

    /**
     * onRestoreInstanceState
     * 1. Restores "filePath", "productId", "isManageProducts", "etProductName" text, "sProductCategory" text, "etProductBrand" text & "etProductBrand" text
     * 2. If the product image's file path is the 'no img file path', calls teh "setImgFromAsset" method from the "Utils" file
     * 3. If not, calls the "setPic" method from the "Utils" file
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        etProductName.setText(savedInstanceState.getString(PRODUCT_NAME_STR))
        sProductCategory.setSelection(savedInstanceState.getInt(PRODUCT_CATEGORY_NUM))
        etProductBrand.setText(savedInstanceState.getString(PRODUCT_BRAND_STR))
        etProductBrand.setText(savedInstanceState.getString(PRODUCT_OBSERVATION_STR))
        filePath = savedInstanceState.getString(PRODUCT_IMAGE_STR).toString()
        productId = savedInstanceState.getString(PRODUCT_ID_EDIT_MODE)?.toLong()
        isManageProducts = savedInstanceState.getBoolean(IS_MANAGE_PRODUCT_ACTIVITY)

        if(filePath == ASSET_IMAGE_PATH_NO_IMG)
            Utils.setImgFromAsset(ivPreview, ASSET_IMAGE_PATH_NO_IMG)
        else
            Utils.setPic(ivPreview, filePath)
    }

}