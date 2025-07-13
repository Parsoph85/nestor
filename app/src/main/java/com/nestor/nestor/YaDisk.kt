package com.nestor.nestor

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File


// Функция-расширение для OkHttp Call, чтобы использовать с корутинами
suspend fun Call.await(): Response = withContext(Dispatchers.IO) {
    execute()
}
suspend fun uploadFileToYandexDisk(token: String, filename: String, context: Context): Boolean = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val file = File(context.filesDir, filename)

    if (!file.exists()) {
        return@withContext false
    }

    val urlWithParams = HttpUrl.Builder()
        .scheme("https")
        .host("cloud-api.yandex.net")
        .addPathSegments("v1/disk/resources/upload")
        .addQueryParameter("path", filename)
        .addQueryParameter("overwrite", "true")
        .build()

    val requestGetUrl = Request.Builder()
        .url(urlWithParams)
        .addHeader("Authorization", "OAuth $token")
        .build()

    val response = client.newCall(requestGetUrl).await()
    if (!response.isSuccessful) {
        return@withContext false
    }

    val bodyString = response.body?.string() ?: return@withContext false
    val href = JSONObject(bodyString).optString("href")
    if (href.isEmpty()) {
        return@withContext false
    }

    return@withContext uploadFileToHref(client, file, href)
}




suspend fun downloadFileFromYandexDisk(token: String, filename: String): String? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val url = HttpUrl.Builder()
        .scheme("https")
        .host("cloud-api.yandex.net")
        .addPathSegments("v1/disk/resources/download")
        .addQueryParameter("path", filename)
        .build()

    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "OAuth $token")
        .build()

    val response = client.newCall(request).await()
    if (!response.isSuccessful) {
        return@withContext null
    }

    val bodyString = response.body?.string() ?: return@withContext null
    val json = JSONObject(bodyString)
    val href = json.optString("href")
    if (href.isEmpty()) {
        return@withContext null
    }

    val fileRequest = Request.Builder().url(href).build()
    val fileResponse = client.newCall(fileRequest).await()
    if (!fileResponse.isSuccessful) {
        return@withContext null
    }
    return@withContext fileResponse.body?.string()
}



private suspend fun uploadFileToHref(client: OkHttpClient, file: File, href: String): Boolean = withContext(Dispatchers.IO) {
    val mediaType = "application/octet-stream".toMediaTypeOrNull() ?: return@withContext false
    val requestPut = Request.Builder()
        .url(href)
        .put(file.asRequestBody(mediaType))
        .build()

    val response = client.newCall(requestPut).await()
    response.isSuccessful
}
