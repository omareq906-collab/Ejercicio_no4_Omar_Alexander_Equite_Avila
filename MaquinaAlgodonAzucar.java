public class MaquinaAlgodonAzucar extends Maquina {
    private int potenciaVatios;

    public MaquinaAlgodonAzucar(String codigo, String marca, String modelo,
                               double tarifa, int potencia) {
        super(codigo, marca, modelo, tarifa);
        if (potencia <= 0) {
            throw new IllegalArgumentException("La potencia debe ser un entero positivo.");
        }
        this.potenciaVatios = potencia;
    }

    @Override
    public double calcularCosto(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días deben ser mayores que cero.");
        }

        double total = getTarifaDiaria() * dias;
        if (potenciaVatios > 1000) {
            total += 60; // Cargo único por todo el alquiler.
        }
        return total;
    }

    @Override
    public String getTipo() {
        return "Algodón de azúcar";
    }

    @Override
    protected String obtenerDetalles() {
        return "Potencia: " + potenciaVatios + " W";
    }
}
