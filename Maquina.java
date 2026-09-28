import java.util.Locale;

// Clase base: reúne los datos que tienen todas las máquinas.
public abstract class Maquina {
    private String codigoInventario;
    private String marca;
    private String modelo;
    private double tarifaDiaria;
    private boolean disponible;

    public Maquina(String codigo, String marca, String modelo, double tarifa) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo no puede estar vacío.");
        }
        if (!Double.isFinite(tarifa) || tarifa <= 0) {
            throw new IllegalArgumentException("La tarifa debe ser un número positivo.");
        }

        this.codigoInventario = codigo.trim();
        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.tarifaDiaria = tarifa;
        this.disponible = true;
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    protected double getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void marcarAlquilada() {
        disponible = false;
    }

    public void marcarDisponible() {
        disponible = true;
    }

    public String obtenerDescripcion() {
        String estado = disponible ? "Disponible" : "Alquilada";
        return "Código: " + codigoInventario
                + "\nCategoría: " + getTipo()
                + "\nMarca: " + marca
                + "\nModelo: " + modelo
                + "\nTarifa diaria: " + String.format(Locale.US, "Q%.2f", tarifaDiaria)
                + "\nEstado: " + estado
                + "\n" + obtenerDetalles();
    }

    // Cada subclase implementa su propia fórmula de cobro.
    public abstract double calcularCosto(int dias);

    public abstract String getTipo();

    protected abstract String obtenerDetalles();
}
