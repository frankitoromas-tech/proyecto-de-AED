package com.trafico.datos;

import com.trafico.errores.ErrorSistema;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.CategoriaCalle;
import com.trafico.modelo.Interseccion;

import java.io.*;

/**
 * Cargador de Mapa — lee la definición de una ciudad desde un archivo de texto.
 *
 * <p>El archivo debe tener el siguiente formato (una definición por línea):</p>
 * <pre>
 *   # Comentarios con almohadilla
 *   INTERSECCION;id;nombre;x;y
 *   CALLE;origen_id;destino_id;distancia_metros;vel_max_kmh;categoria;doble_via
 * </pre>
 *
 * <p>Ejemplo:</p>
 * <pre>
 *   INTERSECCION;A;Plaza Central;0;0
 *   INTERSECCION;B;Av. Norte y Calle 1;500;0
 *   CALLE;A;B;500;60;AVENIDA;true
 * </pre>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class CargadorMapa {

    /**
     * Lee un archivo de ciudad y carga su contenido en el mapa vial dado.
     *
     * @param rutaArchivo ruta absoluta o relativa al archivo .txt de la ciudad
     * @param mapa        el mapa vial donde se cargarán las intersecciones y calles
     * @throws ErrorSistema si el archivo no existe o tiene un formato inválido
     */
    public void cargar(String rutaArchivo, MapaVial mapa) throws ErrorSistema {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue; // ignorar vacíos y comentarios

                String[] partes = linea.split(";");

                switch (partes[0].toUpperCase()) {
                    case "INTERSECCION":
                        procesarInterseccion(partes, numeroLinea, mapa);
                        break;
                    case "CALLE":
                        procesarCalle(partes, numeroLinea, mapa);
                        break;
                    default:
                        System.err.printf("  ⚠ Línea %d: tipo desconocido '%s' — omitida.%n",
                            numeroLinea, partes[0]);
                }
            }

            System.out.printf("  ✓ Mapa cargado: %d intersecciones, %d calles.%n",
                mapa.totalIntersecciones(), mapa.totalCalles());

        } catch (FileNotFoundException e) {
            throw new ErrorSistema("Archivo no encontrado: " + rutaArchivo, e);
        } catch (IOException e) {
            throw new ErrorSistema("Error al leer el archivo: " + rutaArchivo, e);
        }
    }

    private void procesarInterseccion(String[] partes, int linea, MapaVial mapa) throws ErrorSistema {
        if (partes.length < 5)
            throw new ErrorSistema("Línea " + linea + ": INTERSECCION requiere id;nombre;x;y");
        try {
            String id = partes[1].trim();
            String nombre = partes[2].trim();
            double x = Double.parseDouble(partes[3].trim());
            double y = Double.parseDouble(partes[4].trim());
            mapa.agregarInterseccion(new Interseccion(id, nombre, x, y));
        } catch (NumberFormatException e) {
            throw new ErrorSistema("Línea " + linea + ": coordenadas inválidas — " + e.getMessage());
        }
    }

    private void procesarCalle(String[] partes, int linea, MapaVial mapa) throws ErrorSistema {
        if (partes.length < 7)
            throw new ErrorSistema("Línea " + linea + ": CALLE requiere origen;destino;dist;vel;categoria;dobleVia");
        try {
            Interseccion origen  = mapa.buscarInterseccion(partes[1].trim());
            Interseccion destino = mapa.buscarInterseccion(partes[2].trim());
            if (origen == null)  throw new ErrorSistema("Línea " + linea + ": intersección origen '" + partes[1] + "' no encontrada.");
            if (destino == null) throw new ErrorSistema("Línea " + linea + ": intersección destino '" + partes[2] + "' no encontrada.");

            double distancia = Double.parseDouble(partes[3].trim());
            double velMax    = Double.parseDouble(partes[4].trim());
            CategoriaCalle categoria = CategoriaCalle.valueOf(partes[5].trim().toUpperCase());
            boolean dobleVia = Boolean.parseBoolean(partes[6].trim());

            mapa.agregarCalle(new Calle(origen, destino, distancia, velMax, categoria, dobleVia));
        } catch (IllegalArgumentException e) {
            throw new ErrorSistema("Línea " + linea + ": categoría de calle inválida — " + e.getMessage());
        }
    }
}
