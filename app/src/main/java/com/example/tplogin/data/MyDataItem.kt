package com.example.tplogin.data

import com.example.tplogin.Url


data class MyDataItem(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int,
    val email : String,
    val name: String,
    val password: String,
    val photoUrl: String,
    val urls: Url

) {
    companion object {
        val email: Any
            get() {
                TODO()
            }
    }
}