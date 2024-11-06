package Run;

import java.io.IOException;

public class TraceRun {

    public void start() {
        String pathToExe = "JournalTrace\\JournalTrace.exe";

        ProcessBuilder processBuilder = new ProcessBuilder(pathToExe);
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
