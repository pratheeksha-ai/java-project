import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final Connection connection;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    public void addTransaction(Transaction transaction) throws LibraryException {
        String sql = "INSERT INTO transactions (isbn, member_id, borrow_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getIsbn());
            stmt.setString(2, transaction.getMemberId());
            stmt.setTimestamp(3, Timestamp.valueOf(transaction.getBorrowDate()));
            stmt.setString(4, transaction.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error adding transaction", e);
        }
    }

    public void updateReturnDate(String isbn, String memberId, LocalDateTime returnDate) throws LibraryException {
        String sql = "UPDATE transactions SET return_date = ?, status = 'returned' WHERE isbn = ? AND member_id = ? AND status = 'borrowed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(returnDate));
            stmt.setString(2, isbn);
            stmt.setString(3, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating return date", e);
        }
    }

    public boolean isBorrowedByMember(String isbn, String memberId) throws LibraryException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE isbn = ? AND member_id = ? AND status = 'borrowed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setString(2, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking borrow status", e);
        }
        return false;
    }

    public List<Transaction> getAllTransactions() throws LibraryException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY borrow_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LocalDateTime borrowDate = rs.getTimestamp("borrow_date").toLocalDateTime();
                LocalDateTime returnDate = rs.getTimestamp("return_date") != null ?
                    rs.getTimestamp("return_date").toLocalDateTime() : null;
                Transaction transaction = new Transaction(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("member_id"),
                    borrowDate,
                    returnDate,
                    rs.getString("status")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving transactions", e);
        }
        return transactions;
    }

    public List<Transaction> getBorrowedBooks() throws LibraryException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status = 'borrowed' ORDER BY borrow_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LocalDateTime borrowDate = rs.getTimestamp("borrow_date").toLocalDateTime();
                Transaction transaction = new Transaction(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("member_id"),
                    borrowDate,
                    null,
                    rs.getString("status")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving borrowed books", e);
        }
        return transactions;
    }
}
