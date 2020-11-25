package pt.isec.amovtp1.grocerylistmanagement.database

import android.provider.BaseColumns

object DatabaseConstants {
    // Database name
    const val DATABASE_NAME = "glmDb.db"

    // Database version
    const val DATABASE_VERSION = 1

    /*---- PRODUCT ENTITY ----*/
    object ProductConstants : BaseColumns {
        // Product table name
        const val PRODUCT_TABLE_NAME = "glm_product"

        // Product id column name
        const val PRODUCT_ID = "productId"

        // Product name column name
        const val PRODUCT_NAME = "productName"

        // Product brand column name
        const val PRODUCT_BRAND = "productBrand"

        // Product image filepath column name
        const val PRODUCT_IMAGE_FILEPATH = "productImageFilepath"
    }
    /*------------------------*/

    /*---- CATEGORY ENTITY ----*/
    object CategoryConstants : BaseColumns {
        // Category table name
        const val CATEGORY_TABLE_NAME = "glm_category"

        // Category id column name
        const val CATEGORY_ID = "categoryId"

        // Category name column name
        const val CATEGORY_NAME = "categoryName"
    }
    /*-------------------------*/

    /*---- UNIT ENTITY ----*/
    object UnitConstants : BaseColumns {
        // ProductUnit table name
        const val UNIT_TABLE_NAME = "glm_unit"

        // ProductUnit id column name
        const val UNIT_ID = "unitId"

        // ProductUnit name column name
        const val UNIT_NAME = "unitName"
    }
    /*-----------------------*/

    /*---- PRODUCT PRICE ENTITY ----*/
    object ProductPriceConstants : BaseColumns {
        // Product Price table name
        const val PRODUCT_PRICE_TABLE_NAME = "productPrice"

        // Product Price id column name
        const val PRODUCT_PRICE_ID = "productPriceId"

        // Product Price price column name
        const val PRODUCT_PRICE_PRICE = "price"

        // Product Price date column name
        const val PRODUCT_PRICE_DATE = "priceDate"
    }
    /*------------------------------*/

    /*---- PRODUCT OBSERVATION ENTITY ----*/
    object ProductObservationConstants : BaseColumns {
        // Product Observation table name
        const val PRODUCT_OBSERVATION_TABLE_NAME = "glm_product_observation"

        // Product Observation id column name
        const val PRODUCT_OBSERVATION_ID = "productObservationId"

        // Product Observation observation column name
        const val PRODUCT_OBSERVATION_OBSERVATION = "productObservationTxt"

        // Product Observation date column name
        const val PRODUCT_OBSERVATION_DATE = "observationDate"
    }
    /*------------------------------------*/

    /*---- LIST ENTITY ----*/
    object ListConstants : BaseColumns {
        // Product Lists table name
        const val LIST_TABLE_NAME = "glm_product_lists"

        // Product Lists id column name
        const val LIST_ID = "listId"

        // Product Lists name column name
        const val LIST_NAME = "listName"

        // Product Lists date column name
        const val LIST_DATE = "listDate"

        // Product Lists is bought column name
        const val LIST_IS_BOUGHT = "isBought"
    }
    /*---------------------*/

    /*---- LIST_PRODUCT ENTITY ----*/
    object ListProductConstants : BaseColumns {
        // ProductLists Product table name
        const val LIST_PRODUCT_TABLE_NAME = "glm_list_product"

        // Product quantity column name
        const val LIST_PRODUCT_QUANTITY = "productQuantity"
    }
    /*----------------------------*/

}