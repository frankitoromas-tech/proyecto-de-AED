package com.trafico.gui;

import com.trafico.simulacion.InformeTrafico;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <b>Ventana de Reporte Grafico Estadistico (Java Swing)</b>
 *
 * <p>Genera graficos de barras en 2D desde cero (usando Graphics2D y AWT)
 * para representar de forma visual las estadisticas finales de la simulacion:
 * las vias mas saturadas y la distribucion del trafico.</p>
 *
 * <p><b>Valor AED UTP:</b> Muestra como procesar y mapear estructuras de datos
 * complejas (como HashMaps y listas) en coordenadas visuales proporcionales.</p>
 *
 * @author Sistema de Gestion de Trafico
 * @version 1.0
 */
public class VentanaReporteGrafico extends JFrame {

    private final InformeTrafico informe;
    private final int totalVehiculos;
    private final int totalLlegados;

    public VentanaReporteGrafico(InformeTrafico informe, int totalVehiculos, int totalLlegados) {
        this.informe = informe;
        this.totalVehiculos = totalVehiculos;
        this.totalLlegados = totalLlegados;

        setTitle("Reporte Estadistico Visual - AED UTP");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con resumen rapido
        JPanel panelResumen = crearPanelResumen();
        add(panelResumen, BorderLayout.NORTH);

        // Canvas central para dibujar el grafico de barras
        PanelGrafico panelGrafico = new PanelGrafico();
        add(panelGrafico, BorderLayout.CENTER);
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(230, 240, 250));

        panel.add(crearTarjeta("Vehiculos Totales", String.valueOf(totalVehiculos)));
        panel.add(crearTarjeta("Vehiculos Llegaron", String.valueOf(totalLlegados)));
        panel.add(crearTarjeta("Interrupciones Emergencia", String.valueOf(informe.getInterrupcionesEmergencia())));
        panel.add(crearTarjeta("Pico Maximo Transito", String.valueOf(informe.getPicoMaximoVehiculos())));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220), 1));
        tarjeta.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 10));
        lblTitulo.setForeground(Color.GRAY);

        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 22));
        lblValor.setForeground(new Color(20, 80, 140));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        return tarjeta;
    }

    // ─── Canvas de Dibujo de Grafico de Barras ────────────────────────────────

    private class PanelGrafico extends JPanel {

        public PanelGrafico() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Top 5 Vias con Mayor Frecuencia de Saturacion (Ticks)"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Map<String, Integer> saturadas = informe.getCallesSaturadas();
            if (saturadas.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 14));
                g2.drawString("No se registraron congestiones ni calles saturadas durante esta simulacion.", 50, 150);
                return;
            }

            // Filtrar y ordenar el Top 5
            var top5 = saturadas.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .collect(Collectors.toList());

            int maxValor = top5.get(0).getValue();
            int xInicio = 100;
            int yBase = getHeight() - 80;
            int anchoBarra = 60;
            int espacio = 40;
            int alturaMaximaGrafico = getHeight() - 180;

            // Dibujar Ejes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(xInicio - 10, 50, xInicio - 10, yBase); // Eje Y
            g2.drawLine(xInicio - 10, yBase, getWidth() - 50, yBase); // Eje X

            // Dibujar Barras
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i < top5.size(); i++) {
                var entrada = top5.get(i);
                int valor = entrada.getValue();
                String via = entrada.getKey();

                // Proporcionalidad de la altura
                int alturaBarra = (int) (((double) valor / maxValor) * alturaMaximaGrafico);
                int x = xInicio + i * (anchoBarra + espacio);
                int y = yBase - alturaBarra;

                // Color degradado para las barras (Rojo a Naranja)
                GradientPaint degradado = new GradientPaint(
                    x, y, new Color(220, 53, 69),
                    x + anchoBarra, yBase, new Color(255, 120, 0)
                );
                g2.setPaint(degradado);
                g2.fillRect(x, y, anchoBarra, alturaBarra);

                // Contorno de barra
                g2.setColor(new Color(150, 0, 0));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(x, y, anchoBarra, alturaBarra);

                // Escribir Valor arriba de la barra
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(String.valueOf(valor), x + anchoBarra/2 - 10, y - 8);

                // Escribir nombre de la via abajo (rotada o en dos lineas)
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString(via, x - 10, yBase + 20);
            }
        }
    }
}
