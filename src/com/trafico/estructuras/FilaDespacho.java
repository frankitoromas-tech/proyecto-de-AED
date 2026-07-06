package com.trafico.estructuras;

import com.trafico.modelo.vehiculos.Vehiculo;

/**
 * <b>Fila de Despacho de Vehículos</b> — Cola de Prioridad basada en Min-Heap.
 *
 * <p>Despacha vehículos priorizando a los de mayor urgencia. Como los vehículos
 * de emergencia tienen prioridad numérica 1 (el menor valor), siempre son
 * atendidos primero — incluso si llegaron después que otros vehículos.</p>
 *
 * <p><b>Uso en este proyecto:</b> gestiona el orden de paso de vehículos en
 * intersecciones congestionadas. Una {@link com.trafico.modelo.vehiculos.UnidadEmergencia}
 * siempre pasa primero, sin importar cuántos autos estén esperando.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class FilaDespacho {

    private Vehiculo[] datos;
    private int tamanio;
    private static final int CAPACIDAD_INICIAL = 20;

    /** Crea una fila de despacho vacía. */
    public FilaDespacho() {
        datos = new Vehiculo[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    // ─── Operaciones principales ───────────────────────────────────────────────

    /**
     * Agrega un vehículo a la fila manteniendo el orden de prioridad.
     * Complejidad: O(log n).
     *
     * @param vehiculo el vehículo a encolar
     */
    public void encolar(Vehiculo vehiculo) {
        if (vehiculo == null) throw new IllegalArgumentException("El vehículo no puede ser null.");
        if (tamanio >= datos.length) redimensionar();
        datos[tamanio] = vehiculo;
        subirEnFila(tamanio);
        tamanio++;
    }

    /**
     * Extrae y retorna el vehículo de mayor prioridad (prioridad numérica más baja).
     * Complejidad: O(log n).
     *
     * @return el vehículo que debe ser despachado primero
     * @throws java.util.NoSuchElementException si la fila está vacía
     */
    public Vehiculo despachar() {
        if (estaVacia()) throw new java.util.NoSuchElementException("La fila de despacho está vacía.");
        Vehiculo prioritario = datos[0];
        tamanio--;
        datos[0] = datos[tamanio];
        datos[tamanio] = null;
        if (!estaVacia()) bajarEnFila(0);
        return prioritario;
    }

    /**
     * Consulta el próximo vehículo a despachar sin sacarlo de la fila.
     *
     * @return el vehículo de mayor prioridad
     */
    public Vehiculo verProximo() {
        if (estaVacia()) throw new java.util.NoSuchElementException("La fila está vacía.");
        return datos[0];
    }

    public boolean estaVacia() { return tamanio == 0; }
    public int getTamanio()    { return tamanio; }
    public void vaciar()       { datos = new Vehiculo[CAPACIDAD_INICIAL]; tamanio = 0; }

    // ─── Lógica interna del heap ───────────────────────────────────────────────

    private void subirEnFila(int pos) {
        while (pos > 0) {
            int padre = (pos - 1) / 2;
            if (datos[pos].getPrioridad() < datos[padre].getPrioridad()) {
                intercambiar(pos, padre); pos = padre;
            } else break;
        }
    }

    private void bajarEnFila(int pos) {
        while (true) {
            int izq = 2 * pos + 1, der = 2 * pos + 2, menor = pos;
            if (izq < tamanio && datos[izq].getPrioridad() < datos[menor].getPrioridad()) menor = izq;
            if (der < tamanio && datos[der].getPrioridad() < datos[menor].getPrioridad()) menor = der;
            if (menor != pos) { intercambiar(pos, menor); pos = menor; }
            else break;
        }
    }

    private void intercambiar(int i, int j) {
        Vehiculo tmp = datos[i]; datos[i] = datos[j]; datos[j] = tmp;
    }

    private void redimensionar() {
        Vehiculo[] nuevo = new Vehiculo[datos.length * 2];
        System.arraycopy(datos, 0, nuevo, 0, datos.length);
        datos = nuevo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FilaDespacho[\n");
        for (int i = 0; i < tamanio; i++)
            sb.append(String.format("  P%d → %s (%s)%n",
                datos[i].getPrioridad(), datos[i].getPlaca(), datos[i].getCategoria()));
        return sb.append("]").toString();
    }
}
