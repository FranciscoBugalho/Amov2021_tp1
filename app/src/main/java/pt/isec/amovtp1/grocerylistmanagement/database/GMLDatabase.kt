package pt.isec.amovtp1.grocerylistmanagement.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import pt.isec.amovtp1.grocerylistmanagement.Utils.convertDateToDatetime
import pt.isec.amovtp1.grocerylistmanagement.Utils.convertToDate
import pt.isec.amovtp1.grocerylistmanagement.data.Constants
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.ALPHABETICAL_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.CREATED_DATE_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.IS_BOUGHT_ORDER
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.ListOrders.ORDER_ASC
import pt.isec.amovtp1.grocerylistmanagement.data.ListInformation
import pt.isec.amovtp1.grocerylistmanagement.data.Product
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.DATABASE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.DATABASE_VERSION
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_IS_BOUGHT
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListProductConstants.LIST_PRODUCT_QUANTITY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListProductConstants.LIST_PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_BRAND
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_IMAGE_FILEPATH
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_OBSERVATION
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_TABLE_NAME
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
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_INFO_IN_LIST_PRODUCT
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_LIST_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_DATE_ASC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_DATE_DESC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_IS_BOUGHT_ASC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_IS_BOUGHT_DESC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_NAME_ASC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_NAME_DESC
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCT_INFO
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCT_NAME_CATEGORY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_ALL_PRODUCT_OBSERVATIONS
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORIES_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORY_NAMES
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CATEGORY_NAME_BY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_CHECKED_PRODUCT_NAME_CATEGORY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_LISTS_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_LIST_ID_BY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_LIST_ID_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_LIST_NAME_BY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_NUM_PRODUCTS
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_NUM_UNITS
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_BRAND_FROM_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_ID_BY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_ID_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_IMAGE_FILEPATH_FROM_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_INFO_BY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_NAME_BY_CATEGORY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_NAME_BY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_PRODUCT_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_UNITS_WITH_SAME_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_UNIT_ID_BY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_UNIT_NAMES
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseQueries.SelectQueries.SELECT_UNIT_NAME_BY_ID
import java.util.*
import kotlin.collections.HashMap

class GMLDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

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
                valuesO.put(
                    PRODUCT_OBSERVATION_DATE,
                    convertDateToDatetime(product.observations[i].date)
                )
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
            val cursorO = db.rawQuery(
                SELECT_ALL_PRODUCT_OBSERVATIONS,
                arrayOf(idProduct.toString())
            )
            cursorO.moveToFirst()

            while(!cursorO.isAfterLast) {
                observations.add(
                    Product.Observation(
                        cursorO.getString(cursorO.getColumnIndex(PRODUCT_OBSERVATION_OBSERVATION)),
                        convertToDate(
                            cursorO.getString(
                                cursorO.getColumnIndex(
                                    PRODUCT_OBSERVATION_DATE
                                )
                            )
                        )
                    )
                )
                cursorO.moveToNext()
            }
            cursorO.close()

            productList.add(
                Product(
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

    fun getAllProductsNameCategory(): HashMap<String, String> {
        if(countDbProducts() == 0)
            return hashMapOf()

        val db = writableDatabase
        val productsHashMap = HashMap<String, String>()

        val cursorP = db.rawQuery(SELECT_ALL_PRODUCT_NAME_CATEGORY, null)
        cursorP.moveToFirst()

        while (!cursorP.isAfterLast) {

            val idCategory = cursorP.getInt(cursorP.getColumnIndex(CATEGORY_ID))
            val cursorC = db.rawQuery(SELECT_CATEGORY_NAME_BY_ID, arrayOf(idCategory.toString()))
            cursorC.moveToFirst()
            val categoryName = cursorC.getString(cursorC.getColumnIndex(CATEGORY_NAME))
            cursorC.close()

            productsHashMap[cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME))] = categoryName

            cursorP.moveToNext()
        }

        cursorP.close()

        return productsHashMap
    }

    fun getCheckedProductsNameCategory(checkedProductsName: MutableSet<String>): HashMap<String, String> {
        if(countDbProducts() == 0 || checkedProductsName.isEmpty())
            return hashMapOf()

        val db = writableDatabase
        val productsHashMap = HashMap<String, String>()

        repeat(checkedProductsName.size) {
            val cursorP = db.rawQuery(
                SELECT_CHECKED_PRODUCT_NAME_CATEGORY, arrayOf(
                    checkedProductsName.elementAt(
                        it
                    ).toString()
                )
            )
            cursorP.moveToFirst()

            while (!cursorP.isAfterLast) {

                val idCategory = cursorP.getInt(cursorP.getColumnIndex(CATEGORY_ID))
                val cursorC = db.rawQuery(
                    SELECT_CATEGORY_NAME_BY_ID,
                    arrayOf(idCategory.toString())
                )
                cursorC.moveToFirst()
                val categoryName = cursorC.getString(cursorC.getColumnIndex(CATEGORY_NAME))
                cursorC.close()

                productsHashMap[cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME))] = categoryName

                cursorP.moveToNext()
            }

            cursorP.close()
        }

        return productsHashMap
    }

    fun getProductNameByCategory(categoryName: String): HashMap<String, String> {
        if(countDbProducts() == 0 || categoryName.isEmpty())
            return hashMapOf()

        val db = writableDatabase
        val productsHashMap = HashMap<String, String>()

        if(!categoryExists(categoryName)) {
            productsHashMap[Constants.ERROR] = ""
            return productsHashMap
        }

        val cursorP = db.rawQuery(
            SELECT_PRODUCT_NAME_BY_CATEGORY, arrayOf(
                getCategoryId(
                    categoryName
                ).toString()
            )
        )
        cursorP.moveToFirst()

        while (!cursorP.isAfterLast) {

            productsHashMap[cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME))] = categoryName

            cursorP.moveToNext()
        }

        cursorP.close()

        return productsHashMap
    }

    fun countDbProducts() : Int {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_NUM_PRODUCTS, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count
    }

    fun countDbUnits(): Int {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_NUM_UNITS, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getAllUnitsNames(): List<String> {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_UNIT_NAMES, null)
        cursor.moveToFirst()

        val units = arrayListOf<String>()
        while(!cursor.isAfterLast) {
            units.add(cursor.getString(cursor.getColumnIndex(UNIT_NAME)))
            cursor.moveToNext()
        }
        cursor.close()
        return units
    }

    fun addNewUnit(unitName: String): Boolean {
        if(unitExist(unitName))
            return false

        // Insert unit
        val db = writableDatabase
        val values = ContentValues()
        values.put(UNIT_NAME, unitName)
        db.insert(UNIT_TABLE_NAME, null, values)

        return true
    }

    private fun unitExist(unitName: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_UNITS_WITH_SAME_NAME, arrayOf(unitName))
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count != 0
    }

    fun listNameExists(listName: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_LISTS_WITH_SAME_NAME, arrayOf(listName))
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count != 0
    }

    fun saveList(listName: String, selectedProducts: HashMap<String, String?>): Boolean {
        if(listNameExists(listName))
            return false

        val db = writableDatabase

        // Insert list in the list table
        val valuesL = ContentValues()
        valuesL.put(LIST_NAME, listName)
        valuesL.put(LIST_DATE, convertDateToDatetime(Date()))
        valuesL.put(LIST_IS_BOUGHT, 0)
        val listId = db.insert(LIST_TABLE_NAME, null, valuesL)

        for(key in selectedProducts.keys) {

            val cursorP = db.rawQuery(SELECT_PRODUCT_ID_BY_NAME, arrayOf(key))
            cursorP.moveToFirst()
            val productId = cursorP.getLong(cursorP.getColumnIndex(PRODUCT_ID))

            val valuesPL = ContentValues()
            valuesPL.put(LIST_ID, listId)
            valuesPL.put(PRODUCT_ID, productId)

            if(selectedProducts[key] != null) {
                val amount = selectedProducts[key]!!.split(" ")
                valuesPL.put(LIST_PRODUCT_QUANTITY, amount[0])

                if(amount.size == 2) {
                    val cursorU = db.rawQuery(SELECT_UNIT_ID_BY_NAME, arrayOf(amount[1]))
                    cursorU.moveToFirst()
                    val unitId = cursorU.getLong(cursorU.getColumnIndex(UNIT_ID))
                    valuesPL.put(UNIT_ID, unitId)
                } else
                    valuesPL.putNull(UNIT_ID)
            } else {
                valuesPL.putNull(LIST_PRODUCT_QUANTITY)
                valuesPL.putNull(UNIT_ID)
            }

            db.insert(LIST_PRODUCT_TABLE_NAME, null, valuesPL)
        }

        return true
    }

    fun getProductInfoByName(productName: String): List<String?> {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_PRODUCT_INFO_BY_NAME, arrayOf(productName))
        cursor.moveToFirst()

        val productInfo = arrayListOf<String?>()
        productInfo.add(cursor.getStringOrNull(cursor.getColumnIndex(PRODUCT_BRAND)))
        productInfo.add(cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_FILEPATH)))

        val cursorC = db.rawQuery(SELECT_CATEGORY_NAME_BY_ID, arrayOf(cursor.getLong(cursor.getColumnIndex(CATEGORY_ID)).toString()))
        cursorC.moveToFirst()

        productInfo.add(cursorC.getString(cursorC.getColumnIndex(CATEGORY_NAME)))

        return productInfo
    }

    fun closeDB() {
        val db = readableDatabase
        if (db != null && db.isOpen)
            db.close()
    }

    fun getProductIdByName(productName: String): Long {
        val db = writableDatabase

        val cursor = db.rawQuery(SELECT_PRODUCT_ID_BY_NAME, arrayOf(productName))
        cursor.moveToFirst()
        return cursor.getLong(cursor.getColumnIndex(PRODUCT_ID))
    }

    fun editProduct(product: Product, productId: Long): Boolean {
        val db = writableDatabase

        // Gets the product id with the product name that user inserted
        val cursor = db.rawQuery(SELECT_PRODUCT_ID_WITH_SAME_NAME, arrayOf(product.name))
        cursor.moveToFirst()
        val count = cursor.count

        // If exist one and it isn't with the same id
        if(count > 1 && cursor.getLong(cursor.getColumnIndex(PRODUCT_ID)) != productId) {
            cursor.close()
            return false
        }

        // Search category id
        val categoryId = getCategoryId(product.category)

        // Insert Product
        val valuesP = ContentValues()
        valuesP.put(PRODUCT_NAME, product.name)
        valuesP.put(PRODUCT_BRAND, product.brand)
        valuesP.put(PRODUCT_IMAGE_FILEPATH, product.filePath)
        valuesP.put(CATEGORY_ID, categoryId)
        db.update(PRODUCT_TABLE_NAME, valuesP, "$PRODUCT_ID = ?", arrayOf(productId.toString()))

        // Insert Observations
        if(product.observations.isNotEmpty()) {
            for(i in product.observations.indices) {
                val valuesO = ContentValues()
                valuesO.put(PRODUCT_OBSERVATION_OBSERVATION, product.observations[i].observation)
                valuesO.put(
                        PRODUCT_OBSERVATION_DATE,
                        convertDateToDatetime(product.observations[i].date)
                )
                valuesO.put(PRODUCT_ID, productId)
                db.insert(PRODUCT_OBSERVATION_TABLE_NAME, null, valuesO)
            }
        }

        return true
    }

    fun getAllObservations(productName: String): List<Product.Observation> {
        val idProduct = getProductIdByName(productName)

        if(countDbProductObservations(idProduct) == 0)
            return arrayListOf()

        val productObservations = arrayListOf<Product.Observation>()

        val db = writableDatabase
        val cursorO = db.rawQuery(SELECT_ALL_PRODUCT_OBSERVATIONS, arrayOf(idProduct.toString()))
        cursorO.moveToFirst()

        while(!cursorO.isAfterLast) {
            productObservations.add(
                    Product.Observation(
                            cursorO.getString(cursorO.getColumnIndex(PRODUCT_OBSERVATION_OBSERVATION)),
                            convertToDate(
                                    cursorO.getString(
                                            cursorO.getColumnIndex(
                                                    PRODUCT_OBSERVATION_DATE
                                            )
                                    )
                            )
                    )
            )
            cursorO.moveToNext()
        }


        return productObservations
    }

    private fun countDbProductObservations(productId: Long): Int {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_ALL_PRODUCT_OBSERVATIONS, arrayOf(productId.toString()))
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getProductFilePath(productName: String): String {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_PRODUCT_IMAGE_FILEPATH_FROM_NAME, arrayOf(productName))
        cursor.moveToFirst()
        val productImageFilePath = cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_FILEPATH))
        cursor.close()
        return productImageFilePath
    }

    fun getProductBrand(productName: String): String? {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_PRODUCT_BRAND_FROM_NAME, arrayOf(productName))
        cursor.moveToFirst()
        val productBrand = cursor.getString(cursor.getColumnIndex(PRODUCT_BRAND))
        cursor.close()
        return productBrand
    }

    fun getAllLists(listOrder: HashMap<String, String?>): List<ListInformation> {
        if(countDbNumLists() == 0)
            return arrayListOf()

        val db = writableDatabase
        val lists = arrayListOf<ListInformation>()

        val query = getOrderedLists(listOrder)

        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            lists.add(ListInformation(cursor.getString(cursor.getColumnIndex(LIST_NAME)),
                    convertToDate(cursor.getString(cursor.getColumnIndex(LIST_DATE))))
            )

            cursor.moveToNext()
        }
        cursor.close()
        return lists
    }

    private fun countDbNumLists(): Int {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_ALL_LIST_ID, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count
    }

    fun productsInThisList(listName: String): List<String> {
        val db = writableDatabase

        val cursorPL = db.rawQuery(SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT, arrayOf(getListIdByName(listName).toString()))

        cursorPL.moveToFirst()

        val idProducts = arrayListOf<Int>()
        while(!cursorPL.isAfterLast) {
            idProducts.add(cursorPL.getInt(cursorPL.getColumnIndex(PRODUCT_ID)))
            cursorPL.moveToNext()
        }
        cursorPL.close()

        val productNameList = arrayListOf<String>()
        for(i in 0 until idProducts.size) {
            val cursorP = db.rawQuery(SELECT_PRODUCT_NAME_BY_ID, arrayOf(idProducts[i].toString()))
            cursorP.moveToFirst()

            while (!cursorP.isAfterLast) {
                productNameList.add(cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME)))
                cursorP.moveToNext()
            }
            cursorP.close()
        }

        return productNameList
    }

    fun getListIdByName(listName: String): Long {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_LIST_ID_BY_NAME, arrayOf(listName))
        cursor.moveToFirst()
        val listId = cursor.getLong(cursor.getColumnIndex(LIST_ID))
        cursor.close()
        return listId
    }

    fun getListNameById(listId: Long): String {
        val db = writableDatabase
        val cursor = db.rawQuery(SELECT_LIST_NAME_BY_ID, arrayOf(listId.toString()))
        cursor.moveToFirst()
        val listName = cursor.getString(cursor.getColumnIndex(LIST_NAME))
        cursor.close()
        return listName
    }

    fun getListInformation(listId: Long?): HashMap<String, String?> {
        val db = writableDatabase
        val selectedProducts = hashMapOf<String, String>()
        val cursorPL = db.rawQuery(SELECT_ALL_INFO_IN_LIST_PRODUCT, arrayOf(listId.toString()))
        cursorPL.moveToFirst()

        val idProducts = arrayListOf<Int>()
        val amount = arrayListOf<String?>()
        while(!cursorPL.isAfterLast) {
            idProducts.add(cursorPL.getInt(cursorPL.getColumnIndex(PRODUCT_ID)))

            val unitId = cursorPL.getLongOrNull(cursorPL.getColumnIndex(UNIT_ID))
            if(unitId != null) {
                val cursorU = db.rawQuery(SELECT_UNIT_NAME_BY_ID, arrayOf(unitId.toString()))
                cursorU.moveToFirst()

                val unit = cursorU.getString(cursorU.getColumnIndex(UNIT_NAME))

                cursorU.close()

                val quantity = cursorPL.getInt(cursorPL.getColumnIndex(LIST_PRODUCT_QUANTITY))

                amount.add("$quantity $unit")
            } else amount.add("0")

            cursorPL.moveToNext()
        }
        cursorPL.close()

        for(i in 0 until idProducts.size) {
            val cursorP = db.rawQuery(SELECT_PRODUCT_NAME_BY_ID, arrayOf(idProducts[i].toString()))
            cursorP.moveToFirst()

            while (!cursorP.isAfterLast) {
                selectedProducts[cursorP.getString(cursorP.getColumnIndex(PRODUCT_NAME))] = amount[i].toString()
                cursorP.moveToNext()
            }
            cursorP.close()
        }

        return selectedProducts as HashMap<String, String?>
    }

    fun editList(listName: String, selectedProducts: HashMap<String, String?>, listId: Long): Boolean {
        val db = writableDatabase

        // Gets the list id with the list name that user inserted
        val cursor = db.rawQuery(SELECT_LIST_ID_WITH_SAME_NAME, arrayOf(listName))
        cursor.moveToFirst()
        val count = cursor.count

        // If exist one and it isn't with the same id
        if(count > 1 && cursor.getLong(cursor.getColumnIndex(LIST_ID)) != listId) {
            cursor.close()
            return false
        }

        // Update list in the list table
        val valuesL = ContentValues()
        valuesL.put(LIST_NAME, listName)
        valuesL.put(LIST_DATE, convertDateToDatetime(Date()))
        valuesL.put(LIST_IS_BOUGHT, 0)
        db.update(LIST_TABLE_NAME, valuesL,"$LIST_ID = ?", arrayOf(listId.toString()))

        // Get the products id associated to this list
        val cursorPL = db.rawQuery(SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT, arrayOf(getListIdByName(listName).toString()))
        cursorPL.moveToFirst()

        val idProducts = arrayListOf<Int>()
        while(!cursorPL.isAfterLast) {
            idProducts.add(cursorPL.getInt(cursorPL.getColumnIndex(PRODUCT_ID)))
            cursorPL.moveToNext()
        }
        cursorPL.close()

        // Remove those lines from the list_product table
        for(i in 0 until idProducts.size)
            db.delete(LIST_PRODUCT_TABLE_NAME, "$PRODUCT_ID = ? AND $LIST_ID = ?", arrayOf(idProducts[i].toString(), listId.toString()))

        // Set all the new products to the list_product table
        for(key in selectedProducts.keys) {

            val cursorP = db.rawQuery(SELECT_PRODUCT_ID_BY_NAME, arrayOf(key))
            cursorP.moveToFirst()
            val productId = cursorP.getLong(cursorP.getColumnIndex(PRODUCT_ID))

            val valuesPL = ContentValues()
            valuesPL.put(LIST_ID, listId)
            valuesPL.put(PRODUCT_ID, productId)

            if(selectedProducts[key] != null) {
                val amount = selectedProducts[key]!!.split(" ")
                valuesPL.put(LIST_PRODUCT_QUANTITY, amount[0])

                if(amount.size == 2){
                    val cursorU = db.rawQuery(SELECT_UNIT_ID_BY_NAME, arrayOf(amount[1]))
                    cursorU.moveToFirst()
                    val unitId = cursorU.getLong(cursorU.getColumnIndex(UNIT_ID))
                    valuesPL.put(UNIT_ID, unitId)
                }
                else
                    valuesPL.putNull(UNIT_ID)
            } else {
                valuesPL.put(LIST_PRODUCT_QUANTITY, 0)
                valuesPL.putNull(UNIT_ID)
            }

            db.insert(LIST_PRODUCT_TABLE_NAME, null, valuesPL)
        }

        return true
    }

    fun deleteListByName(listName: String) {
        val db = writableDatabase

        val listId = getListIdByName(listName)

        // Get the products id associated to this list
        val cursorPL = db.rawQuery(SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT, arrayOf(listId.toString()))
        cursorPL.moveToFirst()

        val idProducts = arrayListOf<Int>()
        while(!cursorPL.isAfterLast) {
            idProducts.add(cursorPL.getInt(cursorPL.getColumnIndex(PRODUCT_ID)))
            cursorPL.moveToNext()
        }
        cursorPL.close()

        // Remove those lines from the list_product table
        for(i in 0 until idProducts.size)
            db.delete(LIST_PRODUCT_TABLE_NAME, "$PRODUCT_ID = ? AND $LIST_ID = ?", arrayOf(idProducts[i].toString(), listId.toString()))

        // Remove the list from the list table
        db.delete(LIST_TABLE_NAME, "$LIST_ID = ?", arrayOf(listId.toString()))
    }

    private fun getOrderedLists(listOrder: HashMap<String, String?>): String {
        val query: String
        when {
            listOrder.keys.contains(ALPHABETICAL_ORDER) -> {
                query = if(listOrder[ALPHABETICAL_ORDER] == ORDER_ASC)
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_NAME_ASC
                else
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_NAME_DESC
            }
            listOrder.keys.contains(CREATED_DATE_ORDER) -> {
                query = if(listOrder[CREATED_DATE_ORDER] == ORDER_ASC)
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_DATE_ASC
                else
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_DATE_DESC
            }
            listOrder.keys.contains(IS_BOUGHT_ORDER) -> {
                query = if(listOrder[IS_BOUGHT_ORDER] == ORDER_ASC)
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_IS_BOUGHT_ASC
                else
                    SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT_ORDER_BY_IS_BOUGHT_DESC
            }
            else -> {
                query = SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT
            }
        }
        return query
    }

}