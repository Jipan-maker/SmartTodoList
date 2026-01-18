# Smart Todo List

Smart Todo List adalah aplikasi desktop berbasis Java Swing yang dirancang untuk membantu Anda mengelola tugas dengan efisien. Aplikasi ini memiliki antarmuka (UI) bertema gelap yang modern dengan dashboard untuk statistik cepat dan kemampuan edit/hapus yang "pintar".

## Fitur

- **Dashboard**: Statistik visual untuk tugas Hari Ini (Today), Terjadwal (Scheduled), Semua (All), dan Ditandai (Flagged).
- **Manajemen Tugas**: Tambah, edit, dan hapus tugas dengan mudah.
- **Aksi Pintar (Smart Actions)**:
  - **Smart Edit**: Pilih tugas melalui checkbox atau sorotan baris untuk mengedit. Mencegah pemilihan ganda untuk menghindari kesalahan.
  - **Smart Delete**: Hapus banyak tugas sekaligus melalui checkbox atau hapus satu per satu melalui pemilihan baris.
- **Filter**: Filter tugas berdasarkan "Today", "Scheduled", "All", atau "Flagged".
- **Penyimpanan Data**: Tugas disimpan secara otomatis ke file `tasks.csv`.

## Prasyarat

- Java Development Kit (JDK) 8 atau lebih tinggi.

## Cara Menjalankan

1.  **Clone repository ini:**
    ```bash
    git clone https://github.com/Jipan-maker/SmartTodoList.git
    ```
2.  **Masuk ke direktori proyek:**
    ```bash
    cd SmartTodoList
    ```
3.  **Kompilasi kode sumber:**
    ```bash
    javac -d bin src/model/*.java src/controller/*.java src/view/*.java
    ```
    *(Catatan: Buat folder `bin` jika belum ada: `mkdir bin`)*

4.  **Jalankan aplikasi:**
    ```bash
    java -cp bin view.TodoApp
    ```

## Struktur Proyek

- `src/model`: Berisi model data (contoh: `Task.java`).
- `src/controller`: Berisi logika dan manajemen data (contoh: `TaskManager.java`).
- `src/view`: Berisi komponen antarmuka pengguna (contoh: `TodoApp.java`, `DashboardCard`).
