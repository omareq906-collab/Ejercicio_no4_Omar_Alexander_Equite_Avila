public class MaquinaPalomitas extends Maquina {
    private int porcionesPorHora;
    private boolean carritoIntegrado;

    public MaquinaPalomitas(String codigo, String marca, String modelo,
                           double tarifa, int porciones, boolean carrito) {
        super(codigo, marca, modelo, tarifa);
        if (porciones <= 0) {
            throw new IllegalArgumentException("Las porciones por hora deben ser positivas.");
        }
        this.porcionesPorHora = porciones;
        this.carritoIntegrado = carrito;
    }

    @Override
    public double calcularCosto(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días deben ser mayores que cero.");
        }

        double tarifa = getTarifaDiaria();
        if (carritoIntegrado) {
            tarifa += 40; // Q40 adicionales por cada día.
        }
        return tarifa * dias;
    }

    @Override
    public String getTipo() {
        return "Palomitas";
    }

    @Override
    protected String obtenerDetalles() {
        return "Porciones por hora: " + porcionesPorHora
                + "\nCarrito integrado: " + (carritoIntegrado ? "Sí" : "No");
    }
}
