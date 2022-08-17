package com.hyoseok.common.utils

object Base62Util {
    private val BASE62: CharArray = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    fun encode(value: Long): String {
        val stringBuilder = StringBuilder()
        var tempValue = value
        do {
            val i = (tempValue % 62).toInt()
            stringBuilder.append(BASE62[i])
            tempValue /= 62
        } while (tempValue > 0)
        return stringBuilder.reverse().toString()
    }

    fun decode(value: String): Int {
        var result = 0
        var power = 1
        for (i in value.indices) {
            val digit = String(BASE62).indexOf(value[i])
            result += digit * power
            power *= 62
        }
        return result
    }
}
