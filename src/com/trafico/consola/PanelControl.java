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
 * Panel de Control — interfaz de usuario interactiva por consola.
 *
 * <p>Permite al operador del sistema configurar la red vial, registrar vehiculos,
 * calcular rutas, ver el estado de los semaforos, iniciar la simulacion y
 * exportar informes — todo desde un menu navegable por teclado.</p>
 *
 * <p><b>Rubrica UTP:</b> Integra demostraciones especificas de Arreglos, Listas Enlazadas,
 * Pilas, Colas, Arbol AVL y Algoritmos de Busqueda y Ordenamiento.</p>
 *
 * @author Sistema de Gestion de Trafico
 * @version 1.1
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

    // ─── Bucle principal ──────────────────────────────────────────────────────

    public void iniciar() {
        mostrarBienvenida();
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEnteroValidado("Seleccione una opcion", 1, 9);

            switch (opcion) {
                case 1: menuDisenarRedVial();       break;
                case 2: menuRegistrarVehiculo();    break;
                case 3: menuCalcularRuta();         break;
                case 4: menuEstadoSemaforos();      break;
                case 5: menuIniciarSimulacion();    break;
                case 6: menuVerEstadisticas();      break;
                case 7: menuGuardarCargarMapa();    break;
                case 8: menuRubricaUTP();           break;
                case 9: ejecutando = false;          break;
            }
        }

        System.out.println("\n  Hasta luego. Sistema cerrado.\n");
        entrada.close();
    }

    // ─── Menus ────────────────────────────────────────────────────────────────

    private void mostrarBienvenida() {
        System.out.println("\n");
        System.out.println("  +---------------------------------------------------+");
        System.out.println("  |                                                   |");
        System.out.println("  |     SISTEMA DE GESTION DE TRAFICO VIAL            |");
        System.out.println("  |         Algoritmos y Estructuras de Datos         |");
        System.out.println("  |                                                   |");
        System.out.println("  +---------------------------------------------------+");
        System.out.println();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n  +-----------------------------------------------+");
        System.out.println("  |              MENU PRINCIPAL                   |");
        System.out.println("  +-----------------------------------------------+");
        System.out.printf("  |  Red vial: %3d intersecciones, %3d calles      |%n",
            mapa.totalIntersecciones(), mapa.totalCalles());
        System.out.printf("  |  Vehiculos: %3d activos | Semaforos: %3d       |%n",
            flota.totalActivos(), controladorSemaforos.totalSemaforos());
        System.out.println("  +-----------------------------------------------+");
        System.out.println("  |  1. Disenar la red vial de la ciudad          |");
        System.out.println("  |  2. Registrar un vehiculo nuevo               |");
        System.out.println("  |  3. Calcular ruta entre dos puntos            |");
        System.out.println("  |  4. Consultar estado de semaforos             |");
        System.out.println("  |  5. Iniciar simulacion de trafico             |");
        System.out.println("  |  6. Ver estadisticas de la simulacion         |");
        System.out.println("  |  7. Guardar / Cargar mapa de ciudad           |");
        System.out.println("  |  8. Demostracion de Estructuras (Rubrica UTP) |");
        System.out.println("  |  9. Salir del sistema                         |");
        System.out.println("  +-----------------------------------------------+");
    }

    // ─── Opcion 1: Disenar la red vial ────────────────────────────────────────

    private void menuDisenarRedVial() {
        System.out.println("\n  === DISENAR RED VIAL ===");
        System.out.println("  1. Agregar interseccion manualmente");
        System.out.println("  2. Agregar calle entre dos intersecciones");
        System.out.println("  3. Ver el mapa actual");
        System.out.println("  4. Cargar ciudad de ejemplo (demo)");
        System.out.println("  0. Volver");

        int opcion = leerEnteroValidado("Opcion", 0, 4);
        switch (opcion) {
            case 1: agregarInterseccion(); break;
            case 2: agregarCalle();        break;
            case 3: mapa.mostrarMapa();    break;
            case 4: cargarCiudadDemo();    break;
        }
    }

    private void agregarInterseccion() {
        System.out.print("  ID de la interseccion: ");
        String id = entrada.nextLine().trim();
        System.out.print("  Nombre (ej. Av. Central con Calle 5): ");
        String nombre = entrada.nextLine().trim();
        System.out.print("  Coordenada X (metros): ");
        double x = leerDouble();
        System.out.print("  Coordenada Y (metros): ");
        double y = leerDouble();
        System.out.print("  ¿Tiene semaforo? (s/n): ");
        boolean semaforo = entrada.nextLine().trim().equalsIgnoreCase("s");

        try {
            Interseccion nueva = new Interseccion(id, nombre, x, y);
            nueva.setTieneSemaforo(semaforo);
            mapa.agregarInterseccion(nueva);

            if (semaforo) {
                Semaforo sem = new Semaforo("SEM-" + id, nueva, 10, 10);
                controladorSemaforos.registrar(sem);
            }
            System.out.println("  * Interseccion '" + nombre + "' agregada.");
        } catch (ErrorSistema e) {
            System.out.println("  x Error: " + e.getMessage());
        }
    }

    private void agregarCalle() {
        System.out.print("  ID interseccion de origen: ");
        String origenId = entrada.nextLine().trim();
        System.out.print("  ID interseccion de destino: ");
        String destinoId = entrada.nextLine().trim();

        Interseccion origen  = mapa.buscarInterseccion(origenId);
        Interseccion destino = mapa.buscarInterseccion(destinoId);

        if (origen == null || destino == null) {
            System.out.println("  x Una o ambas intersecciones no existen.");
            return;
        }

        System.out.print("  Distancia en metros: ");
        double dist = leerDouble();
        System.out.print("  Velocidad maxima (km/h): ");
        double vel = leerDouble();
        System.out.println("  Categoria (RESIDENCIAL/AVENIDA/AUTOPISTA/EXCLUSIVA_BUS): ");
        String cat = entrada.nextLine().trim().toUpperCase();
        System.out.print("  ¿Es doble via? (s/n): ");
        boolean doble = entrada.nextLine().trim().equalsIgnoreCase("s");

        try {
            com.trafico.modelo.CategoriaCalle categoria =
                com.trafico.modelo.CategoriaCalle.valueOf(cat);
            mapa.agregarCalle(new com.trafico.modelo.Calle(origen, destino, dist, vel, categoria, doble));
            System.out.println("  * Calle agregada: " + origenId + " -> " + destinoId);
        } catch (IllegalArgumentException | ErrorSistema e) {
            System.out.println("  x Error: " + e.getMessage());
        }
    }

    private void cargarCiudadDemo() {
        System.out.println("\n  Cargando ciudad de demo (12 intersecciones)...");
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

            System.out.printf("  * Ciudad demo cargada: %d intersecciones, %d calles, %d semaforos.%n",
                mapa.totalIntersecciones(), mapa.totalCalles(), controladorSemaforos.totalSemaforos());
            System.out.println("  ¿Conectada? -> " + (mapa.esMapaConexo() ? "Si" : "No"));

        } catch (ErrorSistema e) {
            System.out.println("  x Error al cargar demo: " + e.getMessage());
        }
    }

    // ─── Opcion 2: Registrar vehiculo ─────────────────────────────────────────

    private void menuRegistrarVehiculo() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  x Debes tener al menos 2 intersecciones para agregar vehiculos.");
            return;
        }

        System.out.println("\n  === REGISTRAR VEHICULO ===");
        System.out.println("  Tipos de vehiculo:");
        System.out.println("  1. Auto Particular    2. Unidad de Emergencia");
        System.out.println("  3. Bus Urbano         4. Camion de Carga");
        System.out.println("  5. Motocicleta");

        int tipo = leerEnteroValidado("Tipo de vehiculo", 1, 5);
        System.out.print("  Numero de placa: ");
        String placa = entrada.nextLine().trim().toUpperCase();

        mapa.mostrarMapa();
        System.out.print("  ID de la interseccion de ORIGEN: ");
        String origenId = entrada.nextLine().trim();
        System.out.print("  ID de la interseccion de DESTINO: ");
        String destinoId = entrada.nextLine().trim();

        Interseccion origen  = mapa.buscarInterseccion(origenId);
        Interseccion destino = mapa.buscarInterseccion(destinoId);

        if (origen == null || destino == null) {
            System.out.println("  x Interseccion no encontrada.");
            return;
        }

        Vehiculo vehiculo;
        switch (tipo) {
            case 1: vehiculo = FabricaVehiculos.crearAutoParticular(placa, "Modelo-Eco", origen, destino); break;
            case 2: vehiculo = FabricaVehiculos.crearUnidadEmergencia(placa, UnidadEmergencia.Tipo.AMBULANCIA, origen, destino); break;
            case 3: vehiculo = FabricaVehiculos.crearBusUrbano(placa, "Ruta-Demo", 45, origen, destino); break;
            case 4: vehiculo = FabricaVehiculos.crear(CategoriaVehiculo.CARGA, placa, origen, destino); break;
            case 5: vehiculo = FabricaVehiculos.crear(CategoriaVehiculo.MOTOCICLETA, placa, origen, destino); break;
            default: return;
        }

        flota.registrar(vehiculo);
    }

    // ─── Opcion 3: Calcular ruta ──────────────────────────────────────────────

    private void menuCalcularRuta() {
        System.out.println("\n  === CALCULAR RUTA OPTIMA ===");
        System.out.println("  Algoritmos disponibles:");
        System.out.println("  1. Dijkstra (Ruta mas corta)");
        System.out.println("  2. A* (Ruta optimizada con heuristica)");
        System.out.println("  3. Bellman-Ford (Ruta con gestion de atascos)");
        System.out.println("  4. BFS (Ruta con menos semaforos)");
        System.out.println("  5. Comparar todos los algoritmos");

        int algoritmo = leerEnteroValidado("Algoritmo", 1, 5);
        System.out.print("  ID interseccion de origen: ");
        String origenId = entrada.nextLine().trim();
        System.out.print("  ID interseccion de destino: ");
        String destinoId = entrada.nextLine().trim();

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
                System.out.printf("  %2d. [%s] %s%n", i + 1, ruta.get(i).getId(), ruta.get(i).getNombre());
            }
        } catch (ErrorRutaImposible e) {
            System.out.println("  x " + e.getMessage());
        }
    }

    // ─── Opcion 4: Estado semaforos ────────────────────────────────────────────

    private void menuEstadoSemaforos() {
        controladorSemaforos.mostrarEstado();
    }

    // ─── Opcion 5: Iniciar simulacion ─────────────────────────────────────────

    private void menuIniciarSimulacion() {
        if (mapa.totalIntersecciones() < 2) {
            System.out.println("  x Carga una ciudad primero (opcion 1 -> demo).");
            return;
        }

        System.out.println("\n  === INICIAR SIMULACION ===");
        System.out.println("  1. Iniciar Simulador Visual (Interfaz Grafica Swing)");
        System.out.println("  2. Iniciar Simulacion en Consola (Tradicional)");
        int modo = leerEnteroValidado("Seleccione modo", 1, 2);

        System.out.print("  ¿Agregar vehiculos aleatorios de prueba? (s/n): ");
        if (entrada.nextLine().trim().equalsIgnoreCase("s")) {
            agregarVehiculosAleatorios(5);
        }

        if (modo == 1) {
            System.out.println("  * Abriendo ventana del simulador grafico...");
            SwingUtilities.invokeLater(() -> {
                VentanaSimulador gui = new VentanaSimulador(mapa, flota, controladorSemaforos);
                gui.setVisible(true);
            });
        } else {
            System.out.print("  Duracion en ticks (1 tick = 1 segundo, recomendado 100): ");
            int ticks = leerEnteroValidado("Ticks", 1, 10000);

            MotorSimulacion motor = MotorSimulacion.getInstancia();
            motor.configurar(mapa, flota, controladorSemaforos, ticks);
            motor.ejecutar();

            InformeTrafico informe = motor.getInforme();
            informe.mostrarEnConsola(motor.getTickActual(), flota.totalVehiculos(), flota.totalLlegados());
        }
    }

    // ─── Opcion 6: Estadisticas ───────────────────────────────────────────────

    private void menuVerEstadisticas() {
        System.out.println("\n  === ESTADISTICAS DEL SISTEMA ===");
        System.out.printf("  Intersecciones : %d%n", mapa.totalIntersecciones());
        System.out.printf("  Calles         : %d%n", mapa.totalCalles());
        System.out.printf("  Semaforos      : %d%n", controladorSemaforos.totalSemaforos());
        System.out.printf("  Vehiculos activos: %d%n", flota.totalActivos());
        System.out.printf("  Vehiculos llegados: %d%n", flota.totalLlegados());
        System.out.printf("  Cache de rutas : %s%n", flota.getEstadisticasCache());
        System.out.printf("  Mapa conexo    : %s%n", mapa.esMapaConexo() ? "Si" : "No");
    }

    // ─── Opcion 7: Guardar/Cargar mapa ────────────────────────────────────────

    private void menuGuardarCargarMapa() {
        System.out.println("\n  === GUARDAR / CARGAR MAPA ===");
        System.out.println("  1. Cargar mapa desde archivo .txt");
        System.out.println("  2. Exportar mapa actual a archivo .txt");
        System.out.println("  0. Volver");

        int opcion = leerEnteroValidado("Opcion", 0, 2);
        switch (opcion) {
            case 1:
                System.out.print("  Ruta del archivo (ej. ciudad.txt): ");
                String rutaCarga = entrada.nextLine().trim();
                try {
                    mapa = new MapaCiudadGrande();
                    flota = new ControladorFlota(mapa);
                    cargadorMapa.cargar(rutaCarga, mapa);
                } catch (ErrorSistema e) {
                    System.out.println("  x Error: " + e.getMessage());
                }
                break;
            case 2:
                System.out.print("  Carpeta destino (ej. .): ");
                String carpeta = entrada.nextLine().trim();
                try {
                    generadorInforme.exportarMapa(mapa, carpeta);
                } catch (ErrorSistema e) {
                    System.out.println("  x Error: " + e.getMessage());
                }
                break;
        }
    }

    // ─── Opcion 8: Rubrica UTP (AVL, Arreglos, Ordenamiento y Busqueda) ───────

    private void menuRubricaUTP() {
        System.out.println("\n  === DEMOSTRACION DE ESTRUCTURAS EXIGIDAS (RUBRICA UTP) ===");
        System.out.println("  1. Arbol AVL (Insercion, Busqueda, Eliminacion y Recorridos)");
        System.out.println("  2. Lista Enlazada Simple (Nodos, Insertar, Eliminar, Recorrer)");
        System.out.println("  3. Algoritmos de Ordenamiento (Burbuja, Insercion, Seleccion sobre Arreglos)");
        System.out.println("  4. Algoritmos de Busqueda (Secuencial y Binaria)");
        System.out.println("  0. Volver");

        int opcion = leerEnteroValidado("Opcion UTP", 0, 4);
        switch (opcion) {
            case 1: demoArbolAVL(); break;
            case 2: demoListaEnlazada(); break;
            case 3: demoOrdenamientoArreglos(); break;
            case 4: demoBusquedas(); break;
        }
    }

    private void demoArbolAVL() {
        System.out.println("\n  --- DEMOSTRACION: ARBOL AVL ---");
        System.out.println("  (Balanceo automatico de nodos)");

        // Insertar claves
        System.out.println("  [AVL] Insertando: C, B, A (Causa rotacion a la derecha)...");
        arbolAVL.insertar("C", "Plaza Central");
        arbolAVL.insertar("B", "Av. Norte con Calle 1");
        arbolAVL.insertar("A", "Av. Central");

        System.out.print("  [AVL] Recorrido In-Order actual: ");
        arbolAVL.recorridoInOrder();

        System.out.println("  [AVL] Insertando: D, E (Causa rotacion a la izquierda)...");
        arbolAVL.insertar("D", "Aeropuerto");
        arbolAVL.insertar("E", "Zona Residencial");

        System.out.print("  [AVL] Recorrido In-Order final: ");
        arbolAVL.recorridoInOrder();

        // Buscar
        System.out.print("  [AVL] Buscando clave 'D': ");
        String resultado = arbolAVL.buscar("D");
        System.out.println(resultado != null ? "Encontrado -> " + resultado : "No encontrado");

        // Eliminar
        System.out.println("  [AVL] Eliminando clave 'B'...");
        arbolAVL.eliminar("B");

        System.out.print("  [AVL] Recorrido In-Order tras eliminar: ");
        arbolAVL.recorridoInOrder();
    }

    private void demoListaEnlazada() {
        System.out.println("\n  --- DEMOSTRACION: LISTA ENLAZADA SIMPLE ---");
        System.out.println("  (Implementacion real de nodos desde cero)");

        listaEnlazada.insertar("Interseccion A");
        listaEnlazada.insertar("Interseccion B");
        listaEnlazada.insertar("Interseccion C");

        System.out.print("  [Lista] Contenido actual: ");
        listaEnlazada.recorrer();

        System.out.println("  [Lista] Buscando 'Interseccion B': " + (listaEnlazada.buscar("Interseccion B") ? "Encontrado" : "No encontrado"));

        System.out.println("  [Lista] Eliminando 'Interseccion B'...");
        listaEnlazada.eliminar("Interseccion B");

        System.out.print("  [Lista] Contenido tras eliminar: ");
        listaEnlazada.recorrer();
    }

    private void demoOrdenamientoArreglos() {
        System.out.println("\n  --- DEMOSTRACION: ORDENAMIENTO EN ARREGLOS ---");
        String[] datosOriginales = {"Carro", "Ambulancia", "Bus", "Motocicleta", "Trailer"};

        System.out.println("  1. Ordenamiento Burbuja (Bubble Sort)");
        System.out.println("  2. Ordenamiento Insercion (Insertion Sort)");
        System.out.println("  3. Ordenamiento Seleccion (Selection Sort)");
        int metodo = leerEnteroValidado("Seleccione metodo", 1, 3);

        String[] copia = datosOriginales.clone();
        System.out.print("  Original: ");
        imprimirArreglo(copia);

        if (metodo == 1) {
            OrdenamientoBusqueda.ordenarBurbuja(copia);
            System.out.print("  Burbuja : ");
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
        System.out.println("\n  --- DEMOSTRACION: ALGORITMOS DE BUSQUEDA ---");
        String[] marcas = {"Audi", "Chevrolet", "Ford", "Mazda", "Toyota"}; // ordenado para binaria
        System.out.print("  Arreglo de busqueda: ");
        imprimirArreglo(marcas);

        System.out.print("  Ingrese marca a buscar: ");
        String clave = entrada.nextLine().trim();

        System.out.println("  1. Busqueda Secuencial (O(n))");
        System.out.println("  2. Busqueda Binaria (O(log n) - requiere ordenamiento)");
        int metodo = leerEnteroValidado("Seleccione metodo", 1, 2);

        int pos;
        if (metodo == 1) {
            pos = OrdenamientoBusqueda.busquedaSecuencial(marcas, clave);
        } else {
            pos = OrdenamientoBusqueda.busquedaBinaria(marcas, clave);
        }

        if (pos != -1) {
            System.out.println("  * Elemento encontrado en el indice: " + pos);
        } else {
            System.out.println("  x Elemento no encontrado.");
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

    // ─── Utilidades de lectura ────────────────────────────────────────────────

    private void agregarVehiculosAleatorios(int cantidad) {
        List<Interseccion> lista = mapa.todasLasIntersecciones();
        if (lista.size() < 2) return;

        String[] placas = {"ABC-123","DEF-456","GHI-789","JKL-012","MNO-345","PQR-678"};
        for (int i = 0; i < Math.min(cantidad, placas.length); i++) {
            Interseccion o = lista.get(i % lista.size());
            Interseccion d = lista.get((i + lista.size() / 2) % lista.size());
            if (!o.equals(d)) {
                flota.registrar(FabricaVehiculos.crear(CategoriaVehiculo.PARTICULAR, placas[i], o, d));
            }
        }
        System.out.println("  * " + cantidad + " vehiculos de prueba agregados.");
    }

    private int leerEnteroValidado(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.printf("  %s (%d-%d): ", mensaje, min, max);
                int valor = Integer.parseInt(entrada.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.printf("  Ingresa un numero entre %d y %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Entrada invalida — ingresa un numero entero.");
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
