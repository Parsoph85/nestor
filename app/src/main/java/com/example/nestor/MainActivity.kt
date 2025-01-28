package com.example.nestor

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import androidx.core.content.res.ResourcesCompat
import android.text.style.StyleSpan
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var buttonsLayouts: LinearLayout
    private lateinit var headLayout: LinearLayout
    companion object {const val REQUEST_CODE = 1
    const val COLUMN_LABELS_ID = "id"
    const val COLUMN_LABELS_NAME = "name"
    const val COLUMN_LABEL_COLOR1 = "color1"
    const val COLUMN_LABEL_COLOR2 = "color2"}


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Получаем размеры экрана
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val elementHeight = (height / 10)

        val notesDatabaseHelper = NotesDatabaseHelper(this)
        headLayout = findViewById(R.id.headLayout)
        buttonsLayouts = findViewById(R.id.buttonsLayouts) // Получаем ссылку на LinearLayout для кнопок


        val headLabel = TextView(this).apply {
            text = "Заметки"

            layoutParams = LinearLayout.LayoutParams(
                width - 2 * elementHeight, // Ширина - заполняет доступное пространство
                elementHeight // Высота - устанавливаем, например, 100 пикселей
            )

            // Устанавливаем выравнивание текста по левому краю
            gravity = Gravity.CENTER // Или используйте Gravity.LEFT для старых версий Android

            // Установка размера шрифта (в sp)
            textSize = 28f // Например, размер шрифта 24sp
            // Установка типа шрифта
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)

            // Установка цвета текста
            setTextColor(Color.parseColor("#40474f")) // Например, темно-серый цвет
        }
        headLayout.addView(headLabel)


        val addButton = Button(this).apply {
            text = ""
            layoutParams = LinearLayout.LayoutParams(
                elementHeight /2, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                elementHeight /2  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
            ).apply {
                // Задаем отступы (левый, верхний, правый, нижний)
                setMargins(elementHeight / 4, 0, elementHeight / 4, 0) // Отступы между кнопками
            }

            // Создаем фоновый drawable с закругленными углами
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color) // Устанавливаем цвет кнопки
                cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
            }

            background = drawable // Применяем фоновый drawable к кнопке
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)

            setBackgroundResource(R.drawable.add) // Замените your_background_image на имя вашего изображения
            setOnClickListener {
                val newNote = notesDatabaseHelper.addNote()
                val intent = Intent(context, NoteEdit::class.java).apply {
                    // Передайте параметр
                    putExtra("EXTRA_THEME", newNote.toString()) // измените note.theme на нужный вам параметр
                }
                context.startActivity(intent)  // Запустите новую Activity
            }
        }
        headLayout.addView(addButton)



        val settingsButton = Button(this).apply {
            text = ""
            layoutParams = LinearLayout.LayoutParams(
                elementHeight /2, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                elementHeight /2  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
            ).apply {
                // Задаем отступы (левый, верхний, правый, нижний)
                setMargins(0, 0, elementHeight / 4, 0) // Отступы между кнопками
            }

            // Создаем фоновый drawable с закругленными углами
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color) // Устанавливаем цвет кнопки
                cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
            }

            background = drawable // Применяем фоновый drawable к кнопке
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)

            setBackgroundResource(R.drawable.down_arrow) // Замените your_background_image на имя вашего изображения

            setOnClickListener {
            }
        }
        headLayout.addView(settingsButton) // Добавляем кнопку в контейнер




        val notes = notesDatabaseHelper.getAllNotes()

        for (note in notes) {
            val label: String = note.label.toString()     // Получаем цвет метки (может быть null)
            val labelI: Int = label.toInt()

            val labelData = notesDatabaseHelper.getLabelById(labelI)
            val labelName = labelData[COLUMN_LABELS_NAME]
            val labelColor = labelData[COLUMN_LABEL_COLOR1] ?: "#FFFFFF" // Используем цвет по умолчанию, если значение null
            val color = Color.parseColor(labelColor)

            val button = Button(this).apply {

                // Создаем SpannableString для установки разных шрифтов
                val spannableString = SpannableString("${note.theme}\n${note.text}")

                // Установка жирного шрифта и размера для первого текста
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), // Устанавливаем жирный стиль
                    0,
                    note.theme.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    AbsoluteSizeSpan(18, true), // размер шрифта в dp
                    0,
                    note.theme.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Установка размера шрифта для второго текста
                spannableString.setSpan(
                    AbsoluteSizeSpan(16, true), // размер шрифта в dp
                    note.theme.length + 1, // Учитываем символ новой строки
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Выравнивание текста и другие настройки
                isAllCaps = false   // Задаем флаг "все заглавные" с использованием синтаксиса свойств
                text = spannableString // Применяем созданный SpannableString к элементу

                // Устанавливаем внутренние отступы (left, top, right, bottom)
                setPadding((width * 0.05).toInt(), 0, 0, 0) // Отступ 20 пунктов слева, 0 сверху, 0 справа, 0 снизу
                // Устанавливаем выравнивание текста по левому краю
                gravity = Gravity.START or Gravity.CENTER_VERTICAL // Или используйте Gravity.LEFT для старых версий Android

                layoutParams = LinearLayout.LayoutParams(
                    width * 94 / 100, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                    elementHeight  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                ).apply {
                    // Задаем отступы (левый, верхний, правый, нижний)
                    setMargins(0, 0, 0, elementHeight / 8) // Отступы между кнопками
                }

                // Создаем фоновый drawable с закругленными углами
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(color) // Устанавливаем цвет кнопки
                    cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
                }

                background = drawable // Применяем фоновый drawable к кнопке
                val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
                setTypeface(typeface)
                // Установка цвета текста
                setTextColor(Color.parseColor("#40474f")) // Например, темно-серый цвет

                // Установите обработчик нажатия кнопки, если необходимо
                setOnClickListener {
                    // Создайте Intent для открытия новой Activity
                    val intent = Intent(context, NoteEdit::class.java).apply {
                        // Передайте параметр
                        putExtra("EXTRA_THEME", note.id.toString()) // измените note.theme на нужный вам параметр
                    }
                    // Запустите новую Activity
                    (context as Activity).startActivityForResult(intent, REQUEST_CODE)
                }
            }
            buttonsLayouts.addView(button) // Добавляем кнопку в контейнер
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Получите данные из Intent
            val updatedData = data?.getStringExtra("RESULT_KEY")
            // Обновите buttonsLayouts или выполните другие действия
            println("Код для обновления - ${updatedData}")
        }
    }
}