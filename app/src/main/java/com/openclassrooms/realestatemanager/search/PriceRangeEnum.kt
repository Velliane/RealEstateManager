package com.openclassrooms.realestatemanager.search

enum class PriceRangeEnum(val priceRange: String, val minValue: Int, val maxValue: Int) {

    ANY("any", 0, 0),
    LESS_THAN_HUNDRED("< 100k", 0, 100000),
    BETWEEN_HUNDRED_TWOHUNDRED("100k < 200k", 100000, 200000),
    BETWEEN_TWOHUNDER_THREEHUNDRED("200k < 300k", 200000, 300000),
    BETWEEN_THREEHUNDRED_FOURHUNDRED("300k < 400k", 300000, 400000),
    BETWEEN_FOURHUNDRED_FIVEHUNDRED("400k < 500k", 400000, 500000),
    BETWEEN_FIVEHUNDRED_SIXHUNDRED("500k < 600k", 500000, 600000),
    BETWEEN_SIXHUNDRED_SEVENHUNDRED("600k < 700k", 600000, 700000);

    companion object {
        fun getPriceRangeEnumByValue(priceRange: String) = valueOf(priceRange)
    }

}

