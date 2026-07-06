package com.trafico.semaforos;

/**
 * Contrato del patrón Observer para los semáforos.
 *
 * <p>Cualquier entidad que quiera reaccionar automáticamente al cambio de luz
 * de un semáforo debe implementar esta interfaz y suscribirse al semáforo
 * con {@link Semaforo#suscribir(ObservadorSemaforo)}.</p>
 *
 * <p>En la práctica, los vehículos cercanos a una intersección se suscriben
 * al semáforo de ese cruce. Al cambiar a verde, el vehículo recibe la
 * notificación y reanuda su marcha automáticamente.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public interface ObservadorSemaforo {

    /**
     * Llamado automáticamente cuando la luz del semáforo cambia de estado.
     *
     * @param semaforo el semáforo que cambió de estado (contiene el nuevo estado)
     */
    void onCambioLuz(Semaforo semaforo);
}
