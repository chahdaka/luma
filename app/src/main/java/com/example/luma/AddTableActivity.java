package com.example.luma;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddTableActivity extends AppCompatActivity {

    private EditText rowsEditText, columnsEditText;
    private Button createTableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        rowsEditText = findViewById(R.id.rowsEditText);
        columnsEditText = findViewById(R.id.columnsEditText);
        createTableButton = findViewById(R.id.createTableButton);

        createTableButton.setOnClickListener(v -> createMarkdownTable());
    }

    private void createMarkdownTable() {
        String rowsStr = rowsEditText.getText().toString();
        String columnsStr = columnsEditText.getText().toString();

        if (TextUtils.isEmpty(rowsStr) || TextUtils.isEmpty(columnsStr)) {
            rowsEditText.setError("Enter number of rows");
            columnsEditText.setError("Enter number of columns");
            return;
        }

        int rows = Integer.parseInt(rowsStr);
        int columns = Integer.parseInt(columnsStr);

        if (rows <= 0 || columns <= 0) {
            rowsEditText.setError("Invalid number of rows");
            columnsEditText.setError("Invalid number of columns");
            return;
        }

        // إنشاء جدول بصيغة Markdown
        StringBuilder markdownTable = new StringBuilder();

        // إنشاء عنوان الجدول
        markdownTable.append("|");
        for (int i = 0; i < columns; i++) {
            markdownTable.append(" Column ").append(i + 1).append(" |");
        }
        markdownTable.append("\n");

        // إنشاء خط الفاصل
        markdownTable.append("|");
        for (int i = 0; i < columns; i++) {
            markdownTable.append(" --- |");
        }
        markdownTable.append("\n");

        // إنشاء الصفوف
        for (int i = 0; i < rows; i++) {
            markdownTable.append("|");
            for (int j = 0; j < columns; j++) {
                markdownTable.append(" Cell ").append(i + 1).append("-").append(j + 1).append(" |");
            }
            markdownTable.append("\n");
        }

        // إرجاع الجدول إلى النشاط السابق
        Intent resultIntent = new Intent();
        resultIntent.putExtra("MARKDOWN_TABLE", markdownTable.toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

