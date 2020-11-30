package pt.isec.amovtp1.grocerylistmanagement.data

import java.util.*
import kotlin.collections.ArrayList

class ListInformation(var listName: String, var date: Date, var isBought: Boolean)

class Product(var name: String, var category: String, var brand: String?, var filePath: String, var observations: ArrayList<Observation>) {
    // Amount Class
    data class Amount(var quantity: Double, var unit: String)
    var amount: Amount? = null

    // Observations Class
    data class Observation(var observation: String, var date: Date)

    // Price class
    data class Price(var price: Double, var priceDate: Date)
    var prices: ArrayList<Price> = arrayListOf()

    constructor(name: String, category: String, brand: String?, filePath: String, observations: ArrayList<Observation>, quantity: Double, unit: String)
            : this(name, category, brand, filePath, observations) {
        amount = Amount(quantity, unit)
    }

    // TODO: identificador baseado no código de barras do produto -> BONUS
}