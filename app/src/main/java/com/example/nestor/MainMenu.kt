package com.example.nestor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


fun mainMenu(context: Context, width: Int, height: Int, notesDatabaseHelper: NotesDatabaseHelper, onSortingSelected: (Int) -> Unit) {
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.main_menu, null)
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)

    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()

    // Добавление пунктов меню
    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.sort, "Сортировка") {
        sortingMenu(context, height) { selectedSort -> onSortingSelected(selectedSort) }
    })

    // Изменен вызов labelMenu, чтобы он сработал при нажатии на кнопку
    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.offer, "Метки") {
        labelMenu(context, width, height, notesDatabaseHelper) { selectedSort -> onSortingSelected(selectedSort) }
    })

    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.export, "Экспорт") {
        exportAll(context, notesDatabaseHelper)
    })

    menuLayout.addView(createMenuItem(context, dialog, height, R.drawable.auth, "Вход") {
        // authPopup()
    })

    dialog.show() // Показываем диалог
}

// Функция для создания элемента меню
fun createMenuItem(context: Context, dialog: AlertDialog, heightF: Int, iconResId: Int, title: String, onClick: () -> Unit): LinearLayout {
    return LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(10, 10, 10, 10)

        // Иконка
        val icon = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(heightF / 20, heightF / 20) // Используем heightF вместо height
            setImageDrawable(ContextCompat.getDrawable(context, iconResId))
            setPadding(10, heightF / 200, 10, heightF / 200)
        }

        // Название пункта меню
        val menuItem = TextView(context).apply {
            text = title
            layoutParams = LinearLayout.LayoutParams(0, heightF / 20, 1f) // Используем обертку для ширины
            textSize = 18f
            setPadding(10, heightF / 200, 10, heightF / 200)
        }

        addView(icon)
        addView(menuItem)

        // Обработчик клика
        setOnClickListener {
            onClick()  // Выполняем переданную лямбду.
            dialog.dismiss()  // Закрываем диалог в случае необходимости
        }
    }
}
