package com.nestor.nestor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.nestor.R


fun sortingMenu(context: Context, height: Int, onSortingSelected: (Int) -> Unit){
    val layoutInflater = LayoutInflater.from(context)
    val dialogView = layoutInflater.inflate(R.layout.main_menu, null)
    val menuLayout = dialogView.findViewById<LinearLayout>(R.id.menuLayout)

    val builder = AlertDialog.Builder(context).setView(dialogView)
    val dialog = builder.create()

    // Добавление пунктов меню

    menuLayout.addView(sortMenuItem(context, dialog,  height, R.drawable.desort, "Создан") {
        onSortingSelected(0)
    })
    menuLayout.addView(sortMenuItem(context, dialog,  height, R.drawable.sort, "Создан") {
        onSortingSelected(1)
    })
    menuLayout.addView(sortMenuItem(context, dialog,  height, R.drawable.desort, "Имя") {
        onSortingSelected(2)
    })
    menuLayout.addView(sortMenuItem(context, dialog, height, R.drawable.sort, "Имя") {
        onSortingSelected(3)
    })
    menuLayout.addView(sortMenuItem(context, dialog,  height, R.drawable.desort, "Изменен") {
        onSortingSelected(4)
    })
    menuLayout.addView(sortMenuItem(context, dialog,  height, R.drawable.sort, "Изменен") {
        onSortingSelected(5)
    })


    dialog.show() // Показываем диалог
}

// Функция для создания элемента меню
fun sortMenuItem(context: Context, dialog: AlertDialog, heightF: Int, iconResId: Int, title: String, onClick: () -> Unit): LinearLayout {
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
