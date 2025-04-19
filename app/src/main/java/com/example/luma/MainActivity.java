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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
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

//archived notes
        Button viewArchivedNotesButton = findViewById(R.id.viewArchivedNotesButton);
        viewArchivedNotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ArchivedNotesActivity.class);
            startActivity(intent);
        });





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
            intent.putExtra("NOTE_ID", notesList.get(position).getId());
            intent.putExtra("NOTE_TITLE", notesList.get(position).getTitle());
            intent.putExtra("NOTE_CONTENT", notesList.get(position).getContent());
            startActivityForResult(intent, 2);
        });


//delete
        registerForContextMenu(notesListView); // إضافة هذا السطر
//delete
    }



//delete
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu); // تحميل القائمة من ملف XML
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position; // الحصول على موقع العنصر المحدد
        Note selectedNote = notesList.get(position); // الحصول على الملاحظة المحددة

        if (item.getItemId() == R.id.delete_note) { // التحقق مما إذا كان الخيار المختار هو "حذف"
            // حذف الملاحظة من قاعدة البيانات
            boolean isDeleted = databaseHelper.deleteNote(selectedNote.getId());
            if (isDeleted) {
                loadNotes(); // تحديث القائمة بعد الحذف
            }
            return true;
        }else if (item.getItemId() == R.id.archive_note) {
            boolean isArchived = databaseHelper.archiveNote(selectedNote.getId(), true);
            if (isArchived) {
                loadNotes();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
//delete


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