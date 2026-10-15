package estructuras;

// Cola con nodos enlazados
// Lanza excepción si se saca de una cola vacía
// o si se llena una cola con capacidad máxima
public class Cola<T> {

    private NodoDouble<T> inicio;
    private NodoDouble<T> fin;
    private int tam; // tamaño
    private final int cap; // capacidad

    public Cola() {
        this(Integer.MAX_VALUE);
    }

    public Cola(int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.cap = cap;
        this.inicio = null;
        this.fin = null;
        this.tam = 0;
    }

    public void enqueue(T elemento) {
        if (estaLlena()) {
            throw new IllegalStateException("Desbordamiento (overflow): la cola está llena");
        }
        NodoDouble<T> nvo = new NodoDouble<>(elemento);
        if (fin == null) {
            // cola vacía
            inicio = nvo;
        } else {
            fin.setSiguiente(nvo);
        }
        fin = nvo;
        tam++;
    }

    public T dequeue() {
        if (estaVacia()) {
            throw new IllegalStateException("Subdesbordamiento (underflow): la cola está vacía");
        }
        NodoDouble<T> elim = inicio;
        T dato = elim.getDato();
        inicio = inicio.getSiguiente();
        // si quedó vacía, fin también debe ser null
        if (inicio == null) {
            fin = null;
        }
        elim.setSiguiente(null);
        tam--;
        return dato;
    }

    // Ve el elemento del frente sin sacarlo
    public T frente() {
        if (estaVacia()) {
            throw new IllegalStateException("Subdesbordamiento (underflow): la cola está vacía");
        }
        return inicio.getDato();
    }

    // Compara por referencia
    public boolean contiene(T elemento) {
        NodoDouble<T> act = inicio;
        while (act != null) {
            if (act.getDato() == elemento) {
                return true;
            }
            act = act.getSiguiente();
        }
        return false;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    public boolean estaLlena() {
        return tam >= cap;
    }

    public int getTamano() {
        return tam;
    }

    // Muestra del frente al final
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  (cola vacía)");
            return;
        }
        NodoDouble<T> act = inicio;
        int pos = 1;
        while (act != null) {
            System.out.println("  " + pos + ". " + act.getDato());
            pos++;
            act = act.getSiguiente();
        }
    }
}
