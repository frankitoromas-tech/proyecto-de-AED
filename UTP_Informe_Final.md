# 📘 Informe de Trabajo Final — UTP
## Curso: Algoritmos y Estructuras de Datos

---

## 1. Título del Proyecto
**Sistema de Gestión y Simulación de Tráfico Vehicular Urbano utilizando Grafos, Árboles AVL y Estructuras Dinámicas de Datos.**

---

## 2. Introducción
El constante crecimiento demográfico y la rápida urbanización en el Perú han incrementado los índices de congestión vehicular, especialmente en ciudades metropolitanas como Lima. Este proyecto propone un **Sistema de Gestión de Tráfico Vehicular** desarrollado en Java puro, fundamentado en conceptos de Algoritmos y Estructuras de Datos. Mediante el uso de grafos dirigidos, árboles auto-balanceados AVL, y diversos algoritmos de navegación, el sistema simula el tránsito citadino permitiendo una toma de decisiones optimizada en la red vial.

---

## 3. Planteamiento del Problema
### Situación Actual
Las intersecciones viales en zonas de alta densidad presentan saturación constante debido a planes semafóricos estáticos que no se adaptan al flujo real. 
### Consecuencias
- Incremento del tiempo de traslado de los ciudadanos.
- Falta de prioridad para vehículos de emergencia, arriesgando vidas humanas.
- Pérdida de información histórica del flujo vial.
### Solución
Se plantea una solución informática autónoma en Java que modele la red vial como un grafo dinámico, controle semáforos inteligentes de forma dinámica e incorpore priorización algorítmica.

---

## 4. Objetivos
### Objetivo General
Desarrollar un software de simulación y gestión de tráfico vial urbano utilizando estructuras dinámicas de datos y algoritmos de optimización de rutas.
### Objetivos Específicos
- Diseñar e implementar representaciones de Grafos (Lista y Matriz de Adyacencia).
- Desarrollar un Árbol AVL desde cero para indexación de intersecciones.
- Aplicar algoritmos de ordenamiento (Burbuja, Inserción, Selección) y búsqueda (Secuencial, Binaria) sobre datos de simulación.
- Implementar pilas LIFO para inversión de rutas y colas FIFO con Min-Heap para prioridad de vehículos.

---

## 5. Alcance del Sistema
### El sistema permite:
- Diseñar redes viales (nodos/aristas).
- Registrar vehículos con placas, destinos y prioridades.
- Simular el avance del tráfico mediante ticks de reloj.
- Consultar semáforos con lógica de extensión dinámica.
### El sistema NO incluye:
- Acceso web ni entorno de red externa.
- Conexión a bases de datos relacionales externas (todo reside en archivos planos `.txt`).
- Aplicación móvil.

---

## 6. Análisis del Problema
### 6.1. Actores
- **Operador del Sistema:** Configura el mapa, registra vehículos e inicia simulaciones.
- **Unidad de Emergencia:** Vehículo prioritario que modifica los ciclos de semáforos a su paso.
- **Vehículo Común:** Sigue su ruta óptima respetando las restricciones viales.

### 6.2. Requerimientos Funcionales
- **RF-01:** Cargar/Guardar mapa vial desde archivos `.txt`.
- **RF-02:** Calcular rutas óptimas usando Dijkstra, A*, BFS o Bellman-Ford.
- **RF-03:** Simular ticks de tiempo actualizando la congestión de las vías.
- **RF-04:** Forzar luz verde en semáforos ante aproximación de emergencias.

### 6.3. Requerimientos No Funcionales
- **RNF-01:** Ejecución fluida en consola sin dependencias externas (Java Standard Edition).
- **RNF-02:** Búsquedas eficientes de nodos mediante árbol AVL ($O(\log n)$).
- **RNF-03:** Persistencia local simple mediante archivos planos delimitados por caracteres.

---

## 7. Diseño de la Solución
### 7.1. Diagrama de Relación de Módulos
```
Usuario -> PanelControl -> MotorSimulacion -> MapaVial (Grafo) -> ArbolAVL
                                          -> ControladorFlota -> Vehiculos
```

---

## 8. Estructuras de Datos Implementadas
### 8.1. Arreglos
Utilizados para almacenar temporalmente los vehículos de la simulación y estructurar los heaps.
### 8.2. Listas Enlazadas
Implementación real de nodos en `ListaEnlazadaSimple` y `HistorialViaje` (Lista doblemente enlazada).
### 8.3. Pilas
`PilaRecorrido` (LIFO) para invertir la ruta calculada desde el nodo de destino hacia el origen.
### 8.4. Colas
`FilaDespacho` basada en Min-Heap para despachar de forma priorizada a los vehículos.
### 8.5. Árboles AVL
`ArbolAVL` auto-balanceado para indexar las intersecciones de la ciudad y realizar búsquedas de nodos en tiempo $O(\log n)$.

---

## 9. Algoritmos Aplicados
- **Búsqueda Secuencial y Binaria:** Implementadas sobre arreglos de elementos para encontrar registros de vehículos rápidamente.
- **Burbuja, Inserción y Selección:** Algoritmos clásicos de ordenamiento de arreglos aplicados en la sección de demostración UTP.

---

## 10. Implementación
Estructura de clases limpia, organizada en paquetes (`com.trafico.modelo`, `com.trafico.estructuras`, `com.trafico.mapa`, etc.) con validación de datos integrada.

---

## 11. Pruebas del Sistema
- **Caso 1: Insertar intersección en AVL** -> Nodo agregado y árbol balanceado mediante rotaciones.
- **Caso 2: Búsqueda de ruta** -> Ruta calculada exitosamente usando Dijkstra.
- **Caso 3: Simulación** -> Tráfico avanzando dinámicamente y ambulancia forzando luz verde.

---

## 12. Resultados
El uso del árbol AVL redujo los tiempos de búsqueda de nodos de $O(n)$ (lista simple) a $O(\log n)$, lo cual optimizó la simulación al procesar múltiples vehículos simultáneamente.

---

## 13. Conclusiones
- Las estructuras dinámicas optimizan la gestión de memoria durante ejecuciones continuas.
- El balanceo del árbol AVL evita la degeneración del árbol a listas enlazadas en búsquedas.
- Los grafos representan de manera fidedigna la topología de una red vial urbana.

---

## 14. Recomendaciones
- Implementar una interfaz gráfica básica utilizando Java Swing en futuras versiones.
- Permitir la simulación multithread de los semáforos para mayor realismo.

---

## 15. Bibliografía
- Cairó, O. (2006). *Estructuras de datos*. Grupo Editorial Patria.
- Sedgewick, R., & Wayne, K. (2011). *Algorithms*. Addison-Wesley Professional.
