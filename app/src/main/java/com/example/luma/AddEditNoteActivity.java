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
import android.content.Intent;
import android.widget.Toast; // استيراد الكلاس Toast
import java.util.ArrayList; // استيراد ArrayList
import android.net.Uri; // استيراد Uri
import java.util.Calendar;
import android.net.Uri;
import android.media.MediaPlayer;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import android.util.Log; // استيراد الكلاس Log
import io.noties.markwon.AbstractMarkwonPlugin;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.text.style.URLSpan;
import androidx.annotation.NonNull; // استيراد الكلاس NonNull
import java.io.InputStream; // استيراد الكلاس InputStream
import java.io.OutputStream; // استيراد الكلاس OutputStream
import java.io.FileOutputStream; // استيراد الكلاس FileOutputStream

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TimePicker;



public class AddEditNoteActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private static final String CHANNEL_ID = "note_alarm_channel";

    private Button addTableButton, previewButton;

    private TextView contentTextView; // إعلان العنصر
    private DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private boolean isPreviewMode = false; // تعريف المتغير هنا
    private static final int PICK_AUDIO_REQUEST = 3; // كود الطلب لاختيار ملف صوتي
    private static final int REQUEST_AUDIO_PICK = 3; // رمز للطلب

    private static final int PICK_IMAGE_REQUEST = 4; // كود الطلب لاختيار صورة
    private static final int CAPTURE_IMAGE_REQUEST = 5; // كود الطلب لالتقاط صورة
    private Uri imageUri; // لتخزين مسار الصورة
    private MediaPlayer mediaPlayer;
    private Uri selectedAudioUri; // لتخزين URI الملف الصوتي

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

//الجدول
        Button addTableButton = findViewById(R.id.addTableButton);
        addTableButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTableActivity.class);
            startActivityForResult(intent, 1); // فتح نشاط إضافة الجداول
        });
        // التبديل بين وضع التحرير والمعاينة
// table
        previewButton.setOnClickListener(v -> togglePreviewMode());
//liste
        Button addListButton = findViewById(R.id.addListButton);
        addListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddListActivity.class);
            startActivityForResult(intent, 2); // فتح نشاط إضافة القوائم
        });
//liste
//audio
        Button addAudioButton = findViewById(R.id.addAudioButton);
        addAudioButton.setOnClickListener(v -> openAudioPicker());

        Button addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(v -> showImageOptions());

        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> shareNote());

        Button setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setOnClickListener(v -> showTimePicker());


//audio+photo
        contentTextView.setOnClickListener(v -> {
           String text = contentTextView.getText().toString();
            if (text.contains("[Audio File](")) {
                String audioPath = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
               playAudio(audioPath);
            }

            // التحقق مما إذا كان النص يحتوي على رابط صورة
            else if (text.contains("[Image](")) {
                String imagePath = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
                openFullImage(Uri.parse(imagePath)); // فتح الصورة بحجمها الكامل
            }

        });
        //photo
        //photo

// photo







//save automatic
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


//table
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
    }//table


//table markdown
/*    private void displayMarkdownContent(String markdownContent) {
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
        // جعل الروابط قابلة للنقر
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(contentTextView, Linkify.ALL);
    }*/
    //table



    private void displayMarkdownContent(String markdownContent) {
        if (TextUtils.isEmpty(markdownContent)) {
            contentTextView.setText("");
            return;
        }

        // إعداد Markwon
        Markwon markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(this))
                .build();

        // تحويل النص المنسق إلى نص قابل للعرض
        markwon.setMarkdown(contentTextView, markdownContent);

        // جعل الروابط قابلة للنقر
        CharSequence text = contentTextView.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;

            // البحث عن الروابط في النص
            URLSpan[] urlSpans = spannable.getSpans(0, text.length(), URLSpan.class);
            for (URLSpan urlSpan : urlSpans) {
                String url = urlSpan.getURL();
                int start = spannable.getSpanStart(urlSpan);
                int end = spannable.getSpanEnd(urlSpan);

                // إزالة URLSpan الحالي وإضافة ClickableSpan جديد
                spannable.removeSpan(urlSpan);
                spannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {

                        // تسجيل URI للتحقق
                        Log.d("ImageClick", "Clicked URI: " + url);

                        // فتح النشاط لعرض الصورة
                        openFullImage(Uri.parse(url));
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        // تفعيل النقرات داخل TextView
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(contentTextView, Linkify.ALL);

    }
    private void openFullImage(Uri imageUri) {
        Intent intent = new Intent(this, FullImageActivity.class);
        intent.putExtra("IMAGE_URI", imageUri.toString());
        startActivity(intent);
    }
//alarme
// عرض TimePickerDialog لاختيار وقت التنبيه
private void showTimePicker() {
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
        scheduleAlarm(selectedHour, selectedMinute);
    }, hour, minute, true);
    timePickerDialog.show();
}

    // جدولة التنبيه باستخدام AlarmManager
    private void scheduleAlarm(int hour, int minute) {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        // التحقق من أن الوقت المحدد ليس في الماضي
        if (alarmCalendar.before(Calendar.getInstance())) {
            alarmCalendar.add(Calendar.DAY_OF_MONTH, 1); // إضافة يوم واحد إذا كان الوقت في الماضي
        }

        // إنشاء Intent للتنبيه
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("NOTE_TITLE", titleEditText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

        // استخدام AlarmManager لجدولة التنبيه
        try {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Failed to set alarm.", Toast.LENGTH_SHORT).show();
        }
        } catch (Exception e) {
            android.util.Log.e("AlarmManager", "Error setting alarm: " + e.getMessage());
        }

        // إنشاء قناة إشعارات (مطلوب لنظام Android Oreo وأحدث)
        createNotificationChannel();
    }





    // إنشاء قناة إشعارات (مطلوب لنظام Android Oreo وأحدث)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Note Alarm Channel";
            String description = "Channel for note alarms";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


   /* private void scheduleAlarm(int hour, int minute) {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        if (alarmCalendar.before(Calendar.getInstance())) {
            alarmCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("NOTE_TITLE", "Test Note");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
        }
    }*/

