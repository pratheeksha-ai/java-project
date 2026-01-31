import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public void addBook(Book book) throws LibraryException {
        String sql = "INSERT INTO books (title, author, isbn, is_available) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBoolean(4, book.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new LibraryException("ISBN already exists: " + book.getIsbn());
            }
            throw new LibraryException("Error adding book", e);
        }
    }

    public List<Book> getAllBooks() throws LibraryException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setAvailable(rs.getBoolean("is_available"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving books", e);
        }
        return books;
    }

    public boolean isBookAvailable(String isbn) throws LibraryException {
        String sql = "SELECT is_available FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_available");
                } else {
                    throw new LibraryException("Book not found: " + isbn);
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking book availability", e);
        }
    }

    public void setAvailability(String isbn, boolean available) throws LibraryException {
        String sql = "UPDATE books SET is_available = ? WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setString(2, isbn);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new LibraryException("No book updated (not found): " + isbn);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error updating book availability", e);
        }
    }

    public void deleteBook(String isbn) throws LibraryException {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            int deleted = stmt.executeUpdate();
            if (deleted == 0) {
                throw new LibraryException("Book not found: " + isbn);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error deleting book", e);
        }
    }
}
