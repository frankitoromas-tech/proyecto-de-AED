package com.trafico.mapa;

import com.trafico.errores.ErrorSistema;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Mapa de Ciudad Grande</b> — Implementación del mapa vial con Lista de Adyacencia.
 *
 * <p>Usa un {@code HashMap<String, List<Calle>>} donde cada intersección apunta
 * a la lista de calles que salen de ella. Es la representación preferida para
 * ciudades con muchas intersecciones y pocas conexiones directas entre ellas
 * (grafos dispersos), ya que consume mucha menos memoria que la matriz.</p>
 *
 * <p><b>Complejidades:</b> inserción O(1), búsqueda de vecinos O(grado del nodo),
 * verificación de conectividad O(V + E) con BFS.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class MapaCiudadGrande implements MapaVial {

    /** Cada intersección (por ID) mapea a sus calles salientes. */
    private final Map<String, List<Calle>> listaAdyacencia;

    /** Acceso rápido a los objetos Interseccion por su ID. */
    private final Map<String, Interseccion> intersecciones;

    private int totalCalles;

    public MapaCiudadGrande() {
        listaAdyacencia = new HashMap<>();
        intersecciones  = new HashMap<>();
        totalCalles = 0;
    }

    @Override
    public void agregarInterseccion(Interseccion interseccion) throws ErrorSistema {
        if (interseccion == null) throw new ErrorSistema("La intersección no puede ser null.");
        if (intersecciones.containsKey(interseccion.getId()))
            throw new ErrorSistema("Ya existe la intersección '" + interseccion.getId() + "'.");
        intersecciones.put(interseccion.getId(), interseccion);
        listaAdyacencia.put(interseccion.getId(), new ArrayList<>());
    }

    @Override
    public void agregarCalle(Calle calle) throws ErrorSistema {
        if (calle == null) throw new ErrorSistema("La calle no puede ser null.");
        validarExistencia(calle.getOrigen().getId());
        validarExistencia(calle.getDestino().getId());

        listaAdyacencia.get(calle.getOrigen().getId()).add(calle);
        totalCalles++;

        if (calle.isEsDobleVia()) {
            Calle inversa = new Calle(calle.getDestino(), calle.getOrigen(),
                calle.getDistancia(), calle.getVelocidadMaxima(), calle.getCategoria(), false);
            listaAdyacencia.get(calle.getDestino().getId()).add(inversa);
            totalCalles++;
        }
    }

    @Override
    public void eliminarCalle(String origenId, String destinoId) throws ErrorSistema {
        validarExistencia(origenId);
        boolean eliminada = listaAdyacencia.get(origenId)
            .removeIf(c -> c.getDestino().getId().equals(destinoId));
        if (eliminada) totalCalles--;
        else throw new ErrorSistema("No existe calle de '" + origenId + "' a '" + destinoId + "'.");
    }

    @Override
    public List<Calle> callesDesdePunto(String interseccionId) throws ErrorSistema {
        validarExistencia(interseccionId);
        return Collections.unmodifiableList(listaAdyacencia.get(interseccionId));
    }

    @Override
    public List<Interseccion> todasLasIntersecciones() {
        return new ArrayList<>(intersecciones.values());
    }

    @Override
    public Interseccion buscarInterseccion(String id) {
        return intersecciones.get(id);
    }

    @Override
    public Calle buscarCalle(String origenId, String destinoId) {
        List<Calle> salidas = listaAdyacencia.get(origenId);
        if (salidas == null) return null;
        for (Calle c : salidas)
            if (c.getDestino().getId().equals(destinoId)) return c;
        return null;
    }

    @Override
    public void actualizarCongestion(String origenId, String destinoId, double congestion)
            throws ErrorSistema {
        Calle calle = buscarCalle(origenId, destinoId);
        if (calle == null)
            throw new ErrorSistema("No existe calle de '" + origenId + "' a '" + destinoId + "'.");
        calle.setCongestion(congestion);
    }

    @Override
    public boolean esMapaConexo() {
        if (intersecciones.isEmpty()) return true;
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        String inicio = intersecciones.keySet().iterator().next();
        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            for (Calle c : listaAdyacencia.get(actual)) {
                String vecino = c.getDestino().getId();
                if (!visitados.contains(vecino)) { visitados.add(vecino); cola.add(vecino); }
            }
        }
        return visitados.size() == intersecciones.size();
    }

    @Override
    public int totalIntersecciones() { return intersecciones.size(); }

    @Override
    public int totalCalles() { return totalCalles; }

    @Override
    public void mostrarMapa() {
        System.out.println("\n  +--------------------------------------------------+");
        System.out.println("  |         MAPA VIAL - Ciudad Grande (Adyacencia)   |");
        System.out.println("  +--------------------------------------------------+");
        System.out.printf("  |  Intersecciones: %-5d   Calles: %-14d |%n",
            totalIntersecciones(), totalCalles());
        System.out.println("  +--------------------------------------------------+");
        for (Interseccion i : todasLasIntersecciones()) {
            System.out.printf("  [%s] %s%s%n", i.getId(), i.getNombre(),
                i.isTieneSemaforo() ? "  [Semaforo]" : "");
            List<Calle> salidas = listaAdyacencia.get(i.getId());
            if (salidas.isEmpty()) {
                System.out.println("      +- (sin salidas)");
            } else {
                for (int k = 0; k < salidas.size(); k++) {
                    Calle c = salidas.get(k);
                    String rama = (k == salidas.size() - 1) ? "+-" : "|-";
                    System.out.printf("      %s -> [%s] %-22s %.0fm %.0fkm/h %s%n",
                        rama, c.getDestino().getId(), c.getDestino().getNombre(),
                        c.getDistancia(), c.getVelocidadMaxima(),
                        c.estaSaturada() ? "[SATURADA]" : "");
                }
            }
        }
        System.out.println();
    }

    private void validarExistencia(String id) throws ErrorSistema {
        if (!intersecciones.containsKey(id))
            throw new ErrorSistema("La intersección '" + id + "' no existe en el mapa.");
    }
}
