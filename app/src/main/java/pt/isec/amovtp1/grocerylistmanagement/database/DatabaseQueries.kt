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
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListProductConstants.LIST_PRODUCT_QUANTITY
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ListProductConstants.LIST_PRODUCT_TABLE_NAME
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_BRAND
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_ID
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_IMAGE_FILEPATH
import pt.isec.amovtp1.grocerylistmanagement.database.DatabaseConstants.ProductConstants.PRODUCT_NAME
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

        const val CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS $CATEGORY_TABLE_NAME (" +
                " $CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $CATEGORY_NAME TEXT NOT NULL )"

        const val CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_TABLE_NAME (" +
                " $PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $PRODUCT_NAME TEXT NOT NULL," +
                " $PRODUCT_BRAND TEXT NULL," +
                " $PRODUCT_IMAGE_FILEPATH TEXT NOT NULL," +
                " $CATEGORY_ID INTEGER NOT NULL," +
                " FOREIGN KEY ($CATEGORY_ID) REFERENCES $CATEGORY_TABLE_NAME ($CATEGORY_ID))"

        const val CREATE_UNIT_TABLE = "CREATE TABLE IF NOT EXISTS $UNIT_TABLE_NAME (" +
                " $UNIT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $UNIT_NAME TEXT NOT NULL )"

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
                " $LIST_PRODUCT_QUANTITY INTEGER NULL," +
                " $UNIT_ID INTEGER NULL," +
                " FOREIGN KEY ($LIST_ID) REFERENCES $LIST_TABLE_NAME ($LIST_ID)," +
                " FOREIGN KEY ($PRODUCT_ID) REFERENCES $PRODUCT_TABLE_NAME ($PRODUCT_ID)," +
                " FOREIGN KEY ($UNIT_ID) REFERENCES $UNIT_TABLE_NAME ($UNIT_ID))"

    }

    object DropTableQueries : BaseColumns {
        const val DROP_LIST_PRODUCT_TABLE = "DROP TABLE IF EXISTS $LIST_PRODUCT_TABLE_NAME"

        const val DROP_PRODUCT_PRICE_TABLE = "DROP TABLE IF EXISTS $PRODUCT_PRICE_TABLE_NAME"

        const val DROP_PRODUCT_OBSERVATION_TABLE = "DROP TABLE IF EXISTS $PRODUCT_OBSERVATION_TABLE_NAME"

        const val DROP_UNIT_TABLE = "DROP TABLE IF EXISTS $UNIT_TABLE_NAME"

        const val DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS $PRODUCT_TABLE_NAME"

        const val DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS $CATEGORY_TABLE_NAME"

        const val DROP_LIST_TABLE = "DROP TABLE IF EXISTS $LIST_TABLE_NAME"
    }

    object SelectQueries : BaseColumns {
        const val SELECT_CATEGORY_NAMES = "SELECT $CATEGORY_NAME FROM $CATEGORY_TABLE_NAME"

        const val SELECT_CATEGORIES_WITH_SAME_NAME = "SELECT $CATEGORY_ID FROM $CATEGORY_TABLE_NAME WHERE $CATEGORY_NAME LIKE ?"

        const val SELECT_PRODUCT_WITH_SAME_NAME = "SELECT $PRODUCT_NAME FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_NUM_PRODUCTS = "SELECT $PRODUCT_ID FROM $PRODUCT_TABLE_NAME"

        const val SELECT_ALL_PRODUCT_INFO = "SELECT * FROM $PRODUCT_TABLE_NAME"

        const val SELECT_CATEGORY_NAME_BY_ID = "SELECT $CATEGORY_NAME FROM $CATEGORY_TABLE_NAME WHERE $CATEGORY_ID = ?"

        const val SELECT_ALL_PRODUCT_OBSERVATIONS = "SELECT * FROM $PRODUCT_OBSERVATION_TABLE_NAME WHERE $PRODUCT_ID = ?"

        const val SELECT_ALL_PRODUCT_NAME_CATEGORY = "SELECT $PRODUCT_NAME, $CATEGORY_ID FROM $PRODUCT_TABLE_NAME"

        const val SELECT_CHECKED_PRODUCT_NAME_CATEGORY = "SELECT $PRODUCT_NAME, $CATEGORY_ID FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_PRODUCT_NAME_BY_CATEGORY = "SELECT $PRODUCT_NAME FROM $PRODUCT_TABLE_NAME WHERE $CATEGORY_ID = ?"

        const val SELECT_NUM_UNITS = "SELECT $UNIT_ID FROM $UNIT_TABLE_NAME"

        const val SELECT_UNIT_NAMES = "SELECT $UNIT_NAME FROM $UNIT_TABLE_NAME"

        const val SELECT_UNITS_WITH_SAME_NAME = "SELECT $UNIT_ID FROM $UNIT_TABLE_NAME WHERE $UNIT_NAME LIKE ?"

        const val SELECT_LISTS_WITH_SAME_NAME = "SELECT $LIST_ID FROM $LIST_TABLE_NAME WHERE $LIST_NAME = ?"

        const val SELECT_PRODUCT_ID_BY_NAME = "SELECT $PRODUCT_ID FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_UNIT_ID_BY_NAME = "SELECT $UNIT_ID FROM $UNIT_TABLE_NAME WHERE $UNIT_NAME = ?"

        const val SELECT_PRODUCT_INFO_BY_NAME = "SELECT $PRODUCT_BRAND, $PRODUCT_IMAGE_FILEPATH, $CATEGORY_ID " +
                "FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_PRODUCT_ID_WITH_SAME_NAME = "SELECT $PRODUCT_ID FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_PRODUCT_IMAGE_FILEPATH_FROM_NAME = "SELECT $PRODUCT_IMAGE_FILEPATH FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_PRODUCT_BRAND_FROM_NAME = "SELECT $PRODUCT_BRAND FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"

        const val SELECT_ALL_LISTS = "SELECT $LIST_NAME, $LIST_DATE FROM $LIST_TABLE_NAME"

        const val SELECT_ALL_LIST_ID = "SELECT $LIST_ID FROM $LIST_TABLE_NAME"

        const val SELECT_ALL_NOT_PURCHASED_LISTS_ID = "SELECT $LIST_ID FROM $LIST_TABLE_NAME WHERE $LIST_IS_BOUGHT = 0"

        const val SELECT_LIST_ID_BY_NAME = "SELECT $LIST_ID FROM $LIST_TABLE_NAME WHERE $LIST_NAME = ?"

        const val SELECT_ALL_PRODUCTS_ID_IN_LIST_PRODUCT = "SELECT $PRODUCT_ID FROM $LIST_PRODUCT_TABLE_NAME WHERE $LIST_ID = ?"

        const val SELECT_PRODUCT_NAME_BY_ID = "SELECT $PRODUCT_NAME FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_ID = ?"

        const val SELECT_LIST_NAME_BY_ID = "SELECT $LIST_NAME FROM $LIST_TABLE_NAME WHERE $LIST_ID = ?"

        const val SELECT_ALL_INFO_IN_LIST_PRODUCT = "SELECT $PRODUCT_ID, $LIST_PRODUCT_QUANTITY, $UNIT_ID " +
                "FROM $LIST_PRODUCT_TABLE_NAME WHERE $LIST_ID = ?"

        const val SELECT_UNIT_NAME_BY_ID = "SELECT $UNIT_NAME FROM $UNIT_TABLE_NAME WHERE $UNIT_ID = ?"

        const val SELECT_LIST_ID_WITH_SAME_NAME = "SELECT $LIST_ID FROM $LIST_TABLE_NAME WHERE $LIST_NAME = ?"

        const val SELECT_LIST_INFO_ORDER_BY_NAME_ASC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_NAME ASC"

        const val SELECT_NOT_PURCHASED_LIST_INFO_ORDER_BY_NAME_ASC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME WHERE $LIST_IS_BOUGHT = 0 ORDER BY $LIST_NAME ASC"

        const val  SELECT_LIST_INFO_ORDER_BY_NAME_DESC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_NAME DESC"

        const val SELECT_NOT_PURCHASED_LIST_INFO_ORDER_BY_NAME_DESC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME WHERE $LIST_IS_BOUGHT = 0 ORDER BY $LIST_NAME DESC"

        const val  SELECT_LIST_INFO_ORDER_BY_DATE_ASC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_DATE ASC"

        const val SELECT_NOT_PURCHASED_LIST_INFO_ORDER_BY_DATE_ASC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME WHERE $LIST_IS_BOUGHT = 0 ORDER BY $LIST_DATE ASC"

        const val SELECT_LIST_INFO_ORDER_BY_DATE_DESC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_DATE DESC"

        const val SELECT_NOT_PURCHASED_LIST_INFO_ORDER_BY_DATE_DESC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME WHERE $LIST_IS_BOUGHT = 0 ORDER BY $LIST_DATE DESC"

        const val  SELECT_LIST_INFO_ORDER_BY_IS_BOUGHT_ASC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_IS_BOUGHT ASC"

        const val  SELECT_LIST_INFO_ORDER_BY_IS_BOUGHT_DESC = "SELECT $LIST_NAME, $LIST_DATE " +
                "FROM $LIST_TABLE_NAME ORDER BY $LIST_IS_BOUGHT DESC"

        const val SELECT_PRODUCT_QUANTITY_UNIT = "SELECT $LIST_PRODUCT_QUANTITY, $UNIT_ID FROM $LIST_PRODUCT_TABLE_NAME " +
                "WHERE $PRODUCT_ID = ? AND $LIST_ID = ?"

        const val SELECT_PRODUCT_PRICES_AND_DATE = "SELECT $PRODUCT_PRICE_PRICE, $PRODUCT_PRICE_DATE FROM $PRODUCT_PRICE_TABLE_NAME " +
                "WHERE $PRODUCT_ID = ? ORDER BY $PRODUCT_PRICE_DATE ASC"

        const val SELECT_LAST_PRODUCT_PRICE_AND_DATE = "SELECT $PRODUCT_PRICE_PRICE, $PRODUCT_PRICE_DATE " +
                "FROM $PRODUCT_PRICE_TABLE_NAME WHERE $PRODUCT_ID = ?"

        const val SELECT_ALL_PRICES_BY_PRODUCT_ID = "SELECT $PRODUCT_PRICE_PRICE FROM $PRODUCT_PRICE_TABLE_NAME WHERE $PRODUCT_ID = ?"

        const val SELECT_CATEGORY_ID_BY_PRODUCT_NAME = "SELECT $CATEGORY_ID FROM $PRODUCT_TABLE_NAME WHERE $PRODUCT_NAME = ?"
    }


}