//alarme









//share note


    // دالة لمشاركة الملاحظة
    private void shareNote() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Note is empty. Nothing to share.", Toast.LENGTH_SHORT).show();
            return;
        }

        // إنشاء نص المشاركة
        String shareText = "Title: " + title + "\n\nContent:\n" + content;

        // إنشاء Intent للمشاركة
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Note");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // التحقق من وجود صورة أو ملف صوتي
        Note note = getCurrentNote(); // الحصول على الملاحظة الحالية
        if (note.getImagePath() != null || note.getAudioPath() != null) {
            ArrayList<Uri> fileUris = new ArrayList<>();

            // إضافة الصورة إذا كانت موجودة
            if (note.getImagePath() != null) {
                Uri imageUri = Uri.parse(note.getImagePath());
                fileUris.add(imageUri);
            }

            // إضافة الملف الصوتي إذا كان موجودًا
            if (note.getAudioPath() != null) {
                Uri audioUri = Uri.parse(note.getAudioPath());
                fileUris.add(audioUri);
            }

            // تحديث نوع النية لدعم الملفات
            shareIntent.setType("*/*");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
        }

        // بدء نشاط المشاركة
        startActivity(Intent.createChooser(shareIntent, "Share Note"));
    }

    // دالة للحصول على الملاحظة الحالية
    private Note getCurrentNote() {
        int id = getIntent().getIntExtra("NOTE_ID", -1);
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");
        String audioPath = getIntent().getStringExtra("AUDIO_PATH");

        return new Note(id, title, content, imagePath, audioPath);
    }
    //share note










    // فتح نشاط اختيار ملف صوتي audio

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }
    //audio


    // دالة لتشغيل ملف صوتي audio
    private void playAudio(String audioPath) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // تحرير الموارد إذا كان هناك ملف قيد التشغيل
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(audioPath)); // تعيين مسار الملف
           /*  mediaPlayer.setOnPreparedListener(mp -> mp.start()); // بدء التشغيل عند الانتهاء من الإعداد
            mediaPlayer.prepareAsync(); // إعداد الملف بشكل غير متزامن*/
           mediaPlayer.prepare(); // إعداد الملف للتشغيل
            mediaPlayer.start(); // بدء التشغيل
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

//audio




















    //photo
// عرض خيارات الصورة (اختيار من المعرض أو التقاط صورة)
private void showImageOptions() {
    final CharSequence[] options = {"Choose from Gallery", "Take Photo", "Cancel"};
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Add Image");
    builder.setItems(options, (dialog, item) -> {
        if (options[item].equals("Choose from Gallery")) {
            openGallery();
        } else if (options[item].equals("Take Photo")) {
            capturePhoto();
        } else if (options[item].equals("Cancel")) {
            dialog.dismiss();
        }
    });
    builder.show();
}

    // فتح المعرض لاختيار صورة
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // التقاط صورة باستخدام الكاميرا
    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }
//photo




