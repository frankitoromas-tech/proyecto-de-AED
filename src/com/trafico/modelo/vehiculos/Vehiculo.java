package com.trafico.modelo.vehiculos;

import com.trafico.estructuras.HistorialViaje;
import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;
import com.trafico.modelo.SituacionVehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Plantilla base de cualquier vehículo que circula en la red vial.
 *
 * <p>Define los atributos y comportamientos comunes a todos los tipos de vehículo.
 * Las subclases concretas solo necesitan especificar sus características únicas
 * y sobreescribir {@link #getDescripcion()}.</p>
 *
 * <p><b>Jerarquía:</b></p>
 * <pre>
 *   Vehiculo (abstracta)
 *   ├── AutoParticular       → 80 km/h | prioridad 5
 *   ├── UnidadEmergencia     → 120 km/h | prioridad 1 🚨
 *   ├── BusUrbano            → 60 km/h | prioridad 3
 *   ├── CamionCarga          → 70 km/h | prioridad 4
 *   └── Motocicleta          → 90 km/h | prioridad 4
 * </pre>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public abstract class Vehiculo implements Movible {

    /** Código único generado automáticamente al crear el vehículo. */
    protected final String id;

    /** Número de placa del vehículo. */
    protected String placa;

    /** Categoría del vehículo (PARTICULAR, BUS, EMERGENCIA, etc.). */
    protected final CategoriaVehiculo categoria;

    /** Velocidad a la que circula actualmente (km/h). */
    protected double velocidadActual;

    /** Velocidad máxima que puede alcanzar este vehículo (km/h). */
    protected final double velocidadMaxima;

    /** Intersección donde se encuentra el vehículo en este momento. */
    protected Interseccion posicion;

    /** Intersección a la que quiere llegar el vehículo. */
    protected Interseccion destino;

    /** La secuencia de intersecciones que el vehículo debe recorrer. */
    protected List<Interseccion> rutaAsignada;

    /** Índice de la próxima intersección en la ruta que debe alcanzar. */
    protected int siguientePunto;

    /** Estado actual del vehículo en la simulación. */
    protected SituacionVehiculo situacion;

    /**
     * Nivel de prioridad para el despacho en intersecciones congestionadas.
     * Menor número = mayor prioridad. Emergencias tienen prioridad 1.
     */
    protected int prioridad;

    /** Registro de todas las intersecciones que el vehículo ha visitado. */
    protected HistorialViaje<Interseccion> historial;

    /** Ticks acumulados esperando en semáforos. */
    protected int ticksEsperando;

    /** Ticks totales que lleva el vehículo desde que empezó su viaje. */
    protected int ticksEnViaje;

    /**
     * Crea un vehículo con sus características básicas.
     *
     * @param placa          número de placa
     * @param categoria      tipo de vehículo
     * @param velocidadMaxima velocidad máxima en km/h
     * @param origen         intersección de partida
     * @param destino        intersección de llegada
     * @param prioridad      prioridad de paso (1 = máxima)
     */
    protected Vehiculo(String placa, CategoriaVehiculo categoria, double velocidadMaxima,
                       Interseccion origen, Interseccion destino, int prioridad) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.placa = placa;
        this.categoria = categoria;
        this.velocidadMaxima = velocidadMaxima;
        this.velocidadActual = 0.0;
        this.posicion = origen;
        this.destino = destino;
        this.prioridad = prioridad;
        this.situacion = SituacionVehiculo.EN_COLA_ENTRADA;
        this.rutaAsignada = new ArrayList<>();
        this.siguientePunto = 0;
        this.historial = new HistorialViaje<>();
        this.ticksEsperando = 0;
        this.ticksEnViaje = 0;
    }

    // ─── Implementación de Movible ─────────────────────────────────────────────

    @Override
    public void avanzar() {
        if (situacion == SituacionVehiculo.LLEGO_AL_DESTINO) return;
        if (situacion == SituacionVehiculo.ESPERANDO_SEMAFORO) { ticksEsperando++; return; }
        if (rutaAsignada.isEmpty() || siguientePunto >= rutaAsignada.size()) {
            situacion = SituacionVehiculo.LLEGO_AL_DESTINO;
            velocidadActual = 0;
            return;
        }

        situacion = SituacionVehiculo.EN_RUTA;
        velocidadActual = velocidadMaxima;
        posicion = rutaAsignada.get(siguientePunto++);
        historial.registrar(posicion);
        ticksEnViaje++;

        if (posicion.equals(destino)) {
            situacion = SituacionVehiculo.LLEGO_AL_DESTINO;
            velocidadActual = 0;
        }
    }

    @Override
    public void detenerse() {
        situacion = SituacionVehiculo.ESPERANDO_SEMAFORO;
        velocidadActual = 0;
    }

    @Override
    public void reanudar() {
        if (situacion == SituacionVehiculo.ESPERANDO_SEMAFORO
                || situacion == SituacionVehiculo.DETENIDO) {
            situacion = SituacionVehiculo.EN_RUTA;
            velocidadActual = velocidadMaxima;
        }
    }

    @Override
    public void seguirRuta(List<Interseccion> ruta) {
        this.rutaAsignada = new ArrayList<>(ruta);
        this.siguientePunto = 0;
        if (!ruta.isEmpty()) situacion = SituacionVehiculo.EN_RUTA;
    }

    @Override
    public double tiempoEstimadoDeArribal() {
        if (rutaAsignada.isEmpty() || velocidadMaxima <= 0) return -1;
        int puntosRestantes = rutaAsignada.size() - siguientePunto;
        // Estimación: ~300m promedio por intersección a velocidad máxima
        return (puntosRestantes * 0.3 / velocidadMaxima) * 60.0;
    }

    /** @return true si el vehículo llegó exitosamente a su destino */
    public boolean haLlegado() {
        return situacion == SituacionVehiculo.LLEGO_AL_DESTINO;
    }

    /**
     * Descripción detallada específica del tipo de vehículo.
     * Cada subclase debe implementar este método.
     *
     * @return texto descriptivo del vehículo
     */
    public abstract String getDescripcion();

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public String getId()                               { return id; }
    public String getPlaca()                            { return placa; }
    public void setPlaca(String placa)                  { this.placa = placa; }
    public CategoriaVehiculo getCategoria()             { return categoria; }
    public double getVelocidadActual()                  { return velocidadActual; }
    public double getVelocidadMaxima()                  { return velocidadMaxima; }
    public Interseccion getPosicion()                   { return posicion; }
    public void setPosicion(Interseccion posicion)      { this.posicion = posicion; }
    public Interseccion getDestino()                    { return destino; }
    public void setDestino(Interseccion destino)        { this.destino = destino; }
    public List<Interseccion> getRutaAsignada()         { return rutaAsignada; }
    public SituacionVehiculo getSituacion()              { return situacion; }
    public void setSituacion(SituacionVehiculo s)       { this.situacion = s; }
    public int getPrioridad()                           { return prioridad; }
    public HistorialViaje<Interseccion> getHistorial()  { return historial; }
    public int getTicksEsperando()                      { return ticksEsperando; }
    public int getTicksEnViaje()                        { return ticksEnViaje; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | %s → %s | %s",
            id, placa, categoria,
            posicion != null ? posicion.getNombre() : "—",
            destino  != null ? destino.getNombre()  : "—",
            situacion);
    }
}
