package com.example.luma;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

       titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        databaseHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("NOTE_ID")) {
            isEditMode = true;
            noteId = getIntent().getIntExtra("NOTE_ID", -1);
            titleEditText.setText(getIntent().getStringExtra("NOTE_TITLE"));
            contentEditText.setText(getIntent().getStringExtra("NOTE_CONTENT"));
        }

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();

            if (isEditMode) {
                databaseHelper.updateNote(new Note(title, content), noteId);
            } else {
                databaseHelper.addNote(new Note(title, content));
            }

            setResult(RESULT_OK);
            finish();
        });
    }
}