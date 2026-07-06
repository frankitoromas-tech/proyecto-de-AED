package com.trafico.modelo;

/**
 * Representa una calle o vía que conecta dos intersecciones de la ciudad.
 *
 * <p>En el grafo vial, cada calle es una <b>arista ponderada y dirigida</b>.
 * Su peso real cambia dinámicamente con la congestión: a mayor saturación,
 * mayor tiempo de recorrido.</p>
 *
 * <p>Fórmula del peso real:
 * <pre>  peso = (distancia_km / velocidad_max) * 60 * (1 + congestion * 3)</pre>
 * El resultado es el tiempo estimado de recorrido en minutos.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class Calle {

    /** Intersección donde comienza la calle. */
    private final Interseccion origen;

    /** Intersección donde termina la calle. */
    private final Interseccion destino;

    /** Longitud total de la calle en metros. */
    private double distancia;

    /** Velocidad máxima permitida en km/h. */
    private double velocidadMaxima;

    /**
     * Nivel de congestión actual:
     * 0.0 = completamente libre | 1.0 = completamente saturada.
     */
    private double congestion;

    /** Tipo de vía (residencial, avenida, autopista...). */
    private CategoriaCalle categoria;

    /** Si es true, los vehículos pueden circular en ambos sentidos. */
    private boolean esDobleVia;

    /** Vehículos actualmente transitando por esta calle. */
    private int vehiculosEnTransito;

    /** Cuántos vehículos puede contener esta calle simultáneamente. */
    private final int aforoMaximo;

    /** Número de veces que esta calle fue utilizada durante la simulación. */
    private int vecesUsada;

    /**
     * Crea una calle con sus características físicas.
     *
     * @param origen          intersección de inicio
     * @param destino         intersección de fin
     * @param distancia       longitud en metros
     * @param velocidadMaxima velocidad permitida en km/h
     * @param categoria       tipo de vía
     * @param esDobleVia      true si permite tráfico en ambas direcciones
     */
    public Calle(Interseccion origen, Interseccion destino, double distancia,
                 double velocidadMaxima, CategoriaCalle categoria, boolean esDobleVia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.velocidadMaxima = velocidadMaxima;
        this.categoria = categoria;
        this.esDobleVia = esDobleVia;
        this.congestion = 0.0;
        this.vehiculosEnTransito = 0;
        this.vecesUsada = 0;
        this.aforoMaximo = calcularAforo();
    }

    /**
     * Calcula el aforo máximo según el tipo y longitud de la vía.
     *
     * @return número máximo de vehículos simultáneos
     */
    private int calcularAforo() {
        double factor;
        switch (categoria) {
            case AUTOPISTA:     factor = 0.05; break;
            case AVENIDA:       factor = 0.03; break;
            case EXCLUSIVA_BUS: factor = 0.02; break;
            case RESIDENCIAL:   factor = 0.015; break;
            default:            factor = 0.01; break;
        }
        return Math.max(5, (int)(distancia * factor));
    }

    /**
     * Calcula el costo real (en tiempo) de recorrer esta calle ahora mismo.
     * El costo aumenta cuando hay más congestión.
     *
     * @return tiempo estimado de recorrido en minutos (ajustado por congestión)
     */
    public double getCostoActual() {
        double tiempoBase = (distancia / 1000.0 / velocidadMaxima) * 60.0;
        return tiempoBase * (1.0 + congestion * 3.0);
    }

    /**
     * Registra la entrada de un vehículo y actualiza el nivel de congestión.
     */
    public void registrarEntradaVehiculo() {
        vehiculosEnTransito++;
        vecesUsada++;
        actualizarCongestion();
    }

    /**
     * Registra la salida de un vehículo y actualiza el nivel de congestión.
     */
    public void registrarSalidaVehiculo() {
        if (vehiculosEnTransito > 0) vehiculosEnTransito--;
        actualizarCongestion();
    }

    /**
     * Recalcula el nivel de congestión basándose en los vehículos en tránsito.
     */
    public void actualizarCongestion() {
        this.congestion = aforoMaximo > 0
            ? Math.min(1.0, (double) vehiculosEnTransito / aforoMaximo)
            : 0.0;
    }

    /**
     * Indica si la calle está saturada (más del 80% de su aforo ocupado).
     *
     * @return true si la calle está congestionada
     */
    public boolean estaSaturada() { return congestion >= 0.8; }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public Interseccion getOrigen()                     { return origen; }
    public Interseccion getDestino()                    { return destino; }
    public double getDistancia()                        { return distancia; }
    public void setDistancia(double distancia)          { this.distancia = distancia; }
    public double getVelocidadMaxima()                  { return velocidadMaxima; }
    public void setVelocidadMaxima(double vel)          { this.velocidadMaxima = vel; }
    public double getCongestion()                       { return congestion; }
    public void setCongestion(double congestion)        { this.congestion = Math.max(0, Math.min(1, congestion)); }
    public CategoriaCalle getCategoria()                { return categoria; }
    public boolean isEsDobleVia()                       { return esDobleVia; }
    public int getVehiculosEnTransito()                 { return vehiculosEnTransito; }
    public int getAforoMaximo()                         { return aforoMaximo; }
    public int getVecesUsada()                          { return vecesUsada; }

    @Override
    public String toString() {
        return String.format("Calle{%s → %s | %.0fm | %.0fkm/h | %s | congestion:%.0f%%%s}",
            origen.getId(), destino.getId(), distancia, velocidadMaxima,
            categoria, congestion * 100,
            estaSaturada() ? " ⚠SATURADA" : "");
    }
}
