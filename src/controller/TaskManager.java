package controller;

import model.Task;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kelas TaskManager (Controller)
 * Mengelola logika penyimpanan data, filtering, dan pencarian tugas.
 */
public class TaskManager {
    private List<Task> taskList;
    private static final String FILE_NAME = "tasks.csv"; // Lokasi file penyimpanan

    public TaskManager() {
        this.taskList = new ArrayList<>();
        loadTasks(); // Load data saat aplikasi dibuka
    }

    // --- CRUD METHODS ---

    /**
     * FITUR BARU: Mengedit tugas yang sudah ada.
     */
    public void editTask(int index, String title, String desc, String date, String time, String priority,
            boolean reminder, boolean isFlagged) {
        if (index >= 0 && index < taskList.size()) {
            Task t = taskList.get(index);
            // Update data dalam objek yang sama
            t.setTitle(title);
            t.setDescription(desc);
            t.setDate(date);
            t.setTime(time);
            t.setPriority(priority);
            t.setReminder(reminder);
            t.setFlagged(isFlagged);

            saveTasks(); // Simpan perubahan ke file
        }
    }

    public void addTask(String title, String desc, String date, String time, String priority, boolean reminder,
            boolean isFlagged) {
        taskList.add(new Task(title, desc, date, time, priority, reminder, isFlagged));
        saveTasks(); // Auto-save
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < taskList.size()) {
            taskList.remove(index);
            saveTasks(); // Auto-save
        }
    }

    public void deleteTasks(List<Task> tasksToDelete) {
        // Hapus semua tugas yang ada di list tasksToDelete dari database utama
        taskList.removeAll(tasksToDelete);
        saveTasks(); // Simpan perubahan
    }

    /**
     * PENTING: Panggil method ini dari UI jika user mencentang checkbox "Selesai"
     * atau mengubah flag, agar perubahan tersimpan ke file.
     */
    public void updateTaskStatus() {
        saveTasks();
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    // --- METODE PENDUKUNG VIEW (DASHBOARD & CALENDAR) ---

    /**
     * DIGUNAKAN OLEH: CalendarPanel.java
     * Mengambil tugas berdasarkan tanggal spesifik (YYYY-MM-DD).
     */
    public List<Task> getTasksByDate(String dateCari) {
        return taskList.stream()
                .filter(t -> t.getDate() != null && t.getDate().equals(dateCari))
                .collect(Collectors.toList());
    }

    /**
     * DIGUNAKAN OLEH: TodoApp.java (Sidebar Filter)
     * Mengambil daftar tugas berdasarkan kategori menu.
     */
    public List<Task> getTasksByFilter(String filterType) {
        String today = LocalDate.now().toString();

        switch (filterType) {
            case "Hari Ini":
            case "Today":
                return taskList.stream()
                        .filter(t -> t.getDate().equals(today) && !t.isCompleted())
                        .collect(Collectors.toList());
            case "Terjadwal":
            case "Scheduled":
                return taskList.stream()
                        .filter(t -> !t.getDate().isEmpty() && !t.isCompleted())
                        .collect(Collectors.toList());
            case "Dibenderai":
            case "Flagged":
            case "Labeled":
                return taskList.stream()
                        .filter(t -> t.isFlagged() && !t.isCompleted())
                        .collect(Collectors.toList());
            case "Selesai":
            case "Finished":
                return taskList.stream()
                        .filter(Task::isCompleted)
                        .collect(Collectors.toList());
            case "Semua":
            case "All":
            default:
                return taskList.stream()
                        .filter(t -> !t.isCompleted())
                        .collect(Collectors.toList());
        }
    }

    /**
     * DIGUNAKAN OLEH: TodoApp.java (Dashboard Cards)
     * Menghitung jumlah tugas untuk kartu statistik.
     */
    public int getTaskCount(String filterType) {
        // Kita gunakan logika yang sama dengan filter untuk konsistensi
        return getTasksByFilter(filterType).size();
    }

    // --- FILE I/O (PENYIMPANAN DATA) ---

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : taskList) {
                // Sanitasi data agar CSV tidak rusak
                String cleanTitle = task.getTitle().replace(";", "-").replace("\n", " ");
                String cleanDesc = task.getDescription().replace(";", "-").replace("\n", " ");
                String date = (task.getDate() == null) ? "" : task.getDate();
                String time = (task.getTime() == null) ? "" : task.getTime();

                // Format: Title;Desc;Date;Time;Priority;Reminder;Completed;Flagged
                String line = String.format("%s;%s;%s;%s;%s;%s;%s;%s",
                        cleanTitle, cleanDesc, date, time,
                        task.getPriority(), task.hasReminder(),
                        task.isCompleted(), task.isFlagged());

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan tasks.csv: " + e.getMessage());
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue; // Skip baris kosong

                String[] parts = line.split(";");
                if (parts.length >= 7) {
                    String title = parts[0];
                    String desc = parts[1];
                    String date = parts[2];
                    String time = parts[3];
                    String priority = parts[4];
                    boolean reminder = Boolean.parseBoolean(parts[5]);
                    boolean completed = Boolean.parseBoolean(parts[6]);

                    boolean flagged = false;
                    if (parts.length >= 8) {
                        flagged = Boolean.parseBoolean(parts[7]);
                    }

                    Task t = new Task(title, desc, date, time, priority, reminder, flagged);
                    t.setCompleted(completed);

                    taskList.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}