import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WebServer {
    private static final int PORT = 8080;
    private static final String WEB_ROOT = "..\\web"; // relative to src

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new RootHandler());
        server.setExecutor(null);
        System.out.println("Starting web server at http://localhost:" + PORT);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // handle CORS preflight
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            // add permissive CORS for browser frontend
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (path.startsWith("/api/")) {
                handleApi(exchange, path);
                return;
            }

            if (path.equals("/") || path.equals("")) {
                serveFile(exchange, WEB_ROOT + "/home.html", "text/html");
            } else {
                // serve static files
                String filePath = WEB_ROOT + path.replace('/', '\\');
                String contentType = guessContentType(filePath);
                serveFile(exchange, filePath, contentType);
            }
        }

        private void handleApi(HttpExchange exchange, String path) throws IOException {
            try {
                LibraryOperations ops = new LibraryOperations();
                String method = exchange.getRequestMethod();

                if (path.equals("/api/books") && method.equalsIgnoreCase("GET")) {
                    List<Book> books = ops.getAllBooks();
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < books.size(); i++) {
                        Book b = books.get(i);
                        sb.append("{")
                                .append("\"title\":\"").append(escape(b.getTitle())).append("\",")
                                .append("\"author\":\"").append(escape(b.getAuthor())).append("\",")
                                .append("\"isbn\":\"").append(escape(b.getIsbn())).append("\",")
                                .append("\"isAvailable\":").append(b.isAvailable())
                                .append("}");
                        if (i < books.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    sendResponse(exchange, 200, sb.toString(), "application/json");
                    return;
                }

                if (path.equals("/api/books") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String title = parseField(body, "title");
                    String author = parseField(body, "author");
                    String isbn = parseField(body, "isbn");
                    if (title == null || author == null || isbn == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing fields\"}", "application/json");
                        return;
                    }
                    ops.addBook(new Book(title, author, isbn));
                    sendResponse(exchange, 201, "{\"status\":\"ok\"}", "application/json");
                    return;
                }

                if (path.equals("/api/members") && method.equalsIgnoreCase("GET")) {
                    List<Member> members = ops.getAllMembers();
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < members.size(); i++) {
                        Member m = members.get(i);
                        sb.append("{")
                                .append("\"name\":\"").append(escape(m.getName())).append("\",")
                                .append("\"memberId\":\"").append(escape(m.getMemberId())).append("\",")
                                .append("\"email\":\"").append(escape(m.getEmail())).append("\"")
                                .append("}");
                        if (i < members.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    sendResponse(exchange, 200, sb.toString(), "application/json");
                    return;
                }

                if (path.equals("/api/members") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String name = parseField(body, "name");
                    String memberId = parseField(body, "memberId");
                    String email = parseField(body, "email");
                    if (name == null || memberId == null || email == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing fields\"}", "application/json");
                        return;
                    }
                    ops.addMember(new Member(name, memberId, email));
                    sendResponse(exchange, 201, "{\"status\":\"ok\"}", "application/json");
                    return;
                }

                if (path.equals("/api/borrow") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String isbn = parseField(body, "isbn");
                    String memberId = parseField(body, "member_id");
                    if (isbn == null || memberId == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing fields\"}", "application/json");
                        return;
                    }
                    ops.borrowBook(isbn, memberId);
                    sendResponse(exchange, 200, "{\"status\":\"borrowed\"}", "application/json");
                    return;
                }

                if (path.equals("/api/return") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String isbn = parseField(body, "isbn");
                    String memberId = parseField(body, "member_id");
                    if (isbn == null || memberId == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing isbn or member_id\"}", "application/json");
                        return;
                    }
                    ops.returnBook(isbn, memberId);
                    sendResponse(exchange, 200, "{\"status\":\"returned\"}", "application/json");
                    return;
                }

                if (path.equals("/api/books/delete") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String isbn = parseField(body, "isbn");
                    if (isbn == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing isbn\"}", "application/json");
                        return;
                    }
                    ops.deleteBook(isbn);
                    sendResponse(exchange, 200, "{\"status\":\"deleted\"}", "application/json");
                    return;
                }

                if (path.equals("/api/members/delete") && method.equalsIgnoreCase("POST")) {
                    String body = readBody(exchange);
                    String memberId = parseField(body, "memberId");
                    if (memberId == null) {
                        sendResponse(exchange, 400, "{\"error\":\"missing memberId\"}", "application/json");
                        return;
                    }
                    ops.deleteMember(memberId);
                    sendResponse(exchange, 200, "{\"status\":\"deleted\"}", "application/json");
                    return;
                }

                if (path.equals("/api/stats") && method.equalsIgnoreCase("GET")) {
                    int totalBooks = ops.getTotalBooks();
                    int availableBooks = ops.getAvailableBooks();
                    int totalMembers = ops.getTotalMembers();
                    String json = String.format("{\"totalBooks\":%d,\"availableBooks\":%d,\"totalMembers\":%d}", totalBooks, availableBooks, totalMembers);
                    sendResponse(exchange, 200, json, "application/json");
                    return;
                }

                if (path.equals("/api/transactions") && method.equalsIgnoreCase("GET")) {
                    List<Transaction> transactions = ops.getAllTransactions();
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < transactions.size(); i++) {
                        Transaction t = transactions.get(i);
                        sb.append("{")
                                .append("\"id\":").append(t.getId()).append(",")
                                .append("\"isbn\":\"").append(escape(t.getIsbn())).append("\",")
                                .append("\"memberId\":\"").append(escape(t.getMemberId())).append("\",")
                                .append("\"borrowDate\":\"").append(t.getBorrowDate()).append("\",")
                                .append("\"returnDate\":").append(t.getReturnDate() != null ? "\"" + t.getReturnDate() + "\"" : "null").append(",")
                                .append("\"status\":\"").append(escape(t.getStatus())).append("\"")
                                .append("}");
                        if (i < transactions.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    sendResponse(exchange, 200, sb.toString(), "application/json");
                    return;
                }

                if (path.equals("/api/borrowed") && method.equalsIgnoreCase("GET")) {
                    List<Transaction> borrowed = ops.getBorrowedBooks();
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < borrowed.size(); i++) {
                        Transaction t = borrowed.get(i);
                        sb.append("{")
                                .append("\"id\":").append(t.getId()).append(",")
                                .append("\"isbn\":\"").append(escape(t.getIsbn())).append("\",")
                                .append("\"memberId\":\"").append(escape(t.getMemberId())).append("\",")
                                .append("\"borrowDate\":\"").append(t.getBorrowDate()).append("\",")
                                .append("\"status\":\"").append(escape(t.getStatus())).append("\"")
                                .append("}");
                        if (i < borrowed.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    sendResponse(exchange, 200, sb.toString(), "application/json");
                    return;
                }

                sendResponse(exchange, 404, "{\"error\":\"not found\"}", "application/json");
            } catch (LibraryException e) {
                sendResponse(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}", "application/json");
            } catch (Exception ex) {
                sendResponse(exchange, 500, "{\"error\":\"internal error\"}", "application/json");
            }
        }

        private String guessContentType(String filePath) {
            if (filePath.endsWith(".js")) return "application/javascript";
            if (filePath.endsWith(".css")) return "text/css";
            if (filePath.endsWith(".html")) return "text/html";
            if (filePath.endsWith(".png")) return "image/png";
            return "application/octet-stream";
        }

        private void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
            Path p = Path.of(filePath).toAbsolutePath();
            if (!Files.exists(p) || Files.isDirectory(p)) {
                sendResponse(exchange, 404, "Not Found", "text/plain");
                return;
            }
            byte[] bytes = Files.readAllBytes(p);
            exchange.getResponseHeaders().add("Content-Type", contentType + "; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String readBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            return new String(is.readAllBytes(), "UTF-8");
        }

        private void sendResponse(HttpExchange exchange, int status, String body, String contentType) throws IOException {
            byte[] bytes = body.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", contentType + "; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String parseField(String json, String field) {
            if (json == null) return null;
            String pattern = "\"" + java.util.regex.Pattern.quote(field) + "\"\\s*:\\s*(\\\"((?:\\\\.|[^\\\"])*?)\\\"|([^,}\\n]+))";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                String quoted = m.group(2);
                String unquoted = m.group(3);
                if (quoted != null) {
                    // unescape simple escapes
                    return quoted.replaceAll("\\\\\\\"", "\"").replaceAll("\\\\\\\\", "\\\\");
                }
                if (unquoted != null) {
                    return unquoted.trim();
                }
            }
            return null;
        }

        private String escape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        }
    }
}
