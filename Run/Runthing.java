package Run;

import java.io.IOException;

public class Runthing {
    public void runEverything() {
        String pathToExe = "everything/everything.exe";
        ProcessBuilder processBuilder = new ProcessBuilder(pathToExe);

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
