# Library Management System

A complete Java-based Library Management System with MySQL database and professional web UI for managing books and members.

## Features

✅ **Book Management**
- Add books with unique ISBN
- View all books with availability status
- Delete books
- Borrow books (only if member exists)
- Return books

✅ **Member Management**
- Add members with unique Member ID and Email
- View all members
- Delete members

✅ **Data Validation**
- ISBN must be unique (no duplicate books)
- Member ID must be unique (no duplicate members)
- Email must be unique (each email registers once)

✅ **Web Interface**
- Professional single-page application (SPA)
- Real-time notifications
- Responsive design

## Quick Start

### Prerequisites
- Java 8+
- MySQL 5.7+
- MySQL Connector JAR (included in `lib/`)

### Setup

1. **Create the database:**
```bash
mysql -u root -p'Nns22bc030@' < create_db.sql
```

2. **Compile Java files:**
```powershell
cd src
javac -cp ".;../lib/mysql-connector-j-9.6.0.jar" *.java
```

### Run Web Server (Recommended)

```powershell
cd src
java -cp ".;../lib/mysql-connector-j-9.6.0.jar;." WebServer
```

Then open: **http://localhost:8080**

### Run CLI

```powershell
cd src
java -cp ".;../lib/mysql-connector-j-9.6.0.jar" Main
```

## Project Structure

```
LibraryManagementSystem/
├── src/
│   ├── Book.java              # Book model
│   ├── Member.java            # Member model
│   ├── BookDAO.java           # Book data access
│   ├── MemberDAO.java         # Member data access
│   ├── LibraryOperations.java # Core operations
│   ├── LibraryException.java  # Custom exceptions
│   ├── DBConnection.java      # Database connection
│   ├── WebServer.java         # Embedded HTTP server
│   ├── Main.java              # CLI application
│   └── TestLibrary.java       # Integration tests
├── web/
│   ├── index.html             # Frontend SPA
│   ├── app.js                 # JavaScript logic
│   └── styles.css             # Styling
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── create_db.sql              # Database schema
├── run.bat                    # One-click Windows launcher
└── README.md
```

## Configuration

Default database credentials (in `src/DBConnection.java`):
- **Host:** localhost:3306
- **Database:** library_db
- **User:** root
- **Password:** Nns22bc030@

You can override these with environment variables:
- `DB_URL` - JDBC connection string
- `DB_USER` - Database username
- `DB_PASS` - Database password

## Technology Stack

- **Backend:** Java 8+
- **Database:** MySQL
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Server:** Built-in Java HttpServer (com.sun.net.httpserver)

## License

MIT
