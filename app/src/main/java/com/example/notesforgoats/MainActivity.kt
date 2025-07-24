package com.example.notesforgoats
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        findViewById<TextView>(R.id.titleTextView).text = "Заметки"
        dbHelper = NotesDatabaseHelper(this)
        recyclerView = findViewById(R.id.notesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NoteAdapter(
            notes,
            onNoteClick = { note -> showEditNoteDialog(note) },
            onNoteLongClick = { note -> deleteNote(note) }
        )

        recyclerView.adapter = adapter

        findViewById<View>(R.id.addNoteButton).setOnClickListener {
            showAddNoteDialog()
        }

        loadNotes()
    }

    private fun loadNotes() {
        notes.clear()
        notes.addAll(dbHelper.getAllNotes())
        adapter.updateNotes(notes)
    }

    private fun showAddNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_note_dialog, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val contentInput = dialogView.findViewById<EditText>(R.id.contentInput)

        AlertDialog.Builder(this)
            .setTitle("Добавить заметку")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = titleInput.text.toString()
                val content = contentInput.text.toString()
                if (title.isNotEmpty() || content.isNotEmpty()) {
                    dbHelper.insertNote(title, content)
                    loadNotes()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditNoteDialog(note: Note) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_note_dialog, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val contentInput = dialogView.findViewById<EditText>(R.id.contentInput)

        titleInput.setText(note.title)
        contentInput.setText(note.content)

        AlertDialog.Builder(this)
            .setTitle("Редактировать заметку")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedTitle = titleInput.text.toString()
                val updatedContent = contentInput.text.toString()
                if (updatedTitle.isNotEmpty() || updatedContent.isNotEmpty()) {
                    val updatedNote = note.copy(title = updatedTitle, content = updatedContent)
                    dbHelper.updateNote(updatedNote)
                    loadNotes()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteNote(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Удалить заметку")
            .setMessage("Вы уверены, что хотите удалить эту заметку?")
            .setPositiveButton("Удалить") { _, _ ->
                dbHelper.deleteNote(note.id)
                loadNotes()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}