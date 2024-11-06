package org.example.Run;

import java.io.*;
import java.nio.file.*;
import java.util.jar.*;

public class Runthing {

    public void runEverything() {
        String tempDir = System.getenv("TEMP") + "\\cheatchecker\\everything"; // Путь к папке TEMP
        String exePath = tempDir + "\\everything.exe";  // Путь к исполняемому файлу

        try {
            extractFileFromJar("everything/everything.exe", exePath);

            File exeFile = new File(exePath);
            if (exeFile.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder(exePath);
                Process process = processBuilder.start();

                Thread processMonitorThread = new Thread(() -> {
                    try {
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.out.println("Ошибка при запуске программы: код завершения " + exitCode);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                });

                processMonitorThread.setDaemon(true);
                processMonitorThread.start();
            } else {
                System.err.println("Файл не найден: " + exePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractFileFromJar(String jarFilePath, String destinationPath) throws IOException {
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        if (jarPath.endsWith(".jar")) {
            try (JarFile jarFile = new JarFile(jarPath)) {
                JarEntry entry = jarFile.getJarEntry(jarFilePath);
                if (entry != null) {
                    Path outputPath = Paths.get(destinationPath);
                    Files.createDirectories(outputPath.getParent());

                    try (InputStream is = jarFile.getInputStream(entry);
                         OutputStream os = new FileOutputStream(outputPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                } else {
                    throw new FileNotFoundException("Файл не найден в JAR: " + jarFilePath);
                }
            }
        } else {
            throw new IOException("Это не JAR файл: " + jarPath);
        }
    }
}
