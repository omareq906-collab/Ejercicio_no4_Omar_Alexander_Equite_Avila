import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Clase principal: lee entradas y muestra resultados en la consola.
public class DulceEstacionApp {
    private Scanner scanner = new Scanner(System.in);
    private SistemaAlquiler sistema = new SistemaAlquiler();

    public static void main(String[] args) {
        DulceEstacionApp app = new DulceEstacionApp();
        app.cargarDatosIniciales();

        try {
            app.mostrarMenu();
        } catch (NoSuchElementException e) {
            // Permite salir sin un error si se cierra la entrada de consola.
            System.out.println("\nSe cerró la entrada. Programa finalizado.");
        } finally {
            app.scanner.close();
        }
    }

    private void cargarDatosIniciales() {
        // Datos de demostración del análisis. Todas inician disponibles.
        sistema.registrarMaquina(new MaquinaPalomitas(
                "PAL-01", "Dulce", "P80", 100, 80, true));
        sistema.registrarMaquina(new MaquinaPalomitas(
                "PAL-02", "Dulce", "P60", 90, 60, false));

        sistema.registrarMaquina(new MaquinaAlgodonAzucar(
                "ALG-01", "Dulce", "A900", 120, 900));
        sistema.registrarMaquina(new MaquinaAlgodonAzucar(
                "ALG-02", "Dulce", "A1200", 130, 1200));

        sistema.registrarMaquina(new FuenteChocolate(
                "CHO-01", "Dulce", "F2.5", 150, 2.5));
        sistema.registrarMaquina(new FuenteChocolate(
                "CHO-02", "Dulce", "F4", 180, 4.0));
    }

    private void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n=== DULCE ESTACIÓN ===");
            System.out.println("1. Registrar máquina");
            System.out.println("2. Consultar inventario");
            System.out.println("3. Cotizar y confirmar alquiler");
            System.out.println("4. Registrar devolución");
            System.out.println("5. Ver reporte general");
            System.out.println("6. Salir");
            opcion = leerEnteroPositivo("Selecciona una opción: ");

