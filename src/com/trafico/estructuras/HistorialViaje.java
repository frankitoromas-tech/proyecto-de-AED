package com.trafico.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <b>Historial de Viaje</b> — Lista Doblemente Enlazada genérica, implementada desde cero.
 *
 * <p>Una lista doblemente enlazada conecta cada elemento con su anterior y su siguiente,
 * permitiendo recorrido en ambas direcciones y operaciones eficientes en los extremos.</p>
 *
 * <p><b>Uso en este proyecto:</b> cada vehículo mantiene un historial de todas las
 * intersecciones que ha visitado durante su viaje. La lista doble permite agregar al
 * final en O(1) y recorrer el historial en orden de visita.</p>
 *
 * @param <T> tipo de elemento almacenado
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class HistorialViaje<T> implements Iterable<T> {

    /** Nodo interno que enlaza un dato con sus vecinos en la lista. */
    private class Nodo {
        T dato;
        Nodo anterior;
        Nodo siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo cabeza;
    private Nodo cola;
    private int tamanio;

    public HistorialViaje() { cabeza = cola = null; tamanio = 0; }

    // ─── Inserción ─────────────────────────────────────────────────────────────

    /**
     * Agrega un elemento al final del historial. Complejidad: O(1).
     *
     * @param dato elemento a registrar
     */
    public void registrar(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacio()) {
            cabeza = cola = nuevo;
        } else {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Complejidad: O(1).
     *
     * @param dato elemento a agregar
     */
    public void agregarAlInicio(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacio()) {
            cabeza = cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamanio++;
    }

    // ─── Extracción ────────────────────────────────────────────────────────────

    /**
     * Elimina y retorna el primer elemento. Complejidad: O(1).
     *
     * @return el primer elemento registrado
     */
    public T eliminarPrimero() {
        if (estaVacio()) throw new NoSuchElementException("El historial está vacío.");
        T dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        if (cabeza != null) cabeza.anterior = null;
        else cola = null;
        tamanio--;
        return dato;
    }

    /**
     * Elimina y retorna el último elemento. Complejidad: O(1).
     *
     * @return el último elemento registrado
     */
    public T eliminarUltimo() {
        if (estaVacio()) throw new NoSuchElementException("El historial está vacío.");
        T dato = cola.dato;
        cola = cola.anterior;
        if (cola != null) cola.siguiente = null;
        else cabeza = null;
        tamanio--;
        return dato;
    }

    // ─── Consulta ──────────────────────────────────────────────────────────────

    public T verPrimero() {
        if (estaVacio()) throw new NoSuchElementException("El historial está vacío.");
        return cabeza.dato;
    }

    public T verUltimo() {
        if (estaVacio()) throw new NoSuchElementException("El historial está vacío.");
        return cola.dato;
    }

    public boolean estaVacio() { return tamanio == 0; }
    public int getTamanio()    { return tamanio; }
    public void vaciar()       { cabeza = cola = null; tamanio = 0; }

    /**
     * Devuelve el historial completo como texto legible.
     *
     * @return secuencia de elementos separados por " → "
     */
    public String mostrarRecorrido() {
        if (estaVacio()) return "(sin recorrido)";
        StringBuilder sb = new StringBuilder();
        Nodo actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) sb.append(" → ");
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo actual = cabeza;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.dato; actual = actual.siguiente; return dato;
            }
        };
    }

    @Override
    public String toString() {
        return "HistorialViaje[" + tamanio + " paradas | " + mostrarRecorrido() + "]";
    }
}
