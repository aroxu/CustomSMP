package me.aroxu.customsmp.utils

object BoolConvert {
    fun boolToInt(bool: Boolean): Int {
        return when (bool) {
            true -> 1
            false -> 0
        }
    }

    fun intToBool(integer: Int): Boolean {
        return when (integer) {
            1 -> true
            0 -> false
            else -> false
        }
    }
}
