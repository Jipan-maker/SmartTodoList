package model;

/**
 * Kelas Task merepresentasikan sebuah tugas atau kegiatan.
 */
public class Task {
    private String title;
    private String description;
    private String date; // Format YYYY-MM-DD
    private String time; // Format HH:MM
    private String priority; // High, Medium, Low
    private boolean isFlagged; // Apakah tugas ini ditandai/dibenderai?

    // --- VARIABEL YANG TADINYA HILANG ---
    private boolean isCompleted; // <--- DITAMBAHKAN
    private boolean hasReminder; // <--- DITAMBAHKAN

    // Helper untuk parsing tanggal
    private java.time.LocalDateTime getDateTime() {
        if (date == null || date.isEmpty() || time == null || time.isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalDateTime.parse(date + "T" + time);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isOverdue() {
        if (isCompleted)
            return false; // Kalau sudah selesai, tidak overdue
        java.time.LocalDateTime taskTime = getDateTime();
        if (taskTime == null)
            return false;

        return taskTime.isBefore(java.time.LocalDateTime.now());
    }

    public Task(String title, String description, String date, String time, String priority, boolean hasReminder,
            boolean isFlagged) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.priority = priority;

        // Sekarang kode ini valid karena variabelnya sudah ada di atas
        this.hasReminder = hasReminder;
        this.isCompleted = false; // Default status pending
        this.isFlagged = isFlagged;
    }

    // --- GETTER (Untuk mengambil data) ---

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean hasReminder() {
        return hasReminder;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    // --- SETTER (Untuk mengubah data) ---

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    /**
     * Mengubah objek Task menjadi array objek untuk ditampilkan di tabel (JTable).
     */
    public Object[] toRowData() {
        String status = "Not Started";
        if (isCompleted) {
            status = "Completed";
        } else if (isOverdue()) {
            status = "Overdue";
        }
        return new Object[] { title, description, date, time, priority, status };
    }
}