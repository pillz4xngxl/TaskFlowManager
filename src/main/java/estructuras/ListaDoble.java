package estructuras;

import modelos.Tarea;

// Lista doble ordenada por prioridad (alta primero) con un cursor para navegar
public class ListaDoble {

    private NodoDouble<Tarea> cabeza;
    private NodoDouble<Tarea> cola;
    private NodoDouble<Tarea> cursor;
    private int tam;

    public ListaDoble() {
        this.cabeza = null;
        this.cola = null;
        this.cursor = null;
        this.tam = 0;
    }

    // Inserta por prioridad (mayor primero)
    // Si hay empate, queda después de las que ya estaban
    public void _insertarOrdenado(Tarea t) {
        if (t == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }
        NodoDouble<Tarea> nvo = new NodoDouble<>(t);

        if (cabeza == null) {
            // lista vacía
            cabeza = nvo;
            cola = nvo;
        } else {
            // busca el primero con menor prioridad
            NodoDouble<Tarea> act = cabeza;
            while (act != null && act.getDato().getPrioridad().getPeso() >= t.getPrioridad().getPeso()) {
                act = act.getSiguiente();
            }

            if (act == null) {
                // al final
                nvo.setAnterior(cola);
                cola.setSiguiente(nvo);
                cola = nvo;
            } else if (act == cabeza) {
                // al inicio
                nvo.setSiguiente(cabeza);
                cabeza.setAnterior(nvo);
                cabeza = nvo;
            } else {
                // en medio
                NodoDouble<Tarea> prev = act.getAnterior();
                nvo.setAnterior(prev);
                nvo.setSiguiente(act);
                prev.setSiguiente(nvo);
                act.setAnterior(nvo);
            }
        }
        if (cursor == null) {
            cursor = cabeza;
        }
        tam++;
    }

    // Mueve el cursor adelante, devuelve null si no hay siguiente
    public Tarea avanzar() {
        if (cursor == null || cursor.getSiguiente() == null) {
            return null;
        }
        cursor = cursor.getSiguiente();
        return cursor.getDato();
    }

    // Mueve el cursor atrás, devuelve null si no hay anterior
    public Tarea retroceder() {
        if (cursor == null || cursor.getAnterior() == null) {
            return null;
        }
        cursor = cursor.getAnterior();
        return cursor.getDato();
    }

    public Tarea actual() {
        if (cursor == null) {
            return null;
        }
        return cursor.getDato();
    }

    public boolean haySiguiente() {
        return cursor != null && cursor.getSiguiente() != null;
    }

    public boolean hayAnterior() {
        return cursor != null && cursor.getAnterior() != null;
    }

    public void irAlInicio() {
        cursor = cabeza;
    }

    // Pone el cursor en la tarea con ese id, false si no existe
    public boolean posicionarEn(int id) {
        NodoDouble<Tarea> act = cabeza;
        while (act != null) {
            if (act.getDato().getId() == id) {
                cursor = act;
                return true;
            }
            act = act.getSiguiente();
        }
        return false;
    }

    // Devuelve false si no existe el id
    public boolean eliminar(int id) {
        NodoDouble<Tarea> act = cabeza;
        while (act != null && act.getDato().getId() != id) {
            act = act.getSiguiente();
        }
        if (act == null) {
            return false;
        }

        NodoDouble<Tarea> prev = act.getAnterior();
        NodoDouble<Tarea> sig = act.getSiguiente();

        if (prev == null) {
            cabeza = sig;
        } else {
            prev.setSiguiente(sig);
        }
        if (sig == null) {
            cola = prev;
        } else {
            sig.setAnterior(prev);
        }
        // el cursor no debe quedar en un nodo eliminado
        if (cursor == act) {
            if (sig != null) {
                cursor = sig;
            } else {
                cursor = prev;
            }
        }
        act.setSiguiente(null);
        act.setAnterior(null);
        tam--;
        return true;
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        cursor = null;
        tam = 0;
    }

    // Muestra la lista y marca el cursor con >>
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  (no hay tareas para navegar)");
            return;
        }
        NodoDouble<Tarea> act = cabeza;
        while (act != null) {
            String marca = "   ";
            if (act == cursor) {
                marca = ">> ";
            }
            System.out.println("  " + marca + act.getDato());
            act = act.getSiguiente();
        }
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getTamano() {
        return tam;
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
}
