package ui;

import java.util.Scanner;

import modelos.Estado;
import modelos.Prioridad;
import modelos.Tarea;
import servicios.GestorTareas;

// Menú de consola que une todos los módulos
public class MenuConsola {

    private static final String FIN_ENTRADA = "Fin de entrada";

    private final GestorTareas gestor;
    private final Scanner sc;

    public MenuConsola(GestorTareas gestor) {
        this.gestor = gestor;
        this.sc = new Scanner(System.in, "UTF-8");
    }

    public void iniciar() {
        System.out.println("=== TaskFlow Manager ===");
        try {
            boolean salir = false;
            while (!salir) {
                mostrarMenu();
                int op = leerEntero("Opción: ");
                System.out.println();
                try {
                    salir = procesarOpcion(op);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    if (FIN_ENTRADA.equals(e.getMessage())) {
                        throw e;
                    }
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IllegalStateException e) {
            // se acabó la entrada (Ctrl+D o archivo), se sale normal
            if (!FIN_ENTRADA.equals(e.getMessage())) {
                throw e;
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println(" 1. Crear tarea (añadir a la Lista Simple)");
        System.out.println(" 2. Modificar tarea (registra cambio en la Pila)");
        System.out.println(" 3. Enviar tarea a la Cola de ejecución");
        System.out.println(" 4. Procesar la Cola (ejecutar en orden FIFO)");
        System.out.println(" 5. Navegar por prioridad (Lista Doble)");
        System.out.println(" 6. Deshacer último cambio (Pila Undo)  [" + gestor.cambiosParaDeshacer() + "]");
        System.out.println(" 7. Rehacer cambio deshecho (Pila Redo) [" + gestor.cambiosParaRehacer() + "]");
        System.out.println(" 8. Mostrar catálogo de tareas");
        System.out.println(" 9. Buscar tarea por id");
        System.out.println("10. Eliminar tarea");
        System.out.println("11. Ver cola de ejecución y próxima tarea");
        System.out.println("12. Cargar datos de ejemplo");
        System.out.println(" 0. Salir");
        System.out.println("-------------------------------------------");
    }

    // Devuelve true si el usuario quiere salir
    private boolean procesarOpcion(int op) {
        switch (op) {
            case 1:
                crearTarea();
                break;
            case 2:
                modificarTarea();
                break;
            case 3:
                enviarACola();
                break;
            case 4:
                procesarCola();
                break;
            case 5:
                navegar();
                break;
            case 6:
                System.out.println(gestor.deshacer());
                break;
            case 7:
                System.out.println(gestor.rehacer());
                break;
            case 8:
                System.out.println("Catálogo (" + gestor.totalTareas() + " tareas):");
                gestor.mostrarCatalogo();
                break;
            case 9:
                buscarTarea();
                break;
            case 10:
                eliminarTarea();
                break;
            case 11:
                verCola();
                break;
            case 12:
                gestor.cargarDatosDeEjemplo();
                System.out.println("Se cargaron 4 tareas de ejemplo");
                break;
            case 0:
                return true;
            default:
                System.out.println("Opción no válida");
        }
        return false;
    }

    // ---------- Opciones ----------

    private void crearTarea() {
        String titulo = leerTextoObligatorio("Título: ");
        String desc = leerLinea("Descripción: ");
        Prioridad prio = leerPrioridad("Prioridad (baja/media/alta): ", false);
        Tarea tarea = gestor.crearTarea(titulo, desc, prio);
        System.out.println("Tarea creada: " + tarea);
    }

    private void modificarTarea() {
        int id = leerEntero("Id de la tarea a modificar: ");
        Tarea tarea = gestor.buscarTarea(id);
        if (tarea == null) {
            System.out.println("No existe una tarea con id " + id);
            return;
        }
        System.out.println("Actual: " + tarea);
        System.out.println("(Presiona Enter para dejar un campo sin cambios)");

        String titulo = leerLinea("Nuevo título: ");
        String desc = leerLinea("Nueva descripción: ");
        Prioridad prio = leerPrioridad("Nueva prioridad (baja/media/alta): ", true);
        Estado estado = leerEstado("Nuevo estado (pendiente/proceso/completada): ");

        // un campo vacío significa "no cambiar"
        if (titulo.isEmpty()) {
            titulo = null;
        }
        if (desc.isEmpty()) {
            desc = null;
        }

        if (gestor.modificarTarea(id, titulo, desc, prio, estado)) {
            System.out.println("Tarea modificada: " + gestor.buscarTarea(id));
            System.out.println("(El cambio quedó registrado, puedes deshacerlo con la opción 6)");
        } else {
            System.out.println("No hubo cambios");
        }
    }

    private void enviarACola() {
        int id = leerEntero("Id de la tarea a enviar a la cola: ");
        gestor.enviarACola(id);
        System.out.println("Tarea #" + id + " encolada, tareas en cola: " + gestor.tamanoCola());
    }

    private void procesarCola() {
        if (gestor.colaVacia()) {
            System.out.println("La cola está vacía, no hay nada que procesar");
            return;
        }
        System.out.println("Procesando la cola en orden de llegada");
        while (!gestor.colaVacia()) {
            Tarea ejecutada = gestor.ejecutarSiguiente();
            System.out.println("  Ejecutada -> " + ejecutada);
        }
        System.out.println("Cola procesada");
    }

    private void navegar() {
        if (gestor.tareaActualNavegacion() == null) {
            System.out.println("No hay tareas para navegar, crea alguna primero");
            return;
        }
        gestor.irAlInicioNavegacion();
        boolean seguir = true;
        while (seguir) {
            System.out.println("Vista por prioridad (>> es tu posición):");
            gestor.mostrarVistaPrioridad();
            String cmd = leerLinea("[s] siguiente  [a] anterior  [x] salir: ").toLowerCase();
            switch (cmd) {
                case "s":
                    if (gestor.avanzar() == null) {
                        System.out.println("Ya estás en la última tarea");
                    }
                    break;
                case "a":
                    if (gestor.retroceder() == null) {
                        System.out.println("Ya estás en la primera tarea");
                    }
                    break;
                case "x":
                    seguir = false;
                    break;
                default:
                    System.out.println("Comando no válido");
            }
            System.out.println();
        }
    }

    private void buscarTarea() {
        int id = leerEntero("Id a buscar: ");
        Tarea tarea = gestor.buscarTarea(id);
        if (tarea == null) {
            System.out.println("No existe una tarea con id " + id);
        } else {
            System.out.println(tarea);
        }
    }

    private void eliminarTarea() {
        int id = leerEntero("Id de la tarea a eliminar: ");
        if (gestor.eliminarTarea(id)) {
            System.out.println("Tarea #" + id + " eliminada");
        } else {
            System.out.println("No existe una tarea con id " + id);
        }
    }

    private void verCola() {
        System.out.println("Cola de ejecución (" + gestor.tamanoCola() + "):");
        gestor.mostrarCola();
        Tarea prox = gestor.verFrenteDeCola();
        if (prox != null) {
            System.out.println("Próxima en ejecutarse -> " + prox);
        }
    }

    // ---------- Lectura de datos ----------

    // Si ya no hay más entrada lanza una excepción para cerrar el programa
    private String leerLinea(String msg) {
        System.out.print(msg);
        if (!sc.hasNextLine()) {
            throw new IllegalStateException(FIN_ENTRADA);
        }
        return sc.nextLine().trim();
    }

    private String leerTextoObligatorio(String msg) {
        String texto = leerLinea(msg);
        while (texto.isEmpty()) {
            System.out.println("Este campo no puede estar vacío");
            texto = leerLinea(msg);
        }
        return texto;
    }

    private int leerEntero(String msg) {
        while (true) {
            String texto = leerLinea(msg);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Escribe un número entero válido");
            }
        }
    }

    // Si permitirVacio es true, Enter devuelve null
    private Prioridad leerPrioridad(String msg, boolean permitirVacio) {
        while (true) {
            String texto = leerLinea(msg).toLowerCase();
            if (texto.isEmpty() && permitirVacio) {
                return null;
            }
            switch (texto) {
                case "baja":
                    return Prioridad.BAJA;
                case "media":
                    return Prioridad.MEDIA;
                case "alta":
                    return Prioridad.ALTA;
                default:
                    System.out.println("Prioridad no válida, usa: baja, media o alta");
            }
        }
    }

    // Enter devuelve null (no cambiar)
    private Estado leerEstado(String msg) {
        while (true) {
            String texto = leerLinea(msg).toLowerCase();
            switch (texto) {
                case "":
                    return null;
                case "pendiente":
                    return Estado.PENDIENTE;
                case "proceso":
                case "en proceso":
                    return Estado.EN_PROCESO;
                case "completada":
                    return Estado.COMPLETADA;
                default:
                    System.out.println("Estado no válido, usa: pendiente, proceso o completada");
            }
        }
    }
}
