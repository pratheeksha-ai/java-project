import java.sql.Timestamp;

public class Transaction {
    private int id;
    private String isbn;
    private String memberId;
    private Timestamp borrowDate;
    private Timestamp returnDate;

    public Transaction(String isbn, String memberId) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = new Timestamp(System.currentTimeMillis());
        this.returnDate = null;
    }

    // Constructor for existing transactions
    public Transaction(int id, String isbn, String memberId, Timestamp borrowDate, Timestamp returnDate) {
        this.id = id;
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
