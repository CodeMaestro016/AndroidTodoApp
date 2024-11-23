package com.example.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var editNote: EditText
    lateinit var saveNoteButton: Button
    lateinit var viewNotesButton: Button
    lateinit var notesListView: ListView

    private val sharedPreferenceFile = "com.example.notes"
    private var selectedNote: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        editNote = findViewById(R.id.editTextNote)
        saveNoteButton = findViewById(R.id.saveNoteButton)
        viewNotesButton = findViewById(R.id.viewNotesButton)
        notesListView = findViewById(R.id.notesListView)


        saveNoteButton.setOnClickListener {
            addNote()
        }


        viewNotesButton.setOnClickListener {
            displayNotes()
        }

    //navigate to stopwatch UI
        val main = findViewById<Button>(R.id.stopwatchButton)
        main.setOnClickListener{
            val Intent = Intent(this,MainActivity2::class.java)
            startActivity(Intent)
        }
    }

    // Function to insert
    private fun addNote() {
        val newNote = editNote.text.toString()

        if (newNote.isEmpty()) {
            Toast.makeText(this, "Please enter a note!", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val notes = sharedPreferences.getStringSet("notes", mutableSetOf()) ?: mutableSetOf()


        if (selectedNote != null && notes.contains(selectedNote)) {
            notes.remove(selectedNote)
        }

        notes.add(newNote)
        editor.putStringSet("notes", notes)
        editor.apply()


        editNote.text.clear()
        selectedNote = null
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()

        displayNotes()
    }

    // Function to  display
    private fun displayNotes() {
        val sharedPreferences = getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE)
        val notes = sharedPreferences.getStringSet("notes", mutableSetOf())


        val notesList = notes?.toList() ?: emptyList()


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList)
        notesListView.adapter = adapter


        notesListView.setOnItemClickListener { parent, view, position, id ->
            val selectNote = parent.getItemAtPosition(position) as String
            editNote.setText(selectNote)
            selectedNote = selectNote
            Toast.makeText(this, "Note loaded for editing...", Toast.LENGTH_SHORT).show()
        }


        notesListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectNote = parent.getItemAtPosition(position) as String
            showNote(selectNote)
            true
        }
    }

    // Function to show a message to delete
    private fun showNote(note: String) {
        val Builder = android.app.AlertDialog.Builder(this)
        Builder.setMessage("Do you want to delete this note?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                deleteNote(note)
            }
            .setNegativeButton("No") { nav, id ->
                nav.dismiss()
            }

        val alert = Builder.create()
        alert.setTitle("Delete Note")
        alert.show()
    }

    // Function to delete a note
    private fun deleteNote(note: String) {
        val sharedPreferences = getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        val notes = sharedPreferences.getStringSet("notes", mutableSetOf())
        notes?.remove(note)

        edit.putStringSet("notes", notes)
        edit.apply()

        displayNotes()
        Toast.makeText(this, "Note deleted!", Toast.LENGTH_LONG).show()


        if (selectedNote == note) {
            selectedNote = null
            editNote.text.clear()
        }
    }
}
