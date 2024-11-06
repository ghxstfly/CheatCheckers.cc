package org.example.Run;

import java.io.*;
import java.nio.file.*;
import java.util.jar.*;

public class ProcessHacker {

    public void start() {
        new Thread(() -> {
            try {
                String tempDir = System.getenv("TEMP") + "\\cheatchecker\\ProcessHacker";
                String exePath = tempDir + "\\ProcessHacker.exe"; // Путь к извлеченному файлу

                extractFileFromJar("Pcheck/ProcessHacker.exe", exePath);
                extractDirectoryFromJar("Pcheck/plugins", tempDir + "\\plugins");

                File exeFile = new File(exePath);
                if (exeFile.exists()) {
                    String[] command = {
                            "powershell", "-Command", "Start-Process", exePath, "-Verb", "RunAs"
                    };
                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    processBuilder.redirectErrorStream(true);

                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        System.out.println("ProcessHacker запущен с правами администратора.");
                    } else {
                        System.err.println("Ошибка при запуске ProcessHacker: код завершения " + exitCode);
                    }
                } else {
                    System.err.println("Файл не найден: " + exePath);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Ошибка при запуске ProcessHacker: " + e.getMessage());
            }
        }).start();
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

    private void extractDirectoryFromJar(String jarDirectoryPath, String destinationDirectoryPath) throws IOException {
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.endsWith(".jar")) {
            try (JarFile jarFile = new JarFile(jarPath)) {
                jarFile.stream()
                        .filter(entry -> entry.getName().startsWith(jarDirectoryPath))
                        .forEach(entry -> {
                            try {
                                Path outputPath = Paths.get(destinationDirectoryPath, entry.getName().substring(jarDirectoryPath.length()));
                                if (entry.isDirectory()) {
                                    Files.createDirectories(outputPath); // Создаём директорию
                                } else {
                                    Files.createDirectories(outputPath.getParent()); // Создаём родительскую директорию
                                    try (InputStream is = jarFile.getInputStream(entry);
                                         OutputStream os = new FileOutputStream(outputPath.toFile())) {
                                        byte[] buffer = new byte[1024];
                                        int bytesRead;
                                        while ((bytesRead = is.read(buffer)) != -1) {
                                            os.write(buffer, 0, bytesRead);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        } else {
            throw new IOException("Это не JAR файл: " + jarPath);
        }
    }
}
