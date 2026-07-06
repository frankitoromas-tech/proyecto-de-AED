package com.trafico.semaforos;

import com.trafico.modelo.Interseccion;
import com.trafico.modelo.LuzSemaforo;

import java.util.ArrayList;
import java.util.List;

/**
 * Semáforo inteligente que controla el flujo vehicular en una intersección.
 *
 * <p>El ciclo normal es: VERDE → AMARILLO → ROJO → VERDE.
 * Cada estado dura una cantidad configurable de ticks de simulación.
 * El semáforo se ajusta dinámicamente: si hay muchos vehículos esperando,
 * extiende el tiempo en verde para aliviar la congestión.</p>
 *
 * <p>Cuando una {@link com.trafico.modelo.vehiculos.UnidadEmergencia} con sirena
 * activa se aproxima, el semáforo cambia a modo emergencia (VERDE forzado)
 * hasta que la unidad pase.</p>
 *
 * <p>Implementa el <b>patrón Observer</b>: los vehículos se suscriben al semáforo
 * y son notificados automáticamente cuando la luz cambia.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class Semaforo {

    /** ID único del semáforo. */
    private final String id;

    /** La intersección donde está instalado este semáforo. */
    private final Interseccion interseccion;

    /** Estado actual de la luz. */
    private LuzSemaforo luzActual;

    /** Duración del verde en ticks (1 tick = 1 segundo simulado). */
    private int duracionVerde;

    /** Duración del rojo en ticks. */
    private int duracionRojo;

    /** Duración del amarillo en ticks (siempre 3). */
    private static final int DURACION_AMARILLO = 3;

    /** Cuántos ticks lleva el semáforo en el estado actual. */
    private int ticksEnEstadoActual;

    /** Vehículos esperando frente a este semáforo en este momento. */
    private int vehiculosEnEspera;

    /** Umbral de vehículos para extender el tiempo verde. */
    private static final int UMBRAL_EXTENSION_VERDE = 5;

    /** Tiempo máximo que se puede extender el verde (en ticks). */
    private static final int EXTENSION_MAXIMA_VERDE = 10;

    /** Si es true, el semáforo está en modo emergencia (verde forzado). */
    private boolean modoEmergenciaActivo;

    /** Total de ticks acumulados en rojo durante toda la simulación. */
    private int ticksTotalesEnRojo;

    /** Lista de observadores suscritos a este semáforo (patrón Observer). */
    private final List<ObservadorSemaforo> observadores;

    /**
     * Crea un semáforo en la intersección indicada.
     *
     * @param id            identificador único
     * @param interseccion  la intersección que controla
     * @param duracionVerde duración del verde en ticks
     * @param duracionRojo  duración del rojo en ticks
     */
    public Semaforo(String id, Interseccion interseccion, int duracionVerde, int duracionRojo) {
        this.id = id;
        this.interseccion = interseccion;
        this.duracionVerde = duracionVerde;
        this.duracionRojo = duracionRojo;
        this.luzActual = LuzSemaforo.ROJO; // empieza en rojo por seguridad
        this.ticksEnEstadoActual = 0;
        this.vehiculosEnEspera = 0;
        this.modoEmergenciaActivo = false;
        this.ticksTotalesEnRojo = 0;
        this.observadores = new ArrayList<>();
        interseccion.setTieneSemaforo(true);
    }

    // ─── Ciclo del semáforo ────────────────────────────────────────────────────

    /**
     * Avanza el semáforo un tick de simulación.
     * Si el tiempo en el estado actual agotó su duración, cambia al siguiente estado
     * y notifica a todos los vehículos suscritos.
     */
    public void avanzarTick() {
        if (modoEmergenciaActivo) { ticksEnEstadoActual++; return; }

        ticksEnEstadoActual++;
        if (luzActual == LuzSemaforo.ROJO) ticksTotalesEnRojo++;

        int duracionActual = getDuracionEstadoActual();
        if (ticksEnEstadoActual >= duracionActual) {
            ticksEnEstadoActual = 0;
            cambiarAlSiguienteEstado();
            notificarObservadores();
        }
    }

    /**
     * Activa el modo emergencia: el semáforo permanece en verde hasta que se desactive.
     * Llama a este método cuando una unidad de emergencia con sirena se aproxima.
     */
    public void activarModoEmergencia() {
        if (!modoEmergenciaActivo) {
            modoEmergenciaActivo = true;
            LuzSemaforo anterior = luzActual;
            luzActual = LuzSemaforo.EMERGENCIA_ACTIVA;
            if (anterior != LuzSemaforo.EMERGENCIA_ACTIVA) notificarObservadores();
        }
    }

    /**
     * Desactiva el modo emergencia y retoma el ciclo normal desde verde.
     */
    public void desactivarModoEmergencia() {
        modoEmergenciaActivo = false;
        luzActual = LuzSemaforo.VERDE;
        ticksEnEstadoActual = 0;
        notificarObservadores();
    }

    // ─── Patrón Observer ───────────────────────────────────────────────────────

    /**
     * Suscribe un observador para recibir notificaciones de cambio de luz.
     *
     * @param observador el observador a suscribir
     */
    public void suscribir(ObservadorSemaforo observador) {
        if (!observadores.contains(observador)) observadores.add(observador);
    }

    /**
     * Cancela la suscripción de un observador.
     *
     * @param observador el observador a retirar
     */
    public void desuscribir(ObservadorSemaforo observador) {
        observadores.remove(observador);
    }

    /** Notifica a todos los observadores que la luz del semáforo cambió. */
    private void notificarObservadores() {
        for (ObservadorSemaforo obs : observadores) obs.onCambioLuz(this);
    }

    // ─── Lógica interna del ciclo ──────────────────────────────────────────────

    /** Avanza al siguiente estado en el ciclo VERDE → AMARILLO → ROJO → VERDE. */
    private void cambiarAlSiguienteEstado() {
        switch (luzActual) {
            case VERDE:   luzActual = LuzSemaforo.AMARILLO; break;
            case AMARILLO: luzActual = LuzSemaforo.ROJO;   break;
            case ROJO:    luzActual = LuzSemaforo.VERDE;   break;
            default:      luzActual = LuzSemaforo.VERDE;   break;
        }
    }

    /**
     * Retorna cuántos ticks debe durar el estado actual.
     * Si hay muchos vehículos esperando, extiende el tiempo verde.
     *
     * @return ticks de duración del estado actual
     */
    private int getDuracionEstadoActual() {
        switch (luzActual) {
            case VERDE:
                int extension = vehiculosEnEspera >= UMBRAL_EXTENSION_VERDE
                    ? Math.min(vehiculosEnEspera, EXTENSION_MAXIMA_VERDE) : 0;
                return duracionVerde + extension;
            case AMARILLO: return DURACION_AMARILLO;
            case ROJO:     return duracionRojo;
            default:       return duracionVerde;
        }
    }

    // ─── Getters y estado ─────────────────────────────────────────────────────

    public String getId()                       { return id; }
    public Interseccion getInterseccion()       { return interseccion; }
    public LuzSemaforo getLuzActual()           { return luzActual; }
    public int getDuracionVerde()               { return duracionVerde; }
    public void setDuracionVerde(int dur)       { this.duracionVerde = dur; }
    public int getDuracionRojo()                { return duracionRojo; }
    public void setDuracionRojo(int dur)        { this.duracionRojo = dur; }
    public int getVehiculosEnEspera()           { return vehiculosEnEspera; }
    public void setVehiculosEnEspera(int n)     { this.vehiculosEnEspera = n; }
    public boolean isModoEmergenciaActivo()     { return modoEmergenciaActivo; }
    public int getTicksTotalesEnRojo()          { return ticksTotalesEnRojo; }

    /** @return true si los vehículos pueden avanzar ahora mismo */
    public boolean permiteAvanzar() {
        return luzActual == LuzSemaforo.VERDE || luzActual == LuzSemaforo.EMERGENCIA_ACTIVA;
    }

    @Override
    public String toString() {
        return String.format("Semaforo[%s @ %s | Luz: %s%s | Esperando: %d]",
            id, interseccion.getNombre(), luzActual,
            modoEmergenciaActivo ? " 🚨" : "", vehiculosEnEspera);
    }
}
