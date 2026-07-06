package com.trafico.consola;

import com.trafico.datos.CargadorMapa;
import com.trafico.datos.GeneradorInforme;
import com.trafico.errores.ErrorRutaImposible;
import com.trafico.errores.ErrorSistema;
import com.trafico.estructuras.*;
import com.trafico.mapa.MapaCiudadGrande;
import com.trafico.mapa.MapaVial;
import com.trafico.modelo.CategoriaVehiculo;
import com.trafico.modelo.Interseccion;
import com.trafico.modelo.vehiculos.*;
import com.trafico.navegacion.*;
import com.trafico.semaforos.ControladorSemaforos;
import com.trafico.semaforos.Semaforo;
import com.trafico.simulacion.ControladorFlota;
import com.trafico.simulacion.InformeTrafico;
import com.trafico.simulacion.MotorSimulacion;
import com.trafico.gui.VentanaSimulador;
import javax.swing.SwingUtilities;

import java.util.List;
import java.util.Scanner;

/**
 * Panel de Control -- interfaz de usuario interactiva por consola.
 *
 * <p>Menu principal intuitivo y facil de usar. Carga automaticamente una ciudad
 * de ejemplo al iniciar para que el usuario pueda explorar el sistema sin
 * necesidad de configuracion manual previa.</p>
 *
 * @author Sistema de Gestion de Trafico
 * @version 2.0
 */
public class PanelControl {

    private final Scanner entrada;
    private MapaVial mapa;
    private ControladorFlota flota;
    private ControladorSemaforos controladorSemaforos;
    private final CargadorMapa cargadorMapa;
    private final GeneradorInforme generadorInforme;

    // Estructuras de la Rubrica UTP para la Demo
    private final ArbolAVL<String, String> arbolAVL;
    private final ListaEnlazadaSimple<String> listaEnlazada;

    public PanelControl() {
        this.entrada = new Scanner(System.in);
        this.mapa = new MapaCiudadGrande();
        this.controladorSemaforos = new ControladorSemaforos();
        this.flota = new ControladorFlota(mapa);
        this.cargadorMapa = new CargadorMapa();
        this.generadorInforme = new GeneradorInforme();
        this.arbolAVL = new ArbolAVL<>();
        this.listaEnlazada = new ListaEnlazadaSimple<>();
    }

    // === BUCLE PRINCIPAL =====================================================

    public void iniciar() {
        mostrarBienvenida();

        // Cargar ciudad demo automaticamente al iniciar
        cargarCiudadDemoSilencioso();

        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEnteroValidado("Tu eleccion", 0, 8);

            switch (opcion) {
                case 1: menuSimuladorVisual();       break;
                case 2: menuSimulacionConsola();      break;
                case 3: menuRegistrarVehiculo();      break;
                case 4: menuCalcularRuta();           break;
                case 5: menuEstadoSemaforos();        break;
                case 6: menuAdministrarCiudad();      break;
                case 7: menuVerEstadisticas();        break;
                case 8: menuRubricaUTP();             break;
                case 0: ejecutando = false;           break;
            }
        }

