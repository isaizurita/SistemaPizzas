package vista;

/**
 * Define el contrato para las pantallas que deben reaccionar a cambios en los pedidos.
 * Implementación del patrón Observer (lado del Suscriptor).
 */
public interface Observador {

    /**
     * Se ejecuta automáticamente cuando un Pedido cambia de estado en el Modelo.
     * @param idOrden      El número de orden
     * @param estado       El estado al que cambió
     * @param detalles     Descripción de la pizza
     */
    void actualizar(String idOrden, String estado, String detalles);
}