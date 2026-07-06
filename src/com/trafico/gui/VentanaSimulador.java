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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

/**
 * <b>Ventana de Simulacion Visual Interactiva</b> (Java Swing puro).
 *
 * <p>Interfaz grafica didactica e intuitiva. Cualquier persona puede controlarla
 * sin conocimientos tecnicos: basta hacer clic en los botones.</p>
 *
 * <p><b>Controles:</b></p>
 * <ul>
 *   <li>Reproducir: inicia la simulacion automaticamente tick a tick.</li>
 *   <li>Pausar: detiene la reproduccion automatica.</li>
 *   <li>Avanzar 1 Paso: ejecuta un unico tick para inspeccion detallada.</li>
 *   <li>Slider de velocidad: controla la frecuencia de los ticks automaticos.</li>
 *   <li>Spawn: agrega vehiculos nuevos en caliente sin detener la simulacion.</li>
 * </ul>
 *
 * @author Sistema de Gestion de Trafico
 * @version 2.0
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

    public VentanaSimulador(MapaVial mapa, ControladorFlota flota, ControladorSemaforos semaforos) {
        this.mapa = mapa;
        this.flota = flota;
        this.semaforos = semaforos;
        this.random = new Random();

        setTitle("Simulador de Trafico Vial - Trabajo Final AED (UTP)");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Central: Mapa + Leyenda ---
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelMapa = new PanelMapa();
        panelCentro.add(panelMapa, BorderLayout.CENTER);
        panelCentro.add(crearPanelLeyenda(), BorderLayout.SOUTH);
        add(panelCentro, BorderLayout.CENTER);

        // --- Panel Derecho: Controles + Logs ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(340, 850));
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
        txtLogs.append("Bienvenido al Simulador Visual.\n");
        txtLogs.append("Haz clic en 'Reproducir' para iniciar.\n");
        txtLogs.append("O usa 'Avanzar 1 Paso' para ir tick a tick.\n\n");
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
                "Puedes agregar mas vehiculos y seguir simulando.",
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

        // Actualizar logs
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
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Controles de la Simulacion"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridx = 0;
        int fila = 0;

        // --- Indicadores ---
        JPanel panelInfo = new JPanel(new GridLayout(1, 3, 5, 0));
        lblTick = new JLabel("Tick: 0", JLabel.CENTER);
        lblTick.setFont(new Font("Arial", Font.BOLD, 11));
        lblVehiculos = new JLabel("En ruta: " + flota.totalActivos(), JLabel.CENTER);
        lblVehiculos.setFont(new Font("Arial", Font.BOLD, 11));
        lblLlegados = new JLabel("Llegados: 0", JLabel.CENTER);
        lblLlegados.setFont(new Font("Arial", Font.BOLD, 11));
        panelInfo.add(lblTick);
        panelInfo.add(lblVehiculos);
        panelInfo.add(lblLlegados);
        gbc.gridy = fila++;
        panel.add(panelInfo, gbc);

        // --- Separador ---
        gbc.gridy = fila++;
        panel.add(new JSeparator(), gbc);

        // --- Botones de control ---
        btnIniciar = new JButton("Reproducir (automatico)");
        btnIniciar.setToolTipText("Inicia la simulacion avanzando ticks automaticamente");
        btnIniciar.addActionListener(e -> {
            asegurarMotorConfigurado();
            temporizador.start();
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnTick.setEnabled(false);
        });
        gbc.gridy = fila++;
        panel.add(btnIniciar, gbc);

        btnPausar = new JButton("Pausar");
        btnPausar.setToolTipText("Detiene la reproduccion automatica");
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(e -> {
            temporizador.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnTick.setEnabled(true);
        });
        gbc.gridy = fila++;
        panel.add(btnPausar, gbc);

        btnTick = new JButton("Avanzar 1 Paso");
        btnTick.setToolTipText("Ejecuta un solo tick para inspeccion detallada");
        btnTick.addActionListener(e -> ejecutarUnTick());
        gbc.gridy = fila++;
        panel.add(btnTick, gbc);

        // --- Slider ---
        gbc.gridy = fila++;
        JLabel lblVel = new JLabel("Velocidad de reproduccion:");
        lblVel.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblVel, gbc);

        JSlider sliderVel = new JSlider(1, 10, 2);
        sliderVel.setToolTipText("Ajusta la velocidad: 1=lento, 10=muy rapido");
        sliderVel.setPaintTicks(true);
        sliderVel.setPaintLabels(true);
        sliderVel.setMajorTickSpacing(3);
        sliderVel.setMinorTickSpacing(1);
        sliderVel.addChangeListener(e -> {
            int valor = sliderVel.getValue();
            temporizador.setDelay(1000 / valor);
        });
        gbc.gridy = fila++;
        panel.add(sliderVel, gbc);

        // --- Separador ---
        gbc.gridy = fila++;
        panel.add(new JSeparator(), gbc);

        // --- Spawn ---
        JLabel lblSpawn = new JLabel("Agregar vehiculos en caliente:");
        lblSpawn.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridy = fila++;
        panel.add(lblSpawn, gbc);

        JButton btnSpawnAuto = new JButton("+ Auto Particular");
        btnSpawnAuto.setToolTipText("Crea un auto particular con origen y destino aleatorios");
        btnSpawnAuto.addActionListener(e -> spawnVehiculo(CategoriaVehiculo.PARTICULAR));
        gbc.gridy = fila++;
        panel.add(btnSpawnAuto, gbc);

        JButton btnSpawnBus = new JButton("+ Bus Urbano");
        btnSpawnBus.setToolTipText("Crea un bus urbano con ruta aleatoria");
        btnSpawnBus.addActionListener(e -> spawnVehiculo(CategoriaVehiculo.BUS));
        gbc.gridy = fila++;
        panel.add(btnSpawnBus, gbc);

        JButton btnSpawnEmergencia = new JButton("+ Ambulancia (Emergencia)");
        btnSpawnEmergencia.setToolTipText("Crea una ambulancia con sirena activa y prioridad maxima");
        btnSpawnEmergencia.setForeground(new Color(0, 80, 180));
        btnSpawnEmergencia.addActionListener(e -> spawnVehiculo(CategoriaVehiculo.EMERGENCIA));
        gbc.gridy = fila++;
        panel.add(btnSpawnEmergencia, gbc);

        return panel;
    }

    // =====================================================================
    //  PANEL DE LOGS
    // =====================================================================

    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Eventos en Tiempo Real"));

        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLogs.setBackground(new Color(250, 250, 250));
        txtLogs.setLineWrap(true);
        txtLogs.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtLogs);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnReporte = new JButton("Ver Reporte Estadistico Final");
        btnReporte.setToolTipText("Abre una ventana con graficos de barras de las vias mas congestionadas");
        btnReporte.addActionListener(e -> {
            asegurarMotorConfigurado();
            MotorSimulacion motor = MotorSimulacion.getInstancia();
            VentanaReporteGrafico rep = new VentanaReporteGrafico(
                motor.getInforme(), flota.totalVehiculos(), flota.totalLlegados()
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Leyenda del Mapa"));
        panel.setBackground(Color.WHITE);

        // Vias
        panel.add(new JLabel("Vias:"));
        panel.add(crearMuestra("Fluida (sin trafico)", new Color(40, 167, 69), false));
        panel.add(crearMuestra("Moderada", new Color(255, 160, 0), false));
        panel.add(crearMuestra("Congestionada", new Color(220, 53, 69), false));

        panel.add(new JLabel("   "));

        // Vehiculos
        panel.add(new JLabel("Vehiculos:"));
        panel.add(crearMuestra("Auto", Color.RED, true));
        panel.add(crearMuestra("Ambulancia", Color.BLUE, true));
        panel.add(crearMuestra("Bus", Color.ORANGE, true));
        panel.add(crearMuestra("Camion", Color.DARK_GRAY, true));
        panel.add(crearMuestra("Moto", Color.MAGENTA, true));

        panel.add(new JLabel("   "));

        // Semaforos
        panel.add(new JLabel("Semaforo:"));
        panel.add(crearMuestra("Verde", Color.GREEN, true));
        panel.add(crearMuestra("Rojo", Color.RED, true));

        return panel;
    }

    private JPanel crearMuestra(String texto, Color color, boolean esCuadrado) {
        JPanel elem = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        elem.setBackground(Color.WHITE);

        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                if (esCuadrado) {
                    g.fillRect(1, 3, 12, 12);
                } else {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(0, 9, 18, 9);
                }
            }
        };
        icono.setPreferredSize(new Dimension(18, 18));
        icono.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));

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

        MotorSimulacion.getInstancia().registrarLog(String.format("Nuevo vehiculo: %s (%s) de '%s' a '%s'",
            placa, cat, origen.getNombre(), destino.getNombre()));

        actualizarInterfaz();
    }

    // =====================================================================
    //  CANVAS DE DIBUJO 2D (MAPA VIAL)
    // =====================================================================

    private class PanelMapa extends JPanel {

        PanelMapa() {
            setBackground(new Color(245, 247, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (mapa.totalIntersecciones() == 0) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 14));
                g2.drawString("No hay mapa cargado. Carga una ciudad desde el menu de consola.", 40, 50);
                return;
            }

            // Calcular limites para escalar
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
            int margen = 80;
            int anchoUtil = getWidth() - margen * 2;
            int altoUtil = getHeight() - margen * 2;

            // 1. Dibujar calles (aristas del grafo)
            for (Interseccion origen : mapa.todasLasIntersecciones()) {
                try {
                    for (Calle calle : mapa.callesDesdePunto(origen.getId())) {
                        Interseccion dest = calle.getDestino();
                        int x1 = margen + (int)(((origen.getX() - minX) / rX) * anchoUtil);
                        int y1 = margen + (int)(((origen.getY() - minY) / rY) * altoUtil);
                        int x2 = margen + (int)(((dest.getX() - minX) / rX) * anchoUtil);
                        int y2 = margen + (int)(((dest.getY() - minY) / rY) * altoUtil);

                        if (calle.estaSaturada()) {
                            g2.setColor(new Color(220, 53, 69));
                            g2.setStroke(new BasicStroke(4f));
                        } else if (calle.getCongestion() > 0.4) {
                            g2.setColor(new Color(255, 160, 0));
                            g2.setStroke(new BasicStroke(2.5f));
                        } else {
                            g2.setColor(new Color(40, 167, 69));
                            g2.setStroke(new BasicStroke(1.8f));
                        }
                        g2.drawLine(x1, y1, x2, y2);
                    }
                } catch (Exception ignored) {}
            }

            // 2. Dibujar intersecciones (nodos del grafo)
            for (Interseccion i : mapa.todasLasIntersecciones()) {
                int x = margen + (int)(((i.getX() - minX) / rX) * anchoUtil);
                int y = margen + (int)(((i.getY() - minY) / rY) * altoUtil);

                // Circulo del nodo
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 14, y - 14, 28, 28);
                g2.setColor(new Color(50, 70, 90));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x - 14, y - 14, 28, 28);

                // ID dentro del circulo
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(i.getId());
                g2.drawString(i.getId(), x - tw / 2, y + 5);

                // Nombre debajo del nodo
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                fm = g2.getFontMetrics();
                String nombre = i.getNombre();
                if (nombre.length() > 18) nombre = nombre.substring(0, 16) + "..";
                int nw = fm.stringWidth(nombre);
                g2.setColor(new Color(80, 80, 80));
                g2.drawString(nombre, x - nw / 2, y + 24);

                // Semaforo (indicador de luz)
                if (i.isTieneSemaforo()) {
                    Semaforo sem = semaforos.buscarPorInterseccion(i.getId());
                    if (sem != null) {
                        switch (sem.getLuzActual()) {
                            case VERDE: g2.setColor(new Color(0, 200, 0)); break;
                            case AMARILLO: g2.setColor(new Color(255, 220, 0)); break;
                            case ROJO: g2.setColor(new Color(230, 30, 30)); break;
                            case EMERGENCIA_ACTIVA: g2.setColor(new Color(0, 100, 255)); break;
                        }
                        g2.fillOval(x + 14, y - 20, 12, 12);
                        g2.setColor(Color.BLACK);
                        g2.drawOval(x + 14, y - 20, 12, 12);
                    }
                }
            }

            // 3. Dibujar vehiculos
            for (Vehiculo v : flota.getVehiculosActivos()) {
                Interseccion pos = v.getPosicion();
                if (pos == null) continue;

                int x = margen + (int)(((pos.getX() - minX) / rX) * anchoUtil);
                int y = margen + (int)(((pos.getY() - minY) / rY) * altoUtil);

                // Desplazar ligeramente para no solaparse con el nodo
                int offset = (v.getPlaca().hashCode() % 20) - 10;
                x += offset;
                y += offset;

                switch (v.getCategoria()) {
                    case EMERGENCIA: g2.setColor(Color.BLUE); break;
                    case BUS: g2.setColor(Color.ORANGE); break;
                    case CARGA: g2.setColor(Color.DARK_GRAY); break;
                    case MOTOCICLETA: g2.setColor(Color.MAGENTA); break;
                    default: g2.setColor(Color.RED); break;
                }

                g2.fillRect(x - 5, y - 5, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawRect(x - 5, y - 5, 10, 10);

                // Placa del vehiculo
                g2.setFont(new Font("Arial", Font.PLAIN, 8));
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(v.getPlaca(), x + 7, y + 3);
            }
        }
    }
}
