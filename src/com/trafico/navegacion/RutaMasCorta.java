package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.estructuras.MontonMinimo;
import com.trafico.estructuras.PilaRecorrido;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Ruta Más Corta — Algoritmo de Dijkstra</b>
 *
 * <p>Dijkstra encuentra la ruta con el <b>menor costo acumulado</b> entre
 * dos intersecciones. En este sistema, el "costo" de una calle es su tiempo
 * de recorrido ajustado por congestión ({@link Calle#getCostoActual()}).</p>
 *
 * <h3>Cómo funciona paso a paso:</h3>
 * <ol>
 *   <li>Asigna costo = 0 al origen y costo = ∞ a todos los demás.</li>
 *   <li>Mete el origen en el {@link MontonMinimo} (prioridad = costo).</li>
 *   <li>Mientras el montón no esté vacío:
 *     <ul>
 *       <li>Extrae la intersección de menor costo.</li>
 *       <li>Si es el destino, se terminó.</li>
 *       <li>Para cada vecino, si el costo por aquí es menor que el registrado, actualiza.</li>
 *     </ul>
 *   </li>
 *   <li>Reconstruye la ruta siguiendo los predecesores con una {@link PilaRecorrido}.</li>
 * </ol>
 *
 * <p><b>Complejidad:</b> O((V + E) log V) con la cola de prioridad.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class RutaMasCorta implements EstrategiaRuta {

    /**
     * Par (nodo, costo) para la cola de prioridad de Dijkstra.
     * Comparable por costo acumulado.
     */
    private static class NodoCosto implements Comparable<NodoCosto> {
        String id;
        double costo;
        NodoCosto(String id, double costo) { this.id = id; this.costo = costo; }
        @Override public int compareTo(NodoCosto otro) { return Double.compare(this.costo, otro.costo); }
    }

    @Override
    public List<Interseccion> calcularRuta(MapaVial mapa, String origenId, String destinoId)
            throws ErrorRutaImposible {

        // ─── 1. Inicialización ────────────────────────────────────────────────
        Map<String, Double> costos = new HashMap<>();
        Map<String, String> predecesor = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        MontonMinimo<NodoCosto> cola = new MontonMinimo<>();

        for (Interseccion i : mapa.todasLasIntersecciones()) costos.put(i.getId(), Double.MAX_VALUE);
        costos.put(origenId, 0.0);
        cola.insertar(new NodoCosto(origenId, 0.0));

        // ─── 2. Exploración ───────────────────────────────────────────────────
        while (!cola.estaVacio()) {
            NodoCosto actual = cola.extraerMinimo();
            if (visitados.contains(actual.id)) continue;
            visitados.add(actual.id);

            if (actual.id.equals(destinoId)) break; // llegamos al destino

            List<Calle> salidas;
            try { salidas = mapa.callesDesdePunto(actual.id); }
            catch (Exception e) { continue; }

            for (Calle calle : salidas) {
                String vecinoId = calle.getDestino().getId();
                if (visitados.contains(vecinoId)) continue;

                double nuevoCosto = costos.get(actual.id) + calle.getCostoActual();
                if (nuevoCosto < costos.get(vecinoId)) {
                    costos.put(vecinoId, nuevoCosto);
                    predecesor.put(vecinoId, actual.id);
                    cola.insertar(new NodoCosto(vecinoId, nuevoCosto));
                }
            }
        }

        // ─── 3. Reconstrucción de la ruta ─────────────────────────────────────
        return reconstruirRuta(mapa, predecesor, origenId, destinoId);
    }

    /**
     * Reconstruye la ruta siguiendo los predecesores hacia atrás y usa la
     * {@link PilaRecorrido} para invertir el orden.
     */
    private List<Interseccion> reconstruirRuta(MapaVial mapa, Map<String, String> predecesor,
                                                String origenId, String destinoId)
            throws ErrorRutaImposible {

        if (!predecesor.containsKey(destinoId) && !origenId.equals(destinoId))
            throw new ErrorRutaImposible(origenId, destinoId, "Dijkstra no encontro camino.");

        PilaRecorrido<String> pila = new PilaRecorrido<>();
        String actual = destinoId;
        while (actual != null) {
            pila.apilar(actual);
            actual = predecesor.get(actual);
        }

        List<Interseccion> ruta = new ArrayList<>();
        while (!pila.estaVacia()) {
            Interseccion i = mapa.buscarInterseccion(pila.desapilar());
            if (i != null) ruta.add(i);
        }
        return ruta;
    }

    @Override
    public String getNombre() { return "Dijkstra (Ruta Mas Corta)"; }
}
