package com.trafico.modelo;

/**
 * Categorías de vías disponibles en la red vial urbana.
 * Define las restricciones de velocidad y acceso para cada tipo de calle.
 */
public enum CategoriaCalle {
    /** Calle en zona residencial — velocidad baja, poco flujo. */
    RESIDENCIAL,
    /** Avenida principal de la ciudad — velocidad media, alto flujo. */
    AVENIDA,
    /** Vía de alta velocidad con accesos limitados. */
    AUTOPISTA,
    /** Carril exclusivo para buses del sistema de transporte público. */
    EXCLUSIVA_BUS,
    /** Calle con tráfico vehicular restringido. */
    PEATONAL
}
