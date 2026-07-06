package com.trafico.gui;

import com.trafico.mapa.MapaVial;
import com.trafico.modelo.Calle;
import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;
import com.trafico.modelo.vehiculos.FabricaVehiculos;
import com.trafico.modelo.vehiculos.Vehiculo;
import com.trafico.semaforos.ControladorSemaforos;
import com.trafico.semaforos.Semaforo;
import com.trafico.simulacion.ControladorFlota;
import com.trafico.simulacion.MotorSimulacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Random;

/**
 * <b>Ventana de Simulacion Visual Interactiva</b> (Java Swing Modernizado).
 *
 * <p>Interfaz grafica con diseño limpio y nativo. Los graficos han sido
 * mejorados para ser mas suaves y agradables a la vista sin ser pesados.</p>
 */
public class VentanaSimulador extends JFrame {

    private final MapaVial mapa;
    private final ControladorFlota flota;
    private final ControladorSemaforos semaforos;
    private final PanelMapa panelMapa;
    private final Timer temporizador;
    private final Random random;

    private JLabel lblTick;
    private JLabel lblVehiculos;
    private JLabel lblLlegados;
    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnTick;
    private JTextArea txtLogs;
    private boolean motorConfigurado = false;

    // Paleta de colores moderna
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_PANEL = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(51, 65, 85);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Font FUENTE_BASE = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 13);

    public VentanaSimulador(MapaVial mapa, ControladorFlota flota, ControladorSemaforos semaforos) {
        this.mapa = mapa;
        this.flota = flota;
        this.semaforos = semaforos;
        this.random = new Random();

        // Intentar usar el estilo visual nativo del sistema operativo (mucho mas moderno y ligero)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usa el por defecto, no importa
        }

        setTitle("Simulador de Tráfico - Interfaz Optimizada");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Espaciado entre paneles
        getContentPane().setBackground(COLOR_FONDO);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panel Central: Mapa + Leyenda ---
        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelMapa = new PanelMapa();
        panelCentro.add(panelMapa, BorderLayout.CENTER);
        panelCentro.add(crearPanelLeyenda(), BorderLayout.SOUTH);
        add(panelCentro, BorderLayout.CENTER);

        // --- Panel Derecho: Controles + Logs ---
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setOpaque(false);
        panelDerecho.setPreferredSize(new Dimension(360, 850));
        panelDerecho.add(crearPanelControles(), BorderLayout.NORTH);
        panelDerecho.add(crearPanelLogs(), BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);

        // --- Temporizador ---
        temporizador = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarUnTick();
            }
        });

        // --- Mensaje inicial ---
        txtLogs.append("👋 Bienvenido al Simulador Visual.\n");
        txtLogs.append("▶ Haz clic en 'Reproducir' para iniciar.\n");
        txtLogs.append("⏭ O usa 'Avanzar 1 Paso' para ir tick a tick.\n\n");
    }

    // =====================================================================
    //  LOGICA DE SIMULACION
    // =====================================================================

    private void asegurarMotorConfigurado() {
        if (!motorConfigurado) {
            MotorSimulacion.getInstancia().configurar(mapa, flota, semaforos, 10000);
            motorConfigurado = true;
        }
    }

    private void ejecutarUnTick() {
        asegurarMotorConfigurado();
        MotorSimulacion motor = MotorSimulacion.getInstancia();

        if (flota.todosLlegaronAlDestino() && flota.totalActivos() == 0) {
            temporizador.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnTick.setEnabled(true);
            actualizarInterfaz();
            JOptionPane.showMessageDialog(this,
                "Todos los vehiculos llegaron a su destino.\n" +
                "Puedes agregar mas vehiculos usando los botones de +",
                "Simulacion Completada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        motor.ejecutarTick();
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        MotorSimulacion motor = MotorSimulacion.getInstancia();
        lblTick.setText("Tick: " + motor.getTickActual());
        lblVehiculos.setText("En ruta: " + flota.totalActivos());
        lblLlegados.setText("Llegados: " + flota.totalLlegados());

        List<String> logsMotor = motor.getLogs();
        StringBuilder sb = new StringBuilder();
        for (String log : logsMotor) {
            sb.append(log).append("\n");
        }
        txtLogs.setText(sb.toString());
        txtLogs.setCaretPosition(txtLogs.getDocument().getLength());

        panelMapa.repaint();
    }

    // =====================================================================
    //  PANEL DE CONTROLES
    // =====================================================================

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        int fila = 0;

        JLabel titulo = new JLabel("Panel de Control");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_TEXTO);
        gbc.gridy = fila++;
        panel.add(titulo, gbc);

        // --- Indicadores ---
        JPanel panelInfo = new JPanel(new GridLayout(1, 3, 10, 0));
        panelInfo.setOpaque(false);
        lblTick = crearLabelIndicador("Tick: 0");
        lblVehiculos = crearLabelIndicador("En ruta: " + flota.totalActivos());
        lblLlegados = crearLabelIndicador("Llegados: 0");
        panelInfo.add(lblTick);
        panelInfo.add(lblVehiculos);
        panelInfo.add(lblLlegados);
        gbc.gridy = fila++;
        gbc.insets = new Insets(10, 0, 15, 0);
        panel.add(panelInfo, gbc);

        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridy = fila++;
        panel.add(new JSeparator(), gbc);

        // --- Botones de control ---
        gbc.insets = new Insets(4, 0, 4, 0);
        btnIniciar = crearBoton("▶ Reproducir (Automático)", COLOR_PRIMARIO, Color.WHITE);
        btnIniciar.addActionListener(e -> {
            asegurarMotorConfigurado();
            temporizador.start();
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnTick.setEnabled(false);
        });
        gbc.gridy = fila++;
        panel.add(btnIniciar, gbc);

        btnPausar = crearBoton("⏸ Pausar", new Color(100, 116, 139), Color.WHITE);
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(e -> {
            temporizador.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnTick.setEnabled(true);
        });
        gbc.gridy = fila++;
        panel.add(btnPausar, gbc);

        btnTick = crearBoton("⏭ Avanzar 1 Paso", new Color(241, 245, 249), COLOR_TEXTO);
        btnTick.addActionListener(e -> ejecutarUnTick());
        gbc.gridy = fila++;
        panel.add(btnTick, gbc);

        // --- Slider ---
        JLabel lblVel = new JLabel("Velocidad de reproducción:");
        lblVel.setFont(FUENTE_BASE);
        gbc.gridy = fila++;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(lblVel, gbc);

        JSlider sliderVel = new JSlider(1, 10, 2);
        sliderVel.setOpaque(false);
        sliderVel.setPaintTicks(true);
        sliderVel.addChangeListener(e -> temporizador.setDelay(1000 / sliderVel.getValue()));
        gbc.gridy = fila++;
        gbc.insets = new Insets(5, 0, 10, 0);
        panel.add(sliderVel, gbc);

        gbc.gridy = fila++;
        panel.add(new JSeparator(), gbc);

        // --- Spawn ---
        JLabel lblSpawn = new JLabel("Añadir tráfico en vivo:");
        lblSpawn.setFont(FUENTE_BASE);
        gbc.gridy = fila++;
        gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(lblSpawn, gbc);

        gbc.insets = new Insets(3, 0, 3, 0);
        panel.add(crearBoton("+ Auto Particular", new Color(241, 245, 249), COLOR_TEXTO, e -> spawnVehiculo(CategoriaVehiculo.PARTICULAR)), gbc);
        gbc.gridy = fila++;
        panel.add(crearBoton("+ Bus Urbano", new Color(241, 245, 249), COLOR_TEXTO, e -> spawnVehiculo(CategoriaVehiculo.BUS)), gbc);
        gbc.gridy = fila++;
        panel.add(crearBoton("🚨 Ambulancia (Emergencia)", new Color(254, 226, 226), new Color(220, 38, 38), e -> spawnVehiculo(CategoriaVehiculo.EMERGENCIA)), gbc);

        return panel;
    }

    private JLabel crearLabelIndicador(String texto) {
        JLabel lbl = new JLabel(texto, JLabel.CENTER);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(241, 245, 249));
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        return lbl;
    }

    private JButton crearBoton(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBoton(String texto, Color bg, Color fg, ActionListener accion) {
        JButton btn = crearBoton(texto, bg, fg);
        btn.addActionListener(accion);
        return btn;
    }

    // =====================================================================
    //  PANEL DE LOGS
    // =====================================================================

    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Historial de Eventos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLogs.setBackground(COLOR_PANEL);
        txtLogs.setForeground(new Color(30, 41, 59));
        txtLogs.setLineWrap(true);
        txtLogs.setWrapStyleWord(true);
        txtLogs.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(txtLogs);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnReporte = crearBoton("📊 Ver Reporte Estadístico Final", COLOR_TEXTO, Color.WHITE);
        btnReporte.addActionListener(e -> {
            asegurarMotorConfigurado();
            VentanaReporteGrafico rep = new VentanaReporteGrafico(
                MotorSimulacion.getInstancia().getInforme(),
                flota.totalVehiculos(), flota.totalLlegados()
            );
            rep.setVisible(true);
        });
        panel.add(btnReporte, BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================================
    //  PANEL DE LEYENDA
    // =====================================================================

    private JPanel crearPanelLeyenda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 12));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JLabel lblLeyenda = new JLabel("LEYENDA:");
        lblLeyenda.setFont(FUENTE_TITULO);
        panel.add(lblLeyenda);

        panel.add(crearMuestra("Vía Fluida", new Color(16, 185, 129), false));
        panel.add(crearMuestra("Vía Lenta", new Color(245, 158, 11), false));
        panel.add(crearMuestra("Atasco", new Color(239, 68, 68), false));

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(crearMuestra("Particular", new Color(59, 130, 246), true));
        panel.add(crearMuestra("Bus", new Color(245, 158, 11), true));
        panel.add(crearMuestra("Carga", new Color(71, 85, 105), true));
        panel.add(crearMuestra("Ambulancia", new Color(239, 68, 68), true));

        return panel;
    }

    private JPanel crearMuestra(String texto, Color color, boolean esCoche) {
        JPanel elem = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        elem.setOpaque(false);

        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                if (esCoche) {
                    g2.fillRoundRect(0, 3, 14, 14, 6, 6);
                } else {
                    g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(0, 10, 16, 10);
                }
            }
        };
        icono.setPreferredSize(new Dimension(16, 20));
        icono.setOpaque(false);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_BASE);
        lbl.setForeground(COLOR_TEXTO);

        elem.add(icono);
        elem.add(lbl);
        return elem;
    }

    // =====================================================================
    //  SPAWN DE VEHICULOS
    // =====================================================================

    private void spawnVehiculo(CategoriaVehiculo cat) {
        asegurarMotorConfigurado();
        List<Interseccion> intersecciones = mapa.todasLasIntersecciones();
        if (intersecciones.size() < 2) return;

        Interseccion origen = intersecciones.get(random.nextInt(intersecciones.size()));
        Interseccion destino;
        do {
            destino = intersecciones.get(random.nextInt(intersecciones.size()));
        } while (origen.equals(destino));

        String placa = cat.name().substring(0, 3) + "-" + (random.nextInt(899) + 100);
        Vehiculo v = FabricaVehiculos.crear(cat, placa, origen, destino);
        flota.registrar(v);

        MotorSimulacion.getInstancia().registrarLog(String.format("Nuevo: %s (%s) %s -> %s",
            placa, cat, origen.getNombre(), destino.getNombre()));

        actualizarInterfaz();
    }

    // =====================================================================
    //  CANVAS DE DIBUJO 2D (MAPA VIAL OPTIMIZADO)
    // =====================================================================

    private class PanelMapa extends JPanel {

        PanelMapa() {
            setBackground(COLOR_PANEL);
            setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // Antialiasing de alta calidad para que las lineas se vean suaves
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Dibujar cuadricula sutil de fondo (estilo blueprint/mapa)
            g2.setColor(new Color(241, 245, 249));
            for (int i = 0; i < getWidth(); i += 40) g2.drawLine(i, 0, i, getHeight());
            for (int i = 0; i < getHeight(); i += 40) g2.drawLine(0, i, getWidth(), i);

            if (mapa.totalIntersecciones() == 0) {
                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                g2.drawString("El mapa está vacío. Carga una ciudad primero.", getWidth()/2 - 150, getHeight()/2);
                return;
            }

            // Normalización de coordenadas
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                if (i.getX() < minX) minX = i.getX();
                if (i.getX() > maxX) maxX = i.getX();
                if (i.getY() < minY) minY = i.getY();
                if (i.getY() > maxY) maxY = i.getY();
            }
            double rX = (maxX - minX == 0) ? 1.0 : (maxX - minX);
            double rY = (maxY - minY == 0) ? 1.0 : (maxY - minY);
            int margen = 90;
            int anchoUtil = getWidth() - margen * 2;
            int altoUtil = getHeight() - margen * 2;

            // 1. Dibujar calles (sombra suave + linea central)
            for (Interseccion origen : mapa.todasLasIntersecciones()) {
                try {
                    for (Calle calle : mapa.callesDesdePunto(origen.getId())) {
                        Interseccion dest = calle.getDestino();
                        int x1 = margen + (int)(((origen.getX() - minX) / rX) * anchoUtil);
                        int y1 = margen + (int)(((origen.getY() - minY) / rY) * altoUtil);
                        int x2 = margen + (int)(((dest.getX() - minX) / rX) * anchoUtil);
                        int y2 = margen + (int)(((dest.getY() - minY) / rY) * altoUtil);

                        // Base de la calle (gris oscuro suave)
                        g2.setColor(new Color(203, 213, 225, 100));
                        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawLine(x1, y1, x2, y2);

                        // Color por trafico
                        if (calle.estaSaturada()) {
                            g2.setColor(new Color(239, 68, 68)); // Rojo
                            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        } else if (calle.getCongestion() > 0.4) {
                            g2.setColor(new Color(245, 158, 11)); // Naranja
                            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        } else {
                            g2.setColor(new Color(16, 185, 129)); // Verde esmeralda
                            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        }
                        g2.drawLine(x1, y1, x2, y2);
                    }
                } catch (Exception ignored) {}
            }

            // 2. Dibujar intersecciones (nodos)
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                int x = margen + (int)(((i.getX() - minX) / rX) * anchoUtil);
                int y = margen + (int)(((i.getY() - minY) / rY) * altoUtil);

                // Circulo principal (con pequeño borde sombra)
                g2.setColor(new Color(226, 232, 240));
                g2.fillOval(x - 16, y - 16, 32, 32);
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 14, y - 14, 28, 28);

                g2.setColor(new Color(71, 85, 105)); // Gris pizarra
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x - 14, y - 14, 28, 28);

                // Letra ID en el centro
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(i.getId());
                g2.drawString(i.getId(), x - tw / 2, y + 4);

                // Nombre completo de la calle debajo
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                String nombre = i.getNombre();
                int nw = fm.stringWidth(nombre);
                g2.setColor(COLOR_TEXTO);
                g2.drawString(nombre, x - nw / 2, y + 26);

                // Mini Semaforo (círculo pequeño brillante)
                if (i.isTieneSemaforo()) {
                    Semaforo sem = semaforos.buscarPorInterseccion(i.getId());
                    if (sem != null) {
                        switch (sem.getLuzActual()) {
                            case VERDE: g2.setColor(new Color(34, 197, 94)); break;
                            case AMARILLO: g2.setColor(new Color(234, 179, 8)); break;
                            case ROJO: g2.setColor(new Color(239, 68, 68)); break;
                            case EMERGENCIA_ACTIVA: g2.setColor(new Color(59, 130, 246)); break;
                        }
                        g2.fillOval(x + 10, y - 22, 12, 12);
                        g2.setColor(new Color(0,0,0,50));
                        g2.drawOval(x + 10, y - 22, 12, 12);
                    }
                }
            }

            // 3. Dibujar Vehiculos (estilo "app moderna" rectangulos redondeados)
            for (Vehiculo v : flota.getVehiculosActivos()) {
                Interseccion pos = v.getPosicion();
                if (pos == null) continue;

                int x = margen + (int)(((pos.getX() - minX) / rX) * anchoUtil);
                int y = margen + (int)(((pos.getY() - minY) / rY) * altoUtil);

                // Desplazamiento inteligente para superposiciones
                int offset = (v.getPlaca().hashCode() % 24) - 12;
                x += offset;
                y += offset;

                switch (v.getCategoria()) {
                    case EMERGENCIA: g2.setColor(new Color(239, 68, 68)); break; // Rojo vivo
                    case BUS: g2.setColor(new Color(245, 158, 11)); break; // Naranja
                    case CARGA: g2.setColor(new Color(71, 85, 105)); break; // Gris
                    case MOTOCICLETA: g2.setColor(new Color(168, 85, 247)); break; // Purpura
                    default: g2.setColor(new Color(59, 130, 246)); break; // Azul (Particular)
                }

                // Coche con bordes redondeados
                RoundRectangle2D rect = new RoundRectangle2D.Float(x - 7, y - 7, 14, 14, 6, 6);
                g2.fill(rect);
                
                // Borde suave oscuro
                g2.setColor(new Color(0,0,0, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);

                // Placa del vehiculo
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(v.getPlaca(), x + 9, y + 4);
            }
        }
    }
}