        System.out.println("\n  Hasta luego. Sistema cerrado.\n");
        entrada.close();
    }

    // === PANTALLAS ===========================================================

    private void mostrarBienvenida() {
        System.out.println();
        System.out.println("  ===================================================");
        System.out.println("     SISTEMA DE GESTION DE TRAFICO VIAL");
        System.out.println("     Trabajo Final - Algoritmos y Estructuras de Datos");
        System.out.println("     Universidad Tecnologica del Peru (UTP)");
        System.out.println("  ===================================================");
        System.out.println();
        System.out.println("  Cargando ciudad de ejemplo automaticamente...");
    }

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("  ---------------------------------------------------");
        System.out.println("                   MENU PRINCIPAL");
        System.out.println("  ---------------------------------------------------");
        System.out.printf("   Ciudad: %d intersecciones | %d calles | %d semaforos%n",
            mapa.totalIntersecciones(), mapa.totalCalles(), controladorSemaforos.totalSemaforos());
        System.out.printf("   Flota:  %d vehiculos activos | %d llegados%n",
            flota.totalActivos(), flota.totalLlegados());
        System.out.println("  ---------------------------------------------------");
        System.out.println();
        System.out.println("   1. Abrir Simulador Visual (Ventana Grafica)");
        System.out.println("   2. Ejecutar Simulacion Rapida (Consola)");
        System.out.println("   3. Agregar Vehiculo a la Flota");
        System.out.println("   4. Calcular Ruta entre Dos Puntos");
        System.out.println("   5. Ver Estado de los Semaforos");
        System.out.println("   6. Administrar la Ciudad (Mapa Vial)");
        System.out.println("   7. Ver Estadisticas del Sistema");
        System.out.println("   8. Estructuras de Datos (Rubrica UTP)");
        System.out.println("   0. Salir");
        System.out.println();
    }

    // === OPCION 1: SIMULADOR VISUAL ==========================================

    private void menuSimuladorVisual() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  [!] Primero carga una ciudad (opcion 6).");
            return;
        }

        System.out.println("\n  Abriendo el Simulador Visual...");
        System.out.println("  Tip: Usa los botones de la ventana para controlar la simulacion.");
        System.out.println("       Puedes agregar vehiculos en caliente y avanzar paso a paso.");

        // Precargar vehiculos de prueba si no hay ninguno
        if (flota.totalActivos() == 0) {
            agregarVehiculosDemoVariados();
        }

        SwingUtilities.invokeLater(() -> {
            VentanaSimulador gui = new VentanaSimulador(mapa, flota, controladorSemaforos);
            gui.setVisible(true);
        });
    }

    // === OPCION 2: SIMULACION EN CONSOLA =====================================

    private void menuSimulacionConsola() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  [!] Primero carga una ciudad (opcion 6).");
            return;
        }

        System.out.println("\n  === SIMULACION RAPIDA EN CONSOLA ===");

        if (flota.totalActivos() == 0) {
            System.out.println("  No hay vehiculos registrados. Agregando flota de prueba...");
            agregarVehiculosDemoVariados();
        }

        System.out.println("  Vehiculos activos: " + flota.totalActivos());
        System.out.print("  Cuantos ticks simular? (recomendado: 50-200): ");
        int ticks = leerEnteroValidado("Ticks", 1, 10000);

        MotorSimulacion motor = MotorSimulacion.getInstancia();
        motor.configurar(mapa, flota, controladorSemaforos, ticks);
        motor.ejecutar();

        InformeTrafico informe = motor.getInforme();
        informe.mostrarEnConsola(motor.getTickActual(), flota.totalVehiculos(), flota.totalLlegados());
    }

    // === OPCION 3: REGISTRAR VEHICULO ========================================

    private void menuRegistrarVehiculo() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  [!] Primero carga una ciudad (opcion 6).");
            return;
        }

        System.out.println("\n  === AGREGAR VEHICULO A LA FLOTA ===");
        System.out.println("  Que tipo de vehiculo quieres agregar?");
        System.out.println();
        System.out.println("   1. Auto Particular      (velocidad media, prioridad normal)");
        System.out.println("   2. Ambulancia            (alta velocidad, prioridad maxima)");
        System.out.println("   3. Bus Urbano            (velocidad baja, transporte masivo)");
        System.out.println("   4. Camion de Carga       (velocidad baja, transporte pesado)");
        System.out.println("   5. Motocicleta           (alta velocidad, agil)");
        System.out.println("   0. Volver al menu");
        System.out.println();

        int tipo = leerEnteroValidado("Tipo de vehiculo", 0, 5);
        if (tipo == 0) return;

        System.out.print("  Placa del vehiculo (ej. ABC-123): ");
        String placa = entrada.nextLine().trim().toUpperCase();
        if (placa.isEmpty()) placa = "VEH-" + (int)(Math.random() * 900 + 100);

        // Mostrar intersecciones disponibles
        System.out.println("\n  Intersecciones disponibles:");
        List<Interseccion> todas = mapa.todasLasIntersecciones();
        for (int i = 0; i < todas.size(); i++) {
            Interseccion inter = todas.get(i);
            System.out.printf("   [%s] %s%n", inter.getId(), inter.getNombre());
        }

        System.out.print("\n  ID del punto de ORIGEN: ");
        String origenId = entrada.nextLine().trim().toUpperCase();
        System.out.print("  ID del punto de DESTINO: ");
        String destinoId = entrada.nextLine().trim().toUpperCase();

        Interseccion origen  = mapa.buscarInterseccion(origenId);
        Interseccion destino = mapa.buscarInterseccion(destinoId);

        if (origen == null || destino == null) {
            System.out.println("  [X] Error: Una o ambas intersecciones no existen.");
            return;
        }
        if (origen.equals(destino)) {
            System.out.println("  [X] Error: Origen y destino deben ser diferentes.");
            return;
        }

        Vehiculo vehiculo;
        switch (tipo) {
            case 1: vehiculo = FabricaVehiculos.crearAutoParticular(placa, "Sedan", origen, destino); break;
            case 2: vehiculo = FabricaVehiculos.crearUnidadEmergencia(placa, UnidadEmergencia.Tipo.AMBULANCIA, origen, destino); break;
            case 3: vehiculo = FabricaVehiculos.crearBusUrbano(placa, "Ruta-Urbana", 45, origen, destino); break;
            case 4: vehiculo = FabricaVehiculos.crear(CategoriaVehiculo.CARGA, placa, origen, destino); break;
            case 5: vehiculo = FabricaVehiculos.crear(CategoriaVehiculo.MOTOCICLETA, placa, origen, destino); break;
            default: return;
        }

        flota.registrar(vehiculo);
    }

    // === OPCION 4: CALCULAR RUTA =============================================

    private void menuCalcularRuta() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  [!] Primero carga una ciudad (opcion 6).");
            return;
        }

        System.out.println("\n  === CALCULAR RUTA ENTRE DOS PUNTOS ===");
        System.out.println("  Que algoritmo quieres usar?");
        System.out.println();
        System.out.println("   1. Dijkstra          - Encuentra la ruta mas corta");
        System.out.println("   2. A* (A-Estrella)   - Ruta optimizada con heuristica");
        System.out.println("   3. Bellman-Ford      - Maneja atascos con pesos negativos");
        System.out.println("   4. BFS               - Ruta con menos semaforos (paradas)");
        System.out.println("   5. Comparar los 4 algoritmos a la vez");
        System.out.println();

        int algoritmo = leerEnteroValidado("Algoritmo", 1, 5);

        // Mostrar intersecciones
        System.out.println("\n  Intersecciones disponibles:");
        for (Interseccion i : mapa.todasLasIntersecciones()) {
            System.out.printf("   [%s] %s%n", i.getId(), i.getNombre());
        }

        System.out.print("\n  ID de origen: ");
        String origenId = entrada.nextLine().trim().toUpperCase();
        System.out.print("  ID de destino: ");
        String destinoId = entrada.nextLine().trim().toUpperCase();

        if (algoritmo == 5) {
            new ComparadorRutas().comparar(mapa, origenId, destinoId);
            return;
        }

        EstrategiaRuta[] estrategias = {
            new RutaMasCorta(), new RutaOptimizada(),
            new RutaConGestion(), new RutaMenosSemaforos()
        };

        try {
            List<Interseccion> ruta = estrategias[algoritmo - 1].calcularRuta(mapa, origenId, destinoId);
            System.out.printf("%n  Ruta encontrada por %s (%d paradas):%n",
                estrategias[algoritmo - 1].getNombre(), ruta.size());
            for (int i = 0; i < ruta.size(); i++) {
                String marca = (i == 0) ? "INICIO" : (i == ruta.size() - 1) ? "FIN" : "  >>  ";
                System.out.printf("   %6s  [%s] %s%n", marca, ruta.get(i).getId(), ruta.get(i).getNombre());
            }
        } catch (ErrorRutaImposible e) {
            System.out.println("  [X] " + e.getMessage());
        }
    }

    // === OPCION 5: ESTADO DE SEMAFOROS ========================================

    private void menuEstadoSemaforos() {
        if (controladorSemaforos.totalSemaforos() == 0) {
            System.out.println("  [!] No hay semaforos configurados en la ciudad.");
            return;
        }
        controladorSemaforos.mostrarEstado();
    }

    // === OPCION 6: ADMINISTRAR CIUDAD ========================================

    private void menuAdministrarCiudad() {
        System.out.println("\n  === ADMINISTRAR CIUDAD (MAPA VIAL) ===");
        System.out.println();
        System.out.println("   1. Ver el mapa actual de la ciudad");
        System.out.println("   2. Recargar la ciudad de ejemplo (demo)");
        System.out.println("   3. Agregar una interseccion manualmente");
        System.out.println("   4. Agregar una calle entre intersecciones");
        System.out.println("   5. Cargar mapa desde archivo .txt");
        System.out.println("   6. Exportar mapa a archivo .txt");
        System.out.println("   0. Volver al menu");
        System.out.println();

        int opcion = leerEnteroValidado("Opcion", 0, 6);
        switch (opcion) {
            case 1: mapa.mostrarMapa();       break;
            case 2: recargarCiudadDemo();     break;
            case 3: agregarInterseccion();    break;
            case 4: agregarCalle();           break;
            case 5: cargarDesdeArchivo();      break;
            case 6: exportarAArchivo();        break;
        }
    }

    // === OPCION 7: ESTADISTICAS ==============================================

    private void menuVerEstadisticas() {
        System.out.println("\n  === ESTADISTICAS DEL SISTEMA ===");
        System.out.println();
        System.out.printf("  Intersecciones    : %d%n", mapa.totalIntersecciones());
        System.out.printf("  Calles            : %d%n", mapa.totalCalles());
        System.out.printf("  Semaforos         : %d%n", controladorSemaforos.totalSemaforos());
        System.out.printf("  Vehiculos activos : %d%n", flota.totalActivos());
        System.out.printf("  Vehiculos llegados: %d%n", flota.totalLlegados());
        System.out.printf("  Cache de rutas    : %s%n", flota.getEstadisticasCache());
        System.out.printf("  Mapa conexo       : %s%n", mapa.esMapaConexo() ? "Si" : "No");
    }

    // === OPCION 8: RUBRICA UTP ===============================================

    private void menuRubricaUTP() {
        System.out.println("\n  === DEMOSTRACION DE ESTRUCTURAS DE DATOS (RUBRICA UTP) ===");
        System.out.println("  Estas demos muestran las estructuras exigidas por la rubrica.");
        System.out.println();
        System.out.println("   1. Arbol AVL       (Insercion, busqueda, eliminacion, recorridos)");
        System.out.println("   2. Lista Enlazada  (Nodos manuales, insertar, eliminar, buscar)");
        System.out.println("   3. Ordenamiento    (Burbuja, Insercion, Seleccion sobre arreglos)");
        System.out.println("   4. Busqueda        (Secuencial y Binaria sobre arreglos)");
        System.out.println("   0. Volver al menu");
        System.out.println();

        int opcion = leerEnteroValidado("Que demo ejecutar", 0, 4);
        switch (opcion) {
            case 1: demoArbolAVL(); break;
            case 2: demoListaEnlazada(); break;
            case 3: demoOrdenamientoArreglos(); break;
            case 4: demoBusquedas(); break;
        }
    }

    // === CARGA AUTOMATICA DE CIUDAD DEMO =====================================

    /**
     * Carga la ciudad de ejemplo automaticamente al iniciar el sistema.
     * No requiere interaccion del usuario.
     */
    private void cargarCiudadDemoSilencioso() {
        try {
            String[][] intersecciones = {
                {"A", "Plaza Central",        "0",    "0"},
                {"B", "Av. Norte con Calle 1","500",  "0"},
                {"C", "Av. Norte con Calle 2","1000", "0"},
                {"D", "Parque Industrial",    "1500", "0"},
                {"E", "Barrio El Prado",      "0",    "500"},
                {"F", "Centro Comercial",     "500",  "500"},
                {"G", "Hospital General",     "1000", "500"},
                {"H", "Terminal de Buses",    "1500", "500"},
                {"I", "Zona Residencial Sur", "0",    "1000"},
                {"J", "Estadio Municipal",    "500",  "1000"},
                {"K", "Universidad Central",  "1000", "1000"},
                {"L", "Aeropuerto",           "1500", "1000"}
            };

            for (String[] datos : intersecciones) {
                Interseccion i = new Interseccion(datos[0], datos[1],
                    Double.parseDouble(datos[2]), Double.parseDouble(datos[3]));
                mapa.agregarInterseccion(i);
            }

            String[][] calles = {
                {"A","B","500","60","AVENIDA","true"},   {"B","C","500","60","AVENIDA","true"},
                {"C","D","500","60","AVENIDA","true"},   {"A","E","500","50","RESIDENCIAL","true"},
                {"B","F","500","50","RESIDENCIAL","true"},{"C","G","500","60","AVENIDA","true"},
                {"D","H","500","50","RESIDENCIAL","true"},{"E","F","500","60","AVENIDA","true"},
                {"F","G","500","60","AVENIDA","true"},   {"G","H","500","60","AVENIDA","true"},
                {"E","I","500","50","RESIDENCIAL","true"},{"F","J","500","50","RESIDENCIAL","true"},
                {"G","K","500","80","AUTOPISTA","false"}, {"H","L","500","80","AUTOPISTA","false"},
                {"I","J","500","60","AVENIDA","true"},   {"J","K","500","60","AVENIDA","true"},
                {"K","L","500","80","AUTOPISTA","false"}, {"A","F","707","70","AVENIDA","false"},
                {"F","K","707","70","AVENIDA","false"}
            };

            for (String[] datos : calles) {
                Interseccion o = mapa.buscarInterseccion(datos[0]);
                Interseccion d = mapa.buscarInterseccion(datos[1]);
                com.trafico.modelo.CategoriaCalle cat =
                    com.trafico.modelo.CategoriaCalle.valueOf(datos[4]);
                mapa.agregarCalle(new com.trafico.modelo.Calle(o, d,
                    Double.parseDouble(datos[2]), Double.parseDouble(datos[3]),
                    cat, Boolean.parseBoolean(datos[5])));
            }

            for (String id : new String[]{"A","B","C","F","G","K"}) {
                Interseccion i = mapa.buscarInterseccion(id);
                i.setTieneSemaforo(true);
                controladorSemaforos.registrar(new Semaforo("SEM-" + id, i, 10, 10));
            }

            System.out.printf("  Ciudad cargada: %d intersecciones, %d calles, %d semaforos.%n",
                mapa.totalIntersecciones(), mapa.totalCalles(), controladorSemaforos.totalSemaforos());
            System.out.println("  Red vial conectada: " + (mapa.esMapaConexo() ? "Si" : "No"));
            agregarVehiculosDemoVariados();
            System.out.println("  El sistema esta listo para usar.");

        } catch (ErrorSistema e) {
            System.out.println("  [!] Error al cargar demo: " + e.getMessage());
        }
    }

    private void recargarCiudadDemo() {
        System.out.println("\n  Reiniciando ciudad...");
        this.mapa = new MapaCiudadGrande();
        this.controladorSemaforos = new ControladorSemaforos();
        this.flota = new ControladorFlota(mapa);
        cargarCiudadDemoSilencioso();
    }

    // === VEHICULOS DE EJEMPLO VARIADOS =======================================

    /**
     * Agrega una flota variada de vehiculos de prueba con rutas diversas.
     * Se usa cuando el usuario inicia la simulacion sin haber agregado vehiculos.
     */
    private void agregarVehiculosDemoVariados() {
        List<Interseccion> lista = mapa.todasLasIntersecciones();
        if (lista.size() < 2) return;

        // Crear vehiculos variados con diferentes origenes y destinos
        String[][] vehiculosDemo = {
            {"ABC-101", "PARTICULAR",  "A", "L"},  // Plaza Central -> Aeropuerto
            {"ABC-202", "PARTICULAR",  "I", "D"},  // Zona Residencial -> Parque Industrial
            {"ABC-303", "PARTICULAR",  "E", "C"},  // Barrio El Prado -> Av. Norte con Calle 2
            {"BUS-001", "BUS",         "A", "K"},  // Plaza Central -> Universidad
            {"BUS-002", "BUS",         "L", "I"},  // Aeropuerto -> Zona Residencial
            {"AMB-001", "EMERGENCIA",  "G", "A"},  // Hospital -> Plaza Central
            {"CAM-001", "CARGA",       "D", "I"},  // Parque Industrial -> Zona Residencial
            {"MOT-001", "MOTOCICLETA", "J", "B"},  // Estadio -> Av. Norte
        };

        for (String[] datos : vehiculosDemo) {
            String placa = datos[0];
            CategoriaVehiculo cat = CategoriaVehiculo.valueOf(datos[1]);
            Interseccion origen = mapa.buscarInterseccion(datos[2]);
            Interseccion destino = mapa.buscarInterseccion(datos[3]);

            if (origen != null && destino != null) {
                Vehiculo v;
                switch (cat) {
                    case EMERGENCIA:
                        v = FabricaVehiculos.crearUnidadEmergencia(placa, UnidadEmergencia.Tipo.AMBULANCIA, origen, destino);
                        break;
                    case BUS:
                        v = FabricaVehiculos.crearBusUrbano(placa, "Ruta-Urbana", 45, origen, destino);
                        break;
                    default:
                        v = FabricaVehiculos.crear(cat, placa, origen, destino);
                        break;
                }
                flota.registrar(v);
            }
        }
        System.out.println("  Se cargaron " + flota.totalActivos() + " vehiculos de prueba variados.");
    }

    // === SUB-FUNCIONES DE ADMINISTRAR CIUDAD ==================================

    private void agregarInterseccion() {
        System.out.print("\n  ID de la interseccion (ej. M): ");
        String id = entrada.nextLine().trim().toUpperCase();
        System.out.print("  Nombre (ej. Av. Central con Calle 5): ");
        String nombre = entrada.nextLine().trim();
        System.out.print("  Coordenada X (metros): ");
        double x = leerDouble();
        System.out.print("  Coordenada Y (metros): ");
        double y = leerDouble();
        System.out.print("  Tiene semaforo? (s/n): ");
        boolean semaforo = entrada.nextLine().trim().equalsIgnoreCase("s");

        try {
            Interseccion nueva = new Interseccion(id, nombre, x, y);
            nueva.setTieneSemaforo(semaforo);
            mapa.agregarInterseccion(nueva);

            if (semaforo) {
                Semaforo sem = new Semaforo("SEM-" + id, nueva, 10, 10);
                controladorSemaforos.registrar(sem);
            }
            System.out.println("  [OK] Interseccion '" + nombre + "' agregada.");
        } catch (ErrorSistema e) {
            System.out.println("  [X] Error: " + e.getMessage());
        }
    }

    private void agregarCalle() {
        System.out.println("\n  Intersecciones existentes:");
        for (Interseccion i : mapa.todasLasIntersecciones()) {
            System.out.printf("   [%s] %s%n", i.getId(), i.getNombre());
        }

        System.out.print("\n  ID de la interseccion ORIGEN: ");
        String origenId = entrada.nextLine().trim().toUpperCase();
        System.out.print("  ID de la interseccion DESTINO: ");
        String destinoId = entrada.nextLine().trim().toUpperCase();

        Interseccion origen  = mapa.buscarInterseccion(origenId);
        Interseccion destino = mapa.buscarInterseccion(destinoId);

        if (origen == null || destino == null) {
            System.out.println("  [X] Una o ambas intersecciones no existen.");
            return;
        }

        System.out.print("  Distancia en metros: ");
        double dist = leerDouble();
        System.out.print("  Velocidad maxima (km/h): ");
        double vel = leerDouble();
        System.out.println("  Tipo de via:");
        System.out.println("   1. RESIDENCIAL   2. AVENIDA   3. AUTOPISTA   4. EXCLUSIVA_BUS");
        int catOpc = leerEnteroValidado("Tipo", 1, 4);
        String[] cats = {"RESIDENCIAL", "AVENIDA", "AUTOPISTA", "EXCLUSIVA_BUS"};
        String cat = cats[catOpc - 1];

        System.out.print("  Es doble via? (s/n): ");
        boolean doble = entrada.nextLine().trim().equalsIgnoreCase("s");

        try {
            com.trafico.modelo.CategoriaCalle categoria =
                com.trafico.modelo.CategoriaCalle.valueOf(cat);
            mapa.agregarCalle(new com.trafico.modelo.Calle(origen, destino, dist, vel, categoria, doble));
            System.out.println("  [OK] Calle agregada: " + origenId + " -> " + destinoId);
        } catch (IllegalArgumentException | ErrorSistema e) {
            System.out.println("  [X] Error: " + e.getMessage());
        }
    }

    private void cargarDesdeArchivo() {
        System.out.print("  Ruta del archivo (ej. ciudad.txt): ");
        String rutaCarga = entrada.nextLine().trim();
        try {
            mapa = new MapaCiudadGrande();
            flota = new ControladorFlota(mapa);
            cargadorMapa.cargar(rutaCarga, mapa);
            System.out.println("  [OK] Mapa cargado desde " + rutaCarga);
        } catch (ErrorSistema e) {
            System.out.println("  [X] Error: " + e.getMessage());
        }
    }

    private void exportarAArchivo() {
        System.out.print("  Carpeta de destino (ej. .): ");
        String carpeta = entrada.nextLine().trim();
        try {
            generadorInforme.exportarMapa(mapa, carpeta);
        } catch (ErrorSistema e) {
            System.out.println("  [X] Error: " + e.getMessage());
        }
    }

    // === DEMOS RUBRICA UTP ===================================================

    private void demoArbolAVL() {
        System.out.println("\n  --- DEMO: ARBOL AVL (Balanceo automatico) ---");

        System.out.println("  Insertando: C, B, A (provoca rotacion a la derecha)...");
        arbolAVL.insertar("C", "Plaza Central");
        arbolAVL.insertar("B", "Av. Norte con Calle 1");
        arbolAVL.insertar("A", "Av. Central");

        System.out.print("  Recorrido In-Order: ");
        arbolAVL.recorridoInOrder();

        System.out.println("  Insertando: D, E (provoca rotacion a la izquierda)...");
        arbolAVL.insertar("D", "Aeropuerto");
        arbolAVL.insertar("E", "Zona Residencial");

        System.out.print("  Recorrido In-Order: ");
        arbolAVL.recorridoInOrder();

        System.out.print("  Buscando clave 'D': ");
        String resultado = arbolAVL.buscar("D");
        System.out.println(resultado != null ? "Encontrado -> " + resultado : "No encontrado");

        System.out.println("  Eliminando clave 'B'...");
        arbolAVL.eliminar("B");

        System.out.print("  Recorrido In-Order tras eliminar: ");
        arbolAVL.recorridoInOrder();
    }

    private void demoListaEnlazada() {
        System.out.println("\n  --- DEMO: LISTA ENLAZADA SIMPLE (nodos manuales) ---");

        listaEnlazada.insertar("Interseccion A");
        listaEnlazada.insertar("Interseccion B");
        listaEnlazada.insertar("Interseccion C");

        System.out.print("  Contenido actual: ");
        listaEnlazada.recorrer();

        System.out.println("  Buscando 'Interseccion B': " + (listaEnlazada.buscar("Interseccion B") ? "Encontrado" : "No encontrado"));

        System.out.println("  Eliminando 'Interseccion B'...");
        listaEnlazada.eliminar("Interseccion B");

        System.out.print("  Contenido tras eliminar: ");
        listaEnlazada.recorrer();
    }

    private void demoOrdenamientoArreglos() {
        System.out.println("\n  --- DEMO: ORDENAMIENTO EN ARREGLOS ---");
        String[] datosOriginales = {"Carro", "Ambulancia", "Bus", "Motocicleta", "Trailer"};

        System.out.println("  Que metodo quieres usar?");
        System.out.println("   1. Burbuja (Bubble Sort)");
        System.out.println("   2. Insercion (Insertion Sort)");
        System.out.println("   3. Seleccion (Selection Sort)");
        int metodo = leerEnteroValidado("Metodo", 1, 3);

        String[] copia = datosOriginales.clone();
        System.out.print("  Original : ");
        imprimirArreglo(copia);

        if (metodo == 1) {
            OrdenamientoBusqueda.ordenarBurbuja(copia);
            System.out.print("  Burbuja  : ");
        } else if (metodo == 2) {
            OrdenamientoBusqueda.ordenarInsercion(copia);
            System.out.print("  Insercion: ");
        } else {
            OrdenamientoBusqueda.ordenarSeleccion(copia);
            System.out.print("  Seleccion: ");
        }
        imprimirArreglo(copia);
    }

    private void demoBusquedas() {
        System.out.println("\n  --- DEMO: ALGORITMOS DE BUSQUEDA ---");
        String[] marcas = {"Audi", "Chevrolet", "Ford", "Mazda", "Toyota"};
        System.out.print("  Arreglo de busqueda (ya ordenado): ");
        imprimirArreglo(marcas);

        System.out.print("  Ingresa la marca a buscar: ");
        String clave = entrada.nextLine().trim();

        System.out.println("  Que metodo quieres usar?");
        System.out.println("   1. Busqueda Secuencial  (recorre todo el arreglo)");
        System.out.println("   2. Busqueda Binaria     (divide y conquista)");
        int metodo = leerEnteroValidado("Metodo", 1, 2);

        int pos;
        if (metodo == 1) {
            pos = OrdenamientoBusqueda.busquedaSecuencial(marcas, clave);
        } else {
            pos = OrdenamientoBusqueda.busquedaBinaria(marcas, clave);
        }

        if (pos != -1) {
            System.out.println("  [OK] Encontrado en el indice: " + pos);
        } else {
            System.out.println("  [X] Elemento no encontrado.");
        }
    }

    private <T> void imprimirArreglo(T[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // === UTILIDADES DE LECTURA ===============================================

    private int leerEnteroValidado(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.printf("  %s (%d-%d): ", mensaje, min, max);
                int valor = Integer.parseInt(entrada.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.printf("  Ingresa un numero entre %d y %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Entrada invalida -- ingresa un numero entero.");
            }
        }
    }

    private double leerDouble() {
        while (true) {
            try {
                return Double.parseDouble(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Numero invalido, intenta de nuevo: ");
            }
        }
    }
}
