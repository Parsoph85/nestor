package com.nestor.nestor

import android.app.Activity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog

fun authPopup(activity: Activity, width: Int) {
    val layoutInflater = activity.layoutInflater
    val dialogView = layoutInflater.inflate(R.layout.auth_popup, null)
    val builder = AlertDialog.Builder(activity).setView(dialogView)
    val dialog = builder.create()
    dialog.show()

    val mainLayout = dialogView.findViewById<LinearLayout>(R.id.mainLayout)
    val authButton = dialogView.findViewById<Button>(R.id.authButton)

    // Используем FrameLayout.LayoutParams, если родитель mainLayout — FrameLayout
    val layoutParams = FrameLayout.LayoutParams((width * 0.9).toInt(), (width * 1.5).toInt())
    mainLayout.layoutParams = layoutParams

    authButton.text = "Авторизоваться через Яндекс"
    authButton.setOnClickListener {
        if (activity is MainActivity) {
            activity.startYandexAuth()
        }
        dialog.dismiss()
    }
}
