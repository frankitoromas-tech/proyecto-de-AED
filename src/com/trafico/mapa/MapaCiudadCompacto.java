package com.trafico.mapa;

import com.trafico.errores.ErrorSistema;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.*;

/**
 * <b>Mapa de Ciudad Compacta</b> — Implementación del mapa vial con Matriz de Adyacencia.
 *
 * <p>Usa un array bidimensional {@code Calle[i][j]} donde la posición {@code [i][j]}
 * contiene la calle que va de la intersección {@code i} a la intersección {@code j},
 * o {@code null} si no existe esa conexión directa.</p>
 *
 * <p>Apropiada para simulaciones con pocas intersecciones (máximo 25), donde la
 * visualización matricial ayuda a entender la estructura de la red vial.
 * La búsqueda de si existe una calle entre dos puntos es O(1).</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class MapaCiudadCompacto implements MapaVial {

    /** La matriz bidimensional de calles. null = no hay conexión. */
    private Calle[][] matriz;

    /** Lista ordenada de intersecciones (el índice = fila/columna en la matriz). */
    private final List<Interseccion> listaOrdenada;

    /** Mapa de ID → índice en la lista, para acceso rápido. */
    private final Map<String, Integer> indiceDeId;

    private final int capacidadMaxima;
    private int totalCallesActual;

    /** Crea un mapa compacto para ciudades de hasta 25 intersecciones. */
    public MapaCiudadCompacto() { this(25); }

    public MapaCiudadCompacto(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.matriz = new Calle[capacidadMaxima][capacidadMaxima];
        this.listaOrdenada = new ArrayList<>();
        this.indiceDeId = new HashMap<>();
        this.totalCallesActual = 0;
    }

    @Override
    public void agregarInterseccion(Interseccion interseccion) throws ErrorSistema {
        if (interseccion == null) throw new ErrorSistema("La intersección no puede ser null.");
        if (indiceDeId.containsKey(interseccion.getId()))
            throw new ErrorSistema("Ya existe la intersección '" + interseccion.getId() + "'.");
        if (listaOrdenada.size() >= capacidadMaxima)
            throw new ErrorSistema("El mapa compacto alcanzó su límite de " + capacidadMaxima + " intersecciones.");
        indiceDeId.put(interseccion.getId(), listaOrdenada.size());
        listaOrdenada.add(interseccion);
    }

    @Override
    public void agregarCalle(Calle calle) throws ErrorSistema {
        if (calle == null) throw new ErrorSistema("La calle no puede ser null.");
        int i = obtenerIndice(calle.getOrigen().getId());
        int j = obtenerIndice(calle.getDestino().getId());
        matriz[i][j] = calle;
        totalCallesActual++;

        if (calle.isEsDobleVia()) {
            Calle inversa = new Calle(calle.getDestino(), calle.getOrigen(),
                calle.getDistancia(), calle.getVelocidadMaxima(), calle.getCategoria(), false);
            matriz[j][i] = inversa;
            totalCallesActual++;
        }
    }

    @Override
    public void eliminarCalle(String origenId, String destinoId) throws ErrorSistema {
        int i = obtenerIndice(origenId), j = obtenerIndice(destinoId);
        if (matriz[i][j] == null)
            throw new ErrorSistema("No existe calle de '" + origenId + "' a '" + destinoId + "'.");
        matriz[i][j] = null;
        totalCallesActual--;
    }

    @Override
    public List<Calle> callesDesdePunto(String interseccionId) throws ErrorSistema {
        int i = obtenerIndice(interseccionId);
        List<Calle> salidas = new ArrayList<>();
        for (int j = 0; j < listaOrdenada.size(); j++)
            if (matriz[i][j] != null) salidas.add(matriz[i][j]);
        return salidas;
    }

    @Override
    public List<Interseccion> todasLasIntersecciones() {
        return Collections.unmodifiableList(listaOrdenada);
    }

    @Override
    public Interseccion buscarInterseccion(String id) {
        Integer idx = indiceDeId.get(id);
        return idx != null ? listaOrdenada.get(idx) : null;
    }

    @Override
    public Calle buscarCalle(String origenId, String destinoId) {
        Integer i = indiceDeId.get(origenId), j = indiceDeId.get(destinoId);
        return (i != null && j != null) ? matriz[i][j] : null;
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
        if (listaOrdenada.isEmpty()) return true;
        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> cola = new LinkedList<>();
        cola.add(0); visitados.add(0);
        while (!cola.isEmpty()) {
            int actual = cola.poll();
            for (int j = 0; j < listaOrdenada.size(); j++)
                if (matriz[actual][j] != null && !visitados.contains(j)) {
                    visitados.add(j); cola.add(j);
                }
        }
        return visitados.size() == listaOrdenada.size();
    }

    @Override
    public int totalIntersecciones() { return listaOrdenada.size(); }

    @Override
    public int totalCalles() { return totalCallesActual; }

    @Override
    public void mostrarMapa() {
        int n = listaOrdenada.size();
        System.out.println("\n  +--------------------------------------------------+");
        System.out.println("  |      MAPA VIAL - Ciudad Compacta (Matriz)        |");
        System.out.println("  +--------------------------------------------------+");
        System.out.printf("  |  Intersecciones: %-5d   Calles: %-14d |%n", n, totalCallesActual);
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  (valores = distancia en metros | 0 = sin conexion)");

        System.out.printf("%10s", "");
        for (Interseccion i : listaOrdenada) System.out.printf("%8s", i.getId());
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%10s", listaOrdenada.get(i).getId());
            for (int j = 0; j < n; j++) {
                if (i == j)           System.out.printf("%8s", "-");
                else if (matriz[i][j] != null) System.out.printf("%8.0f", matriz[i][j].getDistancia());
                else                           System.out.printf("%8s", "0");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int obtenerIndice(String id) throws ErrorSistema {
        Integer idx = indiceDeId.get(id);
        if (idx == null) throw new ErrorSistema("La intersección '" + id + "' no existe en el mapa.");
        return idx;
    }
}