            switch (opcion) {
                case 1:
                    registrarMaquina();
                    break;
                case 2:
                    consultarInventario();
                    break;
                case 3:
                    cotizarYAlquilar();
                    break;
                case 4:
                    devolverMaquina();
                    break;
                case 5:
                    mostrarReporte();
                    break;
                case 6:
                    System.out.println("Gracias por utilizar Dulce Estación.");
                    break;
                default:
                    System.out.println("Selecciona una opción entre 1 y 6.");
            }
        } while (opcion != 6);
    }

    private void registrarMaquina() {
        System.out.println("\nREGISTRAR MÁQUINA");
        System.out.println("1. Palomitas");
        System.out.println("2. Algodón de azúcar");
        System.out.println("3. Fuente de chocolate");

        int tipo = leerEnteroPositivo("Categoría: ");
        if (tipo < 1 || tipo > 3) {
            System.out.println("Categoría inválida. No se registró ninguna máquina.");
            return;
        }

        System.out.print("Código de inventario: ");
        String codigo = scanner.nextLine().trim();
        if (codigo.isEmpty()) {
            System.out.println("El código no puede estar vacío.");
            return;
        }
        if (sistema.buscarPorCodigo(codigo) != null) {
            System.out.println("Ya existe una máquina con ese código.");
            return;
        }

        System.out.print("Marca: ");
        String marca = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String modelo = scanner.nextLine().trim();
        if (marca.isEmpty() || modelo.isEmpty()) {
            System.out.println("La marca y el modelo no pueden estar vacíos.");
            return;
        }

        double tarifa = leerDoublePositivo("Tarifa diaria en quetzales: ");

        try {
            Maquina maquina;
            if (tipo == 1) {
                int porciones = leerEnteroPositivo("Porciones por hora: ");
                String respuesta;
                do {
                    System.out.print("¿Tiene carrito integrado? (S/N): ");
                    respuesta = scanner.nextLine().trim();
                    if (!respuesta.equalsIgnoreCase("S") && !respuesta.equalsIgnoreCase("N")) {
                        System.out.println("Escribe S para sí o N para no.");
                    }
                } while (!respuesta.equalsIgnoreCase("S") && !respuesta.equalsIgnoreCase("N"));

                boolean carrito = respuesta.equalsIgnoreCase("S");
                maquina = new MaquinaPalomitas(codigo, marca, modelo, tarifa, porciones, carrito);
            } else if (tipo == 2) {
                int potencia = leerEnteroPositivo("Potencia en vatios: ");
                maquina = new MaquinaAlgodonAzucar(codigo, marca, modelo, tarifa, potencia);
            } else {
                double capacidad = leerDoublePositivo("Capacidad máxima en kg: ");
                maquina = new FuenteChocolate(codigo, marca, modelo, tarifa, capacidad);
            }

            if (sistema.registrarMaquina(maquina)) {
                System.out.println("Máquina registrada correctamente. Estado: Disponible.");
            } else {
                System.out.println("No se pudo registrar la máquina. Revisa el código y su estado.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void consultarInventario() {
        System.out.println("\n" + sistema.obtenerInventario());
    }

    private void cotizarYAlquilar() {
        System.out.print("\nCódigo de la máquina: ");
        String codigo = scanner.nextLine().trim();
        Maquina maquina = sistema.buscarPorCodigo(codigo);

        if (maquina == null) {
            System.out.println("No existe una máquina con ese código.");
            return;
        }

        int dias = leerEnteroPositivo("Días de alquiler: ");
        try {
            double total = sistema.cotizar(codigo, dias);
            System.out.println("\nCOTIZACIÓN");
            System.out.println(maquina.obtenerDescripcion());
            System.out.println("Días: " + dias);
            System.out.printf(Locale.US, "Total: Q%.2f%n", total);

            if (!maquina.isDisponible()) {
                System.out.println("La máquina está alquilada. Puedes cotizarla, pero debes esperar su devolución para alquilarla.");
                return;
            }

            String respuesta;
            do {
                System.out.print("¿Confirmar el alquiler por este monto? (S/N): ");
                respuesta = scanner.nextLine().trim();
                if (!respuesta.equalsIgnoreCase("S") && !respuesta.equalsIgnoreCase("N")) {
                    System.out.println("Escribe S para sí o N para no.");
                }
            } while (!respuesta.equalsIgnoreCase("S") && !respuesta.equalsIgnoreCase("N"));

            if (respuesta.equalsIgnoreCase("N")) {
                System.out.println("Alquiler no confirmado. No se realizó ningún cobro.");
                return;
            }

            if (sistema.confirmarAlquiler(codigo, dias)) {
                System.out.printf(Locale.US, "Alquiler confirmado. Total cobrado: Q%.2f%n", total);
            } else {
                System.out.println("No se pudo confirmar el alquiler. Revisa los datos y la disponibilidad.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void devolverMaquina() {
        System.out.print("\nCódigo de la máquina a devolver: ");
        String codigo = scanner.nextLine().trim();
        Maquina maquina = sistema.buscarPorCodigo(codigo);

        if (maquina == null) {
            System.out.println("No existe una máquina con ese código.");
        } else if (maquina.isDisponible()) {
            System.out.println("La máquina ya está disponible. No tiene un alquiler activo.");
        } else if (sistema.registrarDevolucion(codigo)) {
            System.out.println("Devolución registrada. La máquina está disponible nuevamente.");
            System.out.println("Los ingresos acumulados no cambiaron.");
        }
    }

    private void mostrarReporte() {
        System.out.println("\n" + sistema.generarReporte());
    }

    private int leerEnteroPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                int numero = Integer.parseInt(entrada);
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // Una entrada inválida vuelve a pedirse sin cerrar el programa.
            }
            System.out.println("Ingresa un número entero mayor que cero.");
        }
    }

    private double leerDoublePositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            // Acepta 2.5 o 2,5. Escribe los números sin separador de miles.
            String entrada = scanner.nextLine().trim().replace(',', '.');
            try {
                double numero = Double.parseDouble(entrada);
                if (Double.isFinite(numero) && numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // Se vuelve a pedir el valor si no se puede convertir.
            }
            System.out.println("Ingresa un número positivo válido, por ejemplo 150 o 2.5.");
        }
    }
}
