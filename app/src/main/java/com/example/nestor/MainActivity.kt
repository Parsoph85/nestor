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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

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

    private var width: Int = 0
    private var elementHeight: Int = 0
    companion object {const val REQUEST_CODE = 1
    }
    private var reloadFlag: Int by Delegates.observable(1) { _, _, newValue -> reloadNotes()}


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Получаем размеры экрана
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels  //720     1080
        val height = displayMetrics.heightPixels //1600  2274
        elementHeight = (height / 10) //160

        notesDatabaseHelper = NotesDatabaseHelper(this)
        buttonsLayout = findViewById(R.id.buttonsLayout)
        headLayout = findViewById(R.id.headLayout)
        imageLogo = findViewById(R.id.imageLogo)
        mainLabel = findViewById(R.id.mainLabel)
        addButton = findViewById(R.id.addButton)
        settingButton = findViewById(R.id.settingButton)


        // вставить лого красиво надо!!!!!
        val imageLogoParams = imageLogo.layoutParams as LinearLayout.LayoutParams
        imageLogoParams.width = (height / 20)
        imageLogoParams.height = (height / 20)
        imageLogo.layoutParams = imageLogoParams
        imageLogoParams.setMargins(width / 100, height / 180, width / 100, 0)



        val mainLabelParams = mainLabel.layoutParams as LinearLayout.LayoutParams
        mainLabelParams.width = width - 2 * elementHeight * 8 / 10
        mainLabelParams.height = elementHeight * 8 / 10
        mainLabel.layoutParams = mainLabelParams
        mainLabel.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)


        val addButtonParams = addButton.layoutParams as LinearLayout.LayoutParams
        addButtonParams.width = (height / 20)
        addButtonParams.height = (height / 20)
        addButton.layoutParams = addButtonParams
        addButtonParams.setMargins(width / 100, height / 200, width / 100, height / 200)

        addButton.setOnClickListener {
            val newNote = notesDatabaseHelper.addNote()
            val intent = Intent(this,  NoteEdit::class.java).apply {
                // Передайте параметр
                putExtra("EXTRA_THEME", newNote.toString()) // измените note.theme на нужный вам параметр
            }
            startActivity(intent)  // Запустите новую Activity
        }


        val settingButtonParams = settingButton.layoutParams as LinearLayout.LayoutParams
        settingButtonParams.width = (height / 20)
        settingButtonParams.height = (height / 20)
        settingButton.layoutParams = settingButtonParams
        settingButtonParams.setMargins(width / 100, height / 200, width / 100, height / 200)

        settingButton.setOnClickListener {
            }


        val notes = notesDatabaseHelper.getAllNotes()

        for (note in notes) {
            val noteItem = NoteMin (
                id = note.id,
                theme = note.theme,
                text = note.text,
                label = note.label.toInt()
            )
            val button = createNoteButton(noteItem, this, width, elementHeight)
            buttonsLayout.addView(button)
        }
    }


    private fun reloadNotes() {
        buttonsLayout.removeAllViews()
        val notes = notesDatabaseHelper.getAllNotes()
        for (note in notes) {
            val noteItem = NoteMin(
                id = note.id,
                theme = note.theme,
                text = note.text,
                label = note.label.toInt()
            )
            val button = createNoteButton(noteItem, this, width, elementHeight)
            buttonsLayout.addView(button)
        }
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedData = data?.getStringExtra("RESULT_KEY") //передает нулл почему-то. потом проверим
            println("RESULTES as - $updatedData")
            // Перезагружаем заметки для обновления интерфейса
            reloadNotes()
        }
        reloadNotes()
    }
}