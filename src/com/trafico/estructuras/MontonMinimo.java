package com.trafico.estructuras;

/**
 * <b>Montón Mínimo (Min-Heap) Genérico</b> — implementado desde cero.
 *
 * <p>Un montón mínimo es un árbol binario completo donde el elemento
 * de menor valor siempre está en la raíz. Permite extraer el mínimo
 * en tiempo O(log n) e insertar en O(log n).</p>
 *
 * <p><b>Uso en este proyecto:</b> es la estructura base de las colas de
 * prioridad utilizadas en los algoritmos Dijkstra y A* para siempre
 * explorar primero la intersección de menor costo acumulado.</p>
 *
 * @param <T> tipo de elemento — debe ser {@link Comparable}
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class MontonMinimo<T extends Comparable<T>> {

    private Object[] datos;
    private int tamanio;
    private static final int CAPACIDAD_INICIAL = 16;

    /** Crea un montón vacío con capacidad inicial por defecto. */
    public MontonMinimo() {
        datos = new Object[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    /** Crea un montón vacío con la capacidad especificada. */
    public MontonMinimo(int capacidad) {
        datos = new Object[Math.max(capacidad, CAPACIDAD_INICIAL)];
        tamanio = 0;
    }

    // ─── Operaciones principales ───────────────────────────────────────────────

    /**
     * Inserta un elemento en el montón y restaura la propiedad de mínimo.
     * Complejidad: O(log n).
     *
     * @param elemento elemento a insertar (no puede ser null)
     */
    public void insertar(T elemento) {
        if (elemento == null) throw new IllegalArgumentException("El elemento no puede ser null.");
        if (tamanio >= datos.length) redimensionar();
        datos[tamanio] = elemento;
        subirHastaRaiz(tamanio);
        tamanio++;
    }

    /**
     * Extrae y retorna el elemento mínimo del montón.
     * Complejidad: O(log n).
     *
     * @return el elemento mínimo
     * @throws java.util.NoSuchElementException si el montón está vacío
     */
    @SuppressWarnings("unchecked")
    public T extraerMinimo() {
        if (estaVacio()) throw new java.util.NoSuchElementException("El montón está vacío.");
        T minimo = (T) datos[0];
        tamanio--;
        datos[0] = datos[tamanio];
        datos[tamanio] = null;
        if (!estaVacio()) bajarHastaHoja(0);
        return minimo;
    }

    /**
     * Devuelve el elemento mínimo sin extraerlo.
     * Complejidad: O(1).
     *
     * @return el elemento mínimo
     */
    @SuppressWarnings("unchecked")
    public T verMinimo() {
        if (estaVacio()) throw new java.util.NoSuchElementException("El montón está vacío.");
        return (T) datos[0];
    }

    public boolean estaVacio()  { return tamanio == 0; }
    public int getTamanio()     { return tamanio; }

    public void vaciar() {
        datos = new Object[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    // ─── Lógica interna del heap ───────────────────────────────────────────────

    /** Sube el elemento en la posición dada hasta que cumpla la propiedad de mínimo. */
    @SuppressWarnings("unchecked")
    private void subirHastaRaiz(int pos) {
        while (pos > 0) {
            int padre = (pos - 1) / 2;
            if (((T) datos[pos]).compareTo((T) datos[padre]) < 0) {
                intercambiar(pos, padre);
                pos = padre;
            } else break;
        }
    }

    /** Baja el elemento en la posición dada hasta que cumpla la propiedad de mínimo. */
    @SuppressWarnings("unchecked")
    private void bajarHastaHoja(int pos) {
        while (true) {
            int izq = 2 * pos + 1, der = 2 * pos + 2, menor = pos;
            if (izq < tamanio && ((T) datos[izq]).compareTo((T) datos[menor]) < 0) menor = izq;
            if (der < tamanio && ((T) datos[der]).compareTo((T) datos[menor]) < 0) menor = der;
            if (menor != pos) { intercambiar(pos, menor); pos = menor; }
            else break;
        }
    }

    private void intercambiar(int i, int j) {
        Object tmp = datos[i]; datos[i] = datos[j]; datos[j] = tmp;
    }

    private void redimensionar() {
        Object[] nuevo = new Object[datos.length * 2];
        System.arraycopy(datos, 0, nuevo, 0, datos.length);
        datos = nuevo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MontonMinimo[");
        for (int i = 0; i < tamanio; i++) {
            sb.append(datos[i]);
            if (i < tamanio - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
