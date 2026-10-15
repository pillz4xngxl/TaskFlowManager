package modelos;

// Prioridad de una tarea, el peso sirve para ordenar
public enum Prioridad {
    BAJA(1, "baja"),
    MEDIA(2, "media"),
    ALTA(3, "alta");

    private final int peso;
    private final String etiqueta;

    Prioridad(int peso, String etiqueta) {
        this.peso = peso;
        this.etiqueta = etiqueta;
    }

    public int getPeso() {
        return peso;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
