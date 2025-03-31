package com.example.luma;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView notesListView;
    private Button addNoteButton;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private List<Note> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesListView = findViewById(R.id.notesListView);
        addNoteButton = findViewById(R.id.addNoteButton);
        databaseHelper = new DatabaseHelper(this);

        loadNotes();

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivityForResult(intent, 1);
        });

        notesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra("NOTE_ID", notesList.get(position).getTitle());
            intent.putExtra("NOTE_CONTENT", notesList.get(position).getContent());
            startActivityForResult(intent, 2);
        });
    }

    private void loadNotes() {
        notesList = databaseHelper.getAllNotes();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                notesList.stream().map(Note::getTitle).toArray(String[]::new));
        notesListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadNotes();
        }
    }
}