package pt.isec.amovtp1.grocerylistmanagement.database

import android.provider.BaseColumns
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.CategoryConstants.CATEGORY_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_IS_BOUGHT
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListConstants.LIST_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListProductConstants.LIST_PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_BRAND
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_IMAGE_FILEPATH
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_QUANTITY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_OBSERVATION
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductObservationConstants.PRODUCT_OBSERVATION_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductPriceConstants.PRODUCT_PRICE_DATE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductPriceConstants.PRODUCT_PRICE_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductPriceConstants.PRODUCT_PRICE_PRICE
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductPriceConstants.PRODUCT_PRICE_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.UnitConstants.UNIT_TABLE_NAME

object DatabaseQueries {

    object CreateTableQueries : BaseColumns {
        const val CREATE_LIST_TABLE = "CREATE TABLE IF NOT EXISTS $LIST_TABLE_NAME (" +
                " $LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $LIST_NAME TEXT NOT NULL," +
                " $LIST_DATE DATETIME NOT NULL," +
                " $LIST_IS_BOUGHT INTEGER NOT NULL )"

        const val CREATE_UNIT_TABLE = "CREATE TABLE IF NOT EXISTS $UNIT_TABLE_NAME (" +
                " $UNIT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $UNIT_NAME TEXT NOT NULL )"

        const val CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS $CATEGORY_TABLE_NAME (" +
                " $CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $CATEGORY_NAME TEXT NOT NULL )"

        const val CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_TABLE_NAME (" +
                " $PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $PRODUCT_NAME TEXT NOT NULL," +
                " $PRODUCT_BRAND TEXT NULL," +
                " $PRODUCT_IMAGE_FILEPATH TEXT NOT NULL," +
                " $PRODUCT_QUANTITY INTEGER NULL," +
                " $CATEGORY_ID INTEGER NOT NULL," +
                " $UNIT_ID INTEGER NULL," +
                " FOREIGN KEY ($UNIT_ID) REFERENCES $UNIT_TABLE_NAME ($UNIT_ID)," +
                " FOREIGN KEY ($CATEGORY_ID) REFERENCES $CATEGORY_TABLE_NAME ($CATEGORY_ID))"

        const val CREATE_PRODUCT_OBSERVATION_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_OBSERVATION_TABLE_NAME (" +
                " $PRODUCT_OBSERVATION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $PRODUCT_OBSERVATION_OBSERVATION TEXT NOT NULL," +
                " $PRODUCT_OBSERVATION_DATE DATETIME NOT NULL," +
                " $PRODUCT_ID INTEGER NOT NULL," +
                " FOREIGN KEY ($PRODUCT_ID) REFERENCES $PRODUCT_TABLE_NAME ($PRODUCT_ID))"

        const val CREATE_PRODUCT_PRICE_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_PRICE_TABLE_NAME (" +
                " $PRODUCT_PRICE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $PRODUCT_PRICE_PRICE REAL NOT NULL," +
                " $PRODUCT_PRICE_DATE DATETIME NOT NULL," +
                " $PRODUCT_ID INTEGER NOT NULL," +
                " FOREIGN KEY ($PRODUCT_ID) REFERENCES $PRODUCT_TABLE_NAME ($PRODUCT_ID))"

        const val CREATE_LIST_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS $LIST_PRODUCT_TABLE_NAME (" +
                " $LIST_ID INTEGER NOT NULL," +
                " $PRODUCT_ID INTEGER NOT NULL," +
                " FOREIGN KEY ($LIST_ID) REFERENCES $LIST_TABLE_NAME ($LIST_ID)," +
                " FOREIGN KEY ($PRODUCT_ID) REFERENCES $PRODUCT_TABLE_NAME ($PRODUCT_ID))"

    }

    object DropTableQueries : BaseColumns {
        const val DROP_LIST_PRODUCT_TABLE = "DROP TABLE IF EXISTS $LIST_PRODUCT_TABLE_NAME"

        const val DROP_PRODUCT_PRICE_TABLE = "DROP TABLE IF EXISTS $PRODUCT_PRICE_TABLE_NAME"

        const val DROP_PRODUCT_OBSERVATION_TABLE = "DROP TABLE IF EXISTS $PRODUCT_OBSERVATION_TABLE_NAME"

        const val DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS $PRODUCT_TABLE_NAME"

        const val DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS $CATEGORY_TABLE_NAME"

        const val DROP_UNIT_TABLE = "DROP TABLE IF EXISTS $UNIT_TABLE_NAME"

        const val DROP_LIST_TABLE = "DROP TABLE IF EXISTS $LIST_TABLE_NAME"
    }

    object SelectQueries : BaseColumns {
        const val SELECT_CATEGORY_NAMES = "SELECT $CATEGORY_NAME FROM $CATEGORY_TABLE_NAME"

        const val SELECT_CATEGORIES_WITH_SAME_NAME = "SELECT $CATEGORY_ID FROM $CATEGORY_TABLE_NAME WHERE $CATEGORY_NAME = ?"

        const val SELECT_PRODUCT_WITH_SAME_NAME = "SELECT $PRODUCT_NAME FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"
    }


}