import java.util.Locale;

public class FuenteChocolate extends Maquina {
    private double capacidadKg;

    public FuenteChocolate(String codigo, String marca, String modelo,
                           double tarifa, double capacidad) {
        super(codigo, marca, modelo, tarifa);
        if (!Double.isFinite(capacidad) || capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número positivo.");
        }
        this.capacidadKg = capacidad;
    }

    @Override
    public double calcularCosto(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días deben ser mayores que cero.");
        }

        // Q20 por cada kilogramo de capacidad máxima y por cada día.
        return (getTarifaDiaria() + 20 * capacidadKg) * dias;
    }

    @Override
    public String getTipo() {
        return "Fuente de chocolate";
    }

    @Override
    protected String obtenerDetalles() {
        return String.format(Locale.US, "Capacidad máxima: %.2f kg", capacidadKg);
    }
}
