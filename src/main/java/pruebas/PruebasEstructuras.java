package pruebas;

import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.ListaSimple;
import estructuras.Pila;
import modelos.Estado;
import modelos.Prioridad;
import modelos.Tarea;
import servicios.GestorTareas;

// Pruebas de casos límite: listas vacías, un solo elemento,
// overflow y underflow en pilas y colas
public class PruebasEstructuras {

    private static int ok = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        pruebasListaSimple();
        pruebasListaDoble();
        pruebasPila();
        pruebasCola();
        pruebasGestor();

        System.out.println();
        System.out.println("Resultado: " + ok + " aprobadas, " + fallos + " falladas");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    // ---------- Ayudas ----------

    private static Tarea t(int id, Prioridad p) {
        return new Tarea(id, "Tarea " + id, "desc", p);
    }

    private static void verificar(String nombre, boolean cond) {
        if (cond) {
            ok++;
            System.out.println("  [OK]    " + nombre);
        } else {
            fallos++;
            System.out.println("  [FALLO] " + nombre);
        }
    }

    // Espera que la acción lance IllegalStateException
    private static void verificarExcepcion(String nombre, Runnable accion) {
        try {
            accion.run();
            verificar(nombre, false);
        } catch (IllegalStateException e) {
            verificar(nombre, true);
        }
    }

    // ---------- Lista simple ----------

    private static void pruebasListaSimple() {
        System.out.println("ListaSimple");
        ListaSimple lista = new ListaSimple();

        verificar("vacía: estaVacia y tamaño 0", lista.estaVacia() && lista.getTamano() == 0);
        verificar("vacía: buscar devuelve null", lista.buscar(1) == null);
        verificar("vacía: eliminar devuelve false", !lista.eliminar(1));

        lista.insertarFin(t(1, Prioridad.BAJA));
        verificar("un elemento: tamaño 1", lista.getTamano() == 1);
        verificar("un elemento: buscar lo encuentra", lista.buscar(1) != null);
        verificar("un elemento: eliminar lo quita y queda vacía", lista.eliminar(1) && lista.estaVacia());

        // se reutiliza después de vaciarla
        lista.insertarFin(t(2, Prioridad.BAJA));
        lista.insertarFin(t(3, Prioridad.BAJA));
        lista.insertarInicio(t(4, Prioridad.BAJA));
        Tarea[] orden = lista.aArreglo();
        verificar("insertarInicio/insertarFin respetan el orden (4,2,3)",
                orden.length == 3 && orden[0].getId() == 4 && orden[1].getId() == 2 && orden[2].getId() == 3);

        verificar("eliminar en el medio", lista.eliminar(2) && lista.buscar(2) == null && lista.getTamano() == 2);
        verificar("eliminar el último", lista.eliminar(3));
        lista.insertarFin(t(5, Prioridad.BAJA));
        orden = lista.aArreglo();
        verificar("insertarFin tras eliminar el último enlaza bien (4,5)",
                orden.length == 2 && orden[0].getId() == 4 && orden[1].getId() == 5);
        verificar("eliminar la cabeza", lista.eliminar(4) && lista.aArreglo()[0].getId() == 5);
        verificar("eliminar id inexistente devuelve false", !lista.eliminar(99));
    }

    // ---------- Lista doble ----------

