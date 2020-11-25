package pt.isec.amovtp1.grocerylistmanagement.data

import android.provider.BaseColumns

object Constants {
    // Package name
    const val PACKAGE_NAME = "pt.isec.amovtp1.grocerylistmanagement"

    // Assets Path
    const val ASSET_IMAGE_PATH = "images/none_img.png"

    // Request Code for Gallery
    const val REQUEST_CODE_GALLERY = 10

    // Request Code for Camera
    const val REQUEST_CODE_CAMERA = 20

    // Date format for the image name
    const val DATE_FORMAT_TO_IMG = "yyyyMMdd_HHmmss"

    // Date format to the database
    const val DATE_FORMATE_TO_DB = "dd/MM/yyyy HH:mm:ss"

    object IntentConstants : BaseColumns {
        // Intent putExtra name to open the list details
        const val IS_LIST_DETAILS = "isDetails"

        // Intent putExtra list name
        const val LIST_NAME = "listName"

        // Intent putExtra to check if the activity comes from the CreateNewProduct
        const val IS_NEW_PRODUCT = "isNewProductActivity"
    }

    object SaveDataConstants : BaseColumns {
        // List Name
        const val LIST_NAME_STR = "listName"
    }
}