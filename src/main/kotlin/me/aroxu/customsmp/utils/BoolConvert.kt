package me.aroxu.customsmp.utils

object BoolConvert {
    fun BoolToInt(bool: Boolean): Int {
        return when (bool) {
            true -> return 1
            false -> return 0
        }
    }

    fun IntToBool(integer: Int): Boolean {
        return when(integer) {
            1 -> return true
            0 -> return false
            else -> return false
        }
    }
}
