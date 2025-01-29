package com.example.nestor


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.nestor.MainActivity.Companion.REQUEST_CODE
import kotlin.properties.Delegates


class NoteEdit : AppCompatActivity() {
    private lateinit var labelLayouts: LinearLayout
    private lateinit var headLayout: LinearLayout
    private lateinit var backButton: Button
    private lateinit var editTheme: EditText
    private lateinit var editText: EditText
    private lateinit var bottomLayout: LinearLayout
    private lateinit var checkButton: Button
    private lateinit var labelView: TextView
    private lateinit var notesDatabaseHelper: NotesDatabaseHelper
    private var noteIdDb: Int = 1
    private var noteLabelDb: Int by Delegates.observable(1) { _, _, newValue -> changeLabel(newValue)}


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_edit)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val elementHeight = (height / 10)


        notesDatabaseHelper = NotesDatabaseHelper(this)
        headLayout = findViewById(R.id.headLayout)
        labelLayouts = findViewById(R.id.labelLayouts)
        editText = findViewById(R.id.editText)
        bottomLayout = findViewById(R.id.bottomLayout)
        editTheme = findViewById(R.id.editTheme)
        backButton = findViewById(R.id.backButton)
        checkButton = findViewById(R.id.checkButton)
        labelView = findViewById(R.id.labelView)


        val theme = intent.getStringExtra("EXTRA_THEME")
        noteIdDb = theme?.toInt() ?: 1


        val note = notesDatabaseHelper.getNoteById(noteIdDb)
        val noteThemeDb: String = note?.theme ?: "Без темы..."
        val noteTextDb: String = note?.text.toString()
        noteLabelDb = note?.label?.toInt() ?: 1

        val labelDataDb = notesDatabaseHelper.getLabelById(noteLabelDb)
        val labelNameDb: String = labelDataDb?.name ?: "Без метки..."
        val labelColor1Db: String = labelDataDb?.color1 ?: "#ffffff"


        val backButtonParams = backButton.layoutParams as LinearLayout.LayoutParams
        backButtonParams.width = (height / 15)
        backButtonParams.height = (height / 15)
        backButton.layoutParams = backButtonParams

        backButton.setOnClickListener {
            finish()
        }


        val editThemeParams = editTheme.layoutParams as LinearLayout.LayoutParams
        editThemeParams.width = width - elementHeight * 8 / 10
        editThemeParams.height = height / 10
        editTheme.layoutParams = editThemeParams
        editTheme.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)
        editTheme.setText(noteThemeDb)
        editTheme.textSize = (height / 30).toFloat() / resources.displayMetrics.scaledDensity
        editTheme.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val currentText = dest.toString().trim()
            val newLength = currentText.length + source.length - (dend - dstart)

            if (newLength > 20) {
                val excess = (currentText + source.toString()).take(20)
                excess.substringAfterLast(" ").take(20)
            } else {
                null
            }
        })


        val labelViewParams = labelView.layoutParams as LinearLayout.LayoutParams
        labelViewParams.width = (width / 2)
        labelViewParams.height = (height / 20)
        labelView.layoutParams = labelViewParams
        labelView.text = labelNameDb
        labelView.textSize = (height / 40).toFloat() / resources.displayMetrics.scaledDensity

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor(labelColor1Db))
            cornerRadius = (width * 0.05f).coerceAtLeast(0f)}
        labelView.background = drawable
        labelView.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)

        labelView.setOnClickListener {
            LabelPopupMenu(this, it, notesDatabaseHelper).show()
        }


        editText.setText(noteTextDb)
        editText.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)
        editText.textSize = (height / 35).toFloat() / resources.displayMetrics.scaledDensity
        editText.movementMethod = LinkMovementMethod.getInstance()



        val checkButtonParams = checkButton.layoutParams as LinearLayout.LayoutParams
        checkButtonParams.width = elementHeight * 6 / 10
        checkButtonParams.height = elementHeight * 6 / 10
        checkButton.layoutParams = checkButtonParams

        checkButton.setOnClickListener {
            addCheckbox()
        }
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


    private fun changeLabel(newLabelId: Int) {
        val newLabelData = notesDatabaseHelper.getLabelById(newLabelId)
        val newLabelName: String = newLabelData?.name ?: "Без метки..."
        val newLabelColor1: String = newLabelData?.color1 ?: "#ffffff"
        //labelButton.setBackgroundColor(Color.parseColor(newLabelColor1))
        //labelButton.text = newLabelName
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val dataEcho = data?.getStringExtra("RESULT_LABEL")
            noteLabelDb = dataEcho?.toInt() ?: 1
            // Код для обновления
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        var enteredTheme  = editTheme.text.toString()
        val enteredText  = editText.text.toString()
        if (enteredTheme == "Без темы..."){
            enteredTheme = enteredText.take(26) + "..."
        }

        //notesDatabaseHelper.updateNote(noteIdDb.toLong(), enteredTheme, enteredText, noteLabelDb.toString(), "",false)

        val resultIntent = Intent().apply {
            putExtra("RESULT_KEY", noteIdDb) // Передайте необходимые данные
        }
        setResult(Activity.RESULT_OK, resultIntent)
        // добавить передачу сигнала на обновление и закрытие окна
    }


}
