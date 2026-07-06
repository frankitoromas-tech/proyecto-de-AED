package com.trafico.gui;

import com.trafico.simulacion.InformeTrafico;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <b>Ventana de Reporte Grafico Estadistico (Java Swing Modernizado)</b>
 *
 * <p>Genera graficos de barras en 2D con un diseño moderno, tarjetas de
 * metricas estilo dashboard, fuentes claras y colores pastel.</p>
 */
public class VentanaReporteGrafico extends JFrame {

    private final InformeTrafico informe;
    private final int totalVehiculos;
    private final int totalLlegados;

    // Paleta de colores moderna (Tailwind style)
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_PANEL = Color.WHITE;
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(100, 116, 139);
    private final Color COLOR_VALOR = new Color(15, 23, 42);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 12);
    private final Font FUENTE_NUMERO = new Font("Segoe UI", Font.BOLD, 28);

    public VentanaReporteGrafico(InformeTrafico informe, int totalVehiculos, int totalLlegados) {
        this.informe = informe;
        this.totalVehiculos = totalVehiculos;
        this.totalLlegados = totalLlegados;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Dashboard Estadístico de Tráfico - AED UTP");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 15));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 20, 20, 20));

        // Panel superior con tarjetas (Dashboard Cards)
        JPanel panelResumen = crearPanelResumen();
        add(panelResumen, BorderLayout.NORTH);

        // Canvas central para dibujar el grafico de barras
        PanelGrafico panelGrafico = new PanelGrafico();
        add(panelGrafico, BorderLayout.CENTER);
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);

        panel.add(crearTarjeta("Total Vehículos", String.valueOf(totalVehiculos), new Color(59, 130, 246)));
        panel.add(crearTarjeta("Llegadas Exitosas", String.valueOf(totalLlegados), new Color(16, 185, 129)));
        panel.add(crearTarjeta("Paso de Ambulancias", String.valueOf(informe.getInterrupcionesEmergencia()), new Color(239, 68, 68)));
        panel.add(crearTarjeta("Pico Tráfico Activo", String.valueOf(informe.getPicoMaximoVehiculos()), new Color(245, 158, 11)));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor, Color colorAcento) {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave (simulada dibujando varios rectangulos transparentes)
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, 12, 12);
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 4, 12, 12);

                // Fondo principal blanco
                g2.setColor(COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);

                // Borde suave
                g2.setColor(COLOR_BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);

                // Linea de acento en la parte superior
                g2.setColor(colorAcento);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, 5, 12, 12));
                // Parche para que el acento solo sea redondeado arriba
                g2.fillRect(0, 3, getWidth() - 4, 2);
            }
        };
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setOpaque(false);
        tarjeta.setPreferredSize(new Dimension(180, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 5, 0);

        JLabel lblTitulo = new JLabel(titulo.toUpperCase(), JLabel.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        tarjeta.add(lblTitulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(FUENTE_NUMERO);
        lblValor.setForeground(COLOR_VALOR);
        tarjeta.add(lblValor, gbc);

        return tarjeta;
    }

    // ─── Canvas de Dibujo de Grafico de Barras Moderno ───────────────────────

    private class PanelGrafico extends JPanel {

        public PanelGrafico() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Dibujar fondo blanco del grafico con bordes redondeados
            g2.setColor(COLOR_PANEL);
            g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 12, 12);
            g2.setColor(COLOR_BORDE);
            g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 12, 12);

            // Titulo del grafico
            g2.setColor(COLOR_VALOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString("Top 5 Vías con Mayor Nivel de Saturación (Medido en Ticks)", 30, 35);

            Map<String, Integer> saturadas = informe.getCallesSaturadas();
            if (saturadas.isEmpty()) {
                g2.setColor(COLOR_TEXTO_SECUNDARIO);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                g2.drawString("✓ Flujo vehicular excelente. No se registraron atascos.", 30, 90);
                return;
            }

            // Top 5
            var top5 = saturadas.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .collect(Collectors.toList());

            int maxValor = top5.get(0).getValue();
            // Margenes internos del grafico
            int xInicio = 60;
            int xFin = getWidth() - 40;
            int yBase = getHeight() - 60;
            int yTop = 80;
            int anchoBarra = 70;

            // Dibujar lineas guia horizontales (grid suave)
            g2.setColor(new Color(241, 245, 249)); // Gris muy claro
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int yLinea = yBase - (i * (yBase - yTop) / 4);
                g2.drawLine(xInicio - 10, yLinea, xFin, yLinea);
                
                // Etiqueta del eje Y
                g2.setColor(COLOR_TEXTO_SECUNDARIO);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int valorEje = (maxValor * i) / 4;
                g2.drawString(String.valueOf(valorEje), xInicio - 40, yLinea + 4);
                g2.setColor(new Color(241, 245, 249)); // Restaurar color linea
            }

            // Dibujar Eje X base
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(xInicio - 10, yBase, xFin, yBase);

            // Calcular espacio dinámico
            int espacioTotal = (xFin - xInicio) - (top5.size() * anchoBarra);
            int espacio = espacioTotal / (top5.size() + 1);

            // Dibujar Barras
            for (int i = 0; i < top5.size(); i++) {
                var entrada = top5.get(i);
                int valor = entrada.getValue();
                String via = entrada.getKey();

                int alturaBarra = (int) (((double) valor / maxValor) * (yBase - yTop));
                // Asegurar altura minima visual
                if (alturaBarra < 5 && valor > 0) alturaBarra = 5;
                
                int x = xInicio + espacio + i * (anchoBarra + espacio);
                int y = yBase - alturaBarra;

                // Barra con bordes redondeados y degradado (Rojo coral a Naranja calido)
                GradientPaint degradado = new GradientPaint(
                    x, y, new Color(244, 63, 94),   // Rose
                    x, yBase, new Color(249, 115, 22) // Orange
                );
                g2.setPaint(degradado);
                g2.fillRoundRect(x, y, anchoBarra, alturaBarra, 8, 8);
                // Rectangulo base normal para que no se redondee abajo
                if (alturaBarra > 8) {
                    g2.fillRect(x, yBase - 8, anchoBarra, 8);
                }

                // Escribir Valor arriba de la barra
                g2.setColor(COLOR_VALOR);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(String.valueOf(valor));
                g2.drawString(String.valueOf(valor), x + (anchoBarra - tw)/2, y - 8);

                // Escribir nombre de la via abajo
                g2.setColor(COLOR_TEXTO_SECUNDARIO);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String[] partes = via.split("->");
                if(partes.length == 2) {
                    int w1 = g2.getFontMetrics().stringWidth(partes[0].trim());
                    int w2 = g2.getFontMetrics().stringWidth(partes[1].trim());
                    g2.drawString(partes[0].trim(), x + (anchoBarra - w1)/2, yBase + 20);
                    g2.drawString("↓", x + anchoBarra/2 - 3, yBase + 34);
                    g2.drawString(partes[1].trim(), x + (anchoBarra - w2)/2, yBase + 48);
                } else {
                    int w = g2.getFontMetrics().stringWidth(via);
                    g2.drawString(via, x + (anchoBarra - w)/2, yBase + 20);
                }
            }
        }
    }
}
