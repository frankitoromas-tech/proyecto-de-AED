package com.trafico.modelo;

/**
 * Situación operativa de un vehículo en cada momento de la simulación.
 */
public enum SituacionVehiculo {
    /** Circulando activamente por la ruta planificada. */
    EN_RUTA,
    /** Parado por un evento externo (accidente, bloqueo). */
    DETENIDO,
    /** Esperando a que el semáforo cambie a verde. */
    ESPERANDO_SEMAFORO,
    /** Llegó exitosamente a su destino. */
    LLEGO_AL_DESTINO,
    /** En cola, esperando para ingresar a la red vial. */
    EN_COLA_ENTRADA
}
