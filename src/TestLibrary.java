import java.util.List;

public class TestLibrary {
    public static void main(String[] args) {
        try {
            LibraryOperations ops = new LibraryOperations();

            // Add sample book and member
            Book book = new Book("The Hobbit", "J.R.R. Tolkien", "ISBN-001");
            Member member = new Member("Alice", "M001", "alice@example.com");

            ops.addBook(book);
            ops.addMember(member);

            System.out.println("Added book and member.");

            List<Book> books = ops.getAllBooks();
            System.out.println("Books in library:");
            for (Book b : books) {
                System.out.println(b);
            }

            // Borrow the book
            System.out.println("Borrowing ISBN-001...");
            ops.borrowBook("ISBN-001", "M001");
            System.out.println("Borrowed.");

            // Check availability
            books = ops.getAllBooks();
            System.out.println("Books after borrowing:");
            for (Book b : books) {
                System.out.println(b);
            }

            // Return the book
            System.out.println("Returning ISBN-001...");
            ops.returnBook("ISBN-001");
            System.out.println("Returned.");

            books = ops.getAllBooks();
            System.out.println("Books after return:");
            for (Book b : books) {
                System.out.println(b);
            }

        } catch (LibraryException e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
