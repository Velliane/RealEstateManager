package com.openclassrooms.realestatemanager.add_edit

data class PropertyToEdit (

        var id_property: String = "",

        var agent: String = "",

        var address: Address,
        var type: String = "",
        var type_id: Int = 0,
        var price: Int = 0,
        var surface: Int? = 0,
        var rooms_nbr: Int? = 0,
        var bath_nbr: Int? = 0,
        var bed_nbr: Int? = 0,
        var description: String? = "",
        var  in_sale: Boolean = true,
        var nearby: String? = "",

        var created_date: String = "",
        var sold_date: String? = "",

        var date: String = ""
)