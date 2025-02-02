package com.example.nestor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Labels(
    val id: Int,
    val name: String,
    val color1: String,
    val color2: String
)

data class Notes(
    val id: Int,
    val theme: String,
    val label: String,
    val text: String
)

data class Note(
    val id: Int,
    val theme: String,
    val text: String?,
    val label: String?,
    val tags: String?,
    val chData: String?,
    val isDeleted: Boolean,
    val uid: String
)

data class NoteMin(
    val id: Int,
    val theme: String,
    val text: String,
    val label: Int?
)

data class Label(
    val id: Int,
    val name: String,
    val color1: String,
    val color2: String
)

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NOTES = "notes"
        const val TABLE_SETTING = "setting"
        const val TABLE_LABELS = "labels"

        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_THEME = "theme"
        const val COLUMN_NOTE_TEXT = "text"
        const val COLUMN_NOTE_LABEL = "label"
        const val COLUMN_NOTE_TAGS = "tags"
        const val COLUMN_NOTE_CH_DATA = "ch_data"
        const val COLUMN_NOTE_DELETED = "deleted"
        const val COLUMN_NOTE_UID = "uid"

        const val COLUMN_SETTING_ID = "id"
        const val COLUMN_SETTING_SORTING = "sorting"
        const val COLUMN_SETTING_UNAME = "uname"
        const val COLUMN_SETTING_PWWD = "pwwd"
        const val COLUMN_SETTING_CH_DATA = "ch_data"

        const val COLUMN_LABELS_ID = "id"
        const val COLUMN_LABELS_NAME = "name"
        const val COLUMN_LABEL_COLOR1 = "color1"
        const val COLUMN_LABEL_COLOR2 = "color2"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создание таблиц при первой установке приложения
        db.execSQL("CREATE TABLE $TABLE_NOTES ($COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NOTE_THEME TEXT, $COLUMN_NOTE_TEXT TEXT, $COLUMN_NOTE_LABEL TEXT, $COLUMN_NOTE_TAGS TEXT, $COLUMN_NOTE_CH_DATA TIMESTAMP DEFAULT CURRENT_TIMESTAMP, $COLUMN_NOTE_DELETED BOOLEAN, $COLUMN_NOTE_UID TEXT)")
        db.execSQL("CREATE TABLE $TABLE_SETTING ($COLUMN_SETTING_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_SETTING_SORTING INT, $COLUMN_SETTING_UNAME TEXT, $COLUMN_SETTING_PWWD TEXT, $COLUMN_SETTING_CH_DATA TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        db.execSQL("CREATE TABLE $TABLE_LABELS ($COLUMN_LABELS_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LABELS_NAME TEXT, $COLUMN_LABEL_COLOR1 TEXT, $COLUMN_LABEL_COLOR2 TEXT)")

        // Автоматическое добавление значений в таблицу setting
        db.execSQL("INSERT INTO $TABLE_SETTING ($COLUMN_SETTING_SORTING) VALUES (4)")
        // Автоматическое добавление значений в таблицу labels
        db.execSQL("INSERT INTO $TABLE_LABELS ($COLUMN_LABELS_NAME, $COLUMN_LABEL_COLOR1, $COLUMN_LABEL_COLOR2) VALUES ('Без темы...', '#FFFFFF', '#ced9f2')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTING")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LABELS")
        onCreate(db)
    }



    // Функции CRUD для таблицы notes

    // Добавить
    fun addNote(): Long {
        val id: String = generateRandomDigits()
        val uid: String = generateRandomAlphanumeric()
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_THEME, "Новая заметка")
            put(COLUMN_NOTE_TEXT, "Введите текст")
            put(COLUMN_NOTE_LABEL, "1")
            put(COLUMN_NOTE_TAGS, "")
            put(COLUMN_NOTE_DELETED, false)
        }

        do {
            val cursor = db.query(TABLE_NOTES, arrayOf(COLUMN_NOTE_ID), "$COLUMN_NOTE_ID = ?", arrayOf(id), null, null, null)
            val exists = cursor.count > 0
            cursor.close()
        } while (exists)
        values.put(COLUMN_NOTE_ID, id)

        do {
            val cursor = db.query(TABLE_NOTES, arrayOf(COLUMN_NOTE_UID), "$COLUMN_NOTE_UID = ?", arrayOf(uid), null, null, null)
            val exists = cursor.count > 0
            cursor.close()
        } while (exists)
        values.put(COLUMN_NOTE_UID, uid)


        // Вставляем новую запись и получаем ее ID
        val newRowId = db.insert(TABLE_NOTES, null, values)

        // Закрываем базу данных
        db.close()

        // Возвращаем ID новой записи
        return newRowId
    }

    // Получить по ИД
    fun getNoteById(id: Int): Note? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NOTES,
            null,
            "$COLUMN_NOTE_ID = ? AND $COLUMN_NOTE_DELETED = ?",
            arrayOf(id.toString(), "0"),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_NOTE_ID)
                val themeIndex = it.getColumnIndex(COLUMN_NOTE_THEME)
                val textIndex = it.getColumnIndex(COLUMN_NOTE_TEXT)
                val labelIndex = it.getColumnIndex(COLUMN_NOTE_LABEL)
                val tagsIndex = it.getColumnIndex(COLUMN_NOTE_TAGS)
                val chDataIndex = it.getColumnIndex(COLUMN_NOTE_CH_DATA)
                val deletedIndex = it.getColumnIndex(COLUMN_NOTE_DELETED)
                val uid = it.getColumnIndex(COLUMN_NOTE_UID)

                return Note(
                    id = if (idIndex != -1) it.getInt(idIndex) else -1,
                    theme = if (themeIndex != -1) it.getString(themeIndex) ?: "" else "",
                    text = if (textIndex != -1) it.getString(textIndex) else null,
                    label = if (labelIndex != -1) it.getString(labelIndex) else null,
                    tags = if (tagsIndex != -1) it.getString(tagsIndex) else null,
                    chData = if (chDataIndex != -1) it.getString(chDataIndex) else null,
                    isDeleted = if (deletedIndex != -1) it.getInt(deletedIndex) > 0 else false,
                    uid = if (uid != -1) it.getString(uid) else ""
                )
            }
        }
        return null
    }


    // Получаем массив всех записей
    fun getAllNotes(sort: Int): List<Notes> {
        val notes = mutableListOf<Notes>()
        val db = readableDatabase
        val orderBy: String = when (sort) {
            0 -> "$COLUMN_NOTE_ID ASC"
            1 -> "$COLUMN_NOTE_ID DESC"
            2 -> "$COLUMN_NOTE_THEME ASC"
            3 -> "$COLUMN_NOTE_THEME DESC"
            4 -> "$COLUMN_NOTE_CH_DATA ASC"
            5 -> "$COLUMN_NOTE_CH_DATA DESC"
            else -> "$COLUMN_NOTE_ID ASC"
        }

        val cursor: Cursor = db.query(
            TABLE_NOTES,
            null,
            "$COLUMN_NOTE_DELETED = ?",
            arrayOf("0"),
            null,
            null,
            orderBy
        )

        try {
            if (cursor.moveToFirst()) {
                val themeColumnIndex = cursor.getColumnIndex(COLUMN_NOTE_THEME)
                val idColumnIndex = cursor.getColumnIndex(COLUMN_NOTE_ID)
                val labelColumnIndex = cursor.getColumnIndex(COLUMN_NOTE_LABEL)
                val textColumnIndex = cursor.getColumnIndex(COLUMN_NOTE_TEXT)

                if (themeColumnIndex != -1 && idColumnIndex != -1 && labelColumnIndex != -1 && textColumnIndex != -1) {
                    do {
                        val theme = cursor.getString(themeColumnIndex) // Получаем строку с темой
                        val idNote = cursor.getInt(idColumnIndex) // Получаем ID заметки как целое число
                        val label = cursor.getString(labelColumnIndex) // Получаем цвет метки
                        val previewText = cursor.getString(textColumnIndex)
                        val characterCount = previewText.length
                        val text = if (characterCount > 30) {
                            previewText.take(30) + "..." // Получаем первые 30 символов и добавляем "..."
                        } else {
                            previewText // Если текст меньше или равен 30 символам, используем его полностью
                        }

                        // Создаем объект Note и добавляем его в список
                        notes.add(Notes(id = idNote, theme = theme, label = label, text = text))
                    } while (cursor.moveToNext()) // Переходим к следующей строке
                }
            }
        } finally {
            cursor.close() // Закрываем курсор в любом случае
            db.close()      // Закрываем базу данных
        }

        return notes // Возвращаем список заметок
    }

    // Обновляем
    fun updateNote(id: Long, theme: String, text: String, label: String, tags: String, deleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_THEME, theme)
            put(COLUMN_NOTE_TEXT, text)
            put(COLUMN_NOTE_LABEL, label)
            put(COLUMN_NOTE_TAGS, tags)
            put(COLUMN_NOTE_DELETED, deleted)
        }
        db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    // Обновляем метку
    fun updateNoteLabel(id: Int, label: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_LABEL, label)
        }
        db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    // Помечаем как удаленную
    fun deleteNote(id: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_DELETED, 1)
        }
        db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
    }











    // Функции CRUD для таблицы labels

    // Добавляем новую метку
    fun addLabel(name: String, color1: String, color2: String): Long {
        return writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COLUMN_LABELS_NAME, name)
                put(COLUMN_LABEL_COLOR1, color1)
                put(COLUMN_LABEL_COLOR2, color2)
            }
            db.insert(TABLE_LABELS, null, values)
        }
    }

    // Получаем массив меток
    fun getAllLabels(): List<Labels> {
        val labels = mutableListOf<Labels>() // Список для хранения объектов Labels
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_LABELS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                // Получаем индексы всех необходимых колонок
                val idColumnIndex = cursor.getColumnIndex("id") // Укажите правильное имя колонки для ID
                val nameColumnIndex = cursor.getColumnIndex(COLUMN_LABELS_NAME)
                val color1ColumnIndex = cursor.getColumnIndex("color1") // Укажите правильное имя колонки для color1
                val color2ColumnIndex = cursor.getColumnIndex("color2") // Укажите правильное имя колонки для color2

                if (idColumnIndex != -1 && nameColumnIndex != -1 && color1ColumnIndex != -1 && color2ColumnIndex != -1) {
                    // Создаем объект Labels из значений из курсора и добавляем его в список
                    val id = cursor.getInt(idColumnIndex)
                    val name = cursor.getString(nameColumnIndex)
                    val color1 = cursor.getString(color1ColumnIndex)
                    val color2 = cursor.getString(color2ColumnIndex)

                    labels.add(Labels(id, name, color1, color2))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return labels
    }


    // Обновляем метку
    fun updateLabel(id: Int, name: String, color1: String, color2: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABELS_NAME, name)
            put(COLUMN_LABEL_COLOR1, color1)
            put(COLUMN_LABEL_COLOR2, color2)
        }

        return try {
            val rowsAffected = db.update(TABLE_LABELS, values, "$COLUMN_LABELS_ID = ?", arrayOf(id.toString()))
            rowsAffected > 0 // Возвращаем true, если строки обновлены
        } catch (e: Exception) {
            // Обрабатывать исключение (например, логировать его)
            e.printStackTrace()
            false // Возвращаем false в случае ошибки
        } finally {
            db.close() // Закрываем базу данных
        }
    }



    // Получаем метку по ИД
    fun getLabelById(id: String?): Label? {
        val db = readableDatabase
        var cursor: Cursor? = null // Инициализировать курсор с null

        return try {
            // Сначала пробуем получить метку по переданному id
            cursor = db.query(
                TABLE_LABELS,
                null,
                "$COLUMN_LABELS_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                // Найдена метка с переданным id
                val idColumnIndex = cursor.getColumnIndex(COLUMN_LABELS_ID)
                val nameColumnIndex = cursor.getColumnIndex(COLUMN_LABELS_NAME)
                val color1ColumnIndex = cursor.getColumnIndex(COLUMN_LABEL_COLOR1)
                val color2ColumnIndex = cursor.getColumnIndex(COLUMN_LABEL_COLOR2)

                // Проверяем наличие всех необходимых столбцов
                if (idColumnIndex != -1 && nameColumnIndex != -1 &&
                    color1ColumnIndex != -1 && color2ColumnIndex != -1) {

                    // Получаем данные из курсора
                    val labelId = cursor.getInt(idColumnIndex)
                    val name = cursor.getString(nameColumnIndex)
                    val color1 = cursor.getString(color1ColumnIndex)
                    val color2 = cursor.getString(color2ColumnIndex)

                    Label(id = labelId, name = name, color1 = color1, color2 = color2)
                } else {
                    null // Если столбцы не найдены, возвращаем null
                }
            } else {
                // Если метка с переданным id не найдена, получаем метку с id = 1
                cursor.close() // Закрываем предыдущий курсор перед новым запросом
                cursor = db.query(
                    TABLE_LABELS,
                    null,
                    "$COLUMN_LABELS_ID = ?",
                    arrayOf("1"), // Запрос по id = 1
                    null,
                    null,
                    null
                )

                if (cursor.moveToFirst()) {
                    // Найдена метка с id = 1
                    val idColumnIndex = cursor.getColumnIndex(COLUMN_LABELS_ID)
                    val nameColumnIndex = cursor.getColumnIndex(COLUMN_LABELS_NAME)
                    val color1ColumnIndex = cursor.getColumnIndex(COLUMN_LABEL_COLOR1)
                    val color2ColumnIndex = cursor.getColumnIndex(COLUMN_LABEL_COLOR2)

                    if (idColumnIndex != -1 && nameColumnIndex != -1 &&
                        color1ColumnIndex != -1 && color2ColumnIndex != -1) {

                        // Получаем данные из курсора
                        val labelId = cursor.getInt(idColumnIndex)
                        val name = cursor.getString(nameColumnIndex)
                        val color1 = cursor.getString(color1ColumnIndex)
                        val color2 = cursor.getString(color2ColumnIndex)

                        Label(id = labelId, name = name, color1 = color1, color2 = color2)
                    } else {
                        null // Если столбцы не найдены, возвращаем null
                    }
                } else {
                    null // Если метка с id = 1 тоже не найдена
                }
            }
        } finally {
            cursor?.close() // Закрываем курсор, если он инициализирован
            db.close()      // Закрываем базу данных
        }
    }

    fun deleteLabel(oldLabel: String, newLabel: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_LABEL, newLabel)
        }
        db.update(TABLE_NOTES, values, "$COLUMN_NOTE_LABEL = ?", arrayOf(oldLabel))
        db.delete(TABLE_LABELS, "$COLUMN_LABELS_ID = ?", arrayOf(oldLabel))
        db.close()
    }







    fun getSorting(): Int? {
        val db = readableDatabase
        var sortingId: Int? = null

        val cursor = db.query(
            TABLE_SETTING,
            arrayOf(COLUMN_SETTING_SORTING),
            "$COLUMN_LABELS_ID = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        return try {
            if (cursor.moveToFirst()) {
                sortingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_SORTING))
            }
            sortingId // Возвращаем найденный id сортировки или null, если не найдено
        } catch (e: Exception) {
            e.printStackTrace() // Логируем ошибку для отладки
            null // Возвращаем null в случае ошибки
        } finally {
            cursor.close() // Закрываем курсор
            db.close() // Закрываем базу данных
        }
    }




    // Функции CRUD для таблицы setting
    fun setSorting(id: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SETTING_SORTING, id)
        }

        return try {
            val rowsAffected = db.update(TABLE_SETTING, values, "$COLUMN_LABELS_ID = ?", arrayOf("1"))
            rowsAffected > 0 // Возвращаем true, если строки обновлены
        } catch (e: Exception) {
            e.printStackTrace() // Логируем ошибку для отладки
            false // Возвращаем false в случае ошибки
        } finally {
            db.close() // Закрываем базу данных
        }
    }

   /* fun addColumnToUsersTable() {
        // Формирование SQL-запроса для добавления столбца
        val db = writableDatabase
        val sql = "ALTER TABLE notes ADD COLUMN uid TEXT"
        db.execSQL(sql)

    }*/

    private fun generateRandomDigits(): String {
        val digits = ('0'..'9').toList()
        return (1..10)
            .map { digits.random() }
            .joinToString("")
    }

    private fun generateRandomAlphanumeric(): String {
        val alphanumericChars = ('0'..'9') + ('A'..'Z') + ('a'..'z')
        return (1..10)
            .map { alphanumericChars.random() }
            .joinToString("")
    }





}