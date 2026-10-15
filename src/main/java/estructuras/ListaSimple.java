package estructuras;

import modelos.Tarea;

// Lista enlazada simple: catálogo general de tareas
public class ListaSimple {

    private NodoDouble<Tarea> cabeza;
    private NodoDouble<Tarea> cola;
    private int tam;

    public ListaSimple() {
        this.cabeza = null;
        this.cola = null;
        this.tam = 0;
    }

    public void insertarInicio(Tarea t) {
        validar(t);
        NodoDouble<Tarea> nvo = new NodoDouble<>(t);
        nvo.setSiguiente(cabeza);
        cabeza = nvo;
        // si estaba vacía, también es la cola
        if (cola == null) {
            cola = nvo;
        }
        tam++;
    }

    public void insertarFin(Tarea t) {
        validar(t);
        NodoDouble<Tarea> nvo = new NodoDouble<>(t);
        if (cola == null) {
            cabeza = nvo;
        } else {
            cola.setSiguiente(nvo);
        }
        cola = nvo;
        tam++;
    }

    // Devuelve false si no existe el id
    public boolean eliminar(int id) {
        NodoDouble<Tarea> prev = null;
        NodoDouble<Tarea> act = cabeza;

        while (act != null && act.getDato().getId() != id) {
            prev = act;
            act = act.getSiguiente();
        }
        if (act == null) {
            return false;
        }

        if (prev == null) {
            // era la cabeza
            cabeza = act.getSiguiente();
        } else {
            prev.setSiguiente(act.getSiguiente());
        }
        // si era el último, la cola ahora es el anterior
        if (act == cola) {
            cola = prev;
        }
        act.setSiguiente(null);
        tam--;
        return true;
    }

    // Devuelve null si no existe
    public Tarea buscar(int id) {
        NodoDouble<Tarea> act = cabeza;
        while (act != null) {
            if (act.getDato().getId() == id) {
                return act.getDato();
            }
            act = act.getSiguiente();
        }
        return null;
    }

    public void mostrarCatalogo() {
        if (estaVacia()) {
            System.out.println("  (el catálogo está vacío)");
            return;
        }
        NodoDouble<Tarea> act = cabeza;
        while (act != null) {
            System.out.println("  " + act.getDato());
            act = act.getSiguiente();
        }
    }

    public Tarea[] aArreglo() {
        Tarea[] res = new Tarea[tam];
        NodoDouble<Tarea> act = cabeza;
        int i = 0;
        while (act != null) {
            res[i] = act.getDato();
            i++;
            act = act.getSiguiente();
        }
        return res;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getTamano() {
        return tam;
    }

    private void validar(Tarea t) {
        if (t == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }
    }
}
