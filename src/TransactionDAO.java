import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    public void addTransaction(Transaction transaction) throws LibraryException {
        String sql = "INSERT INTO transactions (isbn, member_id, borrow_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getIsbn());
            stmt.setString(2, transaction.getMemberId());
            stmt.setTimestamp(3, transaction.getBorrowDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error adding transaction: " + e.getMessage());
        }
    }

    public Transaction getActiveTransaction(String isbn) throws LibraryException {
        String sql = "SELECT id, isbn, member_id, borrow_date, return_date FROM transactions WHERE isbn = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Transaction(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("member_id"),
                    rs.getTimestamp("borrow_date"),
                    rs.getTimestamp("return_date")
                );
            }
        } catch (SQLException e) {
            throw new LibraryException("Error getting active transaction: " + e.getMessage());
        }
        return null;
    }

    public void returnBook(String isbn, String memberId) throws LibraryException {
        Transaction transaction = getActiveTransaction(isbn);
        if (transaction == null) {
            throw new LibraryException("Book is not borrowed");
        }
        if (!transaction.getMemberId().equals(memberId)) {
            throw new LibraryException("Only the borrower can return the book");
        }
        String sql = "UPDATE transactions SET return_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, transaction.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error returning book: " + e.getMessage());
        }
    }

    public List<Transaction> getAllTransactions() throws LibraryException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, isbn, member_id, borrow_date, return_date FROM transactions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("member_id"),
                    rs.getTimestamp("borrow_date"),
                    rs.getTimestamp("return_date")
                ));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }

    public boolean hasActiveTransaction(String isbn) throws LibraryException {
        String sql = "SELECT id FROM transactions WHERE isbn = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking active transaction", e);
        }
    }

    public boolean memberHasActiveTransactions(String memberId) throws LibraryException {
        String sql = "SELECT id FROM transactions WHERE member_id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking member transactions", e);
        }
    }

    public void deleteTransactionsByISBN(String isbn) throws LibraryException {
        String sql = "DELETE FROM transactions WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting transactions for book", e);
        }
    }

    public void deleteTransactionsByMemberId(String memberId) throws LibraryException {
        String sql = "DELETE FROM transactions WHERE member_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting transactions for member", e);
        }
    }
}