    private static void pruebasListaDoble() {
        System.out.println("ListaDoble");
        ListaDoble lista = new ListaDoble();

        verificar("vacía: actual es null", lista.actual() == null);
        verificar("vacía: avanzar/retroceder devuelven null", lista.avanzar() == null && lista.retroceder() == null);

        lista._insertarOrdenado(t(1, Prioridad.MEDIA));
        verificar("un elemento: cursor en el único nodo", lista.actual().getId() == 1);
        verificar("un elemento: no avanza ni retrocede", lista.avanzar() == null && lista.retroceder() == null);

        lista._insertarOrdenado(t(2, Prioridad.ALTA));
        lista._insertarOrdenado(t(3, Prioridad.BAJA));
        lista._insertarOrdenado(t(4, Prioridad.MEDIA));
        lista._insertarOrdenado(t(5, Prioridad.ALTA));
        Tarea[] orden = lista.aArreglo();
        verificar("orden por prioridad estable (2,5,1,4,3)",
                orden.length == 5 && orden[0].getId() == 2 && orden[1].getId() == 5
                        && orden[2].getId() == 1 && orden[3].getId() == 4 && orden[4].getId() == 3);

        lista.irAlInicio();
        int ida = 1;
        while (lista.avanzar() != null) {
            ida++;
        }
        verificar("avanzar recorre los 5 y se detiene en el final", ida == 5 && lista.actual().getId() == 3);
        int vuelta = 1;
        while (lista.retroceder() != null) {
            vuelta++;
        }
        verificar("retroceder regresa al inicio", vuelta == 5 && lista.actual().getId() == 2);

        lista.posicionarEn(1);
        verificar("eliminar el nodo del cursor mueve el cursor al siguiente", lista.eliminar(1) && lista.actual().getId() == 4);
        verificar("eliminar la cabeza", lista.eliminar(2) && lista.aArreglo()[0].getId() == 5);
        verificar("eliminar la cola", lista.eliminar(3) && lista.aArreglo()[lista.getTamano() - 1].getId() == 4);
        verificar("eliminar id inexistente devuelve false", !lista.eliminar(99));
        lista.eliminar(5);
        lista.eliminar(4);
        verificar("al eliminar todo queda vacía y sin cursor", lista.estaVacia() && lista.actual() == null);
    }

    // ---------- Pila ----------

    private static void pruebasPila() {
        System.out.println("Pila");
        Pila<Integer> pila = new Pila<>();

        verificar("vacía: estaVacia", pila.estaVacia());
        verificarExcepcion("pop en pila vacía lanza underflow", () -> new Pila<Integer>().pop());
        verificarExcepcion("tope en pila vacía lanza underflow", () -> new Pila<Integer>().tope());

        pila.push(1);
        pila.push(2);
        pila.push(3);
        verificar("tope devuelve el último sin sacarlo", pila.tope() == 3 && pila.getTamano() == 3);
        verificar("LIFO: pop devuelve 3, 2, 1", pila.pop() == 3 && pila.pop() == 2 && pila.pop() == 1);
        verificar("queda vacía tras vaciarla con pop", pila.estaVacia() && pila.getTamano() == 0);

        Pila<Integer> limitada = new Pila<>(2);
        limitada.push(1);
        limitada.push(2);
        verificar("pila con capacidad 2 queda llena", limitada.estaLlena());
        verificarExcepcion("push en pila llena lanza overflow", () -> {
            Pila<Integer> p = new Pila<>(1);
            p.push(1);
            p.push(2);
        });
    }

    // ---------- Cola ----------

    private static void pruebasCola() {
        System.out.println("Cola");
        Cola<Integer> cola = new Cola<>();

        verificar("vacía: estaVacia", cola.estaVacia());
        verificarExcepcion("dequeue en cola vacía lanza underflow", () -> new Cola<Integer>().dequeue());
        verificarExcepcion("frente en cola vacía lanza underflow", () -> new Cola<Integer>().frente());

        cola.enqueue(1);
        verificar("un elemento: frente es 1", cola.frente() == 1);
        verificar("un elemento: dequeue la deja vacía", cola.dequeue() == 1 && cola.estaVacia());

        // se reutiliza después de vaciarla
        cola.enqueue(10);
        cola.enqueue(20);
        cola.enqueue(30);
        verificar("frente no saca el elemento", cola.frente() == 10 && cola.getTamano() == 3);
        verificar("FIFO: dequeue devuelve 10, 20, 30", cola.dequeue() == 10 && cola.dequeue() == 20 && cola.dequeue() == 30);

        Cola<Integer> limitada = new Cola<>(1);
        limitada.enqueue(1);
        verificar("cola con capacidad 1 queda llena", limitada.estaLlena());
        verificarExcepcion("enqueue en cola llena lanza overflow", () -> {
            Cola<Integer> c = new Cola<>(1);
            c.enqueue(1);
            c.enqueue(2);
        });
    }

