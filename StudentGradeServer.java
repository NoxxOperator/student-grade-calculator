import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Student Grade Calculator backend.
 * Java performs validation, total/percentage calculation, grading, and pass/fail checks.
 * Uses only the Java standard library (Java 11+ recommended).
 */
public class StudentGradeServer {
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8765"));
    private static final String[] SUBJECT_IDS = {"dsgt", "cm", "os", "ads", "poa"};
    private static final String[] SUBJECT_NAMES = {"DSGT", "CM", "OS", "ADS", "POA"};

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/", StudentGradeServer::handleRequest);
        server.setExecutor(null);
        server.start();
        System.out.println("Student Grade Calculator is running at http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop the server.");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method) && "/".equals(path)) {
            Path htmlPath = Path.of("index.html");
            if (!Files.exists(htmlPath)) {
                sendText(exchange, 500, "text/plain; charset=UTF-8",
                        "Could not find index.html. Run this program from the project folder.");
                return;
            }
            byte[] page = Files.readAllBytes(htmlPath);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, page.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(page);
            }
            return;
        }

        if ("POST".equalsIgnoreCase(method) && "/calculate".equals(path)) {
            handleCalculation(exchange);
            return;
        }

        sendJson(exchange, 404, "{\"error\":\"Endpoint not found.\"}");
    }

    private static void handleCalculation(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = parseForm(body);
        String name = form.getOrDefault("studentName", "").trim();

        if (name.isEmpty()) {
            sendJson(exchange, 400, "{\"error\":\"Please enter the student's name.\"}");
            return;
        }
        if (name.length() > 80) {
            sendJson(exchange, 400, "{\"error\":\"Student name must be 80 characters or fewer.\"}");
            return;
        }

        int[] marks = new int[SUBJECT_IDS.length];
        int total = 0;
        boolean passed = true;
        for (int i = 0; i < SUBJECT_IDS.length; i++) {
            String raw = form.get(SUBJECT_IDS[i]);
            if (raw == null || raw.trim().isEmpty()) {
                sendJson(exchange, 400, "{\"error\":\"Enter marks for every subject.\"}");
                return;
            }
            try {
                marks[i] = Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, "{\"error\":\"Enter a whole number from 0 to 100 for every subject.\"}");
                return;
            }
            if (marks[i] < 0 || marks[i] > 100) {
                sendJson(exchange, 400, "{\"error\":\"Marks must be between 0 and 100 for every subject.\"}");
                return;
            }
            total += marks[i];
            if (marks[i] < 35) passed = false;
        }

        double percentage = total / 5.0;
        String grade;
        if (!passed || percentage < 50) grade = "F";
        else if (percentage >= 90) grade = "A+";
        else if (percentage >= 80) grade = "A";
        else if (percentage >= 70) grade = "B";
        else if (percentage >= 60) grade = "C";
        else grade = "D";

        StringBuilder subjectResults = new StringBuilder("[");
        for (int i = 0; i < marks.length; i++) {
            if (i > 0) subjectResults.append(',');
            subjectResults.append("{\"subject\":\"").append(jsonEscape(SUBJECT_NAMES[i]))
                    .append("\",\"marks\":").append(marks[i]).append('}');
        }
        subjectResults.append(']');

        String json = "{\"name\":\"" + jsonEscape(name) + "\","
                + "\"total\":" + total + ","
                + "\"percentage\":" + String.format(java.util.Locale.US, "%.2f", percentage) + ","
                + "\"grade\":\"" + grade + "\","
                + "\"status\":\"" + (passed ? "PASS" : "FAIL") + "\","
                + "\"subjects\":" + subjectResults + "}";
        sendJson(exchange, 200, json);
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> values = new HashMap<>();
        if (body == null || body.isEmpty()) return values;
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            values.put(key, value);
        }
        return values;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String jsonEscape(String value) {
        StringBuilder out = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
            }
        }
        return out.toString();
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        sendText(exchange, status, "application/json; charset=UTF-8", json);
    }

    private static void sendText(HttpExchange exchange, int status, String contentType, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
