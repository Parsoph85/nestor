package com.example.nestor

import android.content.Context
import android.view.View
import android.view.Menu
import android.widget.PopupMenu

class LabelPopupMenu(private val context: Context, private val anchor: View, private val notesDatabaseHelper: NotesDatabaseHelper) {

    fun show() {
        val popupMenu = PopupMenu(context, anchor).apply {

            val labelDataDb = notesDatabaseHelper.getAllLabelsId()
            for (currentLabel in labelDataDb) {
                val currentLabelIdDb = currentLabel.toInt()
                val currentLabelDataDb = notesDatabaseHelper.getLabelById(currentLabelIdDb)
                val currentLabelNameDb: String = currentLabelDataDb?.name ?: "Без метки..."
                val currentLabelColor1Db: String = currentLabelDataDb?.color1 ?: "#ffffff"
                val currentLabelColor2Db: String = currentLabelDataDb?.color2 ?: "#ced9f2"
                menu.add(Menu.NONE, currentLabelIdDb, Menu.NONE, currentLabelNameDb)
            }

            // Добавляем пункт "Добавить"
            menu.add(Menu.NONE, ADD_LABEL_ID, Menu.NONE, "Добавить")

        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                ADD_LABEL_ID -> {
                    // Открытие меню для добавления новой метки
                    //AddLabelPopupMenu(context, anchor).show()
                    true
                }
                else -> {
                    // Передаем выбранный идентификатор метки
                    val labelId = menuItem.itemId
                    //Toast.makeText(context, "Выбранная метка ID: $labelId", Toast.LENGTH_SHORT).show()
                    // Здесь можно добавить логику для запуска активности редактирования метки
                    true
                }
            }
        }

        popupMenu.show() // Показываем меню
    }

    companion object {
        private const val ADD_LABEL_ID = 1001 // Уникальный идентификатор для добавления метки
    }
}
