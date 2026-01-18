# Smart Todo List

Smart Todo List is a Java Swing-based desktop application designed to help you manage your tasks efficiently. It features a modern, dark-themed UI with a dashboard for quick insights and "smart" editing/deleting capabilities.

## Features

- **Dashboard**: Visual statistics for Today, Scheduled, All, and Flagged tasks.
- **Task Management**: Add, edit, and delete tasks with ease.
- **Smart Actions**: 
  - **Smart Edit**: Select a task by checkbox or row highlight to edit. Refuses multiple selections to prevent errors.
  - **Smart Delete**: Bulk delete via checkboxes or single delete via row selection.
- **Filtering**: Filter tasks by "Today", "Scheduled", "All", or "Labeled".
- **Data Persistence**: Tasks are saved to `tasks.csv` automatically.

## Prerequisites

- Java Development Kit (JDK) 8 or higher.

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Jipan-maker/SmartTodoList.git
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd SmartTodoList
    ```
3.  **Compile the source code:**
    ```bash
    javac -d bin src/model/*.java src/controller/*.java src/view/*.java
    ```
    *(Note: Create a `bin` directory if it doesn't exist: `mkdir bin`)*

4.  **Run the application:**
    ```bash
    java -cp bin view.TodoApp
    ```

## Project Structure

- `src/model`: Contains data models (e.g., `Task.java`).
- `src/controller`: Contains logic and data management (e.g., `TaskManager.java`).
- `src/view`: Contains UI components (e.g., `TodoApp.java`, `DashboardCard`).

## Author

**Jipan-maker**
