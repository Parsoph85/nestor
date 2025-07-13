package com.nestor.nestor

import android.content.Context
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject



fun syncToServ(context: Context) {

    val notesDatabaseHelper = NotesDatabaseHelper(context)
    val creds = notesDatabaseHelper.getCreds()
    if (!creds.isNullOrEmpty()) {
        val syncData = notesDatabaseHelper.sync().toString()
        val fileName = "nestornote.json"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(syncData.toByteArray(Charsets.UTF_8))
        }
        val token = notesDatabaseHelper.decrypt(creds)

        // Запускаем корутину в IO-диспетчере
        CoroutineScope(Dispatchers.IO).launch {
            try {
                uploadFileToYandexDisk(token, fileName, context)
            } catch (e: Exception) {
                errorLog(e, context)
            }
        }
    }
}


suspend fun syncFromServ(context: Context) = withContext(Dispatchers.IO) {
    val notesDatabaseHelper = NotesDatabaseHelper(context)
    val creds = notesDatabaseHelper.getCreds()
    if (!creds.isNullOrEmpty()) {

        val token = notesDatabaseHelper.decrypt(creds)
        try {
           val fileContent = downloadFileFromYandexDisk(token, "nestornote.json") ?: run {
               return@withContext
           }

           // Сохраняем файл локально
           val localFile = File(context.filesDir, "nestornotes.json")
           localFile.writeText(fileContent)

           // Читаем локальный файл (можно использовать fileContent напрямую, но для примера читаем из файла)
           val jsonString = localFile.readText()
          // Парсим JSON
          val jsonObject = JSONObject(jsonString)

            // Получаем данные
           val sorting = jsonObject.optInt("sorting", -1)
           val notesArray = jsonObject.optJSONArray("notes") ?: JSONArray()
           val labelsArray = jsonObject.optJSONArray("labels") ?: JSONArray()

           // Карта соответствия локальных id меток и id в базе
           val idLabelsSync = mutableMapOf<Int, Long>()

           // Сначала обновляем или добавляем метки
           for (i in 0 until labelsArray.length()) {
               val labelJson = labelsArray.getJSONObject(i)
               notesDatabaseHelper.updateLabelSyn(labelJson, idLabelsSync)
           }

            // Затем обновляем или добавляем заметки, используя idLabelsSync
            for (i in 0 until notesArray.length()) {
               val noteJson = notesArray.getJSONObject(i)
               notesDatabaseHelper.updateNoteSyn(noteJson, idLabelsSync)
           }

           // Обновляем настройку сортировки, если она есть
           if (sorting != -1) {
               notesDatabaseHelper.setSorting(sorting)
           }

        } catch (e: Exception) {
            errorLog(e, context)
       }
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
