package com.trafico.simulacion;

import com.trafico.errores.ErrorRutaImposible;
import com.trafico.estructuras.CacheRutas;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Interseccion;
import com.trafico.modelo.vehiculos.Vehiculo;
import com.trafico.navegacion.EstrategiaRuta;
import com.trafico.navegacion.RutaMasCorta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la flota vehicular activa en la simulación.
 *
 * <p>Administra el ciclo de vida de todos los vehículos: registro, asignación
 * de ruta, seguimiento del estado y eliminación al llegar al destino.</p>
 *
 * <p>Usa la {@link CacheRutas} para evitar recalcular la misma ruta varias veces:
 * si dos vehículos van del mismo origen al mismo destino, el segundo reutiliza
 * la ruta del primero sin ejecutar el algoritmo de nuevo.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class ControladorFlota {

    /** Lista de todos los vehículos activos en la simulación. */
    private final List<Vehiculo> vehiculos;

    /** Caché para no recalcular rutas ya conocidas. */
    private final CacheRutas<String, List<Interseccion>> cacheRutas;

    /** Algoritmo de ruta por defecto (puede cambiarse). */
    private EstrategiaRuta estrategiaActual;

    /** El mapa vial para poder calcular rutas. */
    private final MapaVial mapa;

    /** Contador de vehículos que llegaron exitosamente a su destino. */
    private int contadorLlegados;

    /**
     * Crea el controlador de flota.
     *
     * @param mapa el mapa vial de la ciudad
     */
    public ControladorFlota(MapaVial mapa) {
        this.mapa = mapa;
        this.vehiculos = new ArrayList<>();
        this.cacheRutas = new CacheRutas<>();
        this.estrategiaActual = new RutaMasCorta(); // Dijkstra por defecto
        this.contadorLlegados = 0;
    }

    // ─── Registro de vehículos ────────────────────────────────────────────────

    /**
     * Registra un nuevo vehículo en la flota y le calcula su ruta automáticamente.
     *
     * @param vehiculo el vehículo a registrar
     */
    public void registrar(Vehiculo vehiculo) {
        if (vehiculo == null) return;
        asignarRuta(vehiculo);
        vehiculos.add(vehiculo);
        System.out.printf("  * Vehiculo registrado: %s%n", vehiculo);
    }

    /**
     * Calcula y asigna la ruta al vehiculo, usando la cache si es posible.
     *
     * @param vehiculo el vehiculo al que asignar la ruta
     */
    private void asignarRuta(Vehiculo vehiculo) {
        if (vehiculo.getPosicion() == null || vehiculo.getDestino() == null) return;

        String clave = vehiculo.getPosicion().getId() + "->" + vehiculo.getDestino().getId();
        List<Interseccion> ruta = cacheRutas.recuperar(clave);

        if (ruta == null) {
            try {
                ruta = estrategiaActual.calcularRuta(mapa,
                    vehiculo.getPosicion().getId(), vehiculo.getDestino().getId());
                cacheRutas.guardar(clave, ruta);
            } catch (ErrorRutaImposible e) {
                System.err.println("  [Error] No se pudo calcular ruta para " + vehiculo.getPlaca()
                    + ": " + e.getMessage());
                return;
            }
        }
        vehiculo.seguirRuta(ruta);
    }

    // ─── Gestión durante la simulación ────────────────────────────────────────

    /**
     * Elimina de la lista los vehículos que ya llegaron a su destino.
     * Llamar al final de cada tick.
     */
    public void eliminarLlegados() {
        List<Vehiculo> llegados = vehiculos.stream()
            .filter(Vehiculo::haLlegado)
            .collect(Collectors.toList());
        contadorLlegados += llegados.size();
        vehiculos.removeAll(llegados);
    }

    /**
     * Cambia el algoritmo de ruta usado para todos los nuevos vehículos.
     *
     * @param nueva la nueva estrategia de navegación
     */
    public void cambiarEstrategia(EstrategiaRuta nueva) {
        this.estrategiaActual = nueva;
        System.out.println("  Algoritmo de ruta cambiado a: " + nueva.getNombre());
    }

    // ─── Consultas ─────────────────────────────────────────────────────────────

    /** @return true si todos los vehículos llegaron a su destino */
    public boolean todosLlegaronAlDestino() {
        return vehiculos.isEmpty();
    }

    /** @return lista inmutable de vehículos activos en la simulación */
    public List<Vehiculo> getVehiculosActivos() {
        return Collections.unmodifiableList(vehiculos);
    }

    /** @return número de vehículos actualmente en ruta */
    public int totalActivos()   { return vehiculos.size(); }

    /** @return número total de vehículos registrados (activos + llegados) */
    public int totalVehiculos() { return vehiculos.size() + contadorLlegados; }

    /** @return cuántos vehículos ya llegaron a su destino */
    public int totalLlegados()  { return contadorLlegados; }

    /** @return estadísticas de la caché de rutas */
    public String getEstadisticasCache() { return cacheRutas.getEstadisticas(); }
}
