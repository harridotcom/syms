package com.example.sym.others

import java.util.regex.Pattern
object GrpData {


    fun extractGroupedProductDetails(input: String): List<List<Pair<String, Double>>> {
        // Regular expression to match each "items" entry
        val regex = Regex("items [^=]+=\\[([^]]+)]")
        val groupedProductDetails = mutableListOf<List<Pair<String, Double>>>()

        regex.findAll(input).forEach { matchResult ->
            val itemsString = matchResult.groups[1]?.value ?: return@forEach

            // Patterns to extract product names and prices
            val namePattern = Pattern.compile("name=([^,}]+)")
            val pricePattern = Pattern.compile("price=([0-9.]+)")

            val products = mutableListOf<Pair<String, Double>>()

            // Find each product's name and price
            val nameMatcher = namePattern.matcher(itemsString)
            val priceMatcher = pricePattern.matcher(itemsString)

            while (nameMatcher.find() && priceMatcher.find()) {
                val name = nameMatcher.group(1) ?: "Unknown"
                val price = priceMatcher.group(1)?.toDoubleOrNull() ?: 0.0
                products.add(Pair(name, price)) // Add each product with its price
            }

            groupedProductDetails.add(products) // Add the group to the main list
        }

        return groupedProductDetails
    }


}