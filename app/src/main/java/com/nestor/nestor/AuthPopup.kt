package com.nestor.nestor

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.nestor.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

private var dostup: Boolean = false


fun authPopup(activity: Activity, width: Int) {
    val layoutInflater = LayoutInflater.from(activity)
    val dialogView = layoutInflater.inflate(R.layout.auth_popup, null)
    val builder = AlertDialog.Builder(activity).setView(dialogView)
    val dialog = builder.create()
    dialog.show()

    val mainLayout = dialogView.findViewById<LinearLayout>(R.id.mainLayout)
    val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
    val editLogin = dialogView.findViewById<EditText>(R.id.editLogin)
    val editPassword = dialogView.findViewById<EditText>(R.id.editPassword)
    val authButton = dialogView.findViewById<Button>(R.id.authButton)

    val layoutParams = LinearLayout.LayoutParams((width * 0.9).toInt(), (width * 1.5).toInt())
    layoutParams.setMargins(16, 16, 16, 16)
    mainLayout.layoutParams = layoutParams


    val imageParams = LinearLayout.LayoutParams((width * 0.25).toInt(), (width * 0.25).toInt())
    imageParams.setMargins(16, 16, 16, 8)
    imageView.layoutParams = imageParams


    val loginParams = LinearLayout.LayoutParams((width * 0.7).toInt(), (width * 0.2).toInt())
    loginParams.setMargins(16, 8, 16, 0)
    editLogin.layoutParams = loginParams


    val passParams = LinearLayout.LayoutParams((width * 0.7).toInt(), (width * 0.2).toInt())
    passParams.setMargins(16, 4, 16, 4)
    editPassword.layoutParams = passParams


    val buttonParams = LinearLayout.LayoutParams((width * 0.4).toInt(), (width * 0.15).toInt())
    buttonParams.setMargins(16, 4, 16, 16)
    authButton.layoutParams = buttonParams
    val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 8f
        setColor(Color.WHITE)
    }
    authButton.background = backgroundDrawable


    dostup = checkServerSync(activity)
    if (dostup) {
        authButton.setOnClickListener {
            val login = editLogin.text.toString()
            val password = editPassword.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty()) {
                val jsonRequest = "{\"login\": \"$login\", \"password\": \"$password\"}"

                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val requestBody = jsonRequest.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://nestornote.ru/api/v1/login")
                    .post(requestBody)
                    .build()

                Thread {
                    try {
                        val response: Response = client.newCall(request).execute()

                        val responseCode = response.code
                        val responseMessage = response.body?.string() ?: ""

                        if (responseCode == 200) { // Это код HTTP_OK
                            val jsonResponse = JSONObject(responseMessage)
                            val requestStatus = jsonResponse.getString("request")

                            activity.runOnUiThread {
                                if (requestStatus == "True") {
                                    Toast.makeText(activity, "Авторизация успешна!", Toast.LENGTH_SHORT).show()
                                    val notesDatabaseHelper = NotesDatabaseHelper(activity)
                                    notesDatabaseHelper.saveCreds(login, password)
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(activity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            activity.runOnUiThread {
                                Toast.makeText(activity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        errorLog(e, activity)
                        activity.runOnUiThread {
                            Toast.makeText(activity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            } else {
                Toast.makeText(activity, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        authButton.text = "Нет связи"
    }

}
