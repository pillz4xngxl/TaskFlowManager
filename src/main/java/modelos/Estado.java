package modelos;

// Estado de una tarea
public enum Estado {
    PENDIENTE("pendiente"),
    EN_PROCESO("en proceso"),
    COMPLETADA("completada");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
