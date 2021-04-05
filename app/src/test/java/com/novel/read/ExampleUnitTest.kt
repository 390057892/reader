package com.novel.read

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        romanToInt("III")
    }

    fun romanToInt(s: String): Int {
        val map: MutableMap<Char, Int> = HashMap()
        map['I'] = 1
        map['V'] = 5
        map['X'] = 10
        map['L'] = 50
        map['C'] = 100
        map['D'] = 500
        map['M'] = 1000
        var result = 0
        var preValue = map[s[0]]!!
        for (i in 1 until s.length) {
            val num = map[s[i]]!!
            if (preValue < num) {
                result -= preValue
            } else {
                result += preValue
            }
            preValue = num
        }
        result += preValue
        return result
    }
}