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

data class Note(
    val id: Int,
    val theme: String,
    val text: String,
    val label: Int?
)

class MainActivity : AppCompatActivity() {
    private lateinit var buttonsLayout: LinearLayout
    private lateinit var headLayout: LinearLayout
    private lateinit var mainLabel: TextView
    private lateinit var addButton: Button
    private lateinit var settingButton: Button

    companion object {const val REQUEST_CODE = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Получаем размеры экрана
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels  //720     1080
        val height = displayMetrics.heightPixels //1600  2274
        val elementHeight = (height / 10) //160

        val notesDatabaseHelper = NotesDatabaseHelper(this)
        buttonsLayout = findViewById(R.id.buttonsLayout)
        headLayout = findViewById(R.id.headLayout)
        mainLabel = findViewById(R.id.mainLabel)
        addButton = findViewById(R.id.addButton)
        settingButton = findViewById(R.id.settingButton)

        val mainLabelParams = mainLabel.layoutParams as LinearLayout.LayoutParams
        mainLabelParams.width = width - 2 * elementHeight * 8 / 10
        mainLabelParams.height = elementHeight * 8 / 10
        mainLabel.layoutParams = mainLabelParams
        mainLabel.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)


        val addButtonParams = addButton.layoutParams as LinearLayout.LayoutParams
        addButtonParams.width = elementHeight * 6 / 10
        addButtonParams.height = elementHeight * 6 / 10
        addButton.layoutParams = addButtonParams

        addButton.setOnClickListener {
            val newNote = notesDatabaseHelper.addNote()
            val intent = Intent(this,  NoteEdit::class.java).apply {
                // Передайте параметр
                putExtra("EXTRA_THEME", newNote.toString()) // измените note.theme на нужный вам параметр
            }
            startActivity(intent)  // Запустите новую Activity
        }


        val settingButtonParams = settingButton.layoutParams as LinearLayout.LayoutParams
        settingButtonParams.width = elementHeight * 6 / 10
        settingButtonParams.height = elementHeight * 6 / 10
        settingButton.layoutParams = settingButtonParams

        settingButton.setOnClickListener {
            }


        val notes = notesDatabaseHelper.getAllNotes()

        for (note in notes) {
            val noteItem = Note (
                id = note.id,
                theme = note.theme,
                text = note.text,
                label = note.label.toInt()
            )
            val button = createNoteButton(noteItem, this, width, elementHeight)
            buttonsLayout.addView(button)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedData = data?.getStringExtra("RESULT_KEY")
            println("Result is $updatedData")
            // Код для обновления
        }
    }
}