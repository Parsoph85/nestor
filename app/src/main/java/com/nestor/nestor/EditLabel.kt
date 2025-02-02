package com.nestor.nestor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.nestor.R

fun labelMenu(context: Context, widthFun: Int, heightFun: Int, notesDatabaseHelper: NotesDatabaseHelper, onSortingSelected: (Int) -> Unit) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.main_menu, null)
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)
    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()
    val labels = notesDatabaseHelper.getAllLabels()

    for (label in labels) {
        val noteItem = Label(
            id = label.id,
            name = label.name,
            color1 = label.color1,
            color2 = label.color2,
            uid1 = label.uid1,
            uid2 = label.uid2
        )
        val labelId = noteItem.id
        val labelName = noteItem.name
        val labelColor1 = noteItem.color1
        val labelColor2 = noteItem.color2

        // Создаем новый LinearLayout для элемента меню
        val menuItemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 10)
        }

        // Полосочка в пункте меню
        val icon = TextView(context).apply {
            height = (heightFun / 20)
            textSize = 18f
            setPadding(10, heightFun / 200, 10, heightFun / 200)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(labelColor2))
                cornerRadius = 8f
            }
        }

        // Создаем TextView для названия метки
        val newMenuItem = TextView(context).apply {
            text = labelName
            width = (widthFun)
            height = (heightFun / 20)
            textSize = 18f
            setPadding(10, heightFun / 200, 10, heightFun / 200)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(labelColor1))
                cornerRadius = 8f
            }
        }

        // Добавляем ImageView и TextView в LinearLayout
        menuItemLayout.addView(icon)
        menuItemLayout.addView(newMenuItem)

        menuItemLayout.setOnClickListener {
            editLabelMenu(context, widthFun, heightFun, notesDatabaseHelper, labelId){ selectedSort -> onSortingSelected(selectedSort) }
            dialog.dismiss()
        }

        menuLayout.addView(menuItemLayout)
    }

    // Показываем диалог с метками
    dialog.show()
}

fun editLabelMenu(context: Context, width: Int, height: Int, notesDatabaseHelper: NotesDatabaseHelper, labelId: Int, onSortingSelected: (Int) -> Unit) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.main_menu, null)
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)

    // Создание диалога
    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()

    // Добавление элементов меню
    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.edit, "Редактирование") {
        editLabelPopup(context, labelId, width, notesDatabaseHelper){ selectedSort -> onSortingSelected(selectedSort) }

        dialog.dismiss()
    })
    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.delete, "Удаление") {
        notesDatabaseHelper.deleteLabel(labelId.toString(), "1")
        notesDatabaseHelper.getSorting()?.let { onSortingSelected(it) }
        dialog.dismiss()
    })

    dialog.show()
}

private fun editLabelPopup(context: Context, labelId: Int, widthFn: Int, notesDatabaseHelper: NotesDatabaseHelper, onSortingSelected: (Int) -> Unit) {

    // Загружаем разметку из XML
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sorting, null)

    // Получаем ссылки на элементы из разметки
    val colorsLayout = dialogView.findViewById<LinearLayout>(R.id.colorsLayout)
    val editText = dialogView.findViewById<EditText>(R.id.labelName)
    val buttonsLayout = dialogView.findViewById<LinearLayout>(R.id.buttonsLayout)


    val colorList = listOf("", "#FFFFFF", "#E6E6A1", "#A1C6E7", "#F2B2B2", "#D1E7D1", "#F9E6A1", "#D1C6E7", "#E6B3E0", "#99CCCC")
    val frontColorList = listOf("", "#ced9f2", "#B3B300", "#007BFF", "#FF4C4C", "#28A745", "#FF8C00", "#6F42C1", "#A500B5", "#339999")

    val label = notesDatabaseHelper.getLabelById(labelId.toString())
    val labelName = label?.name
    val labelColorDb = label?.color1
    val indexColor = colorList.indexOf(labelColorDb)

    // Добавляем RadioButton в RadioGroup программно
    var oldLabel = indexColor
    val editing = true

    val colorViewsMap = mutableMapOf<Int, TextView>() // Создаем Map для хранения ссылок на TextView
    var oldColor: String = colorList[oldLabel] // Инициализируем переменную для хранения предыдущего цвета
    var oldColorView: TextView? = null // Объявляем переменную для хранения ссылки на предыдущий выбранный TextView
    val width = widthFn

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

    editText.setText(labelName)


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
                notesDatabaseHelper.updateLabel(labelId, editTextValue, oldColor, endColor2)
            }
            notesDatabaseHelper.getSorting()?.let { onSortingSelected(it) }
            dialog.dismiss() // Закрываем диалог
        } else {
            Toast.makeText(context, "Введите от 1 до 30 символов", Toast.LENGTH_SHORT).show()
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