package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.estructuras.MontonMinimo;
import com.trafico.estructuras.PilaRecorrido;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Ruta Optimizada — Algoritmo A* (A-estrella)</b>
 *
 * <p>A* mejora a Dijkstra usando una <b>heurística</b> para guiar la búsqueda
 * hacia el destino. En lugar de explorar todas las direcciones por igual,
 * A* prioriza los nodos que están más cerca del destino en línea recta.</p>
 *
 * <h3>La fórmula clave de A*:</h3>
 * <pre>  f(n) = g(n) + h(n)</pre>
 * <ul>
 *   <li>{@code g(n)} = costo real acumulado desde el origen hasta n</li>
 *   <li>{@code h(n)} = estimación del costo desde n hasta el destino (heurística)</li>
 *   <li>{@code f(n)} = costo total estimado del camino pasando por n</li>
 * </ul>
 *
 * <p>La heurística usada es la <b>distancia euclidiana</b> entre intersecciones,
 * calculada con {@link Interseccion#distanciaHacia(Interseccion)}.</p>
 *
 * <p><b>Ventaja sobre Dijkstra:</b> en la práctica visita menos nodos porque
 * descarta temprano los caminos que se alejan del destino.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class RutaOptimizada implements EstrategiaRuta {

    /** Nodo para la cola: lleva id, costo g(n) y costo total f(n). */
    private static class NodoF implements Comparable<NodoF> {
        String id;
        double g; // costo real acumulado
        double f; // f = g + h
        NodoF(String id, double g, double f) { this.id = id; this.g = g; this.f = f; }
        @Override public int compareTo(NodoF otro) { return Double.compare(this.f, otro.f); }
    }

    @Override
    public List<Interseccion> calcularRuta(MapaVial mapa, String origenId, String destinoId)
            throws ErrorRutaImposible {

        Interseccion destino = mapa.buscarInterseccion(destinoId);
        if (destino == null) throw new ErrorRutaImposible(origenId, destinoId, "Destino no encontrado.");

        Map<String, Double> costoG = new HashMap<>();
        Map<String, String> predecesor = new HashMap<>();
        Set<String> cerrados = new HashSet<>();
        MontonMinimo<NodoF> abiertos = new MontonMinimo<>();

        for (Interseccion i : mapa.todasLasIntersecciones()) costoG.put(i.getId(), Double.MAX_VALUE);
        costoG.put(origenId, 0.0);

        Interseccion origen = mapa.buscarInterseccion(origenId);
        double h0 = origen != null ? origen.distanciaHacia(destino) / 1000.0 : 0;
        abiertos.insertar(new NodoF(origenId, 0.0, h0));

        while (!abiertos.estaVacio()) {
            NodoF actual = abiertos.extraerMinimo();
            if (cerrados.contains(actual.id)) continue;
            cerrados.add(actual.id);

            if (actual.id.equals(destinoId)) break;

            List<Calle> salidas;
            try { salidas = mapa.callesDesdePunto(actual.id); }
            catch (Exception e) { continue; }

            for (Calle calle : salidas) {
                String vecinoId = calle.getDestino().getId();
                if (cerrados.contains(vecinoId)) continue;

                double nuevoG = costoG.get(actual.id) + calle.getCostoActual();
                if (nuevoG < costoG.get(vecinoId)) {
                    costoG.put(vecinoId, nuevoG);
                    predecesor.put(vecinoId, actual.id);

                    Interseccion vecino = calle.getDestino();
                    double h = vecino.distanciaHacia(destino) / 1000.0;
                    abiertos.insertar(new NodoF(vecinoId, nuevoG, nuevoG + h));
                }
            }
        }

        return reconstruirRuta(mapa, predecesor, origenId, destinoId);
    }

    private List<Interseccion> reconstruirRuta(MapaVial mapa, Map<String, String> predecesor,
                                                String origenId, String destinoId)
            throws ErrorRutaImposible {

        if (!predecesor.containsKey(destinoId) && !origenId.equals(destinoId))
            throw new ErrorRutaImposible(origenId, destinoId, "A* no encontro camino.");

        PilaRecorrido<String> pila = new PilaRecorrido<>();
        String actual = destinoId;
        while (actual != null) { pila.apilar(actual); actual = predecesor.get(actual); }

        List<Interseccion> ruta = new ArrayList<>();
        while (!pila.estaVacia()) {
            Interseccion i = mapa.buscarInterseccion(pila.desapilar());
            if (i != null) ruta.add(i);
        }
        return ruta;
    }

    @Override
    public String getNombre() { return "A* (Ruta Optimizada con Heuristica)"; }
}
