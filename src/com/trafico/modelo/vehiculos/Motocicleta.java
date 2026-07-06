package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Motocicleta — vehículo ágil con ventajas en tráfico congestionado.
 * La motocicleta puede adelantar en calles saturadas reduciendo su penalización
 * de congestión a la mitad.
 * Velocidad máxima: 90 km/h. Prioridad: 4.
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class Motocicleta extends Vehiculo {

    /** Cilindrada de la moto en cc (ej. 125, 250, 600). */
    private final int cilindrada;

    /**
     * Crea una motocicleta.
     *
     * @param placa       número de placa
     * @param cilindrada  cilindrada del motor en cc
     * @param origen      intersección de salida
     * @param destino     intersección de llegada
     */
    public Motocicleta(String placa, int cilindrada, Interseccion origen, Interseccion destino) {
        super(placa, CategoriaVehiculo.MOTOCICLETA, 90.0, origen, destino, 4);
        this.cilindrada = cilindrada;
    }

    public int getCilindrada() { return cilindrada; }

    @Override
    public String getDescripcion() {
        return String.format("Motocicleta | Placa: %s | Motor: %dcc | "
            + "Vel.Max: %.0f km/h | Prioridad: %d",
            placa, cilindrada, velocidadMaxima, prioridad);
    }
}
