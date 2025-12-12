
package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.domain.model.AppCurrency

object CurrencyRepository {
    val worldCurrencies = listOf(
        // --- Major ---
        AppCurrency("USD", "United States Dollar", "$", "🇺🇸"),
        AppCurrency("EUR", "Euro", "€", "🇪🇺"),
        AppCurrency("GBP", "British Pound", "£", "🇬🇧"),
        AppCurrency("INR", "Indian Rupee", "₹", "🇮🇳"),
        AppCurrency("JPY", "Japanese Yen", "¥", "🇯🇵"),
        AppCurrency("CNY", "Chinese Yuan", "¥", "🇨🇳"),
        
        // --- Americas ---
        AppCurrency("CAD", "Canadian Dollar", "$", "🇨🇦"),
        AppCurrency("MXN", "Mexican Peso", "$", "🇲🇽"),
        AppCurrency("BRL", "Brazilian Real", "R$", "🇧🇷"),
        AppCurrency("ARS", "Argentine Peso", "$", "🇦🇷"),
        AppCurrency("CLP", "Chilean Peso", "$", "🇨🇱"),
        AppCurrency("COP", "Colombian Peso", "$", "🇨🇴"),
        AppCurrency("PEN", "Peruvian Sol", "S/", "🇵🇪"),
        
        // --- Europe ---
        AppCurrency("CHF", "Swiss Franc", "Fr", "🇨🇭"),
        AppCurrency("SEK", "Swedish Krona", "kr", "🇸🇪"),
        AppCurrency("NOK", "Norwegian Krone", "kr", "🇳🇴"),
        AppCurrency("DKK", "Danish Krone", "kr", "🇩🇰"),
        AppCurrency("PLN", "Polish Zloty", "zł", "🇵🇱"),
        AppCurrency("CZK", "Czech Koruna", "Kč", "🇨🇿"),
        AppCurrency("HUF", "Hungarian Forint", "Ft", "🇭🇺"),
        AppCurrency("RUB", "Russian Ruble", "₽", "🇷🇺"),
        AppCurrency("TRY", "Turkish Lira", "₺", "🇹🇷"),
        AppCurrency("UAH", "Ukrainian Hryvnia", "₴", "🇺🇦"),
        
        // --- Asia / Pacific ---
        AppCurrency("AUD", "Australian Dollar", "$", "🇦🇺"),
        AppCurrency("NZD", "New Zealand Dollar", "$", "🇳🇿"),
        AppCurrency("SGD", "Singapore Dollar", "$", "🇸🇬"),
        AppCurrency("HKD", "Hong Kong Dollar", "$", "🇭🇰"),
        AppCurrency("KRW", "South Korean Won", "₩", "🇰🇷"),
        AppCurrency("IDR", "Indonesian Rupiah", "Rp", "🇮🇩"),
        AppCurrency("MYR", "Malaysian Ringgit", "RM", "🇲🇾"),
        AppCurrency("PHP", "Philippine Peso", "₱", "🇵🇭"),
        AppCurrency("THB", "Thai Baht", "฿", "🇹🇭"),
        AppCurrency("VND", "Vietnamese Dong", "₫", "🇻🇳"),
        AppCurrency("PKR", "Pakistani Rupee", "₨", "🇵🇰"),
        AppCurrency("BDT", "Bangladeshi Taka", "৳", "🇧🇩"),
        
        // --- Middle East / Africa ---
        AppCurrency("AED", "UAE Dirham", "د.إ", "🇦🇪"),
        AppCurrency("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
        AppCurrency("ILS", "Israeli Shekel", "₪", "🇮🇱"),
        AppCurrency("ZAR", "South African Rand", "R", "🇿🇦"),
        AppCurrency("EGP", "Egyptian Pound", "E£", "🇪🇬"),
        AppCurrency("NGN", "Nigerian Naira", "₦", "🇳🇬"),
        AppCurrency("KES", "Kenyan Shilling", "KSh", "🇰🇪")
    )
}
