public class Member {
    private String name;
    private String memberId;
    private String email;

    public Member(String name, String memberId, String email) throws LibraryException {
        if (memberId == null || !memberId.matches("^\\d+$")) {
            throw new LibraryException("Member ID must contain only numbers");
        }
        
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new LibraryException("Invalid email format");
        }
        
        this.name = name;
        this.memberId = memberId;
        this.email = email;
    }

    Member(String name, String memberId, String email, boolean skipValidation) {
        this.name = name;
        this.memberId = memberId;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", memberId='" + memberId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
