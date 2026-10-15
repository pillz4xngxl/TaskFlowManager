package modelos;

// Un cambio hecho a una tarea
// Guarda cómo estaba antes (para deshacer) y cómo quedó (para rehacer)
public class AccionCambio {

    private final int tareaId;
    private final Tarea estadoPrevio;
    private final Tarea estadoNuevo;
    private final String descripcion;

    public AccionCambio(int tareaId, Tarea estadoPrevio, Tarea estadoNuevo, String descripcion) {
        this.tareaId = tareaId;
        this.estadoPrevio = estadoPrevio;
        this.estadoNuevo = estadoNuevo;
        this.descripcion = descripcion;
    }

    public int getTareaId() {
        return tareaId;
    }

    public Tarea getEstadoPrevio() {
        return estadoPrevio;
    }

    public Tarea getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
