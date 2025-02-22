package com.nestor.nestor

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

private var address: String = "https://nestornote.ru"
private var connect: Boolean = false
private var login: String = ""
private var password: String = ""
private val idLabelsSync = mutableMapOf<Int, Long>()
private var dostup: Boolean = false
private val client = OkHttpClient()

fun syncFromServ(context: Context){
    dostup = checkServerSync(context)

    if (dostup) {
        val notesDatabaseHelper = NotesDatabaseHelper(context)
        val jsonLoginRequest = "{\"login\": \"$login\", \"password\": \"$password\"}"
        val mediaType = "application/json".toMediaType()
        val requestBody = jsonLoginRequest.toRequestBody(mediaType)
        val loginUrl = "$address/api/v1/login"

        thread {
            val request = Request.Builder()
                .url(loginUrl)
                .post(requestBody)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseMessage = response.body?.string()
                    val jsonResponse = JSONObject(responseMessage)
                    val requestStatus = jsonResponse.optString("request", "False")
                    if (requestStatus == "True") {
                        connect = true
                    }
                } else {
                    val errorMessage = "Error: ${response.code} ${response.message}"
                    errorLog(Exception(errorMessage), context)
                }
            } catch (e: Exception) {
                errorLog(e, context)
            }

            if (connect) {
                val jsonGetAllRequest = "{\"login\": \"$login\", \"password\": \"$password\"}"
                val getAllUrl = "$address/api/v1/get_all"
                val getAllRequestBody = jsonGetAllRequest.toRequestBody(mediaType)

                val getAllRequest = Request.Builder()
                    .url(getAllUrl)
                    .post(getAllRequestBody)
                    .build()

                try {
                    val getAllResponse: Response = client.newCall(getAllRequest).execute()
                    if (getAllResponse.isSuccessful) {
                        val responseMessage = getAllResponse.body?.string()
                        val jsonResponse = JSONObject(responseMessage)
                        val sorting = jsonResponse.getInt("sorting")
                        val notes = jsonResponse.getJSONArray("notes")
                        val labels = jsonResponse.getJSONArray("labels")
                        notesDatabaseHelper.setSorting(sorting)


                        for (i in 0 until labels.length()) {
                            val label = labels.getJSONObject(i)
                            notesDatabaseHelper.updateLabelSyn(label, idLabelsSync)
                        }
                        for (i in 0 until notes.length()) {
                            val note = notes.getJSONObject(i)
                            notesDatabaseHelper.updateNoteSyn(note, idLabelsSync)
                        }
                    } else {
                        val errorMessage = "Error getting notes: ${getAllResponse.code} ${getAllResponse.message}"
                        errorLog(Exception(errorMessage), context)
                    }
                } catch (e: Exception) {
                    errorLog(e, context)
                }
            }
        }
    } else {
        errorLog(Exception("Insufficient data for login or server unavailable."), context)
    }
}

@SuppressLint("SuspiciousIndentation")
fun syncToServ(context: Context) {
    dostup = checkServerSync(context)

        if (dostup) {
            val notesDatabaseHelper = NotesDatabaseHelper(context)
            val syncData = notesDatabaseHelper.sync()
            val jsonLoginRequest = "{\"login\": \"$login\", \"password\": \"$password\", $syncData}"
            val url = "$address/api/v1/sync_all"
            val mediaType = "application/json".toMediaType()
            val requestBody = jsonLoginRequest.toRequestBody(mediaType)

            thread {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                try {
                    client.newCall(request).execute()
                } catch (e: Exception) {
                    errorLog(e, context)
                }
            }
        } else {
            errorLog(Exception("Insufficient data for login or server unavailable."), context)
        }
}


fun errorLog(error: Exception, context: Context) {
    val currentTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val logMessage = "$currentTime - ${error.message}\n"
    try {
        val file = File(context.filesDir, "error_log.txt")
        file.appendText(logMessage)
    }catch (e: IOException) {
        //
    }
}

fun checkServerSync(context: Context): Boolean {
    val notesDatabaseHelper = NotesDatabaseHelper(context)
    val creds = notesDatabaseHelper.getCreds()
    if (creds?.first != null && creds.second != null) {
        login = creds.first.toString()
        password = creds.second.toString()
        var isServerAvailable = false
        val latch = CountDownLatch(1)

        checkServer { available ->
            isServerAvailable = available
            latch.countDown()
        }

        latch.await()
        return isServerAvailable
    }else {
        return false
    }
}

fun checkServer(callback: (Boolean) -> Unit) {
    Thread {

        val request = Request.Builder()
            .url(address)
            .build()

        try {
            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                callback(false)
            } else {
                callback(true)
            }
        } catch (e: Exception) {
            callback(false)
        }
    }.start()
}
