package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Kelas DatePickerDialog adalah dialog kustom untuk memilih tanggal.
 * Menampilkan kalender interaktif mirip dengan date picker pada umumnya.
 */
public class DatePickerDialog extends JDialog {
    // Komponen UI
    private JLabel lblMonthYear;
    private JPanel pnlCalendar;
    private Calendar currentCalendar;

    // Variabel untuk menyimpan tanggal yang dipilih (Format: YYYY-MM-DD)
    private String selectedDate = "";

    public DatePickerDialog(Window parent) { // Parent window (bisa JFrame atau JDialog lain)
        super(parent, "Pilih Tanggal", ModalityType.APPLICATION_MODAL); // Modal agar user fokus ke sini
        setSize(400, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        currentCalendar = new GregorianCalendar();

        // --- 1. Header (Tombol Navigasi & Label Bulan) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlHeader.setBackground(new Color(51, 102, 255)); // Warna Biru Header

        JButton btnPrev = new JButton("<"); // Tombol bulan sebelumnya
        JButton btnNext = new JButton(">"); // Tombol bulan berikutnya
        lblMonthYear = new JLabel("", JLabel.CENTER);
        lblMonthYear.setForeground(Color.WHITE);
        lblMonthYear.setFont(new Font("Arial", Font.BOLD, 14));

        btnPrev.setBackground(Color.WHITE);
        btnNext.setBackground(Color.WHITE);

        pnlHeader.add(btnPrev, BorderLayout.WEST);
        pnlHeader.add(lblMonthYear, BorderLayout.CENTER);
        pnlHeader.add(btnNext, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Body (Grid Hari-Hari) ---
        pnlCalendar = new JPanel(new GridLayout(0, 7, 2, 2)); // Grid 7 kolom (Senin-Minggu)
        pnlCalendar.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnlCalendar, BorderLayout.CENTER);

        // Action Listeners (Logika Tombol Navigasi)
        btnPrev.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, -1); // Kurangi 1 bulan
            updateCalendar();
        });

        btnNext.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, 1); // Tambah 1 bulan
            updateCalendar();
        });

        updateCalendar(); // Tampilkan kalender awal
    }

    /**
     * Memperbarui tampilan kalender berdasarkan bulan dan tahun saat ini.
     */
    private void updateCalendar() {
        pnlCalendar.removeAll(); // Bersihkan isi lama

        // Header nama hari
        String[] days = { "M", "S", "S", "R", "K", "J", "S" };
        for (String day : days) {
            JLabel lblDay = new JLabel(day, JLabel.CENTER);
            lblDay.setFont(new Font("Arial", Font.BOLD, 12));
            pnlCalendar.add(lblDay);
        }

        // Kloning kalender agar tidak merusak tanggal asli saat kalkulasi
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1); // Set ke tanggal 1

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // Hari apa tanggal 1 dimulai?
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // Ada berapa hari bulan ini?

        // Tambahkan spasi kosong sebelum tanggal 1
        for (int i = 1; i < firstDayOfWeek; i++) {
            pnlCalendar.add(new JLabel(""));
        }

        // Tambahkan tombol untuk setiap tanggal (1 sampai 30/31)
        for (int i = 1; i <= maxDay; i++) {
            JButton btn = new JButton(String.valueOf(i));
            btn.setMargin(new Insets(1, 1, 1, 1));
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);

            final int day = i;
            // Aksi saat tanggal dipilih
            btn.addActionListener(e -> {
                // Simpan tanggal dalam format: D-M-YYYY (akan di ubah di TodoApp)
                selectedDate = day + "-" + (currentCalendar.get(Calendar.MONTH) + 1) +
                        "-" + currentCalendar.get(Calendar.YEAR);
                dispose(); // Tutup dialog setelah memilih
            });

            pnlCalendar.add(btn);
        }

        // Update Label Header (Contoh: "Januari 2026")
        Locale indo = new Locale("id", "ID");
        String monthName = currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, indo);
        int year = currentCalendar.get(Calendar.YEAR);
        lblMonthYear.setText(monthName + " " + year);

        pnlCalendar.revalidate(); // Refresh layout panel
        pnlCalendar.repaint();
    }

    /**
     * Mengambil tanggal yang telah dipilih user.
     * 
     * @return String tanggal format D-M-YYYY atau kosong jika batal.
     */
    public String getSelectedDate() {
        return selectedDate;
    }
}