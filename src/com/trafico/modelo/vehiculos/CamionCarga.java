package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Camión o tráiler de carga pesada.
 * Más lento que el resto de vehículos y con mayor impacto en la congestión.
 * Velocidad máxima: 70 km/h. Prioridad: 4.
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class CamionCarga extends Vehiculo {

    /** Descripción de la mercancía transportada. */
    private final String tipoCarga;

    /** Peso total de la carga en toneladas. */
    private final double pesoToneladas;

    /** Indica si el camión transporta mercancía peligrosa (requiere ruta alternativa). */
    private final boolean cargaPeligrosa;

    /**
     * Crea un camión de carga.
     *
     * @param placa          número de placa
     * @param tipoCarga      descripción de la mercancía
     * @param pesoToneladas  peso de la carga en toneladas
     * @param cargaPeligrosa true si la carga es peligrosa
     * @param origen         intersección de salida
     * @param destino        intersección de llegada
     */
    public CamionCarga(String placa, String tipoCarga, double pesoToneladas,
                       boolean cargaPeligrosa, Interseccion origen, Interseccion destino) {
        super(placa, CategoriaVehiculo.CARGA, 70.0, origen, destino, 4);
        this.tipoCarga = tipoCarga;
        this.pesoToneladas = pesoToneladas;
        this.cargaPeligrosa = cargaPeligrosa;
    }

    public String getTipoCarga()      { return tipoCarga; }
    public double getPesoToneladas()  { return pesoToneladas; }
    public boolean isCargaPeligrosa() { return cargaPeligrosa; }

    @Override
    public String getDescripcion() {
        return String.format("Camión de Carga | Placa: %s | Carga: %s (%.1f ton) | %s | "
            + "Vel.Max: %.0f km/h | Prioridad: %d",
            placa, tipoCarga, pesoToneladas,
            cargaPeligrosa ? "⚠ PELIGROSA" : "normal",
            velocidadMaxima, prioridad);
    }
}
