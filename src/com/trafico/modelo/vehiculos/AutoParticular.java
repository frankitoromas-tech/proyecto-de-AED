package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Carro de uso personal — el tipo de vehículo más común en la red vial.
 * Velocidad máxima: 80 km/h. Prioridad de paso: 5 (normal).
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class AutoParticular extends Vehiculo {

    /** Modelo del carro (ej. "Toyota Corolla", "Chevrolet Aveo"). */
    private String modelo;

    /**
     * Crea un carro particular.
     *
     * @param placa   número de placa
     * @param modelo  modelo del carro
     * @param origen  intersección de salida
     * @param destino intersección de llegada
     */
    public AutoParticular(String placa, String modelo, Interseccion origen, Interseccion destino) {
        super(placa, CategoriaVehiculo.PARTICULAR, 80.0, origen, destino, 5);
        this.modelo = modelo;
    }

    public String getModelo()           { return modelo; }
    public void setModelo(String modelo){ this.modelo = modelo; }

    @Override
    public String getDescripcion() {
        return String.format("Auto Particular | Placa: %s | Modelo: %s | Vel.Max: %.0f km/h | Prioridad: %d",
            placa, modelo, velocidadMaxima, prioridad);
    }
}
