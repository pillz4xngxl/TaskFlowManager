package estructuras;

// Pila con nodos enlazados
// Lanza excepción si se saca de una pila vacía
// o si se llena una pila con capacidad máxima
public class Pila<T> {

    private NodoDouble<T> tope;
    private int tam;
    private final int cap;

    public Pila() {
        this(Integer.MAX_VALUE);
    }

    public Pila(int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.cap = cap;
        this.tope = null;
        this.tam = 0;
    }

    public void push(T elemento) {
        if (estaLlena()) {
            throw new IllegalStateException("Desbordamiento (overflow): la pila está llena");
        }
        NodoDouble<T> nvo = new NodoDouble<>(elemento);
        nvo.setSiguiente(tope);
        tope = nvo;
        tam++;
    }

    public T pop() {
        if (estaVacia()) {
            throw new IllegalStateException("Subdesbordamiento (underflow): la pila está vacía");
        }
        NodoDouble<T> elim = tope;
        T dato = elim.getDato();
        tope = tope.getSiguiente();
        elim.setSiguiente(null);
        tam--;
        return dato;
    }

    // Ve el tope sin sacarlo
    public T tope() {
        if (estaVacia()) {
            throw new IllegalStateException("Subdesbordamiento (underflow): la pila está vacía");
        }
        return tope.getDato();
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public boolean estaLlena() {
        return tam >= cap;
    }

    public int getTamano() {
        return tam;
    }

    public void vaciar() {
        tope = null;
        tam = 0;
    }

    // Muestra del tope a la base
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  (pila vacía)");
            return;
        }
        NodoDouble<T> act = tope;
        while (act != null) {
            System.out.println("  " + act.getDato());
            act = act.getSiguiente();
        }
    }
}
