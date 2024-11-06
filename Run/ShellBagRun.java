package Run;

import java.io.IOException;

public class ShellBagRun {
    public void runShellBagAnalyzer() {
        String pathToExe = "shell/shellbag_analyzer_cleaner.exe";

        String[] command = {
                "powershell", "-Command", "Start-Process", pathToExe, "-Verb", "RunAs"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
