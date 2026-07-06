package com.trafico.estructuras;

import java.util.LinkedList;

/**
 * <b>Caché de Rutas Calculadas</b> — Tabla Hash con encadenamiento, implementada desde cero.
 *
 * <p>Una tabla hash mapea claves a valores usando una función hash para ubicar
 * los datos directamente, logrando búsquedas en tiempo promedio O(1).
 * Las colisiones se resuelven con <b>encadenamiento</b>: cada posición del
 * array guarda una lista enlazada de pares clave-valor.</p>
 *
 * <p><b>Uso en este proyecto:</b> almacena rutas ya calculadas indexadas por
 * "origenId→destinoId". Antes de ejecutar Dijkstra o A*, el sistema consulta
 * esta caché para evitar recalcular rutas repetidas durante la simulación.</p>
 *
 * @param <K> tipo de la clave (por ejemplo, String "INT-01→INT-09")
 * @param <V> tipo del valor almacenado (por ejemplo, List de Interseccion)
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class CacheRutas<K, V> {

    /** Par clave-valor almacenado en cada posición de la tabla. */
    private static class Entrada<K, V> {
        K clave;
        V valor;
        Entrada(K clave, V valor) { this.clave = clave; this.valor = valor; }
    }

    private LinkedList<Entrada<K, V>>[] cubetas;
    private int capacidad;
    private int tamanio;
    private int aciertos;   // cuántas veces se encontró la ruta en caché
    private int fallos;     // cuántas veces no estaba en caché

    private static final double FACTOR_CARGA_MAX = 0.75;

    @SuppressWarnings("unchecked")
    public CacheRutas() {
        this.capacidad = 32;
        this.cubetas = new LinkedList[capacidad];
        this.tamanio = 0;
    }

    // ─── Operaciones principales ───────────────────────────────────────────────

    /**
     * Guarda una ruta en la caché bajo la clave dada.
     *
     * @param clave clave única (ej. "A→Z")
     * @param valor valor a almacenar (lista de intersecciones)
     */
    public void guardar(K clave, V valor) {
        if (clave == null) throw new IllegalArgumentException("La clave no puede ser null.");
        if ((double)(tamanio + 1) / capacidad > FACTOR_CARGA_MAX) redimensionar();

        int idx = posicion(clave);
        if (cubetas[idx] == null) cubetas[idx] = new LinkedList<>();

        for (Entrada<K, V> e : cubetas[idx]) {
            if (e.clave.equals(clave)) { e.valor = valor; return; } // actualizar
        }
        cubetas[idx].add(new Entrada<>(clave, valor));
        tamanio++;
    }

    /**
     * Recupera la ruta almacenada bajo la clave dada.
     * Registra si fue un acierto o fallo de caché.
     *
     * @param clave clave de búsqueda
     * @return el valor almacenado, o {@code null} si no existe
     */
    public V recuperar(K clave) {
        if (clave == null) return null;
        int idx = posicion(clave);
        if (cubetas[idx] == null) { fallos++; return null; }

        for (Entrada<K, V> e : cubetas[idx]) {
            if (e.clave.equals(clave)) { aciertos++; return e.valor; }
        }
        fallos++;
        return null;
    }

    /**
     * Verifica si existe una ruta guardada bajo esa clave.
     *
     * @param clave clave de búsqueda
     * @return true si la ruta está en caché
     */
    public boolean contiene(K clave) {
        if (clave == null) return false;
        int idx = posicion(clave);
        if (cubetas[idx] == null) return false;
        for (Entrada<K, V> e : cubetas[idx]) {
            if (e.clave.equals(clave)) return true;
        }
        return false;
    }

    /**
     * Elimina una ruta de la caché.
     *
     * @param clave clave a eliminar
     * @return true si se encontró y eliminó
     */
    public boolean eliminar(K clave) {
        if (clave == null) return false;
        int idx = posicion(clave);
        if (cubetas[idx] == null) return false;

        Entrada<K, V> encontrada = null;
        for (Entrada<K, V> e : cubetas[idx])
            if (e.clave.equals(clave)) { encontrada = e; break; }

        if (encontrada != null) { cubetas[idx].remove(encontrada); tamanio--; return true; }
        return false;
    }

    public int getTamanio()  { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }
    public int getAciertos() { return aciertos; }
    public int getFallos()   { return fallos; }

    /** Muestra estadísticas de efectividad de la caché. */
    public String getEstadisticas() {
        int total = aciertos + fallos;
        double tasa = total > 0 ? (aciertos * 100.0 / total) : 0;
        return String.format("Caché: %d rutas | %d aciertos | %d fallos | Efectividad: %.1f%%",
            tamanio, aciertos, fallos, tasa);
    }

    @SuppressWarnings("unchecked")
    public void vaciar() { cubetas = new LinkedList[capacidad]; tamanio = 0; }

    // ─── Lógica interna ────────────────────────────────────────────────────────

    private int posicion(K clave) {
        int h = clave.hashCode();
        h ^= (h >>> 16); // mezcla de bits para mejor distribución
        return Math.abs(h % capacidad);
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        int nuevaCapacidad = capacidad * 2;
        LinkedList<Entrada<K, V>>[] nuevasCubetas = new LinkedList[nuevaCapacidad];
        for (LinkedList<Entrada<K, V>> cubeta : cubetas) {
            if (cubeta == null) continue;
            for (Entrada<K, V> e : cubeta) {
                int idx = Math.abs(e.clave.hashCode() % nuevaCapacidad);
                if (nuevasCubetas[idx] == null) nuevasCubetas[idx] = new LinkedList<>();
                nuevasCubetas[idx].add(e);
            }
        }
        cubetas = nuevasCubetas;
        capacidad = nuevaCapacidad;
    }

    @Override
    public String toString() {
        return "CacheRutas{" + tamanio + " rutas guardadas | " + getEstadisticas() + "}";
    }
}
