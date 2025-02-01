package com.example.nestor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

data class NoteMin(
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
    private lateinit var notesDatabaseHelper: NotesDatabaseHelper
    private lateinit var imageLogo: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var width: Int = 0
    private var height: Int = 0
    private var elementHeight: Int = 0
    private var sorting: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        // Метрики экрана
        val displayMetrics = DisplayMetrics()
        val display = windowManager.currentWindowMetrics
        display.bounds.apply {
            displayMetrics.widthPixels = width()
            displayMetrics.heightPixels = height()
        }
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        elementHeight = height / 10


        // Инициализация элементов
        notesDatabaseHelper = NotesDatabaseHelper(this)
        buttonsLayout = findViewById(R.id.buttonsLayout)
        headLayout = findViewById(R.id.headLayout)
        imageLogo = findViewById(R.id.imageLogo)
        mainLabel = findViewById(R.id.mainLabel)
        addButton = findViewById(R.id.addButton)
        settingButton = findViewById(R.id.settingButton)


        // Получение отклика
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadNotes()
            }
        }


        // лого
        val imageLogoParams = imageLogo.layoutParams as LinearLayout.LayoutParams
        imageLogoParams.width = (height / 20)
        imageLogoParams.height = (height / 20)
        imageLogo.layoutParams = imageLogoParams
        imageLogoParams.setMargins(width / 100, height / 180, width / 100, 0)


        // Заголовок
        val mainLabelParams = mainLabel.layoutParams as LinearLayout.LayoutParams
        mainLabelParams.width = width - 2 * elementHeight * 8 / 10
        mainLabelParams.height = elementHeight * 8 / 10
        mainLabel.layoutParams = mainLabelParams
        mainLabel.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)

        // Добавить
        val addButtonParams = addButton.layoutParams as LinearLayout.LayoutParams
        addButtonParams.width = (height / 20)
        addButtonParams.height = (height / 20)
        addButton.layoutParams = addButtonParams
        addButtonParams.setMargins(width / 100, height / 200, width / 100, height / 200)

        addButton.setOnClickListener {
            val newNote = notesDatabaseHelper.addNote()
            startAnotherActivity(newNote.toInt())
        }


        // Меню
        val settingButtonParams = settingButton.layoutParams as LinearLayout.LayoutParams
        settingButtonParams.width = (height / 20)
        settingButtonParams.height = (height / 20)
        settingButton.layoutParams = settingButtonParams
        settingButtonParams.setMargins(width / 100, height / 200, width / 100, height / 200)

        settingButton.setOnClickListener {
            mainMenu(this, height) { selectedSort ->
                if (selectedSort in 0..5){
                notesDatabaseHelper.setSorting(selectedSort)
                    sorting = selectedSort
                reloadNotes()
                }
            }
        }


        // Кнопки тем
        sorting = notesDatabaseHelper.getSorting() ?: 0
        val notes = notesDatabaseHelper.getAllNotes(sorting)

        for (note in notes) {
            val noteItem = NoteMin (
                id = note.id,
                theme = note.theme,
                text = note.text,
                label = note.label.toInt()
            )
            val button = createNoteButton(noteItem, this, width, elementHeight, resultLauncher)
            buttonsLayout.addView(button)
        }
    }


    // Функция перезагрузки тем - Обновление
    private fun reloadNotes() {
        buttonsLayout.removeAllViews()
        val notes = notesDatabaseHelper.getAllNotes(sorting)
        for (note in notes) {
            val noteItem = NoteMin(
                id = note.id,
                theme = note.theme,
                text = note.text,
                label = note.label.toInt()
            )
            val button = createNoteButton(noteItem, this, width, elementHeight, resultLauncher)
            buttonsLayout.addView(button)
        }
    }


    // Функция запуска редактора
    private fun startAnotherActivity(newNote: Int) {
        val intent = Intent(this, NoteEdit::class.java).apply {
            putExtra("EXTRA_THEME", newNote.toString())
            putExtra("EXTRA_HEIGHT", height)
            putExtra("EXTRA_WIDTH", width)
        }
        resultLauncher.launch(intent)
    }

}