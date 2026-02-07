import java.sql.Connection;
import java.util.List;

import java.time.LocalDateTime;

public class LibraryOperations {
    private Connection connection;
    private BookDAO bookDao;
    private MemberDAO memberDao;
    private TransactionDAO transactionDao;

    public LibraryOperations() throws LibraryException {
        this.connection = DBConnection.getConnection();
        if (this.connection == null) {
            throw new LibraryException("Failed to connect to database");
        }
        this.bookDao = new BookDAO(this.connection);
        this.memberDao = new MemberDAO(this.connection);
        this.transactionDao = new TransactionDAO(this.connection);
    }

    public void addBook(Book book) throws LibraryException {
        bookDao.addBook(book);
    }

    public void addMember(Member member) throws LibraryException {
        memberDao.addMember(member);
    }

    public List<Book> getAllBooks() throws LibraryException {
        return bookDao.getAllBooks();
    }

    public List<Member> getAllMembers() throws LibraryException {
        return memberDao.getAllMembers();
    }

    public void borrowBook(String isbn, String memberId) throws LibraryException {
        if (!memberDao.memberExists(memberId)) {
            throw new LibraryException("Member not found: " + memberId);
        }
        if (!bookDao.isBookAvailable(isbn)) {
            throw new LibraryException("Book is not available");
        }
        bookDao.setAvailability(isbn, false);
        // Record transaction
        Transaction transaction = new Transaction(0, isbn, memberId, LocalDateTime.now(), null, "borrowed");
        transactionDao.addTransaction(transaction);
    }

    public void returnBook(String isbn, String memberId) throws LibraryException {
        if (!memberDao.memberExists(memberId)) {
            throw new LibraryException("Member not found: " + memberId);
        }
        if (!transactionDao.isBorrowedByMember(isbn, memberId)) {
            throw new LibraryException("Book not borrowed by this member");
        }
        bookDao.setAvailability(isbn, true);
        // Update transaction with return date
        transactionDao.updateReturnDate(isbn, memberId, LocalDateTime.now());
    }

    public void deleteBook(String isbn) throws LibraryException {
        bookDao.deleteBook(isbn);
    }

    public void deleteMember(String memberId) throws LibraryException {
        memberDao.deleteMember(memberId);
    }

    public int getTotalBooks() throws LibraryException {
        return bookDao.getTotalBooks();
    }

    public int getAvailableBooks() throws LibraryException {
        return bookDao.getAvailableBooks();
    }

    public int getTotalMembers() throws LibraryException {
        return memberDao.getTotalMembers();
    }

    public List<Transaction> getAllTransactions() throws LibraryException {
        return transactionDao.getAllTransactions();
    }

    public List<Transaction> getBorrowedBooks() throws LibraryException {
        return transactionDao.getBorrowedBooks();
    }
}
