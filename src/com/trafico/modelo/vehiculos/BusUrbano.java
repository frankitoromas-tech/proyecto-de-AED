package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Bus urbano del sistema de transporte público.
 * Circula por rutas fijas y tiene prioridad intermedia.
 * Velocidad máxima: 60 km/h. Prioridad: 3.
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class BusUrbano extends Vehiculo {

    /** Número o nombre de la ruta del bus (ej. "Ruta 12", "Troncal Norte"). */
    private final String numeroRuta;

    /** Capacidad máxima de pasajeros. */
    private final int capacidadPasajeros;

    /** Pasajeros actualmente a bordo. */
    private int pasajerosActuales;

    /**
     * Crea un bus urbano.
     *
     * @param placa               número de placa
     * @param numeroRuta          identificador de la ruta del bus
     * @param capacidadPasajeros  cuántos pasajeros puede llevar
     * @param origen              intersección de salida
     * @param destino             intersección de llegada
     */
    public BusUrbano(String placa, String numeroRuta, int capacidadPasajeros,
                     Interseccion origen, Interseccion destino) {
        super(placa, CategoriaVehiculo.BUS, 60.0, origen, destino, 3);
        this.numeroRuta = numeroRuta;
        this.capacidadPasajeros = capacidadPasajeros;
        this.pasajerosActuales = 0;
    }

    public String getNumeroRuta()           { return numeroRuta; }
    public int getCapacidadPasajeros()      { return capacidadPasajeros; }
    public int getPasajerosActuales()       { return pasajerosActuales; }

    /** Simula la subida de pasajeros en una parada. */
    public void subirPasajeros(int cantidad) {
        pasajerosActuales = Math.min(capacidadPasajeros, pasajerosActuales + cantidad);
    }

    /** Simula la bajada de pasajeros en una parada. */
    public void bajarPasajeros(int cantidad) {
        pasajerosActuales = Math.max(0, pasajerosActuales - cantidad);
    }

    @Override
    public String getDescripcion() {
        return String.format("Bus Urbano | Placa: %s | Ruta: %s | Pasajeros: %d/%d | "
            + "Vel.Max: %.0f km/h | Prioridad: %d",
            placa, numeroRuta, pasajerosActuales, capacidadPasajeros, velocidadMaxima, prioridad);
    }
}
