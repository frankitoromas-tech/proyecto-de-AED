package com.trafico.simulacion;

import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;
import com.trafico.modelo.SituacionVehiculo;
import com.trafico.modelo.vehiculos.UnidadEmergencia;
import com.trafico.modelo.vehiculos.Vehiculo;
import com.trafico.semaforos.ControladorSemaforos;
import com.trafico.semaforos.Semaforo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>Motor de Simulación</b> — el corazón del sistema.
 *
 * <p>Controla el tiempo de la simulación tick a tick (1 tick = 1 segundo simulado).
 * Mantiene un historial de logs didácticos para la visualización en la GUI.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.1
 */
public class MotorSimulacion {

    // ─── Singleton ────────────────────────────────────────────────────────────

    private static MotorSimulacion instancia;

    public static MotorSimulacion getInstancia() {
        if (instancia == null) instancia = new MotorSimulacion();
        return instancia;
    }

    private MotorSimulacion() {
        this.logs = new ArrayList<>();
    }

    // ─── Estado de la simulación ──────────────────────────────────────────────

    private MapaVial mapa;
    private ControladorFlota flota;
    private ControladorSemaforos controladorSemaforos;
    private InformeTrafico informe;

    private int tickActual;
    private int totalTicks;
    private boolean enEjecucion;
    private final List<String> logs;

    private static final double UMBRAL_CONGESTION = 0.8;

    // ─── Configuración ────────────────────────────────────────────────────────

    public void configurar(MapaVial mapa, ControladorFlota flota,
                           ControladorSemaforos controladorSemaforos, int totalTicks) {
        this.mapa = mapa;
        this.flota = flota;
        this.controladorSemaforos = controladorSemaforos;
        this.totalTicks = totalTicks;
        this.tickActual = 0;
        this.enEjecucion = false;
        this.informe = new InformeTrafico();
        vaciarLogs();
        registrarLog("Simulacion configurada e inicializada.");
    }

    // ─── Sistema de Logs didácticos ────────────────────────────────────────────

    public synchronized void registrarLog(String msg) {
        logs.add(String.format("[Tick %d] %s", tickActual, msg));
        if (logs.size() > 100) logs.remove(0);
    }

    public synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public synchronized void vaciarLogs() {
        logs.clear();
    }

    // ─── Ejecución ────────────────────────────────────────────────────────────

    public void ejecutar() {
        if (mapa == null || flota == null)
            throw new IllegalStateException("El motor no está configurado. Llama a configurar() primero.");

        enEjecucion = true;
        registrarLog("Ejecucion automatica iniciada.");

        while (tickActual < totalTicks && !flota.todosLlegaronAlDestino()) {
            ejecutarTick();
            if (tickActual % 10 == 0) mostrarProgreso();
        }

        enEjecucion = false;
        registrarLog("Simulacion finalizada.");
    }

    public void ejecutarTick() {
        tickActual++;

        // 1. Avanzar semáforos
        controladorSemaforos.avanzarTodosUnTick();

        // 2. Mover vehículos
        for (Vehiculo v : flota.getVehiculosActivos()) {
            procesarVehiculo(v);
        }

        // 3. Actualizar congestión en todas las calles
        actualizarCongestion();

        // 4. Registrar estadísticas del tick
        informe.registrarTick(tickActual, flota.totalLlegados(), flota.getVehiculosActivos());

        // 5. Eliminar vehículos que llegaron
        flota.eliminarLlegados();
    }

    // ─── Lógica por vehículo ──────────────────────────────────────────────────

    private void procesarVehiculo(Vehiculo vehiculo) {
        if (vehiculo.haLlegado()) return;

        Interseccion posicion = vehiculo.getPosicion();
        if (posicion == null) return;

        // Verificar si hay semáforo en la posición actual
        Semaforo semaforo = controladorSemaforos.buscarPorInterseccion(posicion.getId());

        if (semaforo != null) {
            // Si es emergencia con sirena, activar modo emergencia
            if (vehiculo instanceof UnidadEmergencia) {
                UnidadEmergencia emergencia = (UnidadEmergencia) vehiculo;
                if (emergencia.isSirenaActiva()) {
                    semaforo.activarModoEmergencia();
                    emergencia.registrarInterrupcionSemaforo();
                    informe.registrarInterrupcionEmergencia();
                    registrarLog(String.format("🚨 Emergencia %s dio paso libre en el semaforo de %s",
                        vehiculo.getPlaca(), posicion.getNombre()));
                }
            }

            // Detenerse si el semáforo está en rojo
            if (!semaforo.permiteAvanzar()) {
                if (vehiculo.getSituacion() != SituacionVehiculo.ESPERANDO_SEMAFORO) {
                    registrarLog(String.format("Vehiculo %s detenido en semaforo rojo de %s",
                        vehiculo.getPlaca(), posicion.getNombre()));
                }
                vehiculo.detenerse();
                semaforo.setVehiculosEnEspera(semaforo.getVehiculosEnEspera() + 1);
                return;
            } else {
                if (vehiculo.getSituacion() == SituacionVehiculo.ESPERANDO_SEMAFORO) {
                    registrarLog(String.format("Vehiculo %s reanudo marcha en semaforo verde de %s",
                        vehiculo.getPlaca(), posicion.getNombre()));
                }
                vehiculo.reanudar();
                if (semaforo.getVehiculosEnEspera() > 0)
                    semaforo.setVehiculosEnEspera(semaforo.getVehiculosEnEspera() - 1);
            }
        }

        // Guardar vieja posición antes de avanzar para el log
        Interseccion antes = vehiculo.getPosicion();
        vehiculo.avanzar();

        if (vehiculo.haLlegado()) {
            registrarLog(String.format("Llegada: Vehiculo %s llego a destino '%s'!",
                vehiculo.getPlaca(), vehiculo.getDestino().getNombre()));
        } else if (!antes.equals(vehiculo.getPosicion())) {
            registrarLog(String.format("Movimiento: Vehiculo %s avanzo de '%s' a '%s'",
                vehiculo.getPlaca(), antes.getNombre(), vehiculo.getPosicion().getNombre()));
        }
    }

    // ─── Actualización de congestión ──────────────────────────────────────────

    private void actualizarCongestion() {
        for (Interseccion i : mapa.todasLasIntersecciones()) {
            try {
                for (Calle c : mapa.callesDesdePunto(i.getId())) {
                    c.actualizarCongestion();
                    if (c.estaSaturada()) {
                        informe.registrarCalleSaturada(c);
                        registrarLog(String.format("⚠ Atasco: Via '%s -> %s' esta congestionada.",
                            c.getOrigen().getId(), c.getDestino().getId()));
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void mostrarProgreso() {
        System.out.printf("  Tick %4d/%d | En ruta: %d | Llegados: %d | Semaforos activos: %d%n",
            tickActual, totalTicks,
            flota.totalActivos(), flota.totalLlegados(),
            controladorSemaforos.totalSemaforos());
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public int getTickActual()       { return tickActual; }
    public int getTotalTicks()       { return totalTicks; }
    public boolean isEnEjecucion()   { return enEjecucion; }
    public InformeTrafico getInforme() { return informe; }
    public MapaVial getMapa()        { return mapa; }
}
