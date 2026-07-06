# 🚦 Sistema de Gestión de Tráfico Vehicular (AED UTP)

Trabajo Final del curso **Algoritmos y Estructuras de Datos**.
**Universidad Tecnológica del Perú (UTP)**

---

## 📝 1. Título del Proyecto
**Sistema de Gestión y Simulación de Tráfico Vehicular Urbano utilizando Grafos, Árboles AVL y Estructuras Dinámicas de Datos.**

---

## 2. Introducción
El constante crecimiento de las zonas urbanas ha provocado un incremento significativo en la congestión vehicular en intersecciones críticas de las grandes urbes. Este sistema simula de manera dinámica el comportamiento del tráfico utilizando **estructuras de datos complejas implementadas desde cero en Java**. A través de grafos dirigidos para modelar calles e intersecciones, y árboles balanceados AVL para optimizar la indexación de los elementos urbanos, proponemos una simulación del flujo de tráfico controlada por ticks temporales de simulación, evaluando en tiempo real la efectividad de diversos algoritmos de navegación.

---

## 3. Planteamiento del Problema
### Situación Actual
Muchas ciudades manejan planes semafóricos rígidos o carecen de sistemas analíticos que predigan las zonas de congestión vehicular o bloqueos totales antes de su ocurrencia. Los despachos de vehículos de emergencia (ambulancias, bomberos) suelen carecer de prioridad computacional en los semáforos activos.
### Consecuencias
- Pérdida de vidas humanas por retrasos en vehículos de emergencia.
- Mayor consumo de combustible y emisiones de gases de efecto invernadero.
- Pérdida de tiempo productivo para la ciudadanía.
### Necesidad de Solución
Un simulador por software basado en la teoría de algoritmos y grafos dinámicos que permita modelar semáforos con lógica Observer, priorización mediante montículos (Min-Heaps), y caché de rutas mediante tablas hash.

---

## 4. Objetivos
### Objetivo General
Desarrollar un simulador del sistema de gestión de tráfico vehicular urbano en Java puro aplicando estructuras de datos dinámicas y algoritmos eficientes de navegación.
### Objetivos Específicos
- **Grafos:** Modelar la red vial como un grafo ponderado y dirigido con representaciones alternables mediante el patrón Strategy.
- **Árboles AVL:** Implementar un árbol AVL balanceado desde cero para la indexación y búsqueda rápida de intersecciones por clave.
- **Colas de Prioridad:** Diseñar un Min-Heap para colas de prioridad aplicadas tanto a Dijkstra/A* como al despacho de unidades de emergencia.
- **Algoritmos de Ordenamiento/Búsqueda:** Integrar métodos clásicos de ordenamiento (Burbuja, Inserción, Selección) y búsqueda (Secuencial, Binaria) sobre los datos recolectados de la simulación.

---

## 5. Estructuras de Datos Implementadas (Desde Cero)
- **Árbol AVL (`ArbolAVL.java`):** Garantiza búsquedas, inserciones y eliminaciones de intersecciones en tiempo $O(\log n)$.
- **Lista Enlazada Simple (`ListaEnlazadaSimple.java`):** Estructura lineal dinámica para registro general con nodos.
- **Montón Mínimo (`MontonMinimo.java`):** Min-Heap genérico usado como cola de prioridad eficiente.
- **Tabla Hash (`CacheRutas.java`):** Solución a colisiones por encadenamiento, usada para cachear rutas y optimizar el tiempo de cálculo.
- **Pila (`PilaRecorrido.java`):** Estructura LIFO utilizada para la inversión de rutas.
- **Lista Doblemente Enlazada (`HistorialViaje.java`):** Almacena el recorrido histórico de cada vehículo en la simulación.

---

## 6. Algoritmos de Navegación Aplicados
- **Dijkstra (`RutaMasCorta`):** Menor tiempo estimado de viaje acumulado.
- **A\* (`RutaOptimizada`):** Dijkstra optimizado con heurística de distancia euclidiana en línea recta.
- **Bellman-Ford (`RutaConGestion`):** Robusto contra atascos, detecta ciclos negativos de tráfico.
- **BFS (`RutaMenosSemaforos`):** Ruta con menor número de intersecciones cruzadas.

---

## 🛠️ Instrucciones de Compilación y Ejecución

### Requisitos
- **JDK 11** o superior instalado en el sistema.

### Compilación (Consola)
Desde el directorio raíz del proyecto (`Proyecto de AED`), ejecuta:
```bash
javac -d bin src/com/trafico/**/*.java src/com/trafico/*.java
```

### Ejecución
Una vez compilado, inicia el Panel de Control interactivo ejecutando:
```bash
java -cp bin com.trafico.SistemaTrafico
```
