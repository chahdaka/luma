package com.example.luma;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ArchivedNotesActivity extends AppCompatActivity {

    private ListView archivedNotesListView;
    private TextView noArchivedNotesTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_notes);

        archivedNotesListView = findViewById(R.id.archivedNotesListView);
        noArchivedNotesTextView = findViewById(R.id.noArchivedNotesTextView);
        databaseHelper = new DatabaseHelper(this);

        loadArchivedNotes();
    }

    private void loadArchivedNotes() {
        List<Note> archivedNotes = databaseHelper.getArchivedNotes();

        if (archivedNotes.isEmpty()) {
            noArchivedNotesTextView.setVisibility(android.view.View.VISIBLE);
            archivedNotesListView.setVisibility(android.view.View.GONE);
        } else {
            noArchivedNotesTextView.setVisibility(android.view.View.GONE);
            archivedNotesListView.setVisibility(android.view.View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    archivedNotes.stream().map(Note::getTitle).toArray(String[]::new));
            archivedNotesListView.setAdapter(adapter);
        }
    }
}