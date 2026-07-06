package com.trafico.modelo;

/**
 * Categorías de vehículos que pueden circular en la red vial.
 */
public enum CategoriaVehiculo {
    /** Carro de uso personal. */
    PARTICULAR,
    /** Bus del sistema de transporte público urbano. */
    BUS,
    /** Metro o transporte guiado (simulado). */
    METRO,
    /** Ambulancia, bomberos o policía. Máxima prioridad. */
    EMERGENCIA,
    /** Camión o tráiler de carga pesada. */
    CARGA,
    /** Motocicleta con ventajas en tráfico congestionado. */
    MOTOCICLETA
}