    // ---------- Gestor (todo junto) ----------

    private static void pruebasGestor() {
        System.out.println("GestorTareas (integración)");
        GestorTareas g = new GestorTareas();

        verificar("deshacer sin cambios no falla", g.deshacer().startsWith("No hay"));
        verificar("rehacer sin cambios no falla", g.rehacer().startsWith("No hay"));

        Tarea a = g.crearTarea("Tarea A", "d", Prioridad.BAJA);
        Tarea b = g.crearTarea("Tarea B", "d", Prioridad.ALTA);
        g.irAlInicioNavegacion();
        verificar("la vista por prioridad pone ALTA primero", g.tareaActualNavegacion().getId() == b.getId());

        g.modificarTarea(a.getId(), "Tarea A2", null, Prioridad.ALTA, null);
        verificar("modificar cambia título y prioridad", a.getTitulo().equals("Tarea A2") && a.getPrioridad() == Prioridad.ALTA);
        verificar("modificar registra 1 acción en Undo", g.cambiosParaDeshacer() == 1);
        verificar("modificar sin cambios reales no registra acción",
                !g.modificarTarea(a.getId(), "Tarea A2", null, null, null) && g.cambiosParaDeshacer() == 1);

        g.deshacer();
        verificar("deshacer restaura título y prioridad", a.getTitulo().equals("Tarea A") && a.getPrioridad() == Prioridad.BAJA);
        verificar("deshacer mueve la acción a Redo", g.cambiosParaDeshacer() == 0 && g.cambiosParaRehacer() == 1);
        g.rehacer();
        verificar("rehacer reaplica el cambio", a.getTitulo().equals("Tarea A2") && a.getPrioridad() == Prioridad.ALTA);

        g.deshacer();
        g.modificarTarea(b.getId(), "B nueva", null, null, null);
        verificar("un cambio nuevo limpia la pila de Redo", g.cambiosParaRehacer() == 0);

        // cola de ejecución
        g.enviarACola(a.getId());
        g.enviarACola(b.getId());
        verificar("el frente de la cola es el primero que entró", g.verFrenteDeCola().getId() == a.getId());

        boolean duplicado = false;
        try {
            g.enviarACola(a.getId());
        } catch (IllegalStateException e) {
            duplicado = true;
        }
        verificar("no permite encolar dos veces la misma tarea", duplicado);

        boolean eliminarEnCola = false;
        try {
            g.eliminarTarea(a.getId());
        } catch (IllegalStateException e) {
            eliminarEnCola = true;
        }
        verificar("no permite eliminar una tarea que está en la cola", eliminarEnCola);

        Tarea primera = g.ejecutarSiguiente();
        Tarea segunda = g.ejecutarSiguiente();
        verificar("se ejecutan en orden FIFO", primera == a && segunda == b);
        verificar("las ejecutadas quedan COMPLETADA", a.getEstado() == Estado.COMPLETADA && b.getEstado() == Estado.COMPLETADA);
        verificar("ejecutar con cola vacía devuelve null", g.ejecutarSiguiente() == null);

        g.deshacer();
        verificar("deshacer revierte la última ejecución", b.getEstado() == Estado.PENDIENTE);

        boolean completada = false;
        try {
            g.enviarACola(a.getId());
        } catch (IllegalStateException e) {
            completada = true;
        }
        verificar("no permite encolar una tarea ya completada", completada);

        verificar("eliminar tarea la quita del catálogo", g.eliminarTarea(b.getId()) && g.buscarTarea(b.getId()) == null);
        // primero se deshace la ejecución de A, luego sigue la acción de B (ya eliminada)
        g.deshacer();
        verificar("deshacer sobre una tarea eliminada no falla", g.deshacer().startsWith("No se pudo"));
    }
}
