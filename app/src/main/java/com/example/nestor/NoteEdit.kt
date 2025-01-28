package com.example.nestor


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import android.widget.EditText
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log


class NoteEdit : AppCompatActivity() {
    private lateinit var labelLayouts: LinearLayout
    private lateinit var headLayout: LinearLayout
    private lateinit var editTheme: EditText
    private lateinit var editText: EditText
    private lateinit var bottomLayout: LinearLayout
    private lateinit var notesDatabaseHelper: NotesDatabaseHelper // Предполагается, что у вас есть этот класс
    private var themeId: Int = 0 // Инициализируйте по необходимости
    private var label: String = "1" // Инициализируйте по необходимости
    companion object {const val COLUMN_LABELS_ID = "id"
    const val COLUMN_LABELS_NAME = "name"
    const val COLUMN_LABEL_COLOR1 = "color1"
    const val COLUMN_LABEL_COLOR2 = "color2"}

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_edit)

        // Получаем размеры экрана
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val elementHeight = (height / 10)

        notesDatabaseHelper = NotesDatabaseHelper(this)
        headLayout = findViewById(R.id.headLayout)
        labelLayouts = findViewById(R.id.labelLayouts) // Получаем ссылку на LinearLayout для кнопок
        editText = findViewById(R.id.editText)
        bottomLayout = findViewById(R.id.bottomLayout)

        // Получите переданный параметр
        val theme = intent.getStringExtra("EXTRA_THEME")
        themeId = theme?.toIntOrNull() ?: 1 // Если theme равно null, установим значение по умолчанию 1


        val note = notesDatabaseHelper.getNoteById(themeId)

        // Задаем переменные, которые не могут быть null
        val noteTheme: String
        val noteText: String
        val labelName: String? // Объявление переменной labelName вне условной конструкции
        val labelColor: Int

        if (note != null) {
            // Получаем цвет метки (может быть null)
            label = note.label?.toString() ?: "" // Замените toString на безопасный вызов, чтобы избежать потенциального NPE
            val labelId: Int = label?.toInt() ?: -1 // Если label не может быть преобразован в Int, используем значение по умолчанию (-1)


            if (labelId != -1) {

                val labelData = notesDatabaseHelper.getLabelById(labelId)
                val lName = labelData[COLUMN_LABELS_NAME]
                val labelColor = labelData[COLUMN_LABEL_COLOR1] ?: "#FFFFFF" // Используем цвет по умолчанию, если значение null
                val color = Color.parseColor(labelColor)
                noteTheme = note.theme ?: "" // Используем безопасный вызов, если theme тоже может быть null
                noteText = note.text ?: "" // Используем безопасный вызов для text
                labelName = lName?.toString()

            } else {
                noteTheme = ""
                noteText = ""
                labelName = ""
                labelColor = 0
            }
        } else {
            noteTheme = ""
            noteText = ""
            labelName = ""
            labelColor = 0
        }

        editTheme = EditText(this).apply {

            // Задаем параметры макета для EditText
            layoutParams = LinearLayout.LayoutParams(
                width - elementHeight, // Ширина - заполняет доступное пространство
                elementHeight // Высота - устанавливаем, например, 100 пикселей
            )

            // Устанавливаем размер шрифта
            textSize = 28f // Например, размер шрифта 18sp

            // Устанавливаем цвет текста
            setTextColor(Color.parseColor("#40474f")) // Темно-серый цвет

            // Установка подсказки (hint)
            hint = "Введите текст здесь" // Подсказка для пользователя
            setText(noteTheme) // Вставка текста

            // Устанавливаем тип шрифта (если нужно)
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)
            // Убираем границы
            background = null // Устанавливаем прозрачный фон
        }

        val backButton = Button(this).apply {
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

            setBackgroundResource(R.drawable.back) // Замените your_background_image на имя вашего изображения
            setOnClickListener {
                finish()


            }
        }
        headLayout.addView(backButton)

        // Добавляем EditText в родительский LinearLayout
        headLayout.addView(editTheme)



        val labelButton = Button(this).apply {
            // Устанавливаем текст кнопки
            text = "${labelName ?: "Без имени"}" // Добавлено значение по умолчанию, если labelName равно null

            // Устанавливаем параметры макета
            layoutParams = LinearLayout.LayoutParams(
                width / 3, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                elementHeight / 3  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
            ).apply {
                // Задаем отступы (левый, верхний, правый, нижний)
                setMargins(0, 0, width / 10, 0) // Отступы: 0 слева, 0 сверху, 10 справа, 0 снизу
            }

            // Создаем фоновый drawable с закругленными углами
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color) // Устанавливаем цвет кнопки
                cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
            }

            // Применяем фоновый drawable к кнопке
            background = drawable

            // Устанавливаем размер и цвет шрифта
            textSize = 12f // Размер шрифта (например, 16sp)
            setTextColor(Color.parseColor("#40474f")) // Цвет текста

            // Установка выравнивания текста по левому краю
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START // Выравнивание текста по левому краю

            // Устанавливаем тип шрифта
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)

            // Устанавливаем отступы внутри кнопки
            setPadding(5, 0, 5, 0) // Отступы: 5 слева, 0 сверху, 5 справа, 0 снизу
            isAllCaps = false   // Задаем флаг "все заглавные" с использованием синтаксиса свойств

            // Устанавливаем выравнивание кнопки по правому краю
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                gravity = Gravity.END // Выравнивание кнопки по правому краю
            }

            // Устанавливаем обработчик нажатия
            setOnClickListener {
                // Создайте Intent для открытия новой Activity
                val intent = Intent(context, LabelEdit::class.java).apply {
                    // Передайте параметр
                    if (note != null) {
                        putExtra("EXTRA_THEME", themeId)
                    } // измените note.theme на нужный вам параметр
                }
                // Запустите новую Activity
                context.startActivity(intent)
            }
        }
        labelLayouts.addView(labelButton)

        editText.setText("${noteText}")
        editText.background = null


        val checkBox = CheckBox(this)
        checkBox.layout(0, 0, 100, 100) // Установите размеры для чекбокса
        checkBox.measure(0, 0) // Необходима для получения ширины

        val checkBoxButton = Button(this).apply {
            text = ""
            layoutParams = LinearLayout.LayoutParams(
                elementHeight /2, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                elementHeight /2  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
            ).apply {
                // Задаем отступы (левый, верхний, правый, нижний)
                setMargins(elementHeight / 4, elementHeight / 8, elementHeight / 4, elementHeight / 8) // Отступы между кнопками
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

            setBackgroundResource(R.drawable.check_button) // Замените your_background_image на имя вашего изображения
            setOnClickListener {
                addCheckbox()
            }
            // Разрешаем ссылки внутри EditText
            editText.movementMethod = LinkMovementMethod.getInstance()
        }
        bottomLayout.addView(checkBoxButton)












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

                    // Убедитесь, что ClickableSpan обновлен
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

    override fun onDestroy() {
        super.onDestroy()
        val enteredTheme  = editTheme.text.toString()
        val enteredText  = editText.text.toString()

        notesDatabaseHelper.updateNote(themeId.toLong(), enteredTheme, enteredText, label.toString(), "",false)

        val resultIntent = Intent().apply {
            putExtra("RESULT_KEY", 1) // Передайте необходимые данные
        }
        setResult(Activity.RESULT_OK, resultIntent)
        // добавить передачу сигнала на обновление и закрытие окна
    }


}
