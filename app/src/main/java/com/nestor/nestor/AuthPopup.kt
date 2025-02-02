package com.nestor.nestor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.nestor.R
import java.net.InetSocketAddress
import java.net.Socket


fun authPopup(context:Context, width: Int) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.auth_popup, null)
    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()
    dialog.show()

    val mainLayout = dialogView.findViewById<LinearLayout>(R.id.mainLayout)
    val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
    val editLogin = dialogView.findViewById<EditText>(R.id.editLogin)
    val editPassword = dialogView.findViewById<EditText>(R.id.editPassword)
    val rememberCheck = dialogView.findViewById<CheckBox>(R.id.rememberCheck)
    val authButton = dialogView.findViewById<Button>(R.id.authButton)


    val dostup: Boolean = checkServer()


    val layoutParams = LinearLayout.LayoutParams((width * 0.9).toInt(), (width * 1.5).toInt())
    layoutParams.setMargins(16, 16, 16, 16) // Установка отступов (по желанию)
    mainLayout.layoutParams = layoutParams


    val imageParams = LinearLayout.LayoutParams((width * 0.25).toInt(), (width * 0.25).toInt())
    imageParams.setMargins(16, 16, 16, 8) // Установка отступов (по желанию)
    imageView.layoutParams = imageParams


    val loginParams = LinearLayout.LayoutParams((width * 0.7).toInt(), (width * 0.2).toInt())
    loginParams.setMargins(16, 8, 16, 0) // Установка отступов (по желанию)
    editLogin.layoutParams = loginParams


    val passParams = LinearLayout.LayoutParams((width * 0.7).toInt(), (width * 0.2).toInt())
    passParams.setMargins(16, 4, 16, 4) // Установка отступов (по желанию)
    editPassword.layoutParams = passParams


    val checkParams = LinearLayout.LayoutParams((width * 0.7).toInt(), (width * 0.2).toInt())
    checkParams.setMargins(16, 4, 16, 4) // Установка отступов (по желанию)
    rememberCheck.layoutParams = checkParams


    val buttonParams = LinearLayout.LayoutParams((width * 0.4).toInt(), (width * 0.15).toInt())
    buttonParams.setMargins(16, 4, 16, 16) // Установка отступов (по желанию)
    authButton.layoutParams = buttonParams
    val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 8f // Скругление углов
        setColor(Color.WHITE) // Цвет фона
    }
    authButton.background = backgroundDrawable


    if (dostup){
        authButton.setOnClickListener {
            val login = editLogin.text.toString()
            val password = editPassword.text.toString()

            // Здесь вы можете добавить свою логику для проверки логина и пароля
            if (login.isNotEmpty() && password.isNotEmpty()) {
                // Пример проверки (замените на свою логику)
                if (login == "admin" && password == "password") {
                    Toast.makeText(context, "Авторизация успешна!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Закрыть диалог при успешной авторизации
                } else {
                    Toast.makeText(context, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }else{
        authButton.text = "Нет связи"
    }
}

fun checkServer(): Boolean {
    val host = "https://myserver.ru"
    val port = 443
    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), 200)
        }
        true
    } catch (e: Exception) {
        false
    }
}
