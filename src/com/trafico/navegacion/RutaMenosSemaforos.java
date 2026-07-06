package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Ruta con Menos Semáforos — Búsqueda en Anchura (BFS)</b>
 *
 * <p>BFS encuentra la ruta que pasa por el <b>menor número de intersecciones</b>
 * (y por ende, el menor número de semáforos cruzados), independientemente
 * de la distancia o el tiempo de recorrido de cada calle.</p>
 *
 * <h3>Cómo funciona:</h3>
 * <ol>
 *   <li>Coloca el origen en una cola (FIFO).</li>
 *   <li>Saca el primero de la cola y explora sus vecinos.</li>
 *   <li>Encola los vecinos no visitados — esto garantiza que los más cercanos
 *       (en número de saltos) se exploran primero.</li>
 *   <li>Para cuando llega al destino.</li>
 * </ol>
 *
 * <p><b>Garantía:</b> la ruta devuelta tiene el mínimo número de intersecciones posible.</p>
 * <p><b>Complejidad:</b> O(V + E).</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class RutaMenosSemaforos implements EstrategiaRuta {

    @Override
    public List<Interseccion> calcularRuta(MapaVial mapa, String origenId, String destinoId)
            throws ErrorRutaImposible {

        // BFS usa una cola FIFO — aquí usamos LinkedList de java.util (no es una
        // estructura propia del proyecto; las ED propias son MontonMinimo, PilaRecorrido, etc.)
        Queue<String> cola = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> predecesor = new HashMap<>();

        cola.add(origenId);
        visitados.add(origenId);

        boolean encontrado = false;

        while (!cola.isEmpty()) {
            String actualId = cola.poll();
            if (actualId.equals(destinoId)) { encontrado = true; break; }

            List<Calle> salidas;
            try { salidas = mapa.callesDesdePunto(actualId); }
            catch (Exception e) { continue; }

            for (Calle calle : salidas) {
                String vecinoId = calle.getDestino().getId();
                if (!visitados.contains(vecinoId)) {
                    visitados.add(vecinoId);
                    predecesor.put(vecinoId, actualId);
                    cola.add(vecinoId);
                }
            }
        }

        if (!encontrado && !origenId.equals(destinoId))
            throw new ErrorRutaImposible(origenId, destinoId, "BFS no encontro camino.");

        // Reconstruir la ruta siguiendo predecesores hacia atras
        List<Interseccion> ruta = new ArrayList<>();
        String actual = destinoId;
        while (actual != null) {
            Interseccion i = mapa.buscarInterseccion(actual);
            if (i != null) ruta.add(0, i); // inserta al inicio para orden correcto
            actual = predecesor.get(actual);
        }
        return ruta;
    }

    @Override
    public String getNombre() { return "BFS (Ruta con Menos Semaforos)"; }
}
