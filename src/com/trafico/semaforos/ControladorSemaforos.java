package com.trafico.semaforos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador central de todos los semáforos de la ciudad.
 *
 * <p>Mantiene un registro de cada semáforo activo y en cada tick de simulación
 * avanza el ciclo de todos ellos. También expone métodos para consultar el
 * estado de un semáforo específico y para activar el modo emergencia en la
 * ruta de una unidad de emergencia.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class ControladorSemaforos {

    /** Lista de todos los semáforos activos en la ciudad. */
    private final List<Semaforo> semaforos;

    /** Construye el controlador con la lista de semáforos vacía. */
    public ControladorSemaforos() {
        semaforos = new ArrayList<>();
    }

    // ─── Registro ──────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo semáforo en el sistema.
     *
     * @param semaforo el semáforo a agregar
     */
    public void registrar(Semaforo semaforo) {
        if (semaforo != null && !semaforos.contains(semaforo))
            semaforos.add(semaforo);
    }

    /**
     * Elimina un semáforo del sistema (p.ej., si la intersección ya no existe).
     *
     * @param id ID del semáforo a eliminar
     */
    public void eliminar(String id) {
        semaforos.removeIf(s -> s.getId().equals(id));
    }

    // ─── Tick de simulación ────────────────────────────────────────────────────

    /**
     * Avanza el ciclo de TODOS los semáforos un tick.
     * Debe llamarse una vez por tick del motor de simulación.
     */
    public void avanzarTodosUnTick() {
        for (Semaforo s : semaforos) s.avanzarTick();
    }

    // ─── Modo emergencia ───────────────────────────────────────────────────────

    /**
     * Activa el modo emergencia en los semáforos de las intersecciones indicadas.
     * Usado cuando una unidad de emergencia con sirena circula por esas intersecciones.
     *
     * @param idsIntersecciones lista de IDs de intersecciones en la ruta de la emergencia
     */
    public void activarEmergenciaEnRuta(List<String> idsIntersecciones) {
        for (Semaforo s : semaforos) {
            if (idsIntersecciones.contains(s.getInterseccion().getId()))
                s.activarModoEmergencia();
        }
    }

    /**
     * Desactiva el modo emergencia en todos los semáforos.
     * Llamar cuando la unidad de emergencia llega a su destino.
     */
    public void desactivarTodasLasEmergencias() {
        for (Semaforo s : semaforos) {
            if (s.isModoEmergenciaActivo()) s.desactivarModoEmergencia();
        }
    }

    // ─── Consultas ─────────────────────────────────────────────────────────────

    /**
     * Busca el semáforo de una intersección específica.
     *
     * @param interseccionId ID de la intersección
     * @return el semáforo, o {@code null} si no hay semáforo en esa intersección
     */
    public Semaforo buscarPorInterseccion(String interseccionId) {
        for (Semaforo s : semaforos)
            if (s.getInterseccion().getId().equals(interseccionId)) return s;
        return null;
    }

    /**
     * Retorna el semáforo con mayor tiempo acumulado en rojo (el más congestionado).
     *
     * @return el semáforo más restrictivo, o {@code null} si no hay semáforos
     */
    public Semaforo elMasRestrictivo() {
        return semaforos.stream()
            .max((a, b) -> a.getTicksTotalesEnRojo() - b.getTicksTotalesEnRojo())
            .orElse(null);
    }

    /** @return lista inmutable de todos los semáforos registrados */
    public List<Semaforo> getTodos() {
        return Collections.unmodifiableList(semaforos);
    }

    /** @return número total de semáforos en el sistema */
    public int totalSemaforos() { return semaforos.size(); }

    /**
     * Muestra el estado actual de todos los semáforos en la consola.
     */
    public void mostrarEstado() {
        System.out.println("\n  +----------------------------------------------+");
        System.out.println("  |         ESTADO DE LOS SEMAFOROS              |");
        System.out.println("  +----------------------------------------------+");
        if (semaforos.isEmpty()) {
            System.out.println("  |  (no hay semaforos registrados)              |");
        } else {
            for (Semaforo s : semaforos) {
                String indicador = switch (s.getLuzActual()) {
                    case VERDE            -> "[VERDE]   ";
                    case AMARILLO         -> "[AMARILLO]";
                    case ROJO             -> "[ROJO]    ";
                    case EMERGENCIA_ACTIVA-> "[EMERGENC]";
                };
                System.out.printf("  |  %-28s %s Espera: %d%n",
                    s.getInterseccion().getNombre(), indicador,
                    s.getVehiculosEnEspera());
            }
        }
        System.out.println("  +----------------------------------------------+\n");
    }
}
