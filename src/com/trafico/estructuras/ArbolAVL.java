package com.trafico.estructuras;

/**
 * <b>Árbol AVL Genérico</b> — Árbol binario de búsqueda auto-balanceado implementado desde cero.
 *
 * <p>Un árbol AVL mantiene su altura balanceada de tal forma que para cualquier nodo,
 * la diferencia de altura entre su subárbol izquierdo y derecho (factor de balanceo)
 * es a lo sumo 1. Esto garantiza que operaciones como buscar, insertar y eliminar
 * tengan una complejidad de O(log n).</p>
 *
 * <p><b>Requisito de la Rúbrica UTP (Criterio 8.5):</b> Debe demostrarse inserción,
 * búsqueda, eliminación y recorridos balanceados.</p>
 *
 * @param <K> clave de búsqueda del nodo (debe ser comparable)
 * @param <V> valor almacenado en el nodo
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class ArbolAVL<K extends Comparable<K>, V> {

    /** Nodo interno del árbol AVL. */
    private class Nodo {
        K clave;
        V valor;
        int altura;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.altura = 1;
        }
    }

    private Nodo raiz;
    private int tamanio;

    public ArbolAVL() {
        this.raiz = null;
        this.tamanio = 0;
    }

    // ─── Operación 1: Insertar (con balanceo) ──────────────────────────────────

    /**
     * Inserta un nuevo par clave-valor en el árbol. Si la clave ya existe, actualiza el valor.
     * Balancea el árbol de forma automática mediante rotaciones simples y dobles.
     *
     * @param clave la clave única del nodo
     * @param valor el valor a almacenar
     */
    public void insertar(K clave, V valor) {
        if (clave == null) throw new IllegalArgumentException("La clave no puede ser null.");
        raiz = insertarRec(raiz, clave, valor);
    }

    private Nodo insertarRec(Nodo nodo, K clave, V valor) {
        if (nodo == null) {
            tamanio++;
            return new Nodo(clave, valor);
        }

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, clave, valor);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRec(nodo.derecho, clave, valor);
        } else {
            nodo.valor = valor; // Clave duplicada: actualiza valor sin aumentar tamaño
            return nodo;
        }

        // Actualizar altura del nodo ancestro
        nodo.altura = 1 + Math.max(getAltura(nodo.izquierdo), getAltura(nodo.derecho));

        // Obtener factor de balanceo y balancear si es necesario
        return balancear(nodo, clave);
    }

    // ─── Operación 2: Buscar ───────────────────────────────────────────────────

    /**
     * Busca el valor asociado a la clave dada.
     *
     * @param clave la clave a buscar
     * @return el valor asociado, o null si la clave no existe
     */
    public V buscar(K clave) {
        if (clave == null) return null;
        Nodo resultado = buscarRec(raiz, clave);
        return resultado != null ? resultado.valor : null;
    }

    private Nodo buscarRec(Nodo nodo, K clave) {
        if (nodo == null) return null;
        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion == 0) return nodo;
        return comparacion < 0 ? buscarRec(nodo.izquierdo, clave) : buscarRec(nodo.derecho, clave);
    }

    // ─── Operación 3: Eliminar (con balanceo) ──────────────────────────────────

    /**
     * Elimina el nodo asociado a la clave dada y restaura el balanceo del árbol.
     *
     * @param clave la clave a eliminar
     * @return true si se eliminó correctamente, false si la clave no existía
     */
    public boolean eliminar(K clave) {
        if (clave == null || buscar(clave) == null) return false;
        raiz = eliminarRec(raiz, clave);
        tamanio--;
        return true;
    }

    private Nodo eliminarRec(Nodo nodo, K clave) {
        if (nodo == null) return null;

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, clave);
        } else if (comparacion > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, clave);
        } else {
            // Nodo con un solo hijo o sin hijos
            if (nodo.izquierdo == null || nodo.derecho == null) {
                Nodo temporal = (nodo.izquierdo != null) ? nodo.izquierdo : nodo.derecho;
                if (temporal == null) {
                    nodo = null; // Sin hijos
                } else {
                    nodo = temporal; // Copia el contenido del hijo no nulo
                }
            } else {
                // Nodo con dos hijos: obtener el sucesor en orden (mínimo del subárbol derecho)
                Nodo temporal = obtenerMinimo(nodo.derecho);
                nodo.clave = temporal.clave;
                nodo.valor = temporal.valor;
                nodo.derecho = eliminarRec(nodo.derecho, temporal.clave);
            }
        }

        if (nodo == null) return null;

        // Actualizar altura
        nodo.altura = 1 + Math.max(getAltura(nodo.izquierdo), getAltura(nodo.derecho));

        // Balancear el nodo
        int balance = getFactorBalance(nodo);

        // Caso Izquierda Izquierda
        if (balance > 1 && getFactorBalance(nodo.izquierdo) >= 0) {
            return rotarDerecha(nodo);
        }
        // Caso Izquierda Derecha
        if (balance > 1 && getFactorBalance(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }
        // Caso Derecha Derecha
        if (balance < -1 && getFactorBalance(nodo.derecho) <= 0) {
            return rotarIzquierda(nodo);
        }
        // Caso Derecha Izquierda
        if (balance < -1 && getFactorBalance(nodo.derecho) > 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // ─── Operación 4: Recorridos ───────────────────────────────────────────────

    /** Realiza un recorrido In-Order (de menor a mayor clave) imprimiendo los elementos. */
    public void recorridoInOrder() {
        recorridoInOrderRec(raiz);
        System.out.println();
    }

    private void recorridoInOrderRec(Nodo nodo) {
        if (nodo != null) {
            recorridoInOrderRec(nodo.izquierdo);
            System.out.print("[" + nodo.clave + ":" + nodo.valor + "] ");
            recorridoInOrderRec(nodo.derecho);
        }
    }

    public int getTamanio() { return tamanio; }
    public boolean estaVacio() { return raiz == null; }

    // ─── Auxiliares de Balanceo y Rotación ─────────────────────────────────────

    private int getAltura(Nodo n) { return n == null ? 0 : n.altura; }

    private int getFactorBalance(Nodo n) { return n == null ? 0 : getAltura(n.izquierdo) - getAltura(n.derecho); }

    private Nodo obtenerMinimo(Nodo nodo) {
        Nodo actual = nodo;
        while (actual.izquierdo != null) actual = actual.izquierdo;
        return actual;
    }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izquierdo;
        Nodo T2 = x.derecho;

        // Rotación
        x.derecho = y;
        y.izquierdo = T2;

        // Actualizar alturas
        y.altura = Math.max(getAltura(y.izquierdo), getAltura(y.derecho)) + 1;
        x.altura = Math.max(getAltura(x.izquierdo), getAltura(x.derecho)) + 1;

        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.derecho;
        Nodo T2 = y.izquierdo;

        // Rotación
        y.izquierdo = x;
        x.derecho = T2;

        // Actualizar alturas
        x.altura = Math.max(getAltura(x.izquierdo), getAltura(x.derecho)) + 1;
        y.altura = Math.max(getAltura(y.izquierdo), getAltura(y.derecho)) + 1;

        return y;
    }

    private Nodo balancear(Nodo nodo, K clave) {
        int balance = getFactorBalance(nodo);

        // Caso Izquierda Izquierda
        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) < 0) {
            return rotarDerecha(nodo);
        }
        // Caso Derecha Derecha
        if (balance < -1 && clave.compareTo(nodo.derecho.clave) > 0) {
            return rotarIzquierda(nodo);
        }
        // Caso Izquierda Derecha (Doble Rotación)
        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) > 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }
        // Caso Derecha Izquierda (Doble Rotación)
        if (balance < -1 && clave.compareTo(nodo.derecho.clave) < 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }
}
