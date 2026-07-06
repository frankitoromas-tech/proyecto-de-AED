package com.trafico.modelo.vehiculos;

import com.trafico.modelo.Interseccion;

import java.util.List;

/**
 * Contrato de movimiento que debe cumplir cualquier vehículo del sistema.
 * Aplica el principio de Segregación de Interfaces (SOLID-I).
 */
public interface Movible {

    /**
     * Avanza el vehículo un paso (un tick de simulación) por su ruta actual.
     * Actualiza la posición y el estado del vehículo automáticamente.
     */
    void avanzar();

    /**
     * Detiene el vehículo — típicamente cuando enfrenta un semáforo en rojo.
     */
    void detenerse();

    /**
     * Reanuda el movimiento después de una detención.
     */
    void reanudar();

    /**
     * Asigna al vehículo la ruta que debe seguir, calculada por un algoritmo de navegación.
     *
     * @param ruta lista ordenada de intersecciones desde el origen hasta el destino
     */
    void seguirRuta(List<Interseccion> ruta);

    /**
     * Estima el tiempo restante para llegar al destino.
     *
     * @return tiempo estimado en minutos, o -1 si no tiene ruta asignada
     */
    double tiempoEstimadoDeArribal();
}
