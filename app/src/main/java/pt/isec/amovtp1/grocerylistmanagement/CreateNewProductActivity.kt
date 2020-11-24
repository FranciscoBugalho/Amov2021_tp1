package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_create_new_product.*
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

class CreateNewProductActivity : AppCompatActivity() {
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product)

        // Add back button on the actionbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Define title
        supportActionBar?.title = getString(R.string.add_new_product_title)

        // Set image form assets
        Utils.setImgFromAsset(ivPreview, Constants.ASSET_IMAGE_PATH)
        filePath = Constants.ASSET_IMAGE_PATH

        addCategoriesOnSpinner()
        addUnitsOnSpinner()

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
            Intent(this, CreateNewProductListActivity::class.java).also {
                startActivity(it)
            }
            /*

            TODO:
                 ENVIAR AS INFORMAÇÕES DA NOVA LISTA PARA A ATIVIDADE ANTERIOR ATRAVÉS DO INTENT

             */
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
    }

    private fun addUnitsOnSpinner() {

    }

    fun addNewCategory(view: View) {
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.add_category_or_unit_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.findViewById<TextView>(R.id.tvDialogTitle).text = getString(R.string.add_new_category_dialog_titlle)
        var editText = dialog.findViewById<EditText>(R.id.etDialog)
        editText.hint = getString(R.string.insert_new_category_et_dialog_placeholder)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnSave = dialog.findViewById(R.id.btnSave) as Button

        btnSave.setOnClickListener {
            if(!saveNewCategory(view.context, editText.text.toString()))
                editText.error = getString(R.string.category_already_exists_error)
            else {
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }


    private fun saveNewCategory(context: Context, categoryName: String) : Boolean {
       return false
    }

    private fun saveNewUnit(context: Context) {

    }


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