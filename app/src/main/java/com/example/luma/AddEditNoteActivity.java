package com.example.luma;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextWatcher;
import android.text.Editable;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import io.noties.markwon.Markwon;
import android.text.TextUtils;
import io.noties.markwon.ext.tables.TablePlugin;
import android.widget.TextView;
public class AddEditNoteActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText;
    private Button saveButton;

    private Button addTableButton, previewButton;

    private TextView contentTextView; // إعلان العنصر
    private DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private boolean isPreviewMode = false; // تعريف المتغير هنا

    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
        // ربط العناصر مع XML
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        contentTextView = findViewById(R.id.contentTextView); // ربط contentTextView
        addTableButton = findViewById(R.id.addTableButton);
        previewButton = findViewById(R.id.previewButton);

        databaseHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("NOTE_ID")) {
            isEditMode = true;
            noteId = getIntent().getIntExtra("NOTE_ID", -1);
            String noteTitle = getIntent().getStringExtra("NOTE_TITLE");
            String noteContent = getIntent().getStringExtra("NOTE_CONTENT");

            titleEditText.setText(noteTitle);
            contentEditText.setText(noteContent);


        }


        Button addTableButton = findViewById(R.id.addTableButton);
        addTableButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTableActivity.class);
            startActivityForResult(intent, 1); // فتح نشاط إضافة الجداول
        });
        // التبديل بين وضع التحرير والمعاينة

        previewButton.setOnClickListener(v -> togglePreviewMode());






        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveNoteAutomatically();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        titleEditText.addTextChangedListener(textWatcher);
        contentEditText.addTextChangedListener(textWatcher);
    }



    // تبديل بين وضع التحرير والمعاينة
    private void togglePreviewMode() {
        if (isPreviewMode) {
            // العودة إلى وضع التحرير
            contentEditText.setVisibility(View.VISIBLE);
            contentTextView.setVisibility(View.GONE);
            previewButton.setText("Preview");
        } else {
            // التبديل إلى وضع المعاينة
            String markdownContent = contentEditText.getText().toString();
            displayMarkdownContent(markdownContent);
            contentEditText.setVisibility(View.GONE);
            contentTextView.setVisibility(View.VISIBLE);
            previewButton.setText("Edit");
        }
        isPreviewMode = !isPreviewMode;
    }



    private void displayMarkdownContent(String markdownContent) {
        if (TextUtils.isEmpty(markdownContent)) {
            contentTextView.setText("");
            return;
        }

        // إعداد Markwon مع دعم الجداول
        Markwon markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(this))
                .build();

        // تحويل النص المكتوب بتنسيق Markdown إلى نص منسق
        markwon.setMarkdown(contentTextView, markdownContent);
    }



//table
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String markdownTable = data.getStringExtra("MARKDOWN_TABLE");
            contentEditText.append("\n\n" + markdownTable); // إضافة الجدول إلى محتوى الملاحظة
        }
    }
//table


    private void saveNoteAutomatically() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (isEditMode) {
            // تحديث الملاحظة الموجودة
            boolean isUpdated = databaseHelper.updateNote(new Note(noteId, title, content));
            if (isUpdated) {
                setResult(RESULT_OK);
            }
        } else {
            // إضافة ملاحظة جديدة
            Note newNote = new Note(title, content);
            boolean isAdded = databaseHelper.addNote(newNote);
            if (isAdded) {
                noteId = getLastInsertedId(); // الحصول على ID الملاحظة الجديدة
                isEditMode = true; // تحويل الوضع إلى تعديل
                setResult(RESULT_OK);
            }
        }
    }

    // دالة للحصول على ID آخر ملاحظة تم إضافتها
    private int getLastInsertedId() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + DatabaseHelper.TABLE_NAME, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

}



