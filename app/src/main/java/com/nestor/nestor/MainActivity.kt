package com.nestor.nestor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.yandex.authsdk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var buttonsLayout: LinearLayout
    private lateinit var headLayout: LinearLayout
    private lateinit var mainLabel: TextView
    private lateinit var addButton: Button
    private lateinit var settingButton: Button
    private lateinit var notesDatabaseHelper: NotesDatabaseHelper
    private lateinit var imageLogo: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var authLauncher: ActivityResultLauncher<YandexAuthLoginOptions>
    private lateinit var yandexAuthSdk: YandexAuthSdk

    private var width: Int = 0
    private var height: Int = 0
    private var elementHeight: Int = 0
    private var sorting: Int = 4

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        CoroutineScope(Dispatchers.Main).launch {
            syncFromServ(this@MainActivity)
        }

        // Получение метрик экрана
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            displayMetrics.widthPixels = bounds.width()
            displayMetrics.heightPixels = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
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
        // Инициализация YandexAuthSdk
        yandexAuthSdk = YandexAuthSdk.create(YandexAuthOptions(this))
        // Регистрация launcher для авторизации через SDK
        authLauncher = registerForActivityResult(yandexAuthSdk.contract) { result ->
            when (result) {
                is YandexAuthResult.Success -> {
                    val token = result.token
                    saveToken(token.toString())
                    val tokenString: String = token.value
                    notesDatabaseHelper.saveCreds(tokenString)
                    // Обновление UI или загрузка данных пользователя
                    reloadNotes()
                }
                is YandexAuthResult.Failure -> {
                    Toast.makeText(
                        this,
                        "Ошибка авторизации: ${result.exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                YandexAuthResult.Cancelled -> {
                    Toast.makeText(this, "Авторизация отменена", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Регистрация launcher для запуска редактора
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadNotes()
            }
        }
        // Настройка UI (логотип, заголовок, кнопки)
        setupUiElements()
        // Настройка кнопки настроек / меню
        settingButton.setOnClickListener {
            mainMenu(this, width, height, notesDatabaseHelper) { selectedSort ->
                if (selectedSort in 0..5) {
                    notesDatabaseHelper.setSorting(selectedSort)
                    sorting = selectedSort
                    reloadNotes()
                }
            }
        }

        // Загрузка и отображение заметок
        sorting = notesDatabaseHelper.getSorting() ?: 0
        reloadNotes()
    }

    private fun setupUiElements() {
        val imageLogoParams = imageLogo.layoutParams as LinearLayout.LayoutParams
        imageLogoParams.width = height / 20
        imageLogoParams.height = height / 20
        imageLogo.layoutParams = imageLogoParams
        imageLogoParams.setMargins(width / 100, height / 180, width / 100, 0)

        val mainLabelParams = mainLabel.layoutParams as LinearLayout.LayoutParams
        mainLabelParams.width = width - 2 * elementHeight * 8 / 10
        mainLabelParams.height = elementHeight * 8 / 10
        mainLabel.layoutParams = mainLabelParams
        mainLabel.typeface = ResourcesCompat.getFont(this, R.font.roboto_mono)

        val addButtonParams = addButton.layoutParams as LinearLayout.LayoutParams
        addButtonParams.width = height / 20
        addButtonParams.height = height / 20
        addButton.layoutParams = addButtonParams
        addButtonParams.setMargins(width / 100, height / 200, width / 100, height / 200)

        addButton.setOnClickListener {
            val newNote = notesDatabaseHelper.addNote()
            startAnotherActivity(newNote.toInt())
        }
    }

    // Сохраняем токен в SharedPreferences
    private fun saveToken(token: String) {
        val prefs = getSharedPreferences("yandex_prefs", MODE_PRIVATE)
        prefs.edit().putString("oauth_token", token).apply()
    }

    // Функция перезагрузки заметок
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

    // Функция запуска редактора заметки
    private fun startAnotherActivity(newNote: Int) {
        val intent = Intent(this, NoteEdit::class.java).apply {
            putExtra("EXTRA_THEME", newNote.toString())
            putExtra("EXTRA_HEIGHT", height)
            putExtra("EXTRA_WIDTH", width)
        }
        resultLauncher.launch(intent)
    }

    // Функция для запуска авторизации из com.nestor.nestor.authPopup
    fun startYandexAuth() {
        authLauncher.launch(YandexAuthLoginOptions())
    }

    override fun onPause() {
        super.onPause()
        syncToServ(this)
    }
}
