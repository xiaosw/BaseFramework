package com.xiaosw.common.util

import android.text.TextPaint

import java.util.ArrayList

/**
 * @ClassName [StringUtils]
 * @Description
 *
 * @Date 2018-02-28.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

object StringUtils {

    private val EMPTY = ""

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    fun isEmpty(str: CharSequence?): Boolean {
        return (str == null || str.toString().trim { it <= ' ' }.isEmpty()
                || str.toString().equals("null", ignoreCase = true))
    }

    fun isNotEmpty(str: String): Boolean {
        return !isEmpty(str)
    }

    /**
     * Helper function for making null strings safe for comparisons, etc.
     *
     * @return (s == null) ? def : s;
     */
    @JvmOverloads
    fun makeSafe(str: String?, def: String = EMPTY): String {
        return str ?: def
    }

    /**
     *
     * @param str
     * @return
     */
    fun trim(str: String?): String {
        return str?.trim { it <= ' ' } ?: EMPTY
    }

    /** 获取字符串宽度  */
    fun getWidthWithText(text: String, size: Float): Float {
        if (isEmpty(text)) {
            return 0f
        }
        val fontPaint = TextPaint()
        fontPaint.textSize = size
        return fontPaint.measureText(text.trim { it <= ' ' }) + (size * 0.1).toInt() // 留点余地
    }

    /**
     * 拼接数组
     *
     * @param array
     * @param separator
     * @return
     */
    fun join(array: ArrayList<String>?,
             separator: String): String {
        val result = StringBuffer()
        if (array != null && array.size > 0) {
            for (str in array) {
                result.append(str)
                result.append(separator)
            }
            result.delete(result.length - 1, result.length)
        }
        return result.toString()
    }

    fun join(iter: Iterator<String>?,
             separator: String): String {
        val result = StringBuffer()
        if (iter != null) {
            while (iter.hasNext()) {
                val key = iter.next()
                result.append(key)
                result.append(separator)
            }
            if (result.isNotEmpty())
                result.delete(result.length - 1, result.length)
        }
        return result.toString()
    }
}
