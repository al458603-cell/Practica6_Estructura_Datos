import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Juego "Adivina el animal".
 * Carga el árbol de decisión desde animales.txt (en la raíz del proyecto),
 * permite jugar, consultar el árbol y guardarlo al salir.
 */
public class JuegoAdivina {
    private static final String FICHERO = "animales.txt";
    private static final String ANIMAL_INICIAL = "Perro";

    private final Scanner teclado;
    private ArbolDecision arbol;

    public JuegoAdivina(Scanner teclado) {
        this.teclado = teclado;
    }

    public static void main(String[] args) {
        JuegoAdivina juego = new JuegoAdivina(new Scanner(System.in));
        try {
            juego.ejecutar();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Fin de la entrada. El árbol no se ha guardado.");
        }
    }

    public void ejecutar() {
        if (!cargarArbol()) {
            return;
        }
        System.out.println("=== ADIVINA EL ANIMAL ===");
        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("1. Jugar  2. Describir  3. Diferencia  4. Estadísticas  5. Guardar y salir");
            String opcion = leerLinea("> ");
            switch (opcion) {
                case "1":
                    jugar();
                    break;
                case "2":
                    describir();
                    break;
                case "3":
                    diferencia();
                    break;
                case "4":
                    estadisticas();
                    break;
                case "5":
                    salir = guardar();
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    // Carga el árbol del fichero. Si no existe, empieza con un árbol que solo conoce un animal.
    private boolean cargarArbol() {
        try {
            arbol = ArbolDecision.cargar(FICHERO);
        } catch (FileNotFoundException e) {
            System.out.println("No se encuentra " + FICHERO + " en la raíz del proyecto.");
            System.out.println("Empiezo conociendo solo un animal: " + ANIMAL_INICIAL + ".");
            arbol = new ArbolDecision(ANIMAL_INICIAL);
        } catch (IOException e) {
            System.out.println("El fichero " + FICHERO + " no tiene un formato correcto: " + e.getMessage());
            return false;
        }
        if (!arbol.esExtendido()) {
            System.out.println("El árbol leído no es extendido: no se puede jugar con él.");
            return false;
        }
        return true;
    }

    // Opción 1: recorre el árbol desde la raíz según las respuestas y aprende si falla
    private void jugar() {
        leerLinea("Piensa en un animal y pulsa Intro.");
        BinaryNode<String> nodo = arbol.getRaiz();
        while (!nodo.isLeaf()) {
            if (leerSiNo(nodo.getData())) {
                nodo = nodo.left();
            } else {
                nodo = nodo.right();
            }
        }
        String propuesto = nodo.getData();
        if (leerSiNo("¿Es un " + propuesto + "?")) {
            System.out.println("¡He acertado!");
            return;
        }
        String nuevo = leerTexto("¡Me has ganado! ¿En qué animal pensabas? > ");
        String pregunta = formatearPregunta(
                leerTexto("Escribe una pregunta que distinga " + nuevo + " de " + propuesto + " > "));
        boolean respuesta = leerSiNo("Para " + nuevo + ", ¿la respuesta es sí o no?");
        try {
            arbol.aprender(nodo, nuevo, pregunta, respuesta);
            System.out.println("Gracias, ya lo he aprendido.");
        } catch (IllegalArgumentException e) {
            System.out.println("No puedo aprenderlo: " + nuevo + " ya está en el árbol.");
        }
    }

    // Opción 2
    private void describir() {
        String animal = leerTexto("¿Qué animal? > ");
        List<String> caracteristicas = arbol.describir(animal);
        if (caracteristicas.isEmpty()) {
            System.out.println("No conozco ese animal o no tengo nada que decir de él.");
        } else {
            for (String c : caracteristicas) {
                System.out.println(c);
            }
        }
    }

    // Opción 3
    private void diferencia() {
        String animal1 = leerTexto("Primer animal > ");
        String animal2 = leerTexto("Segundo animal > ");
        String pregunta = arbol.preguntaDiferenciadora(animal1, animal2);
        if (pregunta == null) {
            System.out.println("No puedo diferenciarlos: alguno no está en el árbol o son el mismo animal.");
        } else {
            System.out.println("Los distingue la pregunta: " + pregunta);
        }
    }

    // Opción 4
    private void estadisticas() {
        System.out.println("Conozco " + arbol.numAnimales() + " animales: " + arbol.animales());
    }

    // Opción 5. Devuelve true si se ha podido guardar y, por tanto, se puede salir.
    private boolean guardar() {
        try {
            arbol.guardar(FICHERO);
            System.out.println("Árbol guardado en " + FICHERO + ". ¡Hasta la próxima!");
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("No se ha podido guardar el árbol: " + e);
            return leerSiNo("¿Quieres salir sin guardar?");
        }
    }

    // ---------- Lectura de teclado ----------

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    private String leerTexto(String mensaje) {
        String texto = leerLinea(mensaje);
        while (texto.isEmpty()) {
            texto = leerLinea(mensaje);
        }
        return texto;
    }

    private boolean leerSiNo(String pregunta) {
        while (true) {
            String r = leerLinea(pregunta + " (s/n) > ").toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) {
                return true;
            }
            if (r.equals("n") || r.equals("no")) {
                return false;
            }
            System.out.println("Responde s o n.");
        }
    }

    // Añade los signos de interrogación si el usuario no los ha escrito
    private static String formatearPregunta(String pregunta) {
        String p = pregunta;
        if (!p.startsWith("¿")) {
            p = "¿" + p;
        }
        if (!p.endsWith("?")) {
            p = p + "?";
        }
        return p;
    }
}
