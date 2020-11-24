package pt.isec.amovtp1.grocerylistmanagement.database.entities

import java.util.*

data class ProductObservation(val productObservationId: Int, var productObservationTxt: String,
                              var observationDate: Date, var productId: Int
)