package com.example.nestor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Environment
import android.text.InputFilter
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates


class NoteEdit : AppCompatActivity() {
    private lateinit var labelLayouts: LinearLayout
    private lateinit var headLayout: LinearLayout
    private lateinit var backButton: Button
    private lateinit var editTheme: EditText
    private lateinit var imageLabel: ImageView
    private lateinit var editText: EditText
    private lateinit var bottomLayout: LinearLayout
    private lateinit var checkButton: Button
    private lateinit var deleteButton: Button
    private lateinit var shareButton: Button
    private lateinit var labelView: TextView
    private lateinit var exportButton: Button
    private lateinit var notesDatabaseHelper: NotesDatabaseHelper
    private var noteIdDb: Int = 1
    private var noteLabelDb: Int by Delegates.observable(1) { _, _, newValue -> changeLabel(newValue)}
    private var width = 0
    private var height = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_edit)



        // Инициализация объектов

        notesDatabaseHelper = NotesDatabaseHelper(this)
        headLayout = findViewById(R.id.headLayout)
        labelLayouts = findViewById(R.id.labelLayouts)
        editText = findViewById(R.id.editText)
        bottomLayout = findViewById(R.id.bottomLayout)
        editTheme = findViewById(R.id.editTheme)
        backButton = findViewById(R.id.backButton)
        checkButton = findViewById(R.id.checkButton)
        deleteButton = findViewById(R.id.deleteButton)
        shareButton = findViewById(R.id.shareButton)
        imageLabel = findViewById(R.id.imageLabel)
        labelView = findViewById(R.id.labelView)
        exportButton = findViewById(R.id.exportButton)

        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveNoteAndExit()
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)


        // Получение ID заметки

        height = intent.getStringExtra("EXTRA_HEIGHT")?.toInt() ?: 1600
        width = intent.getStringExtra("EXTRA_WIDTH")?.toInt() ?: 700
        noteIdDb = intent.getStringExtra("EXTRA_THEME")?.toInt() ?: 1


        // Получение данных из БД: заметка ...

        val note = notesDatabaseHelper.getNoteById(noteIdDb)
        val noteThemeDb: String = note?.theme ?: "Без темы..."
        val noteTextDb: String = note?.text.toString()
        noteLabelDb = note?.label?.toInt() ?: 1

        // ... и метка.

        val labelDataDb = notesDatabaseHelper.getLabelById(noteLabelDb.toString())
        val labelNameDb: String = labelDataDb?.name ?: "Без метки..."
        val labelColor1Db: String = labelDataDb?.color1 ?: "#ffffff"



        // Кнопка возврата

        val backButtonParams = backButton.layoutParams as LinearLayout.LayoutParams
        backButtonParams.width = (height / 20)
        backButtonParams.height = (height / 20)
        backButton.layoutParams = backButtonParams
        backButtonParams.setMargins(width / 20, 0, width / 100, 0)

        backButton.setOnClickListener {
            saveNoteAndExit()
        }


        // Поле редактирования темы

        val editThemeParams = editTheme.layoutParams as LinearLayout.LayoutParams
        editThemeParams.width = width - height / 19
        editThemeParams.height = height / 15
        editTheme.layoutParams = editThemeParams
        editTheme.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)
        editTheme.setText(noteThemeDb)
        editTheme.textSize = (height / 80).toFloat() // resources.configuration.fontScale
        editTheme.filters = arrayOf(InputFilter { source, _, _, dest, dstart, dend ->
            val currentText = dest.toString().trim()
            val newLength = currentText.length + source.length - (dend - dstart)

            if (newLength > 20) {
                // Обрезаем текст до 20 символов
                val excess = (currentText + source.toString()).take(20)
                excess.substring(0, 20) // Возвращаем обрезанный текст
            } else {
                null // Позволяем вводить текст
            }
        })


        // вставить значок ярлычков

        val imageLabelParams = imageLabel.layoutParams as LinearLayout.LayoutParams
        imageLabelParams.width = (height / 40)
        imageLabelParams.height = (height / 40)
        imageLabelParams.setMargins(width / 200, 0, width / 200, height / 150)


        // Метка для названия ярлыка

        val labelViewParams = labelView.layoutParams as LinearLayout.LayoutParams
        labelViewParams.width = (width / 2)
        labelViewParams.height = (height / 20)
        labelView.layoutParams = labelViewParams
        labelView.text = labelNameDb
        labelView.textSize = (height / 90).toFloat() // resources.configuration.fontScale

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor(labelColor1Db))
            cornerRadius = (width * 0.05f).coerceAtLeast(0f)}
        labelView.background = drawable
        labelView.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)

        labelView.setOnClickListener {
            showPopupMenu()
        }


        // Поле редактирования текста

        editText.setText(noteTextDb)
        updateClickableSpan()
        editText.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)
        editText.textSize = (height / 80).toFloat() // resources.configuration.fontScale
        editText.movementMethod = LinkMovementMethod.getInstance()



        // Кнопка добавления чекбаттона в текст

        val checkButtonParams = checkButton.layoutParams as LinearLayout.LayoutParams
        checkButtonParams.width = (height / 20)
        checkButtonParams.height = (height / 20)
        checkButton.layoutParams = checkButtonParams
        checkButtonParams.setMargins(width / 20, height / 150, width / 20, height / 150)

        checkButton.setOnClickListener {
            addCheckbox()
        }


        // Кнопка удаления заметки

        val deleteButtonParams = deleteButton.layoutParams as LinearLayout.LayoutParams
        deleteButtonParams.width = (height / 20)
        deleteButtonParams.height = (height / 20)
        deleteButton.layoutParams = deleteButtonParams
        deleteButtonParams.setMargins(width / 10, height / 150, width / 10, height / 150)

        deleteButton.setOnClickListener {
            notesDatabaseHelper.deleteNote(noteIdDb)
            finish()
        }


        // Кнопка поделиться заметкой

        val shareButtonParams = shareButton.layoutParams as LinearLayout.LayoutParams
        shareButtonParams.width = (height / 20)
        shareButtonParams.height = (height / 20)
        shareButton.layoutParams = shareButtonParams
        deleteButtonParams.setMargins(width / 10, height / 150, width / 10, height / 150)

        shareButton.setOnClickListener {
            val enteredTheme = editTheme.text.toString()
            var enteredText = editText.text.toString()
            var modifiedTheme = enteredTheme
            if ((enteredTheme == "Новая заметка" && enteredText != "Введите текст") || enteredTheme == "") {
                if (enteredText == ""){enteredText = "Пусто. А жаль."}
                modifiedTheme = enteredText.take(26) + "..."
            }
            val noteTextSend = "Тема: $modifiedTheme\n Текст: $enteredText"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, noteTextSend)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться заметкой с помощью:"))
        }


        // Кнопка сохранения на диск

        val exportButtonParams = exportButton.layoutParams as LinearLayout.LayoutParams
        exportButtonParams.width = (height / 20)
        exportButtonParams.height = (height / 20)
        exportButton.layoutParams = exportButtonParams
        exportButtonParams.setMargins(width / 10, height / 150, width / 20, height / 150)

        exportButton.setOnClickListener {
            val enteredTheme = editTheme.text.toString()
            val enteredText = editText.text.toString()

            val fileName = "note_${System.currentTimeMillis()}.txt" // Уникальное имя файла
            val fileContents = "Тема: $enteredTheme\nТекст: $enteredText"

            // Создаем объект File для папки "Загрузки"
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName)

            try {
                // Запись содержимого в файл
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(fileContents.toByteArray())
                fileOutputStream.close() // Закрываем поток
                Toast.makeText(this, "Заметка сохранена в Download/$fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка при сохранении заметки", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun addCheckbox() {
        val cursorPosition = editText.selectionStart
        val checkboxSpan = createCheckboxSpan()
        val spannableString = SpannableString("☐" + " ") // Символ для чекбокса
        spannableString.setSpan(checkboxSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Вставляем чекбокс в EditText
        val editableText = editText.text
        editableText.insert(cursorPosition, spannableString)

        // Устанавливаем курсор после добавленного чекбокса
        editText.setSelection(cursorPosition + spannableString.length)
    }

    private fun createCheckboxSpan(): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(widget: View) {
                val currentText = editText.text.toString()
                val cursorPosition = editText.selectionStart - 2
                val checkboxPosition = currentText.indexOf("☐", cursorPosition).takeIf { it != -1 }
                    ?: currentText.indexOf("☑", cursorPosition).takeIf { it != -1 }

                // Если чекбокс найден
                if (checkboxPosition != null) {
                    // Определяем текущий символ чекбокса
                    val currentChar = currentText[checkboxPosition]
                    // Заменяем его на противоположный
                    val newChar = if (currentChar == '☐') '☑' else '☐'
                    // Обновляем текст в EditText
                    val newText = currentText.replaceRange(checkboxPosition, checkboxPosition + 1, newChar.toString())
                    editText.setText(newText)
                    editText.setSelection(checkboxPosition) // Устанавливаем курсор на место чекбокса

                    updateClickableSpan()
                }
            }
        }
    }

    // Метод для обновления ClickableSpan
    private fun updateClickableSpan() {
        val spannable = SpannableStringBuilder(editText.text) // Используем SpannableStringBuilder
        // Удаляем все предыдущие ClickableSpan
        val spans = spannable.getSpans(0, spannable.length, ClickableSpan::class.java)
        for (span in spans) {
            spannable.removeSpan(span)
        }

        // Устанавливаем ClickableSpan на нужные позиции
        val checkboxPositions = listOf("☐", "☑").flatMap { checkbox ->
            val positions = mutableListOf<Int>()
            var index = editText.text.indexOf(checkbox)
            while (index != -1) {
                positions.add(index)
                index = editText.text.indexOf(checkbox, index + 1)
            }
            positions
        }

        for (position in checkboxPositions) {
            spannable.setSpan(createCheckboxSpan(), position, position + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        editText.text = spannable // Присваиваем изменяемый текст обратно в EditText
    }


    private fun changeLabel(newLabelId: Int) {
        val newLabelData = notesDatabaseHelper.getLabelById(newLabelId.toString())
        val newLabelName: String = newLabelData?.name ?: "Без метки..."
        val newLabelColor1: String = newLabelData?.color1 ?: "#ffffff"
        labelView.setBackgroundColor(Color.parseColor(newLabelColor1))
        labelView.text = newLabelName
    }



    // Сохранение заметки и выход
    private fun saveNoteAndExit() {
        val enteredTheme = editTheme.text.toString()
        var enteredText = editText.text.toString()

        var modifiedTheme = enteredTheme
        if ((enteredTheme == "Новая заметка" && enteredText != "Введите текст") || enteredTheme == "") {
            if (enteredText == "") {
                enteredText = "Пусто. А жаль."
            }
            modifiedTheme = enteredText.take(26) + "..."
        }

        notesDatabaseHelper.updateNote(noteIdDb.toLong(), modifiedTheme, enteredText, noteLabelDb.toString(), "", false)

        // Создаем Intent для передачи результата
        val resultIntent = Intent().apply {
            putExtra("RESULT_KEY", noteIdDb) // Передаем необходимые данные
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }



    // Всплывающее меню выбора метки

    private fun showPopupMenu() {
        val dialogView = layoutInflater.inflate(R.layout.menu_popup, null)
        val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val labels = notesDatabaseHelper.getAllLabels()

        val dialog = builder.create()

        for (label in labels) {
            val noteItem = Label(
                id = label.id,
                name = label.name,
                color1 = label.color1,
                color2 = label.color2
            )
            val labelId = noteItem.id
            val labelName = noteItem.name
            val labelColor1 = noteItem.color1
            val labelColor2 = noteItem.color2

            // Создаем новый LinearLayout для элемента меню
            val menuItemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(10, 10, 10, 10)
            }


            // Полосочка в пункте меню
            val icon = TextView(this)
            icon.height = (height / 20)
            icon.textSize = 18f
            icon.setPadding(10, height / 200, 10, height / 200)
            val drawableIcon = GradientDrawable().apply {
                setColor(Color.parseColor(labelColor2))
                cornerRadius = 8f
            }
            icon.background = drawableIcon


            // Создаем TextView для названия метки
            val newMenuItem = TextView(this)
            newMenuItem.text = labelName
            newMenuItem.width = (width)
            newMenuItem.height = (height / 20)
            newMenuItem.textSize = 18f
            newMenuItem.setPadding(10, height / 200, 10, height / 200)
            val drawable = GradientDrawable().apply {
                setColor(Color.parseColor(labelColor1))
                cornerRadius = 8f
            }
            newMenuItem.background = drawable


            // Добавляем ImageView и TextView в LinearLayout
            menuItemLayout.addView(icon)
            menuItemLayout.addView(newMenuItem)

            menuItemLayout.setOnClickListener {
                notesDatabaseHelper.updateNoteLabel(noteIdDb, labelId.toString())
                noteLabelDb = labelId
                dialog.dismiss()
            }

            menuLayout.addView(menuItemLayout)

        }

        val addLabel = TextView(this)
        addLabel.text = "Добавить метку"
        addLabel.textSize = 18f
        addLabel.setPadding(10, 10, 10, 10)
        addLabel.setOnClickListener {
            showAddLabel()
            dialog.dismiss()
        }

        menuLayout.addView(addLabel)

        // Показать диалог
        dialog.show()

    }



    private fun showAddLabel() {

        // Загружаем разметку из XML
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)

        // Получаем ссылки на элементы из разметки
        val colorsLayout = dialogView.findViewById<LinearLayout>(R.id.colorsLayout)
        val editText = dialogView.findViewById<EditText>(R.id.labelName)
        val buttonsLayout = dialogView.findViewById<LinearLayout>(R.id.buttonsLayout)

        val colorList = listOf("", "#FFFFFF", "#E6E6A1", "#A1C6E7", "#F2B2B2", "#D1E7D1", "#F9E6A1", "#D1C6E7", "#E6B3E0", "#99CCCC")
        val frontColorList = listOf("", "#ced9f2", "#B3B300", "#007BFF", "#FF4C4C", "#28A745", "#FF8C00", "#6F42C1", "#A500B5", "#339999")

        // Добавляем RadioButton в RadioGroup программно
        var oldLabel = 1 // Инициализируем переменную для хранения предыдущего выбранного labelId
        val editing = false

        val colorViewsMap = mutableMapOf<Int, TextView>() // Создаем Map для хранения ссылок на TextView
        var oldColor: String = colorList[oldLabel] // Инициализируем переменную для хранения предыдущего цвета
        var oldColorView: TextView? = null // Объявляем переменную для хранения ссылки на предыдущий выбранный TextView
        val width = width

        var endColor2 = ""

        for (index in 1..9) {

            val currentColor = Color.parseColor(colorList[index])
            val frontCurrentColor = Color.parseColor(frontColorList[index])

            val currentColorLabel = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    width * 8 / 100, // Ширина в пикселях
                    width * 8 / 100  // Высота в пикселях
                ).apply {
                    setMargins((width * 1 / 200), 0, (width * 1 / 200), 0) // Отступы между кнопками
                }

                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(currentColor) // Устанавливаем цвет кнопки
                }
                background = drawable // Применяем фоновый drawable к кнопке

                if (index == oldLabel){
                    setBackgroundColor(frontCurrentColor)
                    oldColor = colorList[index]
                    oldColorView = this
                    endColor2 = frontColorList[index]
                }

                // Устанавливаем обработчик нажатия
                setOnClickListener {
                    if (oldLabel != index) {
                        // Сбрасываем стиль предыдущего выбранного TextView
                        oldColorView?.setBackgroundColor(Color.parseColor(oldColor))

                        oldColor = colorList[index]  // Обновляем старый labelId и цвет
                        oldLabel = index
                        oldColorView = this // Обновляем ссылку на текущий выбранный TextView
                        endColor2 = frontColorList[index]

                        setBackgroundColor(frontCurrentColor)


                    }

                }
            }

            colorViewsMap[index] = currentColorLabel // Сохраняем ссылку на текущий TextView в Map
            colorsLayout.addView(currentColorLabel) // Добавляем TextView в layout
        }


        // Создаем диалог
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create() // Создаем диалог

        // Функция для создания кнопки
        fun createButton(text: String, onClick: () -> Unit): Button {
            return Button(this).apply {
                this.text = text
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(20, 20, 20, 20) // Устанавливаем отступы между кнопками
                }
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(Color.parseColor("#E5E4E2")) // Устанавливаем цвет кнопки (пример)
                    cornerRadius = 40f // Устанавливаем радиус закругления
                }
                background = drawable // Применяем фоновый drawable к кнопке
                setOnClickListener { onClick() }
            }
        }

        // Создаем кнопки
        val saveButton = createButton("Сохранить") {
            // Логика сохранения
            val editTextValue = editText.text.toString().trim() // Удаляем пробелы

            // Проверка длины текста
            if (editTextValue.length in 1..30) {
                if(editing) {
                    notesDatabaseHelper.updateLabel(oldLabel, editTextValue, oldColor, endColor2)
                }else{
                    val newId = notesDatabaseHelper.addLabel(editTextValue, oldColor, endColor2)
                    notesDatabaseHelper.updateNoteLabel(noteIdDb, newId.toString())
                    noteLabelDb = newId.toInt()
                    }

                dialog.dismiss() // Закрываем диалог
            } else {
                Toast.makeText(this, "Введите от 1 до 30 символов", Toast.LENGTH_SHORT).show()
            }
        }

        val closeButton = createButton("Закрыть") {
            dialog.dismiss() // Закрываем диалог
        }

        // Добавляем кнопки в buttonsLayout
        buttonsLayout.addView(saveButton)
        buttonsLayout.addView(closeButton)

        dialog.show() // Показываем диалог
    }


}

