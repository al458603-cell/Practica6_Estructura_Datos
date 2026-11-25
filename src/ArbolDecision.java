import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ArbolDecision {
    private BinaryNode<String> raiz;

    // Crea un árbol que solo conoce un animal
    public ArbolDecision(String animal) {
        raiz = new BinaryNode<>(animal);
    }

    // Crea un árbol a partir de una raíz ya construida (lo usan cargar y los tests)
    protected ArbolDecision(BinaryNode<String> raiz) {
        this.raiz = raiz;
    }

    public BinaryNode<String> getRaiz() {
        return raiz;
    }

    // Métodos a implementar

    //Ejercicio1
    public boolean esExtendido() {

        return(esExtendido(raiz));

    }
    private boolean esExtendido(BinaryNode<String> r){
        if(r==null)
            return true;

        if(r.isLeaf())
            return true;

        if(!r.hasLeft() || !r.hasRight())
            return false;

        return (esExtendido(r.left()) && esExtendido(r.right()));
    }

    //Ejercicio2
    public int numAnimales() {
        return numAnimales(raiz);
    }

    private int numAnimales(BinaryNode<String> r) {

        if (r == null) {
            return 0;
        }

        if (r.isLeaf()) {
            return 1;
        }

        return numAnimales(r.left()) + numAnimales(r.right());
    }


    //Ejercicio3
    public List<String> animales() {
        List<String> lista = new ArrayList<String>();
        return animales(raiz ,lista);
    }

    private List<String> animales(BinaryNode<String> r , List<String> lista){

        if(r == null)
            return lista;

        if(r.isLeaf()){
            lista.add(r.getData());
            return lista;
        }

        animales(r.left(),lista);
        animales(r.right(), lista);

        return lista;
    }



    //Ejercicio 4
    public List<String> camino (String elem) {
        if (elem == null){
            throw new IllegalArgumentException();
        }

        List<String> lista = new ArrayList<String>();
        camino(raiz, lista, elem);
        return lista;
    }

    private boolean camino (BinaryNode<String> r, List<String> lista , String elem){

        if(r == null)
            return false;

        lista.add(r.getData());

        if (r.getData().equals(elem)) {
            return true;
        }

        if (camino(r.left(),lista,elem))
            return true;

        if (camino(r.right(),lista,elem))
            return true;

        lista.remove(lista.size() - 1);

        return false;
    }


    //Ejercicio 5

    public void aprender (BinaryNode<String> hoja, String animalNuevo, String pregunta, boolean respuestaNuevo) {
        //COMPROBCIONES
        if( hoja == null || !hoja.isLeaf() ){
            throw new IllegalArgumentException();
        }
        if( animalNuevo == null || animalNuevo.isEmpty()){
            throw new IllegalArgumentException();
        }
        if( pregunta == null || pregunta.isEmpty() ){
            throw new IllegalArgumentException();
        }
        if (animales().contains(animalNuevo)) {
            throw new IllegalArgumentException();
        }

        BinaryNode<String> nuevo = new BinaryNode<>(animalNuevo);
        BinaryNode<String> antiguo = new BinaryNode<>(hoja.getData());

        hoja.setData(pregunta);

        if (respuestaNuevo) {
            hoja.setLeft(nuevo);
            hoja.setRight(antiguo);
        } else {
            hoja.setLeft(antiguo);
            hoja.setRight(nuevo);
        }
    }



    //Ejercicio 6
    public List<String> describir(String animal) {
        List<String> lista = new ArrayList<String>();
        describir(raiz, lista, animal);
        return lista;
    }

    private boolean describir(BinaryNode<String> r, List<String> lista, String animal) {
        // Árbol vacío: no está
        if (r == null) {
            return false;
        }

        // Hoja: ¿es el animal buscado?
        if (r.isLeaf()) {
            return r.getData().equals(animal);
        }

        // Pregunta: pruebo por el Sí
        lista.add(r.getData() + " Sí");
        if (describir(r.left(), lista, animal)) {
            return true;
        }
        lista.remove(lista.size() - 1);   // no estaba por aquí

        // Pruebo por el No
        lista.add(r.getData() + " No");
        if (describir(r.right(), lista, animal)) {
            return true;
        }
        lista.remove(lista.size() - 1);

        return false;
    }


    //Ejercicio 7
    public String preguntaDiferenciadora(String animal1, String animal2) {
        // El mismo animal: nada que diferenciar
        if (animal1.equals(animal2)) {
            return null;
        }

        List<String> camino1 = camino(animal1);
        List<String> camino2 = camino(animal2);

        // Alguno no está en el árbol
        if (camino1.isEmpty() || camino2.isEmpty()) {
            return null;
        }

        // Avanzo mientras coincidan, sin salirme de ninguna lista
        int i = 0;
        while (i < camino1.size() && i < camino2.size() && camino1.get(i).equals(camino2.get(i))) {
            i++;
        }

        // La última que coincide
        return camino1.get(i - 1);
    }


    public void guardar(String nombreFichero) throws IOException {
        if (raiz == null) {
            throw new IllegalStateException("No se puede guardar un árbol vacío");
        }
        try (PrintWriter out = new PrintWriter(nombreFichero, StandardCharsets.UTF_8.name())) {
            guardar(raiz, out);
            if (out.checkError()) {
                throw new IOException("Error al escribir en " + nombreFichero);
            }
        }
    }

    //Ejercicio 8
    private void guardar(BinaryNode<String> r, PrintWriter out) {
        if (r == null) {
            return;
        }

        // Hoja: es un animal
        if (r.isLeaf()) {
            out.println("A:" + r.getData());
            return;
        }

        // Pregunta: preorden (nodo, izquierda, derecha)
        out.println("P:" + r.getData());
        guardar(r.left(), out);
        guardar(r.right(), out);
    }

    // ---------- cargar ----------

    public static ArbolDecision cargar(String nombreFichero) throws IOException {
        try (Scanner sc = new Scanner(new File(nombreFichero), StandardCharsets.UTF_8.name())) {
            if (!sc.hasNextLine()) {
                throw new IOException("El fichero está vacío");
            }
            BinaryNode<String> raiz = leerNodo(sc);
            while (sc.hasNextLine()) {
                if (!sc.nextLine().trim().isEmpty()) {
                    throw new IOException("Sobran líneas después de completar el árbol");
                }
            }
            return new ArbolDecision(raiz);
        }
    }

    // Lee un subárbol completo: una hoja, o una pregunta seguida de sus dos subárboles
    private static BinaryNode<String> leerNodo(Scanner sc) throws IOException {
        if (!sc.hasNextLine()) {
            throw new IOException("El fichero termina antes de completar el árbol");
        }
        String linea = sc.nextLine();
        if (linea.startsWith("A:")) {
            return new BinaryNode<>(linea.substring(2));
        }
        if (linea.startsWith("P:")) {
            BinaryNode<String> nodo = new BinaryNode<>(linea.substring(2));
            nodo.setLeft(leerNodo(sc));
            nodo.setRight(leerNodo(sc));
            return nodo;
        }
        throw new IOException("Línea con formato incorrecto: " + linea);
    }
}
