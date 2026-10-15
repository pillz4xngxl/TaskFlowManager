package modelos;

// Entidad principal, el id no cambia
public class Tarea {

    private final int id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;

    public Tarea(int id, String titulo, String descripcion, Prioridad prioridad, Estado estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    // Tarea nueva, empieza pendiente
    public Tarea(int id, String titulo, String descripcion, Prioridad prioridad) {
        this(id, titulo, descripcion, prioridad, Estado.PENDIENTE);
    }

    // Copia independiente con el mismo id
    public Tarea copia() {
        return new Tarea(id, titulo, descripcion, prioridad, estado);
    }

    // Copia los datos de otra tarea (menos el id)
    public void restaurarDesde(Tarea otra) {
        this.titulo = otra.titulo;
        this.descripcion = otra.descripcion;
        this.prioridad = otra.prioridad;
        this.estado = otra.estado;
    }

    public boolean mismosDatosQue(Tarea otra) {
        return titulo.equals(otra.titulo)
                && descripcion.equals(otra.descripcion)
                && prioridad == otra.prioridad
                && estado == otra.estado;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "[#" + id + "] " + titulo
                + " | prioridad: " + prioridad.getEtiqueta()
                + " | estado: " + estado.getEtiqueta()
                + " | " + descripcion;
    }
}
