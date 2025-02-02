package com.example.nestor

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog

fun authPopup(context:Context) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.auth_popup, null)

    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()

    dialog.show()
}