package view;

import controller.TaskManager;
import model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TodoApp extends JFrame {

    private TaskManager taskManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblCurrentView;
    private String currentFilter = "Today";

    // Variabel Global UI
    private JScrollPane scrollPane;
    private JPanel footerPanel;
    private JLabel lblTable;
    private JButton btnAdd, btnDelete, btnEdit;

    // Panel Kartu Statistik
    private DashboardCard cardToday, cardScheduled, cardAll, cardFlagged;

    // --- PALET WARNA ---
    private final Color COLOR_BG = new Color(18, 18, 24);
    private final Color COLOR_SIDEBAR = new Color(28, 28, 36);
    private final Color COLOR_TABLE_HEAD = new Color(35, 35, 45);
    private final Color COLOR_ROW_EVEN = new Color(28, 28, 36);
    private final Color COLOR_ROW_ODD = new Color(18, 18, 24);
    private final Color COLOR_TEXT = new Color(240, 240, 255);
    private final Color COLOR_TEXT_DIM = new Color(120, 120, 140);
    private final Color COLOR_HIGHLIGHT = new Color(37, 99, 235); // Biru Seleksi

    // --- WARNA KARTU ---
    private final Color COL_BLUE_1 = new Color(59, 130, 246);
    private final Color COL_BLUE_2 = new Color(37, 99, 235);
    private final Color COL_RED_1 = new Color(248, 113, 113);
    private final Color COL_RED_2 = new Color(220, 38, 38);
    private final Color COL_PURPLE_1 = new Color(167, 139, 250);
    private final Color COL_PURPLE_2 = new Color(124, 58, 237);
    private final Color COL_ORANGE_1 = new Color(251, 191, 36);
    private final Color COL_ORANGE_2 = new Color(217, 119, 6);

    public TodoApp() {
        taskManager = new TaskManager();

        setTitle("Smart Task Manager Pro");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logoLabel = new JLabel("Habit Tracker");
        logoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoLabel.setToolTipText("Back to Home");

        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHomeView();
            }
        });

        sidebarPanel.add(logoLabel);
        add(sidebarPanel, BorderLayout.WEST);

        // --- 2. KONTEN UTAMA ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setMaximumSize(new Dimension(3000, 60));

        lblCurrentView = new JLabel("Dashboard");
        lblCurrentView.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        lblCurrentView.setForeground(COLOR_TEXT);
        headerPanel.add(lblCurrentView, BorderLayout.WEST);

        JLabel lblDateNow = new JLabel(java.time.LocalDate.now().toString());
        lblDateNow.setForeground(COLOR_TEXT_DIM);
        lblDateNow.setFont(new Font("Consolas", Font.PLAIN, 14));
        headerPanel.add(lblDateNow, BorderLayout.EAST);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Kartu Statistik
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(COLOR_BG);
        statsPanel.setMaximumSize(new Dimension(3000, 140));

        cardToday = new DashboardCard("Today", "📅", COL_BLUE_1, COL_BLUE_2,
                () -> applyFilter("Today", "Task Today"));
        cardScheduled = new DashboardCard("Scheduled", "📆", COL_RED_1, COL_RED_2,
                () -> applyFilter("Scheduled", "Task Scheduled"));
        cardAll = new DashboardCard("All", "📥", COL_PURPLE_1, COL_PURPLE_2,
                () -> applyFilter("All", "All Task"));
        cardFlagged = new DashboardCard("Labeled", "🚩", COL_ORANGE_1, COL_ORANGE_2,
                () -> applyFilter("Labeled", "Labeled Task"));

        statsPanel.add(cardToday);
        statsPanel.add(cardScheduled);
        statsPanel.add(cardAll);
        statsPanel.add(cardFlagged);

        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(40));

        // --- TABEL ---
        lblTable = new JLabel("Your Task List");
        lblTable.setFont(new Font("Minion", Font.BOLD, 18));
        lblTable.setForeground(COLOR_TEXT);
        lblTable.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTable);
        contentPanel.add(Box.createVerticalStrut(15));

        String[] columns = { "", "Tittle", "Deadline", "Time", "Priority", "Status" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Kolom 0 adalah Boolean (Checkbox)
                if (columnIndex == 0)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Hanya kolom checkbox (0) yang bisa diklik langsung
                return column == 0;
            }
        };

        table = new JTable(tableModel);
        styleTable(table); // Styling Tabel agar warna menyatu

        // --- FITUR EDIT (DOUBLE CLICK) ---
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Jika Double Click
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    // Jika yang diklik bukan checkboxnya, buka menu edit
                    if (row != -1 && col != 0) {
                        editSelectedTask(row);
                    }
                }
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BG);

        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(20));

        // --- FOOTER ---
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(COLOR_BG);
        footerPanel.setMaximumSize(new Dimension(3000, 70));

        btnEdit = createRoundedButton("Edit", new Color(245, 158, 11)); // Orange/Kuning
        btnDelete = createRoundedButton("Delete", new Color(220, 38, 38));
        btnAdd = createRoundedButton("+ Add", new Color(37, 99, 235));

        footerPanel.add(btnEdit);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(btnDelete);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(btnAdd);

        contentPanel.add(footerPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> showTaskForm(null, -1));

        // PENTING: Tombol Hapus sekarang Pintar (Bisa hapus centang ATAU hapus sorotan)
        btnDelete.addActionListener(e -> smartDeleteAction());

        // Logika Edit Pintar
        btnEdit.addActionListener(e -> smartEditAction());

        // Init
        updateDashboardCounts();
        showHomeView();
        startAutoRefresh();
    }

    // --- LOGIKA UTAMA ---

    private void showHomeView() {
        lblCurrentView.setText(" To-Do List & Task Reminder ");
        updateDashboardCounts();

        if (lblTable != null)
            lblTable.setVisible(false);
        if (scrollPane != null)
            scrollPane.setVisible(false);

        if (footerPanel != null)
            footerPanel.setVisible(true);
        if (btnDelete != null)
            btnDelete.setVisible(false);
        if (btnEdit != null)
            btnEdit.setVisible(false); // Sembunyikan tombol Edit di Home
        if (btnAdd != null)
            btnAdd.setVisible(true);
    }

    private void applyFilter(String filter, String title) {
        lblCurrentView.setText(title);
        currentFilter = filter;

        if (lblTable != null)
            lblTable.setVisible(true);
        if (scrollPane != null)
            scrollPane.setVisible(true);
        if (footerPanel != null)
            footerPanel.setVisible(true);

        if (btnDelete != null)
            btnDelete.setVisible(true);
        if (btnEdit != null)
            btnEdit.setVisible(true); // Tampilkan tombol Edit di List View
        if (btnAdd != null)
            btnAdd.setVisible(true);

        loadTasksToTable(filter);
    }

    private void loadTasksToTable(String filter) {
        tableModel.setRowCount(0);
        List<Task> tasks = taskManager.getTasksByFilter(filter);
        for (Task t : tasks) {
            String status = "Not Started";
            if (t.isCompleted()) {
                status = "Finished";
            } else if (t.isOverdue()) {
                status = "Overdue";
            }

            // Default checkbox false (tidak dicentang)
            tableModel.addRow(new Object[] { false, t.getTitle(), t.getDate(), t.getTime(), t.getPriority(), status });
        }
        updateDashboardCounts();
    }

    // --- TIMER UNTUK AUTO REFRESH (Agar status Overdue muncul realtime) ---
    private void startAutoRefresh() {
        Timer timer = new Timer(60000, e -> { // Refresh setiap 1 menit
            // Hanya refresh jika user sedang tidak mengedit tabel (misal seleksi baris)
            if (!table.isEditing()) {
                loadTasksToTable(currentFilter);
            }
        });
        timer.start();
    }

    private void editSelectedTask(int row) {
        List<Task> currentList = taskManager.getTasksByFilter(currentFilter);
        Task t = currentList.get(row);
        int realIndex = taskManager.getAllTasks().indexOf(t);
        showTaskForm(t, realIndex);
    }

    // =========================================================================
    // LOGIKA EDIT PINTAR (SMART EDIT)
    // =========================================================================
    private void smartEditAction() {
        int targetRow = -1;
        int checkedCount = 0;

        // 1. Cek Checkbox
        for (int i = 0; i < table.getRowCount(); i++) {
            Boolean isChecked = (Boolean) table.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                targetRow = i;
                checkedCount++;
            }
        }

        if (checkedCount > 1) {
            JOptionPane.showMessageDialog(this, "Select only ONE Task to edit.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Jika tidak ada checkbox, cek sorotan (highlight)
        if (checkedCount == 0) {
            targetRow = table.getSelectedRow();
        }

        // 3. Eksekusi
        if (targetRow != -1) {
            editSelectedTask(targetRow);
        } else {
            JOptionPane.showMessageDialog(this, "Select the Task to be Edited (Check or Click the line).", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =========================================================================
    // LOGIKA HAPUS PINTAR (SMART DELETE)
    // =========================================================================
    private void smartDeleteAction() {
        List<Task> currentList = taskManager.getTasksByFilter(currentFilter);
        List<Task> tasksToDelete = new ArrayList<>();

        // 1. Cek Checkbox (Apakah ada yang dicentang?)
        for (int i = 0; i < table.getRowCount(); i++) {
            Boolean isChecked = (Boolean) table.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                if (i < currentList.size())
                    tasksToDelete.add(currentList.get(i));
            }
        }

        // 2. Jika TIDAK ada yang dicentang, cek Highlight/Sorotan (Hapus Satu Baris)
        if (tasksToDelete.isEmpty()) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tasksToDelete.add(currentList.get(selectedRow));
            }
        }

        // 3. Eksekusi Hapus
        if (tasksToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select the Task to be Deleted (Check or Click the line).", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String msg = tasksToDelete.size() > 1 ? "Delete " + tasksToDelete.size() + " Selected Task?"
                : "Delete This Task?";

        int confirm = JOptionPane.showConfirmDialog(this, msg, "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            taskManager.deleteTasks(tasksToDelete);
            loadTasksToTable(currentFilter);
        }
    }

    // =========================================================================
    // STYLING TABEL & CHECKBOX (MENYATUKAN WARNA)
    // =========================================================================
    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Atur lebar kolom Checkbox
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(0).setMinWidth(40);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                l.setBackground(COLOR_TABLE_HEAD);
                l.setForeground(new Color(200, 200, 200));
                l.setFont(new Font("Montserrat", Font.BOLD, 12));
                l.setBorder(new EmptyBorder(10, 10, 10, 10));
                return l;
            }
        });

        // 1. RENDERER UNTUK CHECKBOX (Supaya warna background menyatu dengan baris)
        table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
            private final JCheckBox checkBox = new JCheckBox();
            {
                checkBox.setHorizontalAlignment(JLabel.CENTER);
                checkBox.setOpaque(true); // Penting agar background terlihat
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (isSelected) {
                    checkBox.setBackground(COLOR_HIGHLIGHT); // Biru saat disorot
                } else {
                    // Selang-seling saat tidak disorot (Genap/Ganjil)
                    checkBox.setBackground(row % 2 == 0 ? COLOR_ROW_EVEN : COLOR_ROW_ODD);
                }
                checkBox.setSelected(value != null && (Boolean) value);
                return checkBox;
            }
        });

        // 2. RENDERER UNTUK TEKS (Standard)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                if (isSelected) {
                    c.setBackground(COLOR_HIGHLIGHT);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? COLOR_ROW_EVEN : COLOR_ROW_ODD);
                    c.setForeground(COLOR_TEXT);
                }
                return c;
            }
        });
    }

    // =========================================================================
    // FORM INPUT (SAMA SEPERTI SEBELUMNYA)
    // =========================================================================

    private void showTaskForm(Task taskToEdit, int taskIndex) {
        boolean isEditMode = (taskToEdit != null);
        String titleDialog = isEditMode ? "Edit Task" : "Add New Task";

        JDialog d = new JDialog(this, titleDialog, true);
        d.setSize(480, 600);
        d.setLayout(new GridBagLayout());
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(28, 28, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtTitle = createDarkInput();
        JTextArea txtDesc = new JTextArea(3, 20);
        txtDesc.setBackground(new Color(18, 18, 24));
        txtDesc.setForeground(Color.WHITE);
        txtDesc.setCaretColor(Color.WHITE);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 75)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel pnlDate = new JPanel(new BorderLayout(5, 0));
        pnlDate.setOpaque(false);
        JTextField txtDate = createDarkInput();
        txtDate.setEditable(false);
        txtDate.setText(java.time.LocalDate.now().toString());
        JButton btnDate = new JButton("📅");
        btnDate.setBackground(new Color(59, 130, 246));
        btnDate.setForeground(Color.WHITE);
        pnlDate.add(txtDate, BorderLayout.CENTER);
        pnlDate.add(btnDate, BorderLayout.EAST);

        List<String> timeList = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                timeList.add(String.format("%02d:%02d", h, m));
            }
        }
        JComboBox<String> cmbTime = new JComboBox<>(timeList.toArray(new String[0]));

        String[] priorities = { "High", "Medium", "Low" };
        JComboBox<String> cmbPriority = new JComboBox<>(priorities);

        JCheckBox chkReminder = new JCheckBox("Remind Me");
        chkReminder.setForeground(Color.WHITE);
        chkReminder.setOpaque(false);

        JCheckBox chkFlag = new JCheckBox("Labeled Sign");
        chkFlag.setForeground(new Color(251, 191, 36));
        chkFlag.setOpaque(false);

        if (isEditMode) {
            txtTitle.setText(taskToEdit.getTitle());
            txtDesc.setText(taskToEdit.getDescription());
            txtDate.setText(taskToEdit.getDate());
            cmbTime.setSelectedItem(taskToEdit.getTime());
            cmbPriority.setSelectedItem(taskToEdit.getPriority());
            chkReminder.setSelected(taskToEdit.hasReminder());
            chkFlag.setSelected(taskToEdit.isFlagged());
        }

        addInputRow(d, gbc, 0, "Title           :", txtTitle);
        addInputRow(d, gbc, 1, "Description     :", new JScrollPane(txtDesc));
        addInputRow(d, gbc, 2, "Deadline        :", pnlDate);
        addInputRow(d, gbc, 3, "Time            :", cmbTime);
        addInputRow(d, gbc, 4, "Priority        :", cmbPriority);

        gbc.gridx = 1;
        gbc.gridy = 5;
        d.add(chkReminder, gbc);
        gbc.gridy = 6;
        d.add(chkFlag, gbc);

        String btnText = isEditMode ? "Save Changes" : "Save New Task";
        JButton btnSave = createRoundedButton(btnText, new Color(16, 185, 129));
        gbc.gridy = 7;
        gbc.insets = new Insets(30, 10, 10, 10);
        d.add(btnSave, gbc);

        btnDate.addActionListener(e -> {
            DatePickerDialog picker = new DatePickerDialog(d);
            picker.setVisible(true);
            if (picker.getSelectedDate() != null && !picker.getSelectedDate().isEmpty()) {
                try {
                    String[] p = picker.getSelectedDate().split("-");
                    txtDate.setText(String.format("%04d-%02d-%02d", Integer.parseInt(p[2]), Integer.parseInt(p[1]),
                            Integer.parseInt(p[0])));
                } catch (Exception ex) {
                    txtDate.setText(picker.getSelectedDate());
                }
            }
        });

        btnSave.addActionListener(e -> {
            if (txtTitle.getText().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Task Title is Required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (isEditMode) {
                taskManager.editTask(taskIndex, txtTitle.getText(), txtDesc.getText(), txtDate.getText(),
                        (String) cmbTime.getSelectedItem(), (String) cmbPriority.getSelectedItem(),
                        chkReminder.isSelected(), chkFlag.isSelected());
            } else {
                taskManager.addTask(txtTitle.getText(), txtDesc.getText(), txtDate.getText(),
                        (String) cmbTime.getSelectedItem(), (String) cmbPriority.getSelectedItem(),
                        chkReminder.isSelected(), chkFlag.isSelected());
            }
            loadTasksToTable(currentFilter);
            d.dispose();
        });
        d.setVisible(true);
    }

    // --- Helper UI ---

    private void addInputRow(JDialog d, GridBagConstraints gbc, int y, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.3;
        JLabel l = new JLabel(label);
        l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("Minion", Font.BOLD, 13));
        d.add(l, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        d.add(comp, gbc);
    }

    private JTextField createDarkInput() {
        JTextField t = new JTextField();
        t.setBackground(new Color(18, 18, 24));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 75)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return t;
    }

    private JButton createRoundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class DashboardCard extends JPanel {
        private JLabel lblCount;
        private Color col1, col2;

        public DashboardCard(String title, String icon, Color c1, Color c2, Runnable onClick) {
            this.col1 = c1;
            this.col2 = c2;
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Montserrat", Font.PLAIN, 28));
            lblIcon.setForeground(new Color(255, 255, 255, 200));
            lblCount = new JLabel("0");
            lblCount.setFont(new Font("Montserrat", Font.BOLD, 40));
            lblCount.setForeground(Color.WHITE);
            top.add(lblIcon, BorderLayout.WEST);
            top.add(lblCount, BorderLayout.EAST);
            JLabel lblTitle = new JLabel(title.toUpperCase());
            lblTitle.setFont(new Font("Montserrat", Font.BOLD, 13));
            lblTitle.setForeground(new Color(255, 255, 255, 180));
            add(top, BorderLayout.CENTER);
            add(lblTitle, BorderLayout.SOUTH);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }
            });
        }

        public void setCount(int count) {
            lblCount.setText(String.valueOf(count));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, col1, getWidth(), getHeight(), col2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        }
    }

    private void updateDashboardCounts() {
        cardToday.setCount(taskManager.getTaskCount("Today"));
        cardScheduled.setCount(taskManager.getTaskCount("Scheduled"));
        cardAll.setCount(taskManager.getTaskCount("All"));
        cardFlagged.setCount(taskManager.getTaskCount("Flagged"));
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new TodoApp().setVisible(true));
    }
}