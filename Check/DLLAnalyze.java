package Check;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DLLAnalyze {

    /**
     * Метод для анализа загруженных DLL файлов в указанном процессе.
     * @param processName Имя процесса, который нужно проверить.
     * @return Строка с результатами проверки загруженных DLL.
     * @throws IOException Если происходит ошибка ввода-вывода.
     */
    public String analyze(String processName) throws IOException {
        StringBuilder result = new StringBuilder();

        String command = String.format("tasklist /m /fi \"imagename eq %s\"", processName);

        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        boolean dllFound = false;

        while ((line = reader.readLine()) != null) {
            if (line.contains(".dll")) {
                result.append(line.trim()).append("\n");
                dllFound = true;
            }
        }

        if (!dllFound) {
            result.append("Нет загруженных DLL для процесса ").append(processName).append(".\n");
        }

        reader.close();
        return result.toString();
    }
}
