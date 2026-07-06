package com.trafico.datos;

import com.trafico.errores.ErrorSistema;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;
import com.trafico.simulacion.InformeTrafico;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generador de Informes — exporta el estado del mapa y los resultados de la simulación
 * a archivos de texto con fecha y hora en el nombre.
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class GeneradorInforme {

    /**
     * Exporta el mapa vial completo a un archivo de texto con el formato
     * compatible con {@link CargadorMapa}.
     *
     * @param mapa        el mapa vial a exportar
     * @param carpetaDestino carpeta donde guardar el archivo
     * @throws ErrorSistema si no se puede escribir el archivo
     */
    public void exportarMapa(MapaVial mapa, String carpetaDestino) throws ErrorSistema {
        String nombreArchivo = carpetaDestino + "/mapa_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (PrintWriter escritor = new PrintWriter(new FileWriter(nombreArchivo))) {
            escritor.println("# Mapa Vial — Sistema de Gestión de Tráfico");
            escritor.println("# Generado: " + LocalDateTime.now());
            escritor.println("# Formato: INTERSECCION;id;nombre;x;y");
            escritor.println("# Formato: CALLE;origen;destino;distancia;velMax;categoria;dobleVia");
            escritor.println();

            escritor.println("# --- Intersecciones ---");
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                escritor.printf("INTERSECCION;%s;%s;%.1f;%.1f%n",
                    i.getId(), i.getNombre(), i.getX(), i.getY());
            }

            escritor.println();
            escritor.println("# --- Calles ---");
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                try {
                    for (Calle c : mapa.callesDesdePunto(i.getId())) {
                        escritor.printf("CALLE;%s;%s;%.0f;%.0f;%s;%b%n",
                            c.getOrigen().getId(), c.getDestino().getId(),
                            c.getDistancia(), c.getVelocidadMaxima(),
                            c.getCategoria(), c.isEsDobleVia());
                    }
                } catch (Exception ignored) {}
            }

            System.out.println("  ✓ Mapa exportado a: " + nombreArchivo);
        } catch (IOException e) {
            throw new ErrorSistema("No se pudo exportar el mapa: " + e.getMessage(), e);
        }
    }

    /**
     * Exporta el informe final de simulación a un archivo de texto.
     *
     * @param informe        el informe de la simulación
     * @param tickFinal      en qué tick terminó la simulación
     * @param totalVehiculos total de vehículos que participaron
     * @param totalLlegados  cuántos llegaron exitosamente
     * @param carpetaDestino carpeta donde guardar el archivo
     * @throws ErrorSistema si no se puede escribir el archivo
     */
    public void exportarInforme(InformeTrafico informe, int tickFinal,
                                 int totalVehiculos, int totalLlegados,
                                 String carpetaDestino) throws ErrorSistema {
        String nombreArchivo = carpetaDestino + "/informe_simulacion_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (PrintWriter escritor = new PrintWriter(new FileWriter(nombreArchivo))) {
            escritor.println(informe.generarTextoParaArchivo(tickFinal, totalVehiculos, totalLlegados));
            System.out.println("  ✓ Informe exportado a: " + nombreArchivo);
        } catch (IOException e) {
            throw new ErrorSistema("No se pudo exportar el informe: " + e.getMessage(), e);
        }
    }
}
