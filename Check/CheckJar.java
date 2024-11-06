package Check;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CheckJar {
    private StringBuilder report;

    public CheckJar() {
        report = new StringBuilder();
    }

    public String checkJarFile(File jarFile) {
        if (jarFile == null || !jarFile.exists()) {
            return "Файл не существует или не выбран.\n";
        }

        try (JarFile jar = new JarFile(jarFile)) {
            jar.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .forEach(entry -> checkClass(entry, jar));
        } catch (IOException e) {
            report.append("Ошибка при открытии JAR файла: ").append(e.getMessage()).append("\n");
        }
        return report.length() > 0 ? report.toString() : "В JAR файле не найдено классов для проверки.\n";
    }

    private void checkClass(JarEntry entry, JarFile jar) {
        try (InputStream input = jar.getInputStream(entry)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            String classContent = new String(baos.toByteArray());
            if (classContent.contains("AxisAlignedBB;")) {
                report.append("Класс ").append(entry.getName()).append(" содержит импорт AABB.\n");
            }
            if (classContent.contains("+") || classContent.contains("-")) {
                report.append("Класс ").append(entry.getName()).append(" содержит символы + или -.\n");
            }
        } catch (IOException e) {
            report.append("Ошибка при чтении класса ").append(entry.getName()).append(": ").append(e.getMessage()).append("\n");
        }
    }
}
