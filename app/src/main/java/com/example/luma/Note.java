package com.example.luma;
public class Note {
    private int id;

    private String title;
    private String content;
    private String audioPath;
    private String imagePath;

    // 1. بنية لإنشاء ملاحظة جديدة (بدون ID)
    public Note(String title, String content) {
        this.id = -1; // قيمة افتراضية لـ ID (غير موجود في قاعدة البيانات بعد)
        this.title = title;
        this.content = content;
        this.audioPath = ""; // القيمة الافتراضية للمسار الصوتي
    }

    // 2. بنية لاسترجاع ملاحظة موجودة (مع ID)
    public Note(int id, String title, String content) {
        this.id = id; // استخدام الـ ID المُمرر
        this.title = title;
        this.content = content;
        this.audioPath = ""; // القيمة الافتراضية للمسار الصوتي
    }

    // 3. بنية لدعم المسار الصوتي (مع ID)
    public Note(int id, String title, String content, String audioPath) {
        this.id = id; // استخدام الـ ID المُمرر
        this.title = title;
        this.content = content;
        this.audioPath = audioPath; // استخدام المسار الصوتي المُمرر
    }








    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
