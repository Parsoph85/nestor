package com.example.nestor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


fun mainMenu(context: Context, width: Int, height: Int) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.main_menu, null)
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)

    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()

    // Добавление пунктов меню
    menuLayout.addView(createMenuItem(context, dialog, width, height, R.drawable.sort, "Сортировка") {
        // sortingMenu()
    })
    menuLayout.addView(createMenuItem(context, dialog, width, height, R.drawable.offer, "Метки") {
        // labelMenu()
    })
    menuLayout.addView(createMenuItem(context, dialog, width, height, R.drawable.export, "Экспорт") {
        // exportAll()
    })
    menuLayout.addView(createMenuItem(context, dialog, width, height, R.drawable.importic, "Импорт") {
        // importAll()
    })
    menuLayout.addView(createMenuItem(context, dialog, width, height, R.drawable.auth, "Вход") {
        // authPopup()
    })

    dialog.show() // Показываем диалог
}

// Функция для создания элемента меню
fun createMenuItem(context: Context, dialog: AlertDialog, widthF: Int, heightF: Int, iconResId: Int, title: String, onClick: () -> Unit): LinearLayout {
    return LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(10, 10, 10, 10)

        // Иконка
        val icon = ImageView(context).apply {

            layoutParams = ViewGroup.LayoutParams((height / 20), (height / 20))
            setPadding(10, height / 200, 10, height / 200)
            setImageDrawable(ContextCompat.getDrawable(context, iconResId))
        }

        // Название пункта меню
        val menuItem = TextView(context).apply {
            text = title
            width = widthF * 9 / 10
            height = (heightF / 20)
            textSize = 18f
            setPadding(10, height / 200, 10, height / 200)
        }

        addView(icon)
        addView(menuItem)

        // Обработчик клика
        setOnClickListener {
            onClick()  // Выполняем переданную лямбду.
            // Закрываем диалог в случае необходимости
            dialog.dismiss()  // Здесь необходимо, чтобы "dialog" имел доступ
        }
    }
}













/*

private fun sortingPopup() {

    // Загружаем разметку из XML
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sorting, null)

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
*/