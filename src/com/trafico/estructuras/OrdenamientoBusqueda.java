package com.trafico.estructuras;

/**
 * <b>Algoritmos de Ordenamiento y Búsqueda</b> — Requisito de la Rúbrica UTP (Criterio 8.1 y 9).
 *
 * <p>Esta clase reúne la implementación desde cero de los algoritmos de
 * ordenamiento (Burbuja, Inserción, Selección) y de búsqueda (Secuencial, Binaria)
 * para operar sobre arreglos tradicionales de objetos comparables.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class OrdenamientoBusqueda {

    // Prevent instantiation
    private OrdenamientoBusqueda() {}

    // ─── 1. ALGORITMOS DE ORDENAMIENTO (Criterio 9) ───────────────────────────

    /**
     * Ordena un arreglo usando el algoritmo Burbuja (Bubble Sort).
     * Complejidad: O(n^2).
     *
     * @param <T>     tipo comparable
     * @param arreglo el arreglo a ordenar
     */
    public static <T extends Comparable<T>> void ordenarBurbuja(T[] arreglo) {
        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arreglo[j].compareTo(arreglo[j + 1]) > 0) {
                    T temporal = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = temporal;
                }
            }
        }
    }

    /**
     * Ordena un arreglo usando el algoritmo de Inserción (Insertion Sort).
     * Complejidad: O(n^2).
     *
     * @param <T>     tipo comparable
     * @param arreglo el arreglo a ordenar
     */
    public static <T extends Comparable<T>> void ordenarInsercion(T[] arreglo) {
        int n = arreglo.length;
        for (int i = 1; i < n; ++i) {
            T clave = arreglo[i];
            int j = i - 1;
            while (j >= 0 && arreglo[j].compareTo(clave) > 0) {
                arreglo[j + 1] = arreglo[j];
                j = j - 1;
            }
            arreglo[j + 1] = clave;
        }
    }

    /**
     * Ordena un arreglo usando el algoritmo de Selección (Selection Sort).
     * Complejidad: O(n^2).
     *
     * @param <T>     tipo comparable
     * @param arreglo el arreglo a ordenar
     */
    public static <T extends Comparable<T>> void ordenarSeleccion(T[] arreglo) {
        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < n; j++) {
                if (arreglo[j].compareTo(arreglo[indiceMinimo]) < 0) {
                    indiceMinimo = j;
                }
            }
            T temporal = arreglo[indiceMinimo];
            arreglo[indiceMinimo] = arreglo[i];
            arreglo[i] = temporal;
        }
    }

    // ─── 2. ALGORITMOS DE BÚSQUEDA (Criterio 9) ───────────────────────────────

    /**
     * Busca un elemento en el arreglo de forma secuencial.
     * Complejidad: O(n).
     *
     * @param <T>     tipo comparable
     * @param arreglo el arreglo de elementos
     * @param valor   el elemento buscado
     * @return índice del elemento, o -1 si no se encuentra
     */
    public static <T extends Comparable<T>> int busquedaSecuencial(T[] arreglo, T valor) {
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i].compareTo(valor) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Busca un elemento usando Búsqueda Binaria. El arreglo DEBE estar ordenado.
     * Complejidad: O(log n).
     *
     * @param <T>     tipo comparable
     * @param arreglo el arreglo ordenado de elementos
     * @param valor   el elemento buscado
     * @return índice del elemento, o -1 si no se encuentra
     */
    public static <T extends Comparable<T>> int busquedaBinaria(T[] arreglo, T valor) {
        int inicio = 0;
        int fin = arreglo.length - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int comparacion = arreglo[medio].compareTo(valor);

            if (comparacion == 0) {
                return medio;
            }
            if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }
}
