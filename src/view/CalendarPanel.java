package view;

import controller.TaskManager;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * Kelas CalendarPanel untuk menampilkan kalender bulanan yang menampilkan
 * indikator tugas.
 * Jika tanggal tertentu memiliki tugas, tombol tanggal akan berwarna biru.
 */
public class CalendarPanel extends JPanel {
    private TaskManager taskManager;
    private JPanel gridPanel;
    private JLabel lblMonth;
    private JTextArea txtInfoHarian; // Area teks untuk info detail tugas per hari
    private int currentMonth, currentYear;

    public CalendarPanel(TaskManager tm) {
        this.taskManager = tm;
        setLayout(new BorderLayout());

        // --- Header Kalender (Navigasi Bulan & Tahun) ---
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        JPanel header = new JPanel();
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        lblMonth = new JLabel();
        lblMonth.setFont(new Font("Arial", Font.BOLD, 16));

        header.add(btnPrev);
        header.add(lblMonth);
        header.add(btnNext);
        add(header, BorderLayout.NORTH);

        // --- Grid Tanggal (Senin - Minggu) ---
        gridPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // Grid otomatis
        add(gridPanel, BorderLayout.CENTER);

        // --- Panel Info Tugas di Bawah (Detail Harian) ---
        txtInfoHarian = new JTextArea(5, 20);
        txtInfoHarian.setBorder(BorderFactory.createTitledBorder("Tasks for Selected Date:"));
        add(new JScrollPane(txtInfoHarian), BorderLayout.SOUTH);

        // --- Event Logika Tombol ---
        btnPrev.addActionListener(e -> changeMonth(-1)); // Mundur bulan
        btnNext.addActionListener(e -> changeMonth(1)); // Maju bulan

        refreshCalendar(); // Render awal
    }

    /**
     * Mengubah bulan saat ini.
     * 
     * @param amount Jumlah bulan yang ditambah/dikurang (misal 1 atau -1)
     */
    private void changeMonth(int amount) {
        currentMonth += amount;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        } // Pindah tahun maju
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        } // Pindah tahun mundur
        refreshCalendar();
    }

    /**
     * Menggambar ulang grid kalender dan mengecek tugas di setiap tanggal.
     */
    public void refreshCalendar() {
        gridPanel.removeAll();

        // Update Label Bulan
        String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        lblMonth.setText(months[currentMonth] + " " + currentYear);

        // Header Nama Hari
        String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            gridPanel.add(lbl);
        }

        // Kalkulasi Awal Bulan
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK); // 1 = Minggu
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Tambah Kotak Kosong sebelum tanggal 1
        for (int i = 1; i < startDay; i++) {
            gridPanel.add(new JLabel(""));
        }

        // Render Tombol untuk setiap Tanggal
        for (int day = 1; day <= maxDay; day++) {
            JButton btnDay = new JButton(String.valueOf(day));
            String dateString = String.format("%04d-%02d-%02d", currentYear, (currentMonth + 1), day);

            // Cek apakah ada tugas di tanggal ini?
            int taskCount = taskManager.getTasksByDate(dateString).size();
            if (taskCount > 0) {
                btnDay.setBackground(new Color(173, 216, 230)); // Biru muda jika ada tugas
                btnDay.setToolTipText(taskCount + " Tasks"); // Tooltip jumlah tugas
            } else {
                btnDay.setBackground(Color.WHITE);
            }

            // Aksi saat Tanggal Diklik: Tampilkan detail tugas
            btnDay.addActionListener(e -> showTasksForDate(dateString));
            gridPanel.add(btnDay);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Menampilkan daftar tugas pada area teks info untuk tanggal yang dipilih.
     */
    private void showTasksForDate(String date) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected: ").append(date).append("\n\n");
        for (Task t : taskManager.getTasksByDate(date)) {
            sb.append("• ").append(t.getTitle())
                    .append(" (").append(t.getPriority()).append(")\n");
        }
        if (taskManager.getTasksByDate(date).isEmpty()) {
            sb.append("(No tasks for this day)");
        }
        txtInfoHarian.setText(sb.toString());
    }
}