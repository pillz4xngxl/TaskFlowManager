package estructuras;

// Nodo con referencia al siguiente y al anterior
// Lo usan todas las estructuras (las simples solo usan "siguiente")
public class NodoDouble<T> {

    private T dato;
    private NodoDouble<T> siguiente;
    private NodoDouble<T> anterior;

    public NodoDouble(T dato) {
        this.dato = dato;
        this.siguiente = null;
        this.anterior = null;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoDouble<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDouble<T> siguiente) {
        this.siguiente = siguiente;
    }

    public NodoDouble<T> getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoDouble<T> anterior) {
        this.anterior = anterior;
    }
}
