package com.trafico.modelo.vehiculos;

import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;

/**
 * Fábrica de vehículos — centraliza la creación de todos los tipos de vehículos.
 *
 * <p>Aplica el <b>patrón Factory</b>: el código cliente solo necesita indicar
 * qué tipo de vehículo quiere; la fábrica decide qué subclase instanciar y con
 * qué parámetros por defecto. Esto facilita agregar nuevos tipos de vehículo
 * en el futuro sin modificar el resto del sistema.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   Vehiculo v = FabricaVehiculos.crear(CategoriaVehiculo.EMERGENCIA,
 *                                       "AMB-001", origen, destino);
 * </pre>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class FabricaVehiculos {

    /** Previene la instanciación — es una clase de utilidad. */
    private FabricaVehiculos() {}

    /**
     * Crea un vehículo de la categoría indicada con parámetros básicos.
     *
     * @param categoria categoría del vehículo a crear
     * @param placa     número de placa
     * @param origen    intersección de salida
     * @param destino   intersección de llegada
     * @return el vehículo creado con configuración por defecto para su tipo
     * @throws IllegalArgumentException si la categoría no es reconocida
     */
    public static Vehiculo crear(CategoriaVehiculo categoria, String placa,
                                  Interseccion origen, Interseccion destino) {
        switch (categoria) {
            case PARTICULAR:
                return new AutoParticular(placa, "Modelo Estándar", origen, destino);
            case EMERGENCIA:
                return new UnidadEmergencia(placa, UnidadEmergencia.Tipo.AMBULANCIA, origen, destino);
            case BUS:
                return new BusUrbano(placa, "Ruta General", 40, origen, destino);
            case CARGA:
                return new CamionCarga(placa, "Carga General", 5.0, false, origen, destino);
            case MOTOCICLETA:
                return new Motocicleta(placa, 200, origen, destino);
            default:
                throw new IllegalArgumentException("Categoría de vehículo no reconocida: " + categoria);
        }
    }

    /**
     * Crea un auto particular con modelo específico.
     *
     * @param placa   placa del auto
     * @param modelo  modelo del vehículo
     * @param origen  intersección de salida
     * @param destino intersección de llegada
     * @return un nuevo AutoParticular
     */
    public static AutoParticular crearAutoParticular(String placa, String modelo,
                                                      Interseccion origen, Interseccion destino) {
        return new AutoParticular(placa, modelo, origen, destino);
    }

    /**
     * Crea una unidad de emergencia con tipo específico.
     *
     * @param placa   placa de la unidad
     * @param tipo    tipo de emergencia (AMBULANCIA, BOMBEROS, POLICIA)
     * @param origen  intersección de salida
     * @param destino intersección de llegada
     * @return una nueva UnidadEmergencia
     */
    public static UnidadEmergencia crearUnidadEmergencia(String placa,
                                                          UnidadEmergencia.Tipo tipo,
                                                          Interseccion origen,
                                                          Interseccion destino) {
        return new UnidadEmergencia(placa, tipo, origen, destino);
    }

    /**
     * Crea un bus urbano con ruta y capacidad específicas.
     *
     * @param placa              placa del bus
     * @param numeroRuta         identificador de la ruta del bus
     * @param capacidadPasajeros número máximo de pasajeros
     * @param origen             intersección de salida
     * @param destino            intersección de llegada
     * @return un nuevo BusUrbano
     */
    public static BusUrbano crearBusUrbano(String placa, String numeroRuta,
                                            int capacidadPasajeros,
                                            Interseccion origen, Interseccion destino) {
        return new BusUrbano(placa, numeroRuta, capacidadPasajeros, origen, destino);
    }
}
