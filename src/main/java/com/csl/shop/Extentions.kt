package com.csl.shop

import android.app.Activity
import android.content.Context

fun Activity.setNickname(nickname : String) {
    getSharedPreferences("SHOP", Context.MODE_PRIVATE)
        .edit()
        .putString("NICKNAME", nickname)
        .apply()
}

fun Activity.getNickname() : String? {
    return getSharedPreferences("SHOP", Context.MODE_PRIVATE).getString("NICKNAME", "")
}