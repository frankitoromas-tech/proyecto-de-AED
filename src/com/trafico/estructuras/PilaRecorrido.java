package com.trafico.estructuras;

/**
 * <b>Pila de Recorrido</b> — Pila genérica LIFO implementada desde cero.
 *
 * <p>Una pila es una estructura donde el último elemento en entrar
 * es el primero en salir (Last In, First Out). Las operaciones principales
 * son {@code apilar()} y {@code desapilar()}, ambas en tiempo O(1).</p>
 *
 * <p><b>Uso en este proyecto:</b> una vez que Dijkstra o A* encuentran el destino,
 * reconstruyen la ruta siguiendo los predecesores hacia atrás. La pila invierte
 * ese orden para obtener la ruta correcta de origen a destino.</p>
 *
 * @param <T> tipo de elemento almacenado
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class PilaRecorrido<T> {

    private Object[] datos;
    private int tope;
    private static final int CAPACIDAD_INICIAL = 10;

    /** Crea una pila vacía. */
    public PilaRecorrido() {
        datos = new Object[CAPACIDAD_INICIAL];
        tope = -1;
    }

    // ─── Operaciones principales ───────────────────────────────────────────────

    /**
     * Apila un elemento en el tope. Complejidad: O(1) amortizado.
     *
     * @param elemento elemento a apilar
     */
    public void apilar(T elemento) {
        if (elemento == null) throw new IllegalArgumentException("No se puede apilar null.");
        if (tope + 1 >= datos.length) redimensionar();
        datos[++tope] = elemento;
    }

    /**
     * Saca y retorna el elemento del tope. Complejidad: O(1).
     *
     * @return el elemento del tope
     * @throws java.util.EmptyStackException si la pila está vacía
     */
    @SuppressWarnings("unchecked")
    public T desapilar() {
        if (estaVacia()) throw new java.util.EmptyStackException();
        T elem = (T) datos[tope];
        datos[tope--] = null;
        return elem;
    }

    /**
     * Consulta el elemento del tope sin sacarlo. Complejidad: O(1).
     *
     * @return el elemento del tope
     */
    @SuppressWarnings("unchecked")
    public T verTope() {
        if (estaVacia()) throw new java.util.EmptyStackException();
        return (T) datos[tope];
    }

    public boolean estaVacia() { return tope == -1; }
    public int getTamanio()    { return tope + 1; }
    public void vaciar()       { datos = new Object[CAPACIDAD_INICIAL]; tope = -1; }

    private void redimensionar() {
        Object[] nuevo = new Object[datos.length * 2];
        System.arraycopy(datos, 0, nuevo, 0, datos.length);
        datos = nuevo;
    }

    @Override
    public String toString() {
        if (estaVacia()) return "PilaRecorrido[vacía]";
        StringBuilder sb = new StringBuilder("PilaRecorrido[tope→ ");
        for (int i = tope; i >= 0; i--) {
            sb.append(datos[i]);
            if (i > 0) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
