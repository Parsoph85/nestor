package com.nestor.nestor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.res.ResourcesCompat
import com.example.nestor.R

fun createNoteButton(note: NoteMin, context: Context, width: Int, elementHeight: Int, resultLauncher: ActivityResultLauncher<Intent>): Button {
    val button = Button(context).apply {
        val noteId = note.id
        val noteThemeDb = note.theme
        val noteTextDb = note.text
        val noteLabelDb = note.label
        val notesDatabaseHelper = NotesDatabaseHelper(context)
        val labelDataDb = noteLabelDb?.let { notesDatabaseHelper.getLabelById(it.toString()) }
        val labelColor1Db: String = labelDataDb?.color1 ?: "#ffffff"

        // Создаем SpannableString для установки разных шрифтов
        val spannableString = SpannableString("$noteThemeDb\n$noteTextDb")  // Создаем SpannableString для установки разных шрифтов

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Устанавливаем жирный стиль
            0,
            noteThemeDb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            AbsoluteSizeSpan(18, true), // размер шрифта в dp
            0,
            noteThemeDb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            AbsoluteSizeSpan(16, true), // размер шрифта в dp
            noteThemeDb.length + 1, // Учитываем символ новой строки
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Выравнивание текста и другие настройки
        isAllCaps = false   // Задаем флаг "все заглавные"
        text = spannableString // Применяем созданный SpannableString к элементу

        // Устанавливаем внутренние отступы (left, top, right, bottom)
        setPadding((width * 0.05).toInt(), 0, 0, 0) // Отступ 20 пунктов слева, 0 сверху, 0 справа, 0 снизу
        gravity = Gravity.START or Gravity.CENTER_VERTICAL // Выравнивание текста

        layoutParams = LinearLayout.LayoutParams(
            width * 94 / 100, // Ширина
            elementHeight  // Высота
        ).apply {
            setMargins(0, 0, 0, elementHeight / 8) // Отступы между кнопками
        }

        // Создаем фоновый drawable с закругленными углами
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor(labelColor1Db)) // Цвет кнопки
            cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Радиус закругления
        }

        background = drawable
        typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
        setTextColor(Color.parseColor("#40474f"))

        setOnClickListener {
                val intent = Intent(context, NoteEdit::class.java).apply {
                    putExtra("EXTRA_THEME", noteId.toString())
                    putExtra("EXTRA_HEIGHT", height)
                    putExtra("EXTRA_WIDTH", width)
                }
                resultLauncher.launch(intent)
        }
    }
    return button
}