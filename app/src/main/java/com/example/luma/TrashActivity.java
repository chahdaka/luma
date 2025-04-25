
package com.example.luma;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;
public class TrashActivity extends AppCompatActivity {

    private ListView trashListView;
    private TextView noTrashNotesTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        trashListView = findViewById(R.id.trashListView);
        noTrashNotesTextView = findViewById(R.id.noTrashNotesTextView);
        databaseHelper = new DatabaseHelper(this);

        loadTrashNotes();
    }

    private void loadTrashNotes() {
        List<Note> trashNotes = databaseHelper.getDeletedNotes();

        if (trashNotes.isEmpty()) {
            noTrashNotesTextView.setVisibility(View.VISIBLE);
            trashListView.setVisibility(View.GONE);
        } else {
            noTrashNotesTextView.setVisibility(View.GONE);
            trashListView.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    trashNotes.stream().map(Note::getTitle).toArray(String[]::new));
            trashListView.setAdapter(adapter);

            // التعامل مع النقر على الملاحظة
            trashListView.setOnItemClickListener((parent, view, position, id) -> {
                Note selectedNote = trashNotes.get(position);
                showTrashOptions(selectedNote);
            });
        }
    }

    private void showTrashOptions(Note note) {
        final CharSequence[] options = {"Restore", "Delete Permanently", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Restore")) {
                restoreNote(note.getId());
            } else if (options[item].equals("Delete Permanently")) {
                deleteNotePermanently(note.getId());
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void restoreNote(int noteId) {
        boolean isRestored = databaseHelper.restoreNote(noteId);
        if (isRestored) {
            Toast.makeText(this, "Note restored successfully.", Toast.LENGTH_SHORT).show();
            loadTrashNotes();
        }
    }

    private void deleteNotePermanently(int noteId) {
        boolean isDeleted = databaseHelper.deleteNotePermanently(noteId);
        if (isDeleted) {
            Toast.makeText(this, "Note deleted permanently.", Toast.LENGTH_SHORT).show();
            loadTrashNotes();
        }
    }
}