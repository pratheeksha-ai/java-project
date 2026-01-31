import java.sql.Connection;
import java.util.List;

public class LibraryOperations {
    private Connection connection;
    private BookDAO bookDao;
    private MemberDAO memberDao;

    public LibraryOperations() throws LibraryException {
        this.connection = DBConnection.getConnection();
        if (this.connection == null) {
            throw new LibraryException("Failed to connect to database");
        }
        this.bookDao = new BookDAO(this.connection);
        this.memberDao = new MemberDAO(this.connection);
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
    }

    public void returnBook(String isbn) throws LibraryException {
        bookDao.setAvailability(isbn, true);
    }

    public void deleteBook(String isbn) throws LibraryException {
        bookDao.deleteBook(isbn);
    }

    public void deleteMember(String memberId) throws LibraryException {
        memberDao.deleteMember(memberId);
    }
}
