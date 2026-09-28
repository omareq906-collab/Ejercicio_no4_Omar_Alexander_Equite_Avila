import java.util.ArrayList;
import java.util.Locale;

// Administra el inventario y las operaciones del negocio.
public class SistemaAlquiler {
    private ArrayList<Maquina> maquinas;
    private double ingresosAcumulados;

    public SistemaAlquiler() {
        maquinas = new ArrayList<Maquina>();
        ingresosAcumulados = 0;
    }

    public boolean registrarMaquina(Maquina m) {
        if (m == null || m.getCodigoInventario().trim().isEmpty()) {
            return false;
        }
        if (buscarPorCodigo(m.getCodigoInventario()) != null) {
            return false;
        }
        if (!m.isDisponible()) {
            return false;
        }

        maquinas.add(m);
        return true;
    }

    public Maquina buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        for (Maquina maquina : maquinas) {
            // Evita duplicados como PAL-01 y pal-01.
            if (maquina.getCodigoInventario().equalsIgnoreCase(codigo.trim())) {
                return maquina;
            }
        }
        return null;
    }

    public double cotizar(String codigo, int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días deben ser mayores que cero.");
        }

        Maquina maquina = buscarPorCodigo(codigo);
        if (maquina == null) {
            throw new IllegalArgumentException("No existe una máquina con ese código.");
        }

        // Polimorfismo: se ejecuta la fórmula de la subclase correspondiente.
        double total = maquina.calcularCosto(dias);
        if (!Double.isFinite(total) || total <= 0) {
            throw new IllegalArgumentException("El costo está fuera del rango permitido.");
        }
        // Cotizar no modifica disponibilidad ni ingresos.
        return total;
    }

    public boolean confirmarAlquiler(String codigo, int dias) {
        Maquina maquina = buscarPorCodigo(codigo);
        if (maquina == null || dias <= 0 || !maquina.isDisponible()) {
            return false;
        }

        double total = cotizar(codigo, dias);
        double nuevosIngresos = ingresosAcumulados + total;
        if (!Double.isFinite(nuevosIngresos)) {
            throw new IllegalArgumentException("El ingreso acumulado supera el rango permitido.");
        }

        // Solo una confirmación válida cambia estos dos datos.
        maquina.marcarAlquilada();
        ingresosAcumulados = nuevosIngresos;
        return true;
    }

    public boolean registrarDevolucion(String codigo) {
        Maquina maquina = buscarPorCodigo(codigo);
        if (maquina == null || maquina.isDisponible()) {
            return false;
        }

        maquina.marcarDisponible();
        // Una devolución no cambia los ingresos que ya se cobraron.
        return true;
    }

    public String obtenerInventario() {
        if (maquinas.isEmpty()) {
            return "No hay máquinas registradas.";
        }

        String inventario = "INVENTARIO\n";
        for (Maquina maquina : maquinas) {
            inventario += "\n" + maquina.obtenerDescripcion() + "\n";
        }
        return inventario;
    }

    public String generarReporte() {
        // Se obtienen las categorías de los objetos para admitir nuevas subclases.
        ArrayList<String> categorias = new ArrayList<String>();
        int disponibles = 0;

        for (Maquina maquina : maquinas) {
            if (!categorias.contains(maquina.getTipo())) {
                categorias.add(maquina.getTipo());
            }
            if (maquina.isDisponible()) {
                disponibles++;
            }
        }

        String reporte = "REPORTE GENERAL\n";
        for (String categoria : categorias) {
            int totalCategoria = 0;
            int disponiblesCategoria = 0;

            for (Maquina maquina : maquinas) {
                if (maquina.getTipo().equals(categoria)) {
                    totalCategoria++;
                    if (maquina.isDisponible()) {
                        disponiblesCategoria++;
                    }
                }
            }

            reporte += "\n" + categoria
                    + ": registradas = " + totalCategoria
                    + ", disponibles = " + disponiblesCategoria
                    + ", alquiladas = " + (totalCategoria - disponiblesCategoria);
        }

        reporte += "\n\nTotal de máquinas: " + maquinas.size();
        reporte += "\nDisponibles: " + disponibles;
        reporte += "\nAlquiladas: " + (maquinas.size() - disponibles);
        reporte += "\nIngresos acumulados: "
                + String.format(Locale.US, "Q%.2f", ingresosAcumulados);
        return reporte;
    }
}
