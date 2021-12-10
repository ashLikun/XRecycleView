package com.ashlikun.xrecycleview.simple

import android.content.Context
import java.lang.StringBuilder
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/24 0024　上午 10:53
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
object Utils {
    fun getJson(context: Context): String {
        val sb = StringBuilder()
        try {
            val inputStream = context.assets.open("home.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            inputStream.close()
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}