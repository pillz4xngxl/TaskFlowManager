package servicios;

import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.ListaSimple;
import estructuras.Pila;
import modelos.AccionCambio;
import modelos.Estado;
import modelos.Prioridad;
import modelos.Tarea;

// Junta todas las estructuras:
// ListaSimple = catálogo, ListaDoble = vista por prioridad,
// Pilas = deshacer y rehacer, Cola = ejecución
public class GestorTareas {

    private final ListaSimple catalogo;
    private final ListaDoble vistaPrio;
    private final Pila<AccionCambio> pilaUndo;
    private final Pila<AccionCambio> pilaRedo;
    private final Cola<Tarea> colaEjec;
    private int sigId;

    public GestorTareas() {
        this.catalogo = new ListaSimple();
        this.vistaPrio = new ListaDoble();
        this.pilaUndo = new Pila<>();
        this.pilaRedo = new Pila<>();
        this.colaEjec = new Cola<>();
        this.sigId = 1;
    }

    // ---------- Catálogo y vista por prioridad ----------

    public Tarea crearTarea(String titulo, String desc, Prioridad prio) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (prio == null) {
            throw new IllegalArgumentException("Debe indicarse una prioridad");
        }
        if (desc == null) {
            desc = "";
        }
        Tarea tarea = new Tarea(sigId, titulo.trim(), desc.trim(), prio);
        sigId++;
        catalogo.insertarFin(tarea);
        vistaPrio._insertarOrdenado(tarea);
        return tarea;
    }

    public Tarea buscarTarea(int id) {
        return catalogo.buscar(id);
    }

    // No deja eliminar si la tarea está en la cola
    // Devuelve false si no existe
    public boolean eliminarTarea(int id) {
        Tarea tarea = catalogo.buscar(id);
        if (tarea == null) {
            return false;
        }
        if (colaEjec.contiene(tarea)) {
            throw new IllegalStateException("La tarea #" + id + " está en la cola, procésala antes de eliminarla");
        }
        catalogo.eliminar(id);
        vistaPrio.eliminar(id);
        return true;
    }

    public void mostrarCatalogo() {
        catalogo.mostrarCatalogo();
    }

    public int totalTareas() {
        return catalogo.getTamano();
    }

    // ---------- Modificar (guarda en la pila de deshacer) ----------

    // Los parámetros null no se cambian
    // Devuelve true solo si algo cambió de verdad
    public boolean modificarTarea(int id, String titulo, String desc, Prioridad prio, Estado estado) {
        Tarea tarea = catalogo.buscar(id);
        if (tarea == null) {
            return false;
        }
        if (titulo != null && titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        Tarea prev = tarea.copia();
        if (titulo != null) {
            tarea.setTitulo(titulo.trim());
        }
        if (desc != null) {
            tarea.setDescripcion(desc.trim());
        }
        if (prio != null) {
            tarea.setPrioridad(prio);
        }
        if (estado != null) {
            tarea.setEstado(estado);
        }

        if (tarea.mismosDatosQue(prev)) {
            return false;
        }
        registrarAccion(new AccionCambio(id, prev, tarea.copia(), "Modificación de la tarea #" + id + " (" + prev.getTitulo() + ")"));
        // si cambió la prioridad hay que reordenar la lista doble
        if (prev.getPrioridad() != tarea.getPrioridad()) {
            reconstruirVista();
        }
        return true;
    }

    // Guarda la acción y borra el historial de rehacer
    private void registrarAccion(AccionCambio acc) {
        pilaUndo.push(acc);
        pilaRedo.vaciar();
    }

    // ---------- Deshacer y rehacer ----------

    public String deshacer() {
        if (pilaUndo.estaVacia()) {
            return "No hay cambios para deshacer";
        }
        AccionCambio acc = pilaUndo.pop();
        Tarea tarea = catalogo.buscar(acc.getTareaId());
        if (tarea == null) {
            return "No se pudo deshacer, la tarea #" + acc.getTareaId() + " ya no existe";
        }
        tarea.restaurarDesde(acc.getEstadoPrevio());
        pilaRedo.push(acc);
        reconstruirVista();
        return "Deshecho: " + acc + "\n  Estado actual -> " + tarea;
    }

    public String rehacer() {
        if (pilaRedo.estaVacia()) {
            return "No hay cambios para rehacer";
        }
        AccionCambio acc = pilaRedo.pop();
        Tarea tarea = catalogo.buscar(acc.getTareaId());
        if (tarea == null) {
            return "No se pudo rehacer, la tarea #" + acc.getTareaId() + " ya no existe";
        }
        tarea.restaurarDesde(acc.getEstadoNuevo());
        pilaUndo.push(acc);
        reconstruirVista();
        return "Rehecho: " + acc + "\n  Estado actual -> " + tarea;
    }

    public int cambiosParaDeshacer() {
        return pilaUndo.getTamano();
    }

    public int cambiosParaRehacer() {
        return pilaRedo.getTamano();
    }

    // ---------- Cola de ejecución ----------

    // Falla si no existe, si ya está completada o si ya está en la cola
    public void enviarACola(int id) {
        Tarea tarea = catalogo.buscar(id);
        if (tarea == null) {
            throw new IllegalArgumentException("No existe una tarea con id " + id);
        }
        if (tarea.getEstado() == Estado.COMPLETADA) {
            throw new IllegalStateException("La tarea #" + id + " ya está completada");
        }
        if (colaEjec.contiene(tarea)) {
            throw new IllegalStateException("La tarea #" + id + " ya está en la cola");
        }
        colaEjec.enqueue(tarea);
    }

    // Devuelve null si la cola está vacía
    public Tarea verFrenteDeCola() {
        if (colaEjec.estaVacia()) {
            return null;
        }
        return colaEjec.frente();
    }

    // Saca la tarea del frente y la deja completada
    // Devuelve null si la cola está vacía
    public Tarea ejecutarSiguiente() {
        if (colaEjec.estaVacia()) {
            return null;
        }
        Tarea tarea = colaEjec.dequeue();
        Tarea prev = tarea.copia();
        tarea.setEstado(Estado.COMPLETADA);
        registrarAccion(new AccionCambio(tarea.getId(), prev, tarea.copia(), "Ejecución de la tarea #" + tarea.getId() + " (" + prev.getTitulo() + ")"));
        return tarea;
    }

    public boolean colaVacia() {
        return colaEjec.estaVacia();
    }

    public void mostrarCola() {
        colaEjec.mostrar();
    }

    public int tamanoCola() {
        return colaEjec.getTamano();
    }

    // ---------- Navegación ----------

    public Tarea tareaActualNavegacion() {
        return vistaPrio.actual();
    }

    public Tarea avanzar() {
        return vistaPrio.avanzar();
    }

    public Tarea retroceder() {
        return vistaPrio.retroceder();
    }

    public void irAlInicioNavegacion() {
        vistaPrio.irAlInicio();
    }

    public void mostrarVistaPrioridad() {
        vistaPrio.mostrar();
    }

    // Reordena la lista doble y deja el cursor en la misma tarea
    private void reconstruirVista() {
        int idAct = -1;
        Tarea act = vistaPrio.actual();
        if (act != null) {
            idAct = act.getId();
        }

        vistaPrio.vaciar();
        Tarea[] todas = catalogo.aArreglo();
        for (int i = 0; i < todas.length; i++) {
            vistaPrio._insertarOrdenado(todas[i]);
        }
        if (idAct != -1) {
            vistaPrio.posicionarEn(idAct);
        }
    }

    // Datos de ejemplo para probar rápido
    public void cargarDatosDeEjemplo() {
        crearTarea("Entregar proyecto de Estructura de Datos", "Subir el código al aula virtual", Prioridad.ALTA);
        crearTarea("Estudiar para el examen parcial", "Repasar listas, pilas y colas", Prioridad.MEDIA);
        crearTarea("Lavar la ropa", "", Prioridad.BAJA);
        crearTarea("Preparar presentación", "Diapositivas del avance", Prioridad.ALTA);
    }
}
