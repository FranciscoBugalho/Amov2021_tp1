package pt.isec.amovtp1.grocerylistmanagement.data

import android.provider.BaseColumns

object Constants {
    // Package name
    const val PACKAGE_NAME = "pt.isec.amovtp1.grocerylistmanagement"

    // Assets Path for none_img
    const val ASSET_IMAGE_PATH_NO_IMG = "images/none_img.png"

    // Assets Path for shopping cart empty
    const val ASSET_IMAGE_PATH_SHOPPING_EMPTY = "images/shopping_carts/shopping_cart_empty.png"

    // Assets Path for shopping cart empty
    const val ASSET_IMAGE_PATH_SHOPPING_FULL = "images/shopping_carts/shopping_cart_full.png"

    // Request Code for Gallery
    const val REQUEST_CODE_GALLERY = 10

    // Request Code for Camera
    const val REQUEST_CODE_CAMERA = 20

    // Date format for the image name
    const val DATE_FORMAT_TO_IMG = "yyyyMMdd_HHmmss"

    // Date format to the database
    const val DATE_FORMAT_TO_DB = "dd/MM/yyyy HH:mm:ss"

    // Date format for the CardView
    const val DATE_FORMAT_TO_CARD = "dd/MM/yy"

    // Time format for the CardView
    const val TIME_FORMAT_TO_CARD = "HH:mm"

    const val SHOW_ALL_PRODUCTS = "All"

    const val SHOW_CHECKED_PRODUCTS = "Checked"

    const val ERROR = "Error"

    object IntentConstants : BaseColumns {
        // Intent putExtra name to open the product edition menu
        const val IS_EDIT_PRODUCT = "isEditProduct"

        // Intent putExtra name to refer the product name which will be edited
        const val PRODUCT_NAME_TO_EDIT = "productNameToEdit"

        // Intent putExtra to refer that the activity come from ManageProductsActivity
        const val IS_VIEW_PRODUCTS = "isViewProducts"

        // Intent putExtra name to open the list details
        const val IS_LIST_DETAILS = "isDetails"

        // Intent putExtra list name
        const val LIST_NAME = "listName"

        // Intent putExtra to check if the activity comes from the CreateNewProduct
        const val IS_NEW_PRODUCT = "isNewProductActivity"

        // Intent putExtra to the HashMap with the selected products and their amount
        const val SELECTED_PRODUCTS_LIST = "selectedProducts"

        // Intent putExtra to refer the list name which will be edited
        const val LIST_NAME_TO_EDIT = "listNameToEdit"

        // Intent putExtra with the title from the previous activity (Create or Edit)
        const val MANAGE_PRODUCTS_TITLE = "manageProductTitle"

        // Intent putExtra to define if will show categories or units in the activity
        const val SHOW_CATEGORIES = "showCategories"

        // Intent putExtra to refer the list name which will be shown
        const val LIST_NAME_TO_SHOW = "listNameToShow"
    }

    object SaveDataConstants : BaseColumns {
        const val PRODUCT_NAME_STR = "productName"

        const val PRODUCT_CATEGORY_NUM = "productCategoryNum"

        const val PRODUCT_BRAND_STR = "productBrand"

        const val PRODUCT_OBSERVATION_STR = "productObservation"

        const val PRODUCT_IMAGE_STR = "productImageFilepath"

        const val PRODUCT_ID_EDIT_MODE = "productIdForEditMode"

        const val IS_MANAGE_PRODUCT_ACTIVITY = "isManageProductActivity"

        const val LIST_NAME_STR = "listName"

        const val SELECTED_PRODUCTS_STR = "selectedProducts"

        const val LIST_ID_EDIT_MODE = "listIdForEditMode"

        const val TITLE_STR = "listActivityTitle"

        const val LIST_ORDER_STR = "listOrder"

        const val ALL_PRODCUTS = "allProducts"

        const val PRUCHASED_PRODUCTS = "purchasedProducts"

        const val IS_CATEGORIES = "isCategories"

        const val TOTAL_PRICE = "totalPrice"
    }

    object ListOrders : BaseColumns {
        const val ORDER_ASC = "ASC"

        const val ORDER_DESC = "DESC"

        const val ALPHABETICAL_ORDER = "alphabetical"

        const val CREATED_DATE_ORDER = "createdDate"

        const val IS_BOUGHT_ORDER = "isBought"

        const val NONE_ORDER = "none"
    }
}