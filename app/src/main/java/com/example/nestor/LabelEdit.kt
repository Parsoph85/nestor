package com.example.nestor


    import android.annotation.SuppressLint
    import android.content.Context
    import android.graphics.Color
    import android.graphics.drawable.GradientDrawable
    import android.os.Bundle
    import android.util.DisplayMetrics
    import android.util.TypedValue
    import android.view.Gravity
    import android.view.LayoutInflater
    import android.view.MenuItem
    import android.view.View
    import android.widget.Button
    import android.widget.EditText
    import android.widget.LinearLayout
    import android.widget.PopupMenu
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.res.ResourcesCompat
    import kotlin.properties.Delegates

class LabelEdit : AppCompatActivity() {
        private lateinit var buttonsLayouts: LinearLayout
        private lateinit var bottomLayout: LinearLayout
        private lateinit var headLayout: LinearLayout
        companion object {const val COLUMN_LABELS_ID = "id"
        const val COLUMN_LABELS_NAME = "name"
        const val COLUMN_LABEL_COLOR1 = "color1"
        const val COLUMN_LABEL_COLOR2 = "color2"}
        var WIDTH = 0
        var ElementHeight = 0
        var OldLabel: Int = 1
        var OldLabelView: TextView? = null
        var OldColor: String = "#FFFFFF"
        val LabelViewsMap = mutableMapOf<Int, TextView>()
        val notesDatabaseHelper = NotesDatabaseHelper(this)
        var addButton: Button? = null
        var NEWID: Int by Delegates.observable(0) { _, _, newValue -> addLabel(newValue)}

