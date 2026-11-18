import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Ejecuta los tests de la práctica.
 *
 * Para ejecutar solo los tests de un ejercicio, cambia el valor de EJERCICIO
 * (1 a 8) o pásalo como argumento del programa. Con 0 se ejecutan todos.
 */
public class TestRunner {

    private static final int EJERCICIO = 0;

    private static final String CLASE_TEST = "TestArbolDecision";
    private static final String PREFIJO = "testArbolDecision";

    public static void main(String[] args) throws ClassNotFoundException {
        int ejercicio = args.length > 0 ? Integer.parseInt(args[0]) : EJERCICIO;

        Class<?> clase = Class.forName(CLASE_TEST);
        Request peticion = Request.aClass(clase);
        if (ejercicio > 0) {
            peticion = peticion.filterWith(filtroEjercicio(ejercicio));
            System.out.println("Tests del ejercicio " + ejercicio);
        } else {
            System.out.println("Tests de todos los ejercicios");
        }
        System.out.println("----------------------------------------");

        JUnitCore junit = new JUnitCore();
        junit.addListener(new Informe());
        Result resultado = junit.run(peticion);

        System.out.println();
        System.out.println("----------------------------------------");
        int superados = resultado.getRunCount() - resultado.getFailureCount();
        System.out.println("Tests superados: " + superados + " de " + resultado.getRunCount());
        if (resultado.wasSuccessful()) {
            System.out.println("¡Todos los tests son correctos!");
        }
    }

    // Deja pasar solo los tests cuyo nombre empieza por testArbolDecisionNN
    private static Filter filtroEjercicio(int ejercicio) {
        final String prefijo = String.format("%s%02d", PREFIJO, ejercicio);
        return new Filter() {
            @Override
            public boolean shouldRun(Description d) {
                if (d.isTest()) {
                    return d.getMethodName() != null && d.getMethodName().startsWith(prefijo);
                }
                for (Description hijo : d.getChildren()) {
                    if (shouldRun(hijo)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String describe() {
                return "ejercicio " + ejercicio;
            }
        };
    }

    // Escribe una línea por test: OK o FALLO con el motivo
    private static class Informe extends RunListener {
        private boolean fallado;

        @Override
        public void testStarted(Description d) {
            fallado = false;
        }

        @Override
        public void testFailure(Failure f) {
            fallado = true;
            System.out.println("  [FALLO] " + f.getDescription().getMethodName());
            Throwable e = f.getException();
            if (e instanceof AssertionError) {
                System.out.println("        " + e.getMessage());
            } else {
                System.out.println("        Excepción no esperada: " + e);
                StackTraceElement lugar = lugarEnCodigoAlumno(e);
                if (lugar != null) {
                    System.out.println("        en " + lugar);
                }
            }
        }

        @Override
        public void testFinished(Description d) {
            if (!fallado) {
                System.out.println("  [OK]    " + d.getMethodName());
            }
        }

        // Primera línea de la traza que pertenece al código de la práctica
        private static StackTraceElement lugarEnCodigoAlumno(Throwable e) {
            for (StackTraceElement s : e.getStackTrace()) {
                String c = s.getClassName();
                if (c.equals("ArbolDecision") || c.equals("BinaryNode")) {
                    return s;
                }
            }
            return null;
        }
    }
}
