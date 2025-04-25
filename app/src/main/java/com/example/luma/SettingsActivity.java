package com.example.luma;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        languageSpinner = findViewById(R.id.languageSpinner);

        // إعداد قائمة اللغات
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // تحديد اللغة الحالية
        String currentLanguage = Locale.getDefault().getLanguage();
        if (currentLanguage.equals("ar")) {
            languageSpinner.setSelection(0); // العربية
        } else if (currentLanguage.equals("fr")) {
            languageSpinner.setSelection(1); // الفرنسية
        } else {
            languageSpinner.setSelection(2); // الإنجليزية
        }

        // التعامل مع تغيير اللغة
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = "";
                switch (position) {
                    case 0:
                        selectedLanguage = "ar"; // العربية
                        break;
                    case 1:
                        selectedLanguage = "fr"; // الفرنسية
                        break;
                    case 2:
                        selectedLanguage = "en"; // الإنجليزية
                        break;
                }
                setAppLocale(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // تغيير لغة التطبيق
    private void setAppLocale(String languageCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // إعادة تشغيل النشاط الحالي
        recreate();
    }
}