        @SuppressLint("SetTextI18n", "MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.label_edit)
            val theme = intent.getStringExtra("EXTRA_THEME")
            val themeId = theme?.toIntOrNull() ?: 1 // Если theme равно null, установим значение по умолчанию 1

            // Получаем размеры экрана
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val width = displayMetrics.widthPixels
            WIDTH = width
            val height = displayMetrics.heightPixels
            ElementHeight = (height / 10)

            //val notesDatabaseHelper = NotesDatabaseHelper(this)
            headLayout = findViewById(R.id.headLayout)
            buttonsLayouts = findViewById(R.id.buttonsLayouts) // Получаем ссылку на LinearLayout для кнопок


            val backButton = Button(this).apply {
                text = ""
                layoutParams = LinearLayout.LayoutParams(
                    ElementHeight /2, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                    ElementHeight /2  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                ).apply {
                    // Задаем отступы (левый, верхний, правый, нижний)
                    setMargins(ElementHeight / 4, 0, ElementHeight / 4, 0) // Отступы между кнопками
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

            val acceptButton = Button(this).apply {
                text = ""
                layoutParams = LinearLayout.LayoutParams(
                    ElementHeight /2, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                    ElementHeight /2  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
                ).apply {
                    // Задаем отступы (левый, верхний, правый, нижний)
                    setMargins(ElementHeight / 4, 0, ElementHeight / 4, 0) // Отступы между кнопками
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
                    //notesDatabaseHelper.updateNoteLabel(themeId, OldLabel.toString())



                    finish()
                }
            }
            headLayout.addView(acceptButton)

            val labelsId = notesDatabaseHelper.getAllLabelsId()

            for (labelId in labelsId) {
                val labelData = notesDatabaseHelper.getLabelById(labelId.toInt())
                val lName = labelData[NoteEdit.COLUMN_LABELS_NAME]
                val labelColor = labelData[NoteEdit.COLUMN_LABEL_COLOR1] ?: "#FFFFFF"
                val labelColorFront = labelData[NoteEdit.COLUMN_LABEL_COLOR2] ?: "#ced9f2"
                val labelName = lName?.toString()

                createLabelView(labelName.toString(), labelId.toInt(), labelColor.toString(), labelColorFront.toString())

            }

            createAddButton()
        }

    fun showCustomPopupDialog(context: Context, oldLabelId: Int, labelText: String) {
        // Загружаем разметку из XML
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)

        // Получаем ссылки на элементы из разметки
        val editText = dialogView.findViewById<EditText>(R.id.labelName)
        val buttonsLayout = dialogView.findViewById<LinearLayout>(R.id.buttonsLayout) // Исправлено на LinearLayout
        val colorsLayout = dialogView.findViewById<LinearLayout>(R.id.colorsLayout)

        val colorList = listOf("#FFFFFF", "#dcd9f4", "#e7d9f2", "#d7e8f0", "#ddedea", "#f3edcd", "#f2ded3", "#f0d7d3", "#deeadc")
        val frontColorList = listOf("#ced9f2", "#564af6", "#ab49f6", "#46b2e3", "#60cfbe", "#f7ce00", "#ee6f22", "#e93f25", "#64bb5c")

        // Добавляем RadioButton в RadioGroup программно
        var oldLabel: Int = 1 // Инициализируем переменную для хранения предыдущего выбранного labelId
        var editing = false

        if (oldLabelId != 0){
            editing = true
            val labelData = notesDatabaseHelper.getLabelById(oldLabelId.toInt())
            val labelColor = labelData[NoteEdit.COLUMN_LABEL_COLOR1] ?: "#FFFFFF"
            val index = colorList.indexOfFirst { it == labelColor}
            oldLabel = index + 1
            editText.setText(labelText)
        }

        val colorViewsMap = mutableMapOf<Int, TextView>() // Создаем Map для хранения ссылок на TextView
        var oldColor: String = colorList[oldLabel] // Инициализируем переменную для хранения предыдущего цвета
        var oldColorView: TextView? = null // Объявляем переменную для хранения ссылки на предыдущий выбранный TextView
        val width = WIDTH


        var endColor2: String = ""

        for (i in 1..9) {
            val index = i - 1
            val currentColor = Color.parseColor(colorList[index])
            val frontCurrentColor = Color.parseColor(frontColorList[index])
            val currentColorLabel = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    width * 8 / 100, // Ширина в пикселях
                    width * 8 / 100  // Высота в пикселях
                ).apply {
                    setMargins((width * 1 / 100), 0, 0, 0) // Отступы между кнопками
                }

                // Создаем фоновый drawable с закругленными углами
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(currentColor) // Устанавливаем цвет кнопки
                }
                background = drawable // Применяем фоновый drawable к кнопке

                if (i == oldLabel){
                    setBackgroundColor(frontCurrentColor)
                    oldColor = colorList[i-1]
                    oldColorView = this
                    endColor2 = frontColorList[i-1]
                }

                // Устанавливаем обработчик нажатия
                setOnClickListener {
                    if (oldLabel != i) {
                        // Сбрасываем стиль предыдущего выбранного TextView
                        oldColorView?.let { oldColorView ->
                            oldColorView.setBackgroundColor(Color.parseColor(oldColor))
                        }

                        oldColor = colorList[i-1]  // Обновляем старый labelId и цвет
                        oldLabel = i
                        oldColorView = this // Обновляем ссылку на текущий выбранный TextView
                        endColor2 = frontColorList[i-1]


                        setBackgroundColor(frontCurrentColor)

                    }

                }
            }

            colorViewsMap[i] = currentColorLabel // Сохраняем ссылку на текущий TextView в Map
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
            val notesDatabaseHelper = NotesDatabaseHelper(this)
            val editTextValue = editText.text.toString().trim() // Удаляем пробелы

            // Проверка длины текста
            if (editTextValue.length in 1..30) {
                if(editing){
                    notesDatabaseHelper.updateLabel(oldLabel, editTextValue, oldColor, endColor2)
                    val edButton = LabelViewsMap[oldLabel]
                    if (edButton != null) {
                        edButton.setBackgroundColor(Color.parseColor(oldColor))
                    }
                }else{
                val newId = notesDatabaseHelper.addLabel(editTextValue, oldColor, endColor2)
                NEWID = newId.toInt()}

                dialog.dismiss() // Закрываем диалог
            } else {
                // Здесь можно добавить логику для уведомления пользователя о некорректном вводе
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

    private fun addLabel(labelId: Int) {
        val labelData = notesDatabaseHelper.getLabelById(labelId.toInt())
        val lName = labelData[NoteEdit.COLUMN_LABELS_NAME]
        val labelColor = labelData[NoteEdit.COLUMN_LABEL_COLOR1] ?: "#FFFFFF"
        val labelColorFront = labelData[NoteEdit.COLUMN_LABEL_COLOR2] ?: "#ced9f2"
        val labelName = lName?.toString()
        buttonsLayouts.removeView(addButton)
        createLabelView(labelName.toString(), labelId.toInt(), labelColor.toString(), labelColorFront.toString())
        createAddButton()
    }

    private fun createLabelView(labelName: String, labelId: Int, labelColor: String, colorFront: String) {
        val width = WIDTH
        val currentLabel = TextView(this).apply {
            text = labelName
            isAllCaps = false
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                width * 94 / 100, // Ширина в пикселях
                ElementHeight  // Высота в пикселях
            ).apply {
                setMargins(0, 0, 0, ElementHeight / 8) // Отступы между кнопками
            }

            // Создаем фоновый drawable с закругленными углами
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor(labelColor)) // Устанавливаем цвет кнопки
                cornerRadius = (width * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
            }
            background = drawable // Применяем фоновый drawable к кнопке
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
            setTypeface(typeface)
            setTextColor(Color.parseColor("#40474f"))  // Установка цвета текста
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f) // Установка размера шрифта 24sp
            setPadding((width * 5 / 100), 0, 0, 0) // Отступы (левый, верхний, правый, нижний) в пикселях

