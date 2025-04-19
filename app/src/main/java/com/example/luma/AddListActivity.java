package com.example.luma;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class AddListActivity extends AppCompatActivity {

    private RadioGroup listTypeRadioGroup;
    private EditText itemsEditText;
    private Button createListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        listTypeRadioGroup = findViewById(R.id.listTypeRadioGroup);
        itemsEditText = findViewById(R.id.itemsEditText);
        createListButton = findViewById(R.id.createListButton);

        createListButton.setOnClickListener(v -> createMarkdownList());
    }

    private void createMarkdownList() {
        String itemsText = itemsEditText.getText().toString();

        if (TextUtils.isEmpty(itemsText)) {
            itemsEditText.setError("Enter list items");
            return;
        }

        // تقسيم العناصر بناءً على الأسطر الجديدة
        String[] items = itemsText.split("\n");

        // تحديد نوع القائمة
        int selectedId = listTypeRadioGroup.getCheckedRadioButtonId();
        boolean isOrdered = selectedId == R.id.orderedListRadioButton;

        // إنشاء القائمة بصيغة Markdown
        StringBuilder markdownList = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            if (isOrdered) {
                markdownList.append(i + 1).append(". ").append(items[i]).append("\n");
            } else {
                markdownList.append("- ").append(items[i]).append("\n");
            }
        }

        // إرجاع القائمة إلى النشاط السابق
        Intent resultIntent = new Intent();
        resultIntent.putExtra("MARKDOWN_LIST", markdownList.toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
