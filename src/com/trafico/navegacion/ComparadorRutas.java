package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.List;

/**
 * Comparador de algoritmos de navegación.
 *
 * <p>Ejecuta los cuatro algoritmos disponibles sobre el mismo par de intersecciones
 * y presenta una tabla comparativa con la ruta encontrada, la distancia total,
 * el tiempo estimado y el número de intersecciones cruzadas por cada uno.</p>
 *
 * <p>Útil para entender en clase cuándo conviene usar cada algoritmo.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class ComparadorRutas {

    private final EstrategiaRuta[] estrategias = {
        new RutaMasCorta(),
        new RutaOptimizada(),
        new RutaConGestion(),
        new RutaMenosSemaforos()
    };

    /**
     * Ejecuta los cuatro algoritmos y muestra la tabla comparativa en consola.
     *
     * @param mapa      mapa vial de la ciudad
     * @param origenId  ID de la intersección de partida
     * @param destinoId ID de la intersección de llegada
     */
    public void comparar(MapaVial mapa, String origenId, String destinoId) {
        System.out.println("\n+----------------------------------------------------------------------+");
        System.out.println("|              COMPARATIVA DE ALGORITMOS DE NAVEGACION                 |");
        System.out.printf("|  Origen: %-20s  Destino: %-20s|%n",
            origenId, destinoId);
        System.out.println("+--------------------------+-----------+------------+------------------+");
        System.out.println("| Algoritmo                | Nodos     | Distancia  | Tiempo Est.      |");
        System.out.println("+--------------------------+-----------+------------+------------------+");

        for (EstrategiaRuta estrategia : estrategias) {
            long inicio = System.nanoTime();
            try {
                List<Interseccion> ruta = estrategia.calcularRuta(mapa, origenId, destinoId);
                long fin = System.nanoTime();
                double ms = (fin - inicio) / 1_000_000.0;

                double distanciaTotal = calcularDistanciaTotal(mapa, ruta);
                double tiempoTotal    = calcularTiempoTotal(mapa, ruta);

                System.out.printf("| %-24s | %-9d | %8.0f m  | %8.2f min (%.2fms)|%n",
                    estrategia.getNombre(), ruta.size(), distanciaTotal, tiempoTotal, ms);

            } catch (ErrorRutaImposible e) {
                System.out.printf("| %-24s | SIN RUTA - %s%n",
                    estrategia.getNombre(), e.getMessage());
            }
        }

        System.out.println("+--------------------------+-----------+------------+------------------+\n");
    }

    /**
     * Calcula la distancia total de una ruta sumando las longitudes de cada calle.
     *
     * @param mapa mapa vial para buscar las calles entre intersecciones
     * @param ruta la lista de intersecciones de la ruta
     * @return distancia total en metros
     */
    private double calcularDistanciaTotal(MapaVial mapa, List<Interseccion> ruta) {
        double total = 0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Calle calle = mapa.buscarCalle(ruta.get(i).getId(), ruta.get(i + 1).getId());
            if (calle != null) total += calle.getDistancia();
        }
        return total;
    }

    /**
     * Calcula el tiempo estimado de una ruta sumando el costo actual de cada calle.
     *
     * @param mapa mapa vial para buscar las calles
     * @param ruta la lista de intersecciones de la ruta
     * @return tiempo total estimado en minutos
     */
    private double calcularTiempoTotal(MapaVial mapa, List<Interseccion> ruta) {
        double total = 0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Calle calle = mapa.buscarCalle(ruta.get(i).getId(), ruta.get(i + 1).getId());
            if (calle != null) total += calle.getCostoActual();
        }
        return total;
    }
}
