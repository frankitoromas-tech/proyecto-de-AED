package com.trafico.navegacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.estructuras.PilaRecorrido;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Ruta con Gestión de Atascos — Algoritmo de Bellman-Ford</b>
 *
 * <p>Bellman-Ford puede manejar costos negativos en las aristas, lo que en este
 * sistema representa calles con congestión tan alta que el sistema les asigna
 * un costo negativo (descuento) para forzar rutas alternativas. También detecta
 * <b>ciclos negativos</b> que indicarían un colapso total del tráfico.</p>
 *
 * <h3>Cómo funciona:</h3>
 * <ol>
 *   <li>Costo del origen = 0, resto = ∞.</li>
 *   <li>Repite |V| - 1 veces: para cada calle, relaja si hay mejor camino.</li>
 *   <li>Iteración extra para detectar ciclos negativos.</li>
 * </ol>
 *
 * <p><b>Complejidad:</b> O(V × E) — más lento que Dijkstra pero más robusto.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class RutaConGestion implements EstrategiaRuta {

    @Override
    public List<Interseccion> calcularRuta(MapaVial mapa, String origenId, String destinoId)
            throws ErrorRutaImposible {

        List<Interseccion> todas = mapa.todasLasIntersecciones();
        int v = todas.size();

        Map<String, Double> costos = new HashMap<>();
        Map<String, String> predecesor = new HashMap<>();

        for (Interseccion i : todas) costos.put(i.getId(), Double.MAX_VALUE);
        costos.put(origenId, 0.0);

        // ─── Relajación V-1 veces ──────────────────────────────────────────────
        for (int iter = 0; iter < v - 1; iter++) {
            boolean huboCambio = false;
            for (Interseccion origen : todas) {
                if (costos.get(origen.getId()) == Double.MAX_VALUE) continue;
                List<Calle> salidas;
                try { salidas = mapa.callesDesdePunto(origen.getId()); }
                catch (Exception e) { continue; }

                for (Calle calle : salidas) {
                    String destId = calle.getDestino().getId();
                    double nuevoCosto = costos.get(origen.getId()) + calle.getCostoActual();
                    if (nuevoCosto < costos.get(destId)) {
                        costos.put(destId, nuevoCosto);
                        predecesor.put(destId, origen.getId());
                        huboCambio = true;
                    }
                }
            }
            if (!huboCambio) break; // optimización: si no hubo cambio, ya convergió
        }

        // ─── Detección de ciclos negativos ─────────────────────────────────────
        for (Interseccion origen : todas) {
            if (costos.get(origen.getId()) == Double.MAX_VALUE) continue;
            List<Calle> salidas;
            try { salidas = mapa.callesDesdePunto(origen.getId()); }
            catch (Exception e) { continue; }

                for (Calle calle : salidas) {
                    String destId = calle.getDestino().getId();
                    if (costos.get(origen.getId()) + calle.getCostoActual() < costos.get(destId)) {
                        System.err.println("  [Advertencia] Ciclo negativo detectado - la red tiene un atasco extremo.");
                        break;
                    }
                }
        }

        return reconstruirRuta(mapa, predecesor, origenId, destinoId);
    }

    private List<Interseccion> reconstruirRuta(MapaVial mapa, Map<String, String> predecesor,
                                                String origenId, String destinoId)
            throws ErrorRutaImposible {

        if (!predecesor.containsKey(destinoId) && !origenId.equals(destinoId))
            throw new ErrorRutaImposible(origenId, destinoId, "Bellman-Ford no encontro camino.");

        PilaRecorrido<String> pila = new PilaRecorrido<>();
        String actual = destinoId;
        int guard = 0; // proteccion contra ciclos infinitos
        while (actual != null && guard++ < 1000) { pila.apilar(actual); actual = predecesor.get(actual); }

        List<Interseccion> ruta = new ArrayList<>();
        while (!pila.estaVacia()) {
            Interseccion i = mapa.buscarInterseccion(pila.desapilar());
            if (i != null) ruta.add(i);
        }
        return ruta;
    }

    @Override
    public String getNombre() { return "Bellman-Ford (Ruta con Gestion de Atascos)"; }
}
