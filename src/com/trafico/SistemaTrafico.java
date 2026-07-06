package com.trafico;

import com.trafico.consola.PanelControl;

/**
 * Clase principal y punto de entrada para la simulación del
 * Sistema de Gestión de Tráfico Vehicular.
 *
 * <p>Inicializa la interfaz de usuario interactiva por consola.</p>
 *
 * @author Sistema de Gestión de Tráfico
 * @version 1.0
 */
public class SistemaTrafico {

    /**
     * Método principal que ejecuta la aplicación.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        PanelControl panel = new PanelControl();
        panel.iniciar();
    }
}
