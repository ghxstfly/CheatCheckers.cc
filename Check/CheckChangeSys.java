package Check;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Date;
import javax.swing.JTextArea;

public class CheckChangeSys {
    private static final Path tlauncherDir = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", ".tlauncher", "legacy", "Minecraft", "game", "mods");
    private static final Path versiondir = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", ".tlauncher", "legacy", "Minecraft", "game", "libraries");
    private static final Path librariesdir = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", ".tlauncher", "legacy", "Minecraft", "game", "versions");
    private static final Path minecraftDir = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", ".minecraft");
    private static int hoursAgo = 3; // Время, за которое нужно проверить изменения

    public CheckChangeSys() {
        this.hoursAgo = hoursAgo;
    }

    public static void monitorDirectories(JTextArea console) throws IOException {
        checkDirectoryChanges(tlauncherDir, console);
        checkDirectoryChanges(minecraftDir, console);
        checkDirectoryChanges(versiondir, console);
        checkDirectoryChanges(librariesdir, console);
    }

    private static void checkDirectoryChanges(Path dir, JTextArea console) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            console.append("Директория не найдена или не является директорией: " + dir + "\n");
            return;
        }

        Instant cutoffTime = Instant.now().minusSeconds(hoursAgo * 3600);

        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Instant fileTime = attrs.lastModifiedTime().toInstant();

                if (fileTime.isAfter(cutoffTime)) {
                    console.append("Измененный файл: " + file + " (время изменения: " + Date.from(fileTime) + ")\n");
                }

                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                console.append("Ошибка при доступе к файлу: " + file + " - " + exc.getMessage() + "\n");
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