//table+liste+audio

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // التحقق من أن النتيجة ناجحة وأن البيانات ليست فارغة
        if (resultCode == RESULT_OK && data != null) {
            // التحقق من مصدر النتيجة باستخدام requestCode
            if (requestCode == 1) { // إذا كان المصدر هو نشاط إضافة الجدول
                String markdownTable = data.getStringExtra("MARKDOWN_TABLE");
                contentEditText.append("\n\n" + markdownTable); // إضافة الجدول إلى محتوى الملاحظة

            } else if (requestCode == 2) { // إذا كان المصدر هو نشاط إضافة القائمة
                String markdownList = data.getStringExtra("MARKDOWN_LIST");
                contentEditText.append("\n\n" + markdownList); // إضافة القائمة إلى محتوى الملاحظة

            } else if (requestCode == PICK_AUDIO_REQUEST) { // إذا كان المصدر هو اختيار ملف صوتي
                Uri audioUri = data.getData(); // الحصول على مسار الملف الصوتي
                String audioPath = audioUri.toString(); // تحويل المسار إلى نص
                contentEditText.append("\n\n[Audio File](" + audioPath + ")"); // إضافة الرابط إلى الملاحظ
                 // حفظ الملاحظة تلقائيًا بعد إضافة الملف الصوتي

                }
            else if (requestCode == PICK_IMAGE_REQUEST) { // إذا كان المصدر هو اختيار صورة من المعرض
                Uri imageUri = data.getData(); // الحصول على مسار الصورة من المعرض
                saveImageToNote(imageUri); // حفظ الصورة وإضافتها إلى الملاحظة

            } else if (requestCode == CAPTURE_IMAGE_REQUEST) { // إذا كان المصدر هو التقاط صورة باستخدام الكاميرا
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data"); // الحصول على الصورة من الكاميرا
                Uri imageUri = saveImageToStorage(imageBitmap); // حفظ الصورة في التخزين
                saveImageToNote(imageUri); // إضافة الصورة إلى الملاحظة
            }

            saveNoteAutomatically();
            }
        }*/







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // التحقق من أن النتيجة ناجحة وأن البيانات ليست فارغة
        if (resultCode == RESULT_OK && data != null) {
            // التحقق من مصدر النتيجة باستخدام requestCode
            if (requestCode == 1) { // إذا كان المصدر هو نشاط إضافة الجدول
                String markdownTable = data.getStringExtra("MARKDOWN_TABLE");
                contentEditText.append("\n\n" + markdownTable); // إضافة الجدول إلى محتوى الملاحظة

            } else if (requestCode == 2) { // إذا كان المصدر هو نشاط إضافة القائمة
                String markdownList = data.getStringExtra("MARKDOWN_LIST");
                contentEditText.append("\n\n" + markdownList); // إضافة القائمة إلى محتوى الملاحظة

            } else if (requestCode == PICK_AUDIO_REQUEST) { // إذا كان المصدر هو اختيار ملف صوتي
                Uri audioUri = data.getData(); // الحصول على مسار الملف الصوتي
                String audioPath = audioUri.toString(); // تحويل المسار إلى نص
                contentEditText.append("\n\n[Audio File](" + audioPath + ")"); // إضافة الرابط إلى الملاحظة

            } else if (requestCode == PICK_IMAGE_REQUEST) { // إذا كان المصدر هو اختيار صورة من المعرض
                Uri imageUri = data.getData(); // الحصول على مسار الصورة من المعرض
                Uri savedImageUri = saveImageToAppStorage(imageUri); // نسخ الصورة إلى دليل التطبيق

                if (savedImageUri != null) {
                    saveImageToNote(savedImageUri); // إضافة الصورة بصيغة Markdown
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CAPTURE_IMAGE_REQUEST) { // إذا كان المصدر هو التقاط صورة باستخدام الكاميرا
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data"); // الحصول على الصورة من الكاميرا
                Uri imageUri = saveImageToStorage(imageBitmap); // حفظ الصورة في التخزين
                saveImageToNote(imageUri); // إضافة الصورة إلى الملاحظة
            }

            // حفظ الملاحظة تلقائيًا بعد إضافة أي عنصر جديد
            saveNoteAutomatically();
        } else {
            Toast.makeText(this, "Operation canceled or failed", Toast.LENGTH_SHORT).show();
        }
    }














//photo
    // حفظ الصورة في التخزين الداخلي للتطبيق
    private Uri saveImageToStorage(Bitmap bitmap) {
        File imagesDir = new File(getFilesDir(), "images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }



    private Uri saveImageToAppStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File imagesDir = new File(getFilesDir(), "images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(imagesDir, fileName);

            try (OutputStream out = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return Uri.fromFile(imageFile); // إرجاع المسار الجديد
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    // إضافة الصورة إلى محتوى الملاحظة
    private void saveImageToNote(Uri imageUri) {
        String imagePath = imageUri.toString();
        contentEditText.append("\n\n![Image](" + imagePath + ")"); // إضافة الصورة بصيغة Markdown
    }
       //photo






//save automatic
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



