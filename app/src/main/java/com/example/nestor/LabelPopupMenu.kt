package com.example.nestor

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.content.Context

private lateinit var notesDatabaseHelper: NotesDatabaseHelper
private var noteIdDb: Int = 1
private var noteLabelDb: Int = 1
private var width = 0

fun showPopupMenu(noteIdDbF: Int, noteLabelDbF: Int, widthF: Int, dialogView: View, context: Context) {
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)
    notesDatabaseHelper = NotesDatabaseHelper(context)

    noteIdDb = noteIdDbF
    noteLabelDb = noteLabelDbF
    width = widthF



    val builder = AlertDialog.Builder(context)
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

        val newMenuItem = TextView(context)
        newMenuItem.text = labelName
        newMenuItem.textSize = 18f
        newMenuItem.setPadding(10, 10, 10, 10)
        val drawable = GradientDrawable().apply {
            setColor(Color.parseColor(labelColor1))
            cornerRadius = 8f
        }
        newMenuItem.background = drawable

        newMenuItem.setOnClickListener {
            notesDatabaseHelper.updateNoteLabel(noteIdDb, labelId.toString())
            noteLabelDb = labelId

            dialog.dismiss()
        }

        menuLayout.addView(newMenuItem)
    }

    val addLabel = TextView(context)
    addLabel.text = "Добавить метку"
    addLabel.textSize = 18f
    addLabel.setPadding(10, 10, 10, 10)
    addLabel.setOnClickListener {
        showAddLabel(context)
        dialog.dismiss()
    }
    menuLayout.addView(addLabel)

    // Показать диалог
    dialog.show()


    // можно ли вставить картинку?



}


private fun showAddLabel(context:Context) {

    // Загружаем разметку из XML
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)

    // Получаем ссылки на элементы из разметки
    val colorsLayout = dialogView.findViewById<LinearLayout>(R.id.colorsLayout)
    val editText = dialogView.findViewById<EditText>(R.id.labelName)
    val buttonsLayout = dialogView.findViewById<LinearLayout>(R.id.buttonsLayout)

    val colorList = listOf("", "#FFFFFF", "#dcd9f4", "#e7d9f2", "#d7e8f0", "#ddedea", "#f3edcd", "#f2ded3", "#f0d7d3", "#deeadc")
    val frontColorList = listOf("", "#ced9f2", "#564af6", "#ab49f6", "#46b2e3", "#60cfbe", "#f7ce00", "#ee6f22", "#e93f25", "#64bb5c")

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

        val currentColorLabel = TextView(context).apply {
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
    val builder = AlertDialog.Builder(context)
        .setView(dialogView)

    val dialog = builder.create() // Создаем диалог

    // Функция для создания кнопки
    fun createButton(text: String, onClick: () -> Unit): Button {
        return Button(context).apply {
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
            // Здесь можно добавить логику для уведомления пользователя о некорректном вводе
            //Toast.makeText(this, "Введите от 1 до 30 символов", Toast.LENGTH_SHORT).show()
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