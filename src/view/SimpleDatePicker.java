package view;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * Kelas SimpleDatePicker adalah komponen pemilihan tanggal sederhana.
 * Mirip dengan DatePickerDialog tetapi lebih simpel, digunakan untuk memilih
 * tanggal tugas.
 */
public class SimpleDatePicker extends JDialog {
    private String selectedDate = ""; // Menyimpan tanggal hasil pilihan
    private JLabel lblMonthYear;
    private JPanel pnlDays;
    private Calendar cal; // Calendar untuk logika tanggal

    // Konstruktor menerima parent Window agar bisa muncul diatas JFrame/Dialog lain
    public SimpleDatePicker(Window parent) {
        super(parent, "Pilih Tanggal", ModalityType.APPLICATION_MODAL);
        setSize(300, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        cal = Calendar.getInstance(); // Ambil waktu saat ini

        // --- Header (Navigasi Untuk Bulan) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(50, 100, 200));

        JButton btnPrev = createButton("<");
        JButton btnNext = createButton(">");
        lblMonthYear = new JLabel("", JLabel.CENTER);
        lblMonthYear.setForeground(Color.WHITE);
        lblMonthYear.setFont(new Font("Arial", Font.BOLD, 14));

        // Event Tombol Untuk Bulan
        btnPrev.addActionListener(e -> {
            cal.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        btnNext.addActionListener(e -> {
            cal.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonthYear, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        // --- Grid Hari ---
        pnlDays = new JPanel(new GridLayout(0, 7, 2, 2));
        pnlDays.setBackground(Color.WHITE);
        pnlDays.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(header, BorderLayout.NORTH);
        add(pnlDays, BorderLayout.CENTER);

        updateCalendar(); // Tampilkan tanggal awal
    }

    /**
     * Membuat tombol dengan style yang seragam.
     */
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(50, 100, 200));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    /**
     * Memperbarui tampilan grid tombol tanggal agar sesuai dengan bulan yang aktif.
     */
    private void updateCalendar() {
        pnlDays.removeAll();
        String[] months = { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September",
                "Oktober", "November", "Desember" };
        lblMonthYear.setText(months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR));

        String[] days = { "M", "S", "S", "R", "K", "J", "S" };
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            pnlDays.add(lbl);
        }

        Calendar temp = (Calendar) cal.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int startDay = temp.get(Calendar.DAY_OF_WEEK);
        int maxDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Spacer untuk hari kosong di awal bulan
        for (int i = 1; i < startDay; i++)
            pnlDays.add(new JLabel(""));

        // Tombol Untuk Tanggal 1 - Akhir
        for (int day = 1; day <= maxDay; day++) {
            final int d = day;
            JButton btn = new JButton(String.valueOf(d));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.setFont(new Font("Arial", Font.PLAIN, 11));

            // Aksi pada saat tanggal dipilih
            btn.addActionListener(e -> {
                // Format tanggal YYYY-MM-DD
                selectedDate = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
                        d);
                dispose(); // Tutup
            });
            pnlDays.add(btn);
        }
        pnlDays.revalidate();
        pnlDays.repaint();
    }

    /**
     * Method statis untuk mempermudah pemanggilan dialog dan langsung dapat hasil
     * string tanggal.
     */
    public static String showDialog(Window parent) {
        SimpleDatePicker picker = new SimpleDatePicker(parent);
        picker.setVisible(true);
        return picker.selectedDate;
    }
}