            if (labelId == OldLabel){
                setBackgroundColor(Color.parseColor(colorFront))
                OldColor = labelColor
                OldLabelView = this
            }

            // Устанавливаем обработчик нажатия
            setOnClickListener {
                if (OldLabel != labelId.toInt()) {
                    // Сбрасываем стиль предыдущего выбранного TextView
                    OldLabelView?.let { oldLabelView ->
                        oldLabelView.setBackgroundColor(Color.parseColor(OldColor))
                        val fontTypeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
                        oldLabelView.setTypeface(fontTypeface)
                        oldLabelView.setTextColor(Color.parseColor("#40474f"))
                        oldLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    }

                    // Обновляем старый labelId и цвет
                    OldLabel = labelId.toInt()
                    OldColor = labelColor
                    OldLabelView = this // Обновляем ссылку на текущий выбранный TextView
                    setBackgroundColor(Color.parseColor(colorFront))
                }
            }
            setOnLongClickListener { view ->
                // Обработка долгого нажатия
                showCustomPopupMenu(view, labelId, labelName) // Передаем view и labelId
                true // Возвращаем true, чтобы указать, что событие обработано
            }
        }

        LabelViewsMap[labelId.toInt()] = currentLabel // Сохраняем ссылку на текущий TextView в Map
        buttonsLayouts.addView(currentLabel) // Добавляем TextView в layout
        buttonsLayouts.removeView(addButton)


    }

    private fun createAddButton() {
        addButton = Button(this).apply {
        text = "Добавить ярлычок"
        isAllCaps = false
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            WIDTH * 94 / 100, // Ширина в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
            ElementHeight  // Высота в пикселях (или LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT)
        ).apply {
            setMargins(0, 0, 0, ElementHeight / 8) // Отступы между кнопками
        }

        // Создаем фоновый drawable с закругленными углами
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color) // Устанавливаем цвет кнопки
            cornerRadius = (WIDTH * 0.05f).coerceAtLeast(0f) // Устанавливаем радиус закругления
        }
        background = drawable // Применяем фоновый drawable к кнопке
        val typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
        setTypeface(typeface)
        setTextColor(Color.parseColor("#40474f"))  // Установка цвета текста
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f) // Установка размера шрифта 24sp
        setPadding((WIDTH * 5 / 100), 0, 0, 0) // Отступы (левый, верхний, правый, нижний) в пикселях

        // Устанавливаем обработчик нажатия
        setOnClickListener {
            showCustomPopupDialog(context, 0, "")
        }
        }
    buttonsLayouts.addView(addButton)
    }

    private fun showCustomPopupMenu(view: View, labelId: Int, textLabel: String) {

        // Создаем всплывающее меню
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        // Устанавливаем обработчик кликов для пунктов меню
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    notesDatabaseHelper.deleteLabel(labelId)
                    val delButton = LabelViewsMap[labelId]
                    buttonsLayouts.removeView(delButton)
                    true
                }
                R.id.action_edit -> {
                    //showCustomPopupDialog(this, labelId, textLabel)
                    true
                }
                else -> false
            }
        }

        // Показываем меню
        popupMenu.show()
    }


}
