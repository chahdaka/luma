package com.example.luma;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, "+

                "table_data TEXT, " + // لتخزين بيانات الجدول
                "list_items TEXT, " + // لتخزين عناصر القائمة
                "audio_file_path TEXT, " + // لتخزين مسار الملف الصوتي
                "image_file_path TEXT, " + // لتخزين مسار الصورة

        "is_archived INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                Note note = new Note(id, title, content);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());

        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(note.getId())});
        return result > 0;
    }
    public boolean archiveNote(int id, boolean isArchived) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_archived", isArchived ? 1 : 0);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
    public List<Note> getArchivedNotes() {
        List<Note> archivedNotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE is_archived=1", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                Note note = new Note(id, title, content);
                archivedNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return archivedNotes;
    }
    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}