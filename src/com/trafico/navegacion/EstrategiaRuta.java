package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Interseccion;

import java.util.List;

/**
 * Contrato común de todos los algoritmos de búsqueda de ruta — <b>patrón Strategy</b>.
 *
 * <p>Permite intercambiar el algoritmo de navegación sin modificar el código del
 * vehículo ni del motor de simulación. Cada implementación ofrece distintas
 * garantías: menor distancia, menor tiempo, menos semáforos, etc.</p>
 *
 * <p><b>Implementaciones disponibles:</b></p>
 * <ul>
 *   <li>{@link RutaMasCorta} — Dijkstra: menor distancia acumulada</li>
 *   <li>{@link RutaOptimizada} — A*: Dijkstra + heurística espacial</li>
 *   <li>{@link RutaConGestion} — Bellman-Ford: maneja pesos negativos (atascos extremos)</li>
 *   <li>{@link RutaMenosSemaforos} — BFS: mínimo número de intersecciones cruzadas</li>
 * </ul>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public interface EstrategiaRuta {

    /**
     * Calcula la ruta más conveniente entre dos intersecciones según el criterio
     * del algoritmo que implementa esta estrategia.
     *
     * @param mapa      el mapa vial de la ciudad con todas las intersecciones y calles
     * @param origenId  ID de la intersección de partida
     * @param destinoId ID de la intersección de llegada
     * @return lista ordenada de intersecciones desde el origen hasta el destino (inclusive)
     * @throws ErrorRutaImposible si no existe ningún camino entre los dos puntos
     */
    List<Interseccion> calcularRuta(MapaVial mapa, String origenId, String destinoId)
            throws ErrorRutaImposible;

    /**
     * Nombre descriptivo del algoritmo para mostrar en comparativas.
     *
     * @return nombre del algoritmo (ej. "Dijkstra", "A*")
     */
    String getNombre();
}
