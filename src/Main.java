import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Library Management System Started!");

        try {
            LibraryOperations libraryOps = new LibraryOperations();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nLibrary Management System Menu:");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. View All Books");
                System.out.println("4. View All Members");
                System.out.println("5. Borrow Book");
                System.out.println("6. Return Book");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter book title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter book author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter book ISBN: ");
                        String isbn = scanner.nextLine();
                        Book book = new Book(title, author, isbn);
                        libraryOps.addBook(book);
                        System.out.println("Book added successfully!");
                        break;
                    case 2:
                        System.out.print("Enter member name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter member ID: ");
                        String memberId = scanner.nextLine();
                        System.out.print("Enter member email: ");
                        String email = scanner.nextLine();
                        Member member = new Member(name, memberId, email);
                        libraryOps.addMember(member);
                        System.out.println("Member added successfully!");
                        break;
                    case 3:
                        List<Book> books = libraryOps.getAllBooks();
                        System.out.println("All Books:");
                        for (Book b : books) {
                            System.out.println(b);
                        }
                        break;
                    case 4:
                        List<Member> members = libraryOps.getAllMembers();
                        System.out.println("All Members:");
                        for (Member m : members) {
                            System.out.println(m);
                        }
                        break;
                    case 5:
                        System.out.print("Enter ISBN to borrow: ");
                        String borrowIsbn = scanner.nextLine();
                        System.out.print("Enter member ID: ");
                        String borrowMemberId = scanner.nextLine();
                        libraryOps.borrowBook(borrowIsbn, borrowMemberId);
                        System.out.println("Book borrowed successfully!");
                        break;
                    case 6:
                        System.out.print("Enter ISBN to return: ");
                        String returnIsbn = scanner.nextLine();
                        libraryOps.returnBook(returnIsbn);
                        System.out.println("Book returned successfully!");
                        break;
                    case 7:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (LibraryException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
