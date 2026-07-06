package com.trafico.modelo;

/**
 * Estado de la luz de un semáforo en un instante de la simulación.
 */
public enum LuzSemaforo {
    /** Luz verde — los vehículos pueden avanzar. */
    VERDE,
    /** Luz amarilla — precaución, prepararse para detenerse. */
    AMARILLO,
    /** Luz roja — detención obligatoria. */
    ROJO,
    /** Modo de emergencia — verde forzado por paso de unidad de emergencia. */
    EMERGENCIA_ACTIVA
}
