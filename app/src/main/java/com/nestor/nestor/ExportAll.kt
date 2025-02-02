package com.nestor.nestor

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream


fun exportAll(context: Context, notesDatabaseHelper: NotesDatabaseHelper) {
    val notes = notesDatabaseHelper.getAllNotes(0)
    val massive = StringBuilder("{\"activeNotes\": [")

    for ((index, note) in notes.withIndex()) {
        val noteItem = NoteMin(
            id = note.id,
            theme = note.theme,
            text = note.text,
            label = note.label.toInt()
        )
        val notesDb = notesDatabaseHelper.getNoteById(noteItem.id)
        val label = notesDatabaseHelper.getLabelById(notesDb?.label)

        if (notesDb != null) {
            if (label != null) {
                val formattedText = notesDb.text?.replace("\n", "\\n")
                massive.append("{\"id\": \"${notesDb.id}\",\n" +
                        "      \"theme\": \"${notesDb.theme}\",\n" +
                        "      \"text\": \"${formattedText}\",\n" +
                        "      \"markdown\": \"${label.name}\"}")
            }
        }

        if (index < notes.size - 1) {
            massive.append(",")  // добавляем запятую между элементами, но не после последнего
        }
    }

    massive.append("]}")

    val fileName = "note_${System.currentTimeMillis()}.json"

    // Создаем объект File для папки "Загрузки"
    val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDirectory, fileName)

    try {
        // Запись содержимого в файл
        FileOutputStream(file).use { fileOutputStream ->
            fileOutputStream.write(massive.toString().toByteArray())
        } // Закрываем поток автоматически
        Toast.makeText(context, "Заметки экспортированы в Download/$fileName", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при экспорте заметок", Toast.LENGTH_SHORT).show()
    }
}
