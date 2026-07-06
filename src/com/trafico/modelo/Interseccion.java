package com.trafico.modelo;

/**
 * Representa una intersección (esquina o cruce) dentro de la red vial urbana.
 *
 * <p>En el grafo de la ciudad, cada intersección es un <b>nodo</b>. Las coordenadas
 * cartesianas {@code (x, y)} permiten calcular la heurística del algoritmo A*.</p>
 *
 * <p>Ejemplo real: "Calle 10 con Carrera 5", "Rotonda del Parque Central".</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class Interseccion {

    /** Código único de la intersección (ej. "INT-001", "A", "Parque_Central"). */
    private final String id;

    /** Nombre descriptivo del punto de la ciudad (ej. "Av. Central con Calle 5"). */
    private String nombre;

    /** Coordenada X en el plano de la ciudad (en metros desde el origen). */
    private double x;

    /** Coordenada Y en el plano de la ciudad (en metros desde el origen). */
    private double y;

    /** Indica si hay un semáforo controlando esta intersección. */
    private boolean tieneSemaforo;

    /** Número de vehículos que se encuentran actualmente en esta intersección. */
    private int vehiculosPresentes;

    /**
     * Crea una intersección con nombre y posición en el mapa.
     *
     * @param id     código único de la intersección
     * @param nombre nombre descriptivo del punto de la ciudad
     * @param x      posición horizontal en el mapa (metros)
     * @param y      posición vertical en el mapa (metros)
     */
    public Interseccion(String id, String nombre, double x, double y) {
        this.id = id;
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.tieneSemaforo = false;
        this.vehiculosPresentes = 0;
    }

    /**
     * Calcula la distancia en línea recta hasta otra intersección.
     * Esta distancia se usa como heurística en el algoritmo A*.
     *
     * @param otra la intersección de destino
     * @return distancia euclidiana en metros
     */
    public double distanciaHacia(Interseccion otra) {
        double dx = this.x - otra.x;
        double dy = this.y - otra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Registra la llegada de un vehículo a esta intersección. */
    public void entrarVehiculo() {
        this.vehiculosPresentes++;
    }

    /** Registra la salida de un vehículo de esta intersección. */
    public void salirVehiculo() {
        if (this.vehiculosPresentes > 0) this.vehiculosPresentes--;
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public String getId()                           { return id; }
    public String getNombre()                       { return nombre; }
    public void setNombre(String nombre)            { this.nombre = nombre; }
    public double getX()                            { return x; }
    public void setX(double x)                      { this.x = x; }
    public double getY()                            { return y; }
    public void setY(double y)                      { this.y = y; }
    public boolean isTieneSemaforo()                { return tieneSemaforo; }
    public void setTieneSemaforo(boolean valor)     { this.tieneSemaforo = valor; }
    public int getVehiculosPresentes()              { return vehiculosPresentes; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Interseccion)) return false;
        return this.id.equals(((Interseccion) obj).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return String.format("Interseccion{id='%s', nombre='%s', pos=(%.0f,%.0f)%s}",
            id, nombre, x, y, tieneSemaforo ? ", 🚦semáforo" : "");
    }
}
