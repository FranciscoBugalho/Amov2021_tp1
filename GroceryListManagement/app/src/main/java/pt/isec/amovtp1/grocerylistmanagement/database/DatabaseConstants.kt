package pt.isec.amovtp1.grocerylistmanagement.database

object DatabaseConstants {
    // Database name
    const val DATABASE_NAME = "glmDb.db"

    // Database version
    const val DATABASE_VERSION = 1

    /*---- PRODUCT ENTITY ----*/
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

    // Product quantity column name
    const val PRODUCT_QUANTITY = "quantity"
    /*------------------------*/

    /*---- CATEGORY ENTITY ----*/
    // Category table name
    const val CATEGORY_TABLE_NAME = "glm_category"

    // Category id column name
    const val CATEGORY_ID = "categoryId"

    // Category name column name
    const val CATEGORY_NAME = "categoryName"
    /*-------------------------*/

    /*---- UNIT ENTITY ----*/
    // ProductUnit table name
    const val UNIT_TABLE_NAME = "glm_unit"

    // ProductUnit id column name
    const val UNIT_ID = "unitId"

    // ProductUnit name column name
    const val UNIT_NAME = "unitName"
    /*-----------------------*/

    /*---- PRODUCT PRICE ENTITY ----*/
    // Product Price table name
    const val PRODUCT_PRICE_TABLE_NAME = "productPrice"

    // Product Price id column name
    const val PRODUCT_PRICE_ID = "productPriceId"

    // Product Price price column name
    const val PRODUCT_PRICE_PRICE = "price"
    /*------------------------------*/

    /*---- PRODUCT OBSERVATION ENTITY ----*/
    // Product Observation table name
    const val PRODUCT_OBSERVATION_TABLE_NAME = "glm_product_observation"

    // Product Observation id column name
    const val PRODUCT_OBSERVATION_ID = "productObservationId"

    // Product Observation observation column name
    const val PRODUCT_OBSERVATION_OBSERVATION = "productObservationTxt"

    // Product Observation date column name
    const val PRODUCT_OBSERVATION_DATE = "observationDate"
    /*------------------------------------*/

    /*---- LIST ENTITY ----*/
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
    /*---------------------*/

    /*---- LIST_PRODUCT ENTITY ----*/
    // ProductLists Product table name
    const val LIST_PRODUCT_TABLE_NAME = "glm_list_product"
    /*----------------------------*/

    /*---- PRODUCT_PRICE_PRODUCT ENTITY ----*/
    // Product Price Product table name
    const val PRODUCT_PRICE_PRODUCT_TABLE_NAME = "glm_product_price_product"

    // Product Price Product date column name
    const val PRODUCT_PRICE_PRODUCT_PRICE_DATE = "priceDate"
    /*--------------------------------------*/
}