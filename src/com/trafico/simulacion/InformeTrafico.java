package com.trafico.simulacion;

import com.trafico.modelo.Calle;
import com.trafico.modelo.vehiculos.Vehiculo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Informe de Tráfico — recopila estadísticas durante la simulación y genera el resumen final.
 *
 * <p>Se va actualizando tick a tick y al finalizar la simulación puede generar
 * un reporte completo tanto en consola como en archivo.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class InformeTrafico {

    /** Momento en que se creó este informe (inicio de la simulación). */
    private final LocalDateTime horaInicio;

    /** Calles saturadas detectadas y cuántas veces estuvieron saturadas. */
    private final Map<String, Integer> callesSaturadas;

    /** Contador de interrupciones por unidades de emergencia. */
    private int interrupcionesEmergencia;

    /** Máximo de vehículos activos simultáneos registrado. */
    private int picoMaximoVehiculos;

    /** Historial de llegadas por tick (clave: tick, valor: cuántos llegaron). */
    private final Map<Integer, Integer> llegadasPorTick;

    public InformeTrafico() {
        this.horaInicio = LocalDateTime.now();
        this.callesSaturadas = new LinkedHashMap<>();
        this.llegadasPorTick = new TreeMap<>();
        this.interrupcionesEmergencia = 0;
        this.picoMaximoVehiculos = 0;
    }

    // ─── Registro durante la simulación ───────────────────────────────────────

    /**
     * Registra el estado de un tick en el informe.
     *
     * @param tick          número del tick actual
     * @param llegados      total acumulado de vehículos llegados hasta este tick
     * @param activos       lista de vehículos activos en este tick
     */
    public void registrarTick(int tick, int llegados, List<Vehiculo> activos) {
        llegadasPorTick.put(tick, llegados);
        picoMaximoVehiculos = Math.max(picoMaximoVehiculos, activos.size());
    }

    /**
     * Registra que una calle estuvo saturada en un tick.
     *
     * @param calle la calle saturada
     */
    public void registrarCalleSaturada(Calle calle) {
        String clave = calle.getOrigen().getId() + "->" + calle.getDestino().getId();
        callesSaturadas.merge(clave, 1, Integer::sum);
    }

    /** Registra una interrupción de semáforo por unidad de emergencia. */
    public void registrarInterrupcionEmergencia() {
        interrupcionesEmergencia++;
    }

    // ─── Reporte final ─────────────────────────────────────────────────────────

    /**
     * Genera y muestra el informe completo en la consola.
     *
     * @param tickFinal         en qué tick terminó la simulación
     * @param totalVehiculos    total de vehículos que participaron
     * @param totalLlegados     cuántos llegaron exitosamente
     */
    public void mostrarEnConsola(int tickFinal, int totalVehiculos, int totalLlegados) {
        String separador = "=".repeat(60);
        System.out.println("\n" + separador);
        System.out.println("  INFORME FINAL DE LA SIMULACION DE TRAFICO");
        System.out.println(separador);
        System.out.printf("  Fecha y hora    : %s%n",
            horaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.printf("  Ticks ejecutados: %d%n", tickFinal);
        System.out.printf("  Vehiculos totales: %d  |  Llegaron: %d  |  En transito: %d%n",
            totalVehiculos, totalLlegados, totalVehiculos - totalLlegados);
        System.out.printf("  Pico maximo de vehiculos simultaneos: %d%n", picoMaximoVehiculos);
        System.out.printf("  Interrupciones por emergencias: %d%n", interrupcionesEmergencia);

        System.out.println("\n  Top 5 Calles con Mayor Congestion:");
        callesSaturadas.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(5)
            .forEach(e -> System.out.printf("     %-30s -> %d veces saturada%n", e.getKey(), e.getValue()));

        System.out.println("\n" + separador + "\n");
    }

    /**
     * Genera el contenido del informe como texto para exportar a archivo.
     *
     * @param tickFinal      en qué tick terminó la simulación
     * @param totalVehiculos total de vehículos que participaron
     * @param totalLlegados  cuántos llegaron exitosamente
     * @return el informe completo como cadena de texto
     */
    public String generarTextoParaArchivo(int tickFinal, int totalVehiculos, int totalLlegados) {
        StringBuilder sb = new StringBuilder();
        sb.append("INFORME DE SIMULACIÓN DE TRÁFICO VEHICULAR\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("Generado: ").append(horaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        sb.append("RESUMEN GENERAL\n");
        sb.append("  Ticks ejecutados   : ").append(tickFinal).append("\n");
        sb.append("  Total vehículos    : ").append(totalVehiculos).append("\n");
        sb.append("  Llegaron al destino: ").append(totalLlegados).append("\n");
        sb.append("  Pico de vehículos  : ").append(picoMaximoVehiculos).append("\n");
        sb.append("  Interrupciones emg.: ").append(interrupcionesEmergencia).append("\n\n");
        sb.append("CALLES MÁS CONGESTIONADAS\n");
        callesSaturadas.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(10)
            .forEach(e -> sb.append("  ").append(e.getKey())
                .append(" : ").append(e.getValue()).append(" ticks saturada\n"));
        return sb.toString();
    }

    public LocalDateTime getHoraInicio()        { return horaInicio; }
    public int getInterrupcionesEmergencia()     { return interrupcionesEmergencia; }
    public int getPicoMaximoVehiculos()          { return picoMaximoVehiculos; }
    public Map<String, Integer> getCallesSaturadas() { return Collections.unmodifiableMap(callesSaturadas); }
}
