package com.trafico.errores;

/**
 * Excepción base para todos los errores propios del sistema de tráfico.
 * Toda excepción específica del dominio debe extender esta clase.
 */
public class ErrorSistema extends Exception {

    /**
     * Crea un error del sistema con una descripción del problema.
     *
     * @param mensaje descripción del error ocurrido
     */
    public ErrorSistema(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea un error del sistema con descripción y causa raíz.
     *
     * @param mensaje descripción del error
     * @param causa   excepción original que provocó este error
     */
    public ErrorSistema(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
