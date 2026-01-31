import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    private final Connection connection;

    public MemberDAO(Connection connection) {
        this.connection = connection;
    }

    public void addMember(Member member) throws LibraryException {
        String sql = "INSERT INTO members (name, member_id, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getMemberId());
            stmt.setString(3, member.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("member_id")) {
                    throw new LibraryException("Member ID already exists: " + member.getMemberId());
                } else if (e.getMessage().contains("email")) {
                    throw new LibraryException("Email already registered: " + member.getEmail());
                }
            }
            throw new LibraryException("Error adding member", e);
        }
    }

    public List<Member> getAllMembers() throws LibraryException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Member member = new Member(rs.getString("name"), rs.getString("member_id"), rs.getString("email"));
                members.add(member);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving members", e);
        }
        return members;
    }

    public boolean memberExists(String memberId) throws LibraryException {
        String sql = "SELECT id FROM members WHERE member_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking member", e);
        }
    }

    public void deleteMember(String memberId) throws LibraryException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            int deleted = stmt.executeUpdate();
            if (deleted == 0) {
                throw new LibraryException("Member not found: " + memberId);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error deleting member", e);
        }
    }
}
