package pt.isec.amovtp1.grocerylistmanagement.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getStringOrNull
import pt.isec.amovtp1.grocerylistmanagement.Utils.convertDateToDatetime
import pt.isec.amovtp1.grocerylistmanagement.Utils.convertToDate
import pt.isec.amovtp1.grocerylistmanagement.data.Product
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.DATABASE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.DATABASE_VERSION
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_BRAND
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_IMAGE_FILEPATH
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_OBSERVATION
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_CATEGORY_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_LIST_PRODUCT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_LIST_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_PRODUCT_OBSERVATION_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_PRODUCT_PRICE_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_PRODUCT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.CreateTableQueries.CREATE_UNIT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_CATEGORY_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_LIST_PRODUCT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_LIST_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_PRODUCT_OBSERVATION_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_PRODUCT_PRICE_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_PRODUCT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.DropTableQueries.DROP_UNIT_TABLE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCT_INFO
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCT_OBSERVATIONS
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORIES_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORY_NAMES
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORY_NAME_BY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_NUM_PRODUCTS
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_WITH_SAME_NAME


class GMLDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_LIST_TABLE)
        db.execSQL(CREATE_CATEGORY_TABLE)
        db.execSQL(CREATE_PRODUCT_TABLE)
        db.execSQL(CREATE_UNIT_TABLE)
        db.execSQL(CREATE_PRODUCT_OBSERVATION_TABLE)
        db.execSQL(CREATE_PRODUCT_PRICE_TABLE)
        db.execSQL(CREATE_LIST_PRODUCT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_LIST_PRODUCT_TABLE)
        db.execSQL(DROP_PRODUCT_PRICE_TABLE)
        db.execSQL(DROP_PRODUCT_OBSERVATION_TABLE)
        db.execSQL(DROP_UNIT_TABLE)
        db.execSQL(DROP_PRODUCT_TABLE)
        db.execSQL(DROP_CATEGORY_TABLE)
        db.execSQL(DROP_LIST_TABLE)
        onCreate(db)
    }

    fun getAllCategoryNames() : List<String> {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_CATEGORY_NAMES, null)
        cursor.moveToFirst()

        val categories = arrayListOf<String>()
        while(!cursor.isAfterLast) {
            categories.add(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)))
            cursor.moveToNext()
        }
        cursor.close()
        return categories
    }

    fun addNewCategory(categoryName: String): Boolean {
        if(categoryExists(categoryName))
            return false

        // Insert category
        val db = writableDatabase
        val values = ContentValues()

        values.put(CATEGORY_NAME, categoryName)
        db.insert(CATEGORY_TABLE_NAME, null, values)

        return true
    }

    private fun categoryExists(categoryName: String): Boolean {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_CATEGORIES_WITH_SAME_NAME, arrayOf(categoryName))
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count != 0
    }

    fun saveProduct(product: Product) : Boolean {
        if(productExists(product.name))
            return false

        val db = writableDatabase

        // Search category id
        val categoryId = getCategoryId(product.category)

        // Insert Product
        val valuesP = ContentValues()
        valuesP.put(PRODUCT_NAME, product.name)
        valuesP.put(PRODUCT_BRAND, product.brand)
        valuesP.put(PRODUCT_IMAGE_FILEPATH, product.filePath)
        valuesP.put(CATEGORY_ID, categoryId)
        val productId = db.insert(PRODUCT_TABLE_NAME, null, valuesP)

        // Insert Observations
        if(product.observations.isNotEmpty()) {
            for(i in product.observations.indices) {
                val valuesO = ContentValues()
                valuesO.put(PRODUCT_OBSERVATION_OBSERVATION, product.observations[i].observation)
                valuesO.put(PRODUCT_OBSERVATION_DATE, convertDateToDatetime(product.observations[i].date))
                valuesO.put(PRODUCT_ID, productId)
                db.insert(PRODUCT_OBSERVATION_TABLE_NAME, null, valuesO)
            }
        }

        return true
    }

    fun getCategoryId(categoryName: String): Long {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_CATEGORIES_WITH_SAME_NAME, arrayOf(categoryName))
        cursor.moveToFirst()
        return cursor.getLong(cursor.getColumnIndex(CATEGORY_ID))
    }

    private fun productExists(productName: String): Boolean {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_PRODUCT_WITH_SAME_NAME, arrayOf(productName))
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count != 0
    }

    fun getAllProducts(): List<Product> {
        if(countDbProducts() == 0)
            return arrayListOf()

        val db = writableDatabase
        val cursorP = db.rawQuery(SELECT_ALL_PRODUCT_INFO, null)
        cursorP.moveToFirst()

        val productList = arrayListOf<Product>()
        var categoryName: String
        val observations = arrayListOf<Product.Observation>()
        while(!cursorP.isAfterLast) {
            val idCategory = cursorP.getInt(cursorP.getColumnIndex(CATEGORY_ID))
            val cursorC = db.rawQuery(SELECT_CATEGORY_NAME_BY_ID, arrayOf(idCategory.toString()))
            cursorC.moveToFirst()
            categoryName = cursorC.getString(cursorC.getColumnIndex(CATEGORY_NAME))
            cursorC.close()

            val idProduct = cursorP.getInt(cursorP.getColumnIndex(PRODUCT_ID))
            val cursorO = db.rawQuery(SELECT_ALL_PRODUCT_OBSERVATIONS, arrayOf(idProduct.toString()))
            cursorO.moveToFirst()

            while(!cursorO.isAfterLast) {
                observations.add(Product.Observation(
                        cursorO.getString(cursorO.getColumnIndex(PRODUCT_OBSERVATION_OBSERVATION)),
                        convertToDate(cursorO.getString(cursorO.getColumnIndex(PRODUCT_OBSERVATION_DATE)))
                    )
                )
                cursorO.moveToNext()
            }
            cursorO.close()

            productList.add(Product(
                    cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME)),
                    categoryName,
                    cursorP.getStringOrNull(cursorP.getColumnIndex(PRODUCT_BRAND)),
                    cursorP.getString(cursorP.getColumnIndex(PRODUCT_IMAGE_FILEPATH)),
                    observations
                )
            )
            cursorP.moveToNext()
        }
        cursorP.close()

        return productList
    }

    fun countDbProducts() : Int {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_NUM_PRODUCTS, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count
    }

    fun closeDB() {
        val db = readableDatabase
        if (db != null && db.isOpen)
            db.close()
    }

}