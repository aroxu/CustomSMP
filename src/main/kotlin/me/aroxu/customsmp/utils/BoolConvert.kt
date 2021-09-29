package me.aroxu.customsmp.utils

object BoolConvert {
    fun boolToInt(bool: Boolean): Int {
        return when (bool) {
            true -> return 1
            false -> return 0
        }
    }

    fun intToBool(integer: Int): Boolean {
        return when(integer) {
            1 -> return true
            0 -> return false
            else -> return false
        }
    }
}
