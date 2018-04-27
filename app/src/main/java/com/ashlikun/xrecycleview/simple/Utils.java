package com.ashlikun.xrecycleview.simple;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/24 0024　上午 10:53
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class Utils {

    public static String getJson(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open("home.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
