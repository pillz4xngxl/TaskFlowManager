package ui;

import servicios.GestorTareas;

public class Main {

    public static void main(String[] args) {
        GestorTareas gestor = new GestorTareas();
        MenuConsola menu = new MenuConsola(gestor);
        menu.iniciar();
    }
}
