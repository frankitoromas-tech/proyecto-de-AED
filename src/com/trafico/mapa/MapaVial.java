package com.trafico.mapa;

import com.trafico.errores.ErrorSistema;
import com.trafico.modelo.Calle;
import com.trafico.modelo.Interseccion;

import java.util.List;

/**
 * Contrato que debe cumplir cualquier representación del mapa vial urbano.
 *
 * <p>Permite intercambiar la implementación interna del grafo (lista de adyacencia
 * vs. matriz de adyacencia) sin cambiar el resto del sistema — <b>patrón Strategy</b>.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public interface MapaVial {

    /**
     * Agrega una intersección al mapa vial.
     *
     * @param interseccion la intersección a agregar
     * @throws ErrorSistema si la intersección ya existe
     */
    void agregarInterseccion(Interseccion interseccion) throws ErrorSistema;

    /**
     * Agrega una calle entre dos intersecciones.
     * Si la calle es doble vía, también se agrega en sentido contrario.
     *
     * @param calle la calle con sus datos físicos
     * @throws ErrorSistema si alguna de las intersecciones no existe
     */
    void agregarCalle(Calle calle) throws ErrorSistema;

    /**
     * Elimina la calle entre dos intersecciones.
     *
     * @param origenId  ID de la intersección de inicio
     * @param destinoId ID de la intersección de fin
     * @throws ErrorSistema si la calle no existe
     */
    void eliminarCalle(String origenId, String destinoId) throws ErrorSistema;

    /**
     * Retorna todas las calles que salen de una intersección dada.
     *
     * @param interseccionId ID de la intersección
     * @return lista de calles salientes (vacía si no tiene ninguna)
     * @throws ErrorSistema si la intersección no existe
     */
    List<Calle> callesDesdePunto(String interseccionId) throws ErrorSistema;

    /**
     * Retorna todas las intersecciones registradas en el mapa.
     *
     * @return lista completa de intersecciones
     */
    List<Interseccion> todasLasIntersecciones();

    /**
     * Busca una intersección por su ID.
     *
     * @param id el ID a buscar
     * @return la intersección, o {@code null} si no existe
     */
    Interseccion buscarInterseccion(String id);

    /**
     * Busca la calle que conecta dos intersecciones.
     *
     * @param origenId  ID de la intersección de inicio
     * @param destinoId ID de la intersección de fin
     * @return la calle, o {@code null} si no existe la conexión
     */
    Calle buscarCalle(String origenId, String destinoId);

    /**
     * Actualiza el nivel de congestión de una calle específica.
     *
     * @param origenId        ID del punto de inicio
     * @param destinoId       ID del punto de fin
     * @param nuevaCongestion nuevo nivel [0.0 = libre, 1.0 = saturada]
     * @throws ErrorSistema si la calle no existe
     */
    void actualizarCongestion(String origenId, String destinoId, double nuevaCongestion)
            throws ErrorSistema;

    /**
     * Verifica si todas las intersecciones están conectadas entre sí (usando BFS).
     *
     * @return true si el mapa es completamente conexo
     */
    boolean esMapaConexo();

    /** @return número de intersecciones en el mapa */
    int totalIntersecciones();

    /** @return número de calles (aristas dirigidas) en el mapa */
    int totalCalles();

    /** Imprime el mapa completo en la consola de forma legible. */
    void mostrarMapa();
}
