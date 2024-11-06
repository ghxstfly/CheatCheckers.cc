package Run;

import java.io.IOException;

public class ProcessHacker {

    public void start() {
        new Thread(() -> {
            try {
                String processHackerPath = "pcheck\\ProcessHacker.exe"; // Замените на нужный путь, если он другой

                String[] command = {
                        "powershell", "-Command", "Start-Process", processHackerPath, "-Verb", "RunAs"
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
            } catch (IOException | InterruptedException e) {
                System.err.println("Ошибка при запуске ProcessHacker: " + e.getMessage());
            }
        }).start();
    }
}
