package pt.isec.amovtp1.grocerylistmanagement.database.entities

data class Product(val productId: Int, var productName: String, var productBrand: String? = null,
        var productImageFilepath: String,
        var quantity: Int?,
        var categoryId: Int,
        var unitId: Int? = null)
{

}