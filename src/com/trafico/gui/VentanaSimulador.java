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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

/**
 * <b>Ventana de Simulacion Visual Interactiva Didactica (Java Swing)</b>
 *
 * <p>Diseño mejorado e intuitivo para cualquier usuario. Incluye panel de leyenda,
 * control de ticks paso a paso, spawner en caliente de vehiculos y un log interactivo
 * en tiempo real para entender el funcionamiento interno de los algoritmos de AED.</p>
 *
 * @author Sistema de Gestion de Trafico
 * @version 1.3
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
    private JButton btnIniciar;
    private JButton btnPausar;
    private JTextArea txtLogs;

    public VentanaSimulador(MapaVial mapa, ControladorFlota flota, ControladorSemaforos semaforos) {
        this.mapa = mapa;
        this.flota = flota;
        this.semaforos = semaforos;
        this.random = new Random();

        setTitle("Panel de Simulacion Didactica de Trafico Vial - AED UTP");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Central: Contiene el Mapa y la Leyenda en la parte inferior
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelMapa = new PanelMapa();
        panelIzquierdo.add(panelMapa, BorderLayout.CENTER);

        JPanel panelLeyenda = crearPanelLeyenda();
        panelIzquierdo.add(panelLeyenda, BorderLayout.SOUTH);
        add(panelIzquierdo, BorderLayout.CENTER);

        // Panel Derecho: Controles y Registro de Eventos (Logs)
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(320, 800));

        JPanel panelControles = crearPanelControles();
        panelDerecho.add(panelControles, BorderLayout.NORTH);

        JPanel panelLogs = crearPanelLogs();
        panelDerecho.add(panelLogs, BorderLayout.CENTER);

        add(panelDerecho, BorderLayout.EAST);

        // Temporizador de simulacion
        temporizador = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hacerTickSimulacion();
            }
        });

        // Log Inicial
        txtLogs.setText("[Sistema] Simulador visual iniciado. Carga autos y haz click en 'Iniciar'.\n");
    }

    private void hacerTickSimulacion() {
        MotorSimulacion motor = MotorSimulacion.getInstancia();
        if (flota.todosLlegaronAlDestino()) {
            temporizador.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            actualizarLogs();
            JOptionPane.showMessageDialog(this,
                "Todos los vehiculos han llegado exitosamente a su destino.",
                "Simulacion Completada", JOptionPane.INFORMATION_MESSAGE);
            mostrarReporteFinal();
        } else {
            motor.ejecutarTick();
            actualizarEstadisticas();
            actualizarLogs();
            panelMapa.repaint();
        }
    }

    private void actualizarLogs() {
        List<String> logsMotor = MotorSimulacion.getInstancia().getLogs();
        StringBuilder sb = new StringBuilder();
        for (String log : logsMotor) {
            sb.append(log).append("\n");
        }
        txtLogs.setText(sb.toString());
        txtLogs.setCaretPosition(txtLogs.getDocument().getLength()); // scroll al final
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Controles de Ejecucion"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;

        int fila = 0;

        // Info Tick
        lblTick = new JLabel("Tick Actual: 0", JLabel.CENTER);
        lblTick.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = fila++;
        panel.add(lblTick, gbc);

        // Info Vehiculos
        lblVehiculos = new JLabel("Vehiculos Activos: 0", JLabel.CENTER);
        lblVehiculos.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = fila++;
        panel.add(lblVehiculos, gbc);

        // Boton Iniciar
        btnIniciar = new JButton("Reproducir Automat.");
        btnIniciar.addActionListener(e -> {
            prepararMotorSimulacion();
            temporizador.start();
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
        });
        gbc.gridy = fila++;
        panel.add(btnIniciar, gbc);

        // Boton Pausar
        btnPausar = new JButton("Pausar");
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(e -> {
            temporizador.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
        });
        gbc.gridy = fila++;
        panel.add(btnPausar, gbc);

        // Boton Avanzar 1 Tick (Paso a Paso)
        JButton btnTick = new JButton("Avanzar 1 Paso (Tick)");
        btnTick.addActionListener(e -> {
            prepararMotorSimulacion();
            hacerTickSimulacion();
        });
        gbc.gridy = fila++;
        panel.add(btnTick, gbc);

        // Slider Velocidad
        JLabel lblVel = new JLabel("Frecuencia (Velocidad):");
        gbc.gridy = fila++;
        panel.add(lblVel, gbc);

        JSlider sliderVel = new JSlider(1, 5, 1);
        sliderVel.setPaintTicks(true);
        sliderVel.setPaintLabels(true);
        sliderVel.setMajorTickSpacing(1);
        sliderVel.addChangeListener(e -> {
            int valor = sliderVel.getValue();
            temporizador.setDelay(1000 / valor);
        });
        gbc.gridy = fila++;
        panel.add(sliderVel, gbc);

        // Separador para Spawners
        JSeparator sep = new JSeparator();
        gbc.gridy = fila++;
        panel.add(sep, gbc);

        // Boton Spawn Particular
        JButton btnSpawnAuto = new JButton("Spawn Vehiculo Particular");
        btnSpawnAuto.addActionListener(e -> spawnVehiculo(CategoriaVehiculo.PARTICULAR));
        gbc.gridy = fila++;
        panel.add(btnSpawnAuto, gbc);

        // Boton Spawn Emergencia
        JButton btnSpawnEmergencia = new JButton("Spawn Ambulancia 🚨");
        btnSpawnEmergencia.addActionListener(e -> spawnVehiculo(CategoriaVehiculo.EMERGENCIA));
        btnSpawnEmergencia.setForeground(new Color(0, 100, 200));
        gbc.gridy = fila++;
        panel.add(btnSpawnEmergencia, gbc);

        return panel;
    }

    private void prepararMotorSimulacion() {
        MotorSimulacion motor = MotorSimulacion.getInstancia();
        if (!motor.isEnEjecucion()) {
            motor.configurar(mapa, flota, semaforos, 5000);
        }
    }

    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Eventos (Funcionamiento AED)"));

        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLogs.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(txtLogs);
        panel.add(scroll, BorderLayout.CENTER);

        // Boton Reporte
        JButton btnReporte = new JButton("Ver Reporte Final Grafico");
        btnReporte.addActionListener(e -> mostrarReporteFinal());
        panel.add(btnReporte, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelLeyenda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Leyenda Didactica del Mapa Vial"));
        panel.setBackground(Color.WHITE);

        // Vias
        panel.add(new JLabel("Vias:"));
        panel.add(crearElementoLeyenda("Fluida", new Color(40, 167, 69), false));
        panel.add(crearElementoLeyenda("Moderada", new Color(255, 160, 0), false));
        panel.add(crearElementoLeyenda("Saturada (Atasco)", new Color(220, 53, 69), false));

        // Vehiculos
        panel.add(new JLabel("Vehiculos:"));
        panel.add(crearElementoLeyenda("Particular (Rojo)", Color.RED, true));
        panel.add(crearElementoLeyenda("Emergencia (Azul)", Color.BLUE, true));
        panel.add(crearElementoLeyenda("Bus (Naranja)", Color.ORANGE, true));
        panel.add(crearElementoLeyenda("Cargo (Gris)", Color.DARK_GRAY, true));

        return panel;
    }

    private JPanel crearElementoLeyenda(String texto, Color color, boolean esCuadrado) {
        JPanel elemento = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        elemento.setBackground(Color.WHITE);

        JPanel muestra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                if (esCuadrado) {
                    g.fillRect(0, 4, 12, 12);
                } else {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(0, 10, 20, 10);
                }
            }
        };
        muestra.setPreferredSize(new Dimension(20, 20));
        muestra.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        elemento.add(muestra);
        elemento.add(lbl);
        return elemento;
    }

    private void spawnVehiculo(CategoriaVehiculo cat) {
        List<Interseccion> intersecciones = mapa.todasLasIntersecciones();
        if (intersecciones.size() < 2) return;

        Interseccion origen = intersecciones.get(random.nextInt(intersecciones.size()));
        Interseccion destino;
        do {
            destino = intersecciones.get(random.nextInt(intersecciones.size()));
        } while (origen.equals(destino));

        String placa = "SPW-" + (random.nextInt(899) + 100);
        Vehiculo v = FabricaVehiculos.crear(cat, placa, origen, destino);
        flota.registrar(v);

        MotorSimulacion.getInstancia().registrarLog(String.format("Spawneado: Vehiculo %s (%s) de '%s' a '%s'",
            placa, cat, origen.getNombre(), destino.getNombre()));

        actualizarEstadisticas();
        actualizarLogs();
        panelMapa.repaint();
    }

    private void mostrarReporteFinal() {
        MotorSimulacion motor = MotorSimulacion.getInstancia();
        VentanaReporteGrafico rep = new VentanaReporteGrafico(
            motor.getInforme(), flota.totalVehiculos(), flota.totalLlegados()
        );
        rep.setVisible(true);
    }

    private void actualizarEstadisticas() {
        MotorSimulacion motor = MotorSimulacion.getInstancia();
        lblTick.setText("Tick Actual: " + motor.getTickActual());
        lblVehiculos.setText("Vehiculos Activos: " + flota.totalActivos());
    }

    // ─── Canvas de Dibujo 2D ──────────────────────────────────────────────────

    private class PanelMapa extends JPanel {

        public PanelMapa() {
            setBackground(new Color(240, 242, 245));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (mapa.totalIntersecciones() == 0) {
                g2.setColor(Color.GRAY);
                g2.drawString("No hay mapa vial cargado. Carga el mapa de demo primero.", 50, 50);
                return;
            }

            // Dimensiones para escalar coordenadas
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

            for (Interseccion i : mapa.todasLasIntersecciones()) {
                if (i.getX() < minX) minX = i.getX();
                if (i.getX() > maxX) maxX = i.getX();
                if (i.getY() < minY) minY = i.getY();
                if (i.getY() > maxY) maxY = i.getY();
            }

            double anchoRango = (maxX - minX == 0) ? 1.0 : (maxX - minX);
            double altoRango = (maxY - minY == 0) ? 1.0 : (maxY - minY);

            // 1. Dibujar Calles (Aristas)
            for (Interseccion origen : mapa.todasLasIntersecciones()) {
                try {
                    for (Calle calle : mapa.callesDesdePunto(origen.getId())) {
                        Interseccion dest = calle.getDestino();

                        int x1 = escalar(origen.getX(), minX, anchoRango, getWidth() - 150) + 30;
                        int y1 = escalar(origen.getY(), minY, altoRango, getHeight() - 100) + 30;
                        int x2 = escalar(dest.getX(), minX, anchoRango, getWidth() - 150) + 30;
                        int y2 = escalar(dest.getY(), minY, altoRango, getHeight() - 100) + 30;

                        // Color segun congestión
                        if (calle.estaSaturada()) {
                            g2.setColor(new Color(220, 53, 69)); // Rojo
                            g2.setStroke(new BasicStroke(3.5f));
                        } else if (calle.getCongestion() > 0.4) {
                            g2.setColor(new Color(255, 160, 0)); // Naranja
                            g2.setStroke(new BasicStroke(2.2f));
                        } else {
                            g2.setColor(new Color(40, 167, 69)); // Verde
                            g2.setStroke(new BasicStroke(1.5f));
                        }

                        g2.drawLine(x1, y1, x2, y2);
                    }
                } catch (Exception ignored) {}
            }

            // 2. Dibujar Intersecciones (Nodos)
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                int x = escalar(i.getX(), minX, anchoRango, getWidth() - 150) + 30;
                int y = escalar(i.getY(), minY, altoRango, getHeight() - 100) + 30;

                // Dibujar circulo de interseccion
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 12, y - 12, 24, 24);
                g2.setColor(new Color(60, 80, 100));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x - 12, y - 12, 24, 24);

                // Dibujar identificador
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(i.getId(), x - 4, y + 4);

                // Dibujar Semaforo si tiene
                if (i.isTieneSemaforo()) {
                    Semaforo sem = semaforos.buscarPorInterseccion(i.getId());
                    if (sem != null) {
                        switch (sem.getLuzActual()) {
                            case VERDE: g2.setColor(Color.GREEN); break;
                            case AMARILLO: g2.setColor(Color.YELLOW); break;
                            case ROJO: g2.setColor(Color.RED); break;
                            case EMERGENCIA_ACTIVA: g2.setColor(Color.BLUE); break;
                        }
                        g2.fillOval(x + 12, y - 18, 11, 11);
                        g2.setColor(Color.BLACK);
                        g2.drawOval(x + 12, y - 18, 11, 11);
                    }
                }
            }

            // 3. Dibujar Vehiculos Activos
            for (Vehiculo v : flota.getVehiculosActivos()) {
                Interseccion pos = v.getPosicion();
                if (pos != null) {
                    int x = escalar(pos.getX(), minX, anchoRango, getWidth() - 150) + 30;
                    int y = escalar(pos.getY(), minY, altoRango, getHeight() - 100) + 30;

                    // Color de acuerdo al tipo
                    switch (v.getCategoria()) {
                        case EMERGENCIA: g2.setColor(Color.BLUE); break;
                        case BUS: g2.setColor(Color.ORANGE); break;
                        case CARGA: g2.setColor(Color.DARK_GRAY); break;
                        case MOTOCICLETA: g2.setColor(Color.MAGENTA); break;
                        default: g2.setColor(Color.RED); break;
                    }
                    g2.fillRect(x - 6, y - 6, 12, 12);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x - 6, y - 6, 12, 12);
                }
            }
        }

        private int escalar(double valor, double min, double rango, int dimension) {
            return (int) (((valor - min) / rango) * (dimension - 60));
        }
    }
}
