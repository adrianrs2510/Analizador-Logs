import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AnalizadorLog {

    private Map<String, Integer> contadorLogs = new HashMap<>();
    private Map<String, Integer> mensajesError = new HashMap<>();

    public AnalizadorLog() {
        contadorLogs.put("INFO", 0);
        contadorLogs.put("WARNING", 0);
        contadorLogs.put("ERROR", 0);
    }

    public void leerArchivoLog(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                procesarLinea(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de log: " + e.getMessage());
        }
    }

    private void procesarLinea(String linea) {
        if (linea.contains("INFO")) {
            contadorLogs.put("INFO", contadorLogs.get("INFO") + 1);
        } else if (linea.contains("WARNING")) {
            contadorLogs.put("WARNING", contadorLogs.get("WARNING") + 1);
        } else if (linea.contains("ERROR")) {
            contadorLogs.put("ERROR", contadorLogs.get("ERROR") + 1);
            String mensajeError = extraerMensaje(linea);
            mensajesError.put(mensajeError, mensajesError.getOrDefault(mensajeError, 0) + 1);
        }
    }

    private String extraerMensaje(String linea) {
        return linea.substring(linea.indexOf("ERROR") + 6).trim();
    }

    public void generarInforme(String archivoInforme) {
        try (FileWriter writer = new FileWriter(archivoInforme)) {
            writer.write("Estadísticas del análisis de logs:\n\n");
            writer.write("Conteo de logs por nivel:\n");
            contadorLogs.forEach((nivel, conteo) -> {
                try {
                    writer.write(nivel + ": " + conteo + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.write("\nErrores más comunes:\n");
            mensajesError.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(5)
                    .forEach(entry -> {
                        try {
                            writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Informe generado: " + archivoInforme);
        } catch (IOException e) {
            System.err.println("Error al generar el informe: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AnalizadorLog analizador = new AnalizadorLog();
        analizador.leerArchivoLog("logs/log_ejemplo.log");
        analizador.generarInforme("informes/informe_log.txt");
    }
}
