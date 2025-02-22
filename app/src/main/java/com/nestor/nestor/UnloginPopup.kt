package com.nestor.nestor


import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.nestor.R

fun unloginPopup(activity: Activity, width: Int) {
    val layoutInflater = LayoutInflater.from(activity)
    val dialogView = layoutInflater.inflate(R.layout.unlogin_popup, null)
    val builder = AlertDialog.Builder(activity).setView(dialogView)
    val dialog = builder.create()
    dialog.show()

    val mainLayout = dialogView.findViewById<LinearLayout>(R.id.mainLayout)
    val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
    val unloginButton = dialogView.findViewById<Button>(R.id.unloginButton)

    val layoutParams = LinearLayout.LayoutParams((width * 0.9).toInt(), (width * 1.5).toInt())
    layoutParams.setMargins(16, 16, 16, 16)
    mainLayout.layoutParams = layoutParams


    val imageParams = LinearLayout.LayoutParams((width * 0.25).toInt(), (width * 0.25).toInt())
    imageParams.setMargins(16, 16, 16, 8)
    imageView.layoutParams = imageParams


    val buttonParams = LinearLayout.LayoutParams((width * 0.4).toInt(), (width * 0.15).toInt())
    buttonParams.setMargins(16, 4, 16, 16)
    unloginButton.layoutParams = buttonParams
    val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 8f
        setColor(Color.WHITE)
    }
    unloginButton.background = backgroundDrawable


    val notesDatabaseHelper = NotesDatabaseHelper(activity)
    val creds = notesDatabaseHelper.getCreds()
    if (creds?.first != null && creds.second != null) {
        val login = ""
        val password = ""
        unloginButton.setOnClickListener {
            notesDatabaseHelper.saveCreds(login, password)
            dialog.dismiss()
        }
    }

}
