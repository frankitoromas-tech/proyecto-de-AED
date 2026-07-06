package com.trafico.errores;

/**
 * Error lanzado cuando no existe ningún camino válido entre dos intersecciones.
 *
 * <p>Puede ocurrir si: el mapa no es conexo, los puntos no existen,
 * o todas las calles del camino están bloqueadas.</p>
 */
public class ErrorRutaImposible extends ErrorSistema {

    /** ID de la intersección de origen de la ruta fallida. */
    private final String origenId;

    /** ID de la intersección de destino de la ruta fallida. */
    private final String destinoId;

    /**
     * Crea el error indicando el par de intersecciones sin ruta posible.
     *
     * @param origenId  ID del punto de partida
     * @param destinoId ID del punto de llegada
     */
    public ErrorRutaImposible(String origenId, String destinoId) {
        super(String.format("No existe ruta posible entre '%s' y '%s'.", origenId, destinoId));
        this.origenId = origenId;
        this.destinoId = destinoId;
    }

    /**
     * Crea el error con un mensaje adicional que explica la causa.
     *
     * @param origenId  ID del punto de partida
     * @param destinoId ID del punto de llegada
     * @param detalle   explicación adicional del problema
     */
    public ErrorRutaImposible(String origenId, String destinoId, String detalle) {
        super(String.format("No existe ruta entre '%s' y '%s': %s", origenId, destinoId, detalle));
        this.origenId = origenId;
        this.destinoId = destinoId;
    }

    public String getOrigenId()  { return origenId; }
    public String getDestinoId() { return destinoId; }
}
