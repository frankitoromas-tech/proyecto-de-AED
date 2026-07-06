package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Unidad de emergencia — Ambulancia, Bomberos o Policía.
 *
 * <p>Tiene la <b>máxima prioridad</b> del sistema (prioridad = 1). Cuando su sirena
 * está activa, los semáforos en su ruta cambian automáticamente a verde para
 * permitirle paso libre. Velocidad máxima: 120 km/h.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class UnidadEmergencia extends Vehiculo {

    /** Tipo específico de unidad de emergencia. */
    public enum Tipo { AMBULANCIA, BOMBEROS, POLICIA }

    private final Tipo tipo;

    /** Indica si la sirena está activa (activa el modo emergencia en semáforos). */
    private boolean sirenaActiva;

    /** Contador de cuántos semáforos interrumpió durante su recorrido. */
    private int semaforosInterrumpidos;

    /**
     * Crea una unidad de emergencia.
     *
     * @param placa   número de placa
     * @param tipo    tipo de unidad (AMBULANCIA, BOMBEROS, POLICIA)
     * @param origen  intersección de salida
     * @param destino intersección de llegada
     */
    public UnidadEmergencia(String placa, Tipo tipo, Interseccion origen, Interseccion destino) {
        super(placa, CategoriaVehiculo.EMERGENCIA, 120.0, origen, destino, 1);
        this.tipo = tipo;
        this.sirenaActiva = true; // por defecto viene con sirena activa
        this.semaforosInterrumpidos = 0;
    }

    /** Activa la sirena — los semáforos en su ruta cederán el paso. */
    public void activarSirena() { this.sirenaActiva = true; }

    /** Apaga la sirena — la unidad circula como vehículo normal. */
    public void apagarSirena()  { this.sirenaActiva = false; }

    /** @return true si la sirena está encendida */
    public boolean isSirenaActiva() { return sirenaActiva; }

    /** @return tipo de unidad de emergencia */
    public Tipo getTipoEmergencia() { return tipo; }

    /** @return cuántos semáforos interrumpió esta unidad durante la simulación */
    public int getSemaforosInterrumpidos() { return semaforosInterrumpidos; }

    /** Registra que esta unidad interrumpió un semáforo más. */
    public void registrarInterrupcionSemaforo() { semaforosInterrumpidos++; }

    @Override
    public String getDescripcion() {
        return String.format("Unidad de Emergencia | %s | Placa: %s | Sirena: %s | "
            + "Vel.Max: %.0f km/h | Prioridad: MÁXIMA (%d) | Semáforos interrumpidos: %d",
            tipo, placa, sirenaActiva ? "🚨 ACTIVA" : "apagada",
            velocidadMaxima, prioridad, semaforosInterrumpidos);
    }
}
