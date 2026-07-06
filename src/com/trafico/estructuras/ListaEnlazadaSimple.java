package com.trafico.estructuras;

/**
 * <b>Lista Enlazada Simple</b> — Estructura dinámica de datos implementada desde cero.
 *
 * <p>Mantiene una secuencia de nodos conectados de forma unidireccional.
 * Ofrece la base clásica de las estructuras dinámicas.</p>
 *
 * <p><b>Requisito de la Rúbrica UTP (Criterio 8.2):</b> Debe incluir una implementación
 * real de nodos con operaciones de insertar, eliminar, buscar y recorrer.</p>
 *
 * @param <T> tipo de elemento almacenado
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class ListaEnlazadaSimple<T> {

    /** Nodo interno con referencia al dato y al siguiente nodo. */
    private class Nodo {
        T dato;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo cabeza;
    private int tamanio;

    public ListaEnlazadaSimple() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    // ─── Operación 1: Insertar ─────────────────────────────────────────────────

    /**
     * Inserta un elemento al final de la lista.
     *
     * @param dato el dato a insertar
     */
    public void insertar(T dato) {
        Nodo nuevo = new Nodo(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    // ─── Operación 2: Eliminar ─────────────────────────────────────────────────

    /**
     * Elimina la primera ocurrencia del elemento indicado.
     *
     * @param dato el dato a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;

        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }

        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamanio--;
            return true;
        }

        return false;
    }

    // ─── Operación 3: Buscar ───────────────────────────────────────────────────

    /**
     * Busca si un elemento existe en la lista.
     *
     * @param dato el dato a buscar
     * @return true si el dato existe, false en caso contrario
     */
    public boolean buscar(T dato) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) return true;
            actual = actual.siguiente;
        }
        return false;
    }

    // ─── Operación 4: Recorrer ─────────────────────────────────────────────────

    /**
     * Recorre e imprime los elementos de la lista en consola.
     */
    public void recorrer() {
        if (cabeza == null) {
            System.out.println("(lista vacía)");
            return;
        }
        Nodo actual = cabeza;
        while (actual != null) {
            System.out.print("[" + actual.dato + "]");
            if (actual.siguiente != null) System.out.print(" -> ");
            actual = actual.siguiente;
        }
        System.out.println();
    }

    public int getTamanio() { return tamanio; }
    public boolean estaVacia() { return cabeza == null; }
}
