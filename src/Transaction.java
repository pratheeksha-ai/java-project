import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String isbn;
    private String memberId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;

    public Transaction(int id, String isbn, String memberId, LocalDateTime borrowDate, LocalDateTime returnDate, String status) {
        this.id = id;
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
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

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}
