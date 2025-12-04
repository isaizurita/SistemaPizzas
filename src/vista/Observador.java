package vista;

/**
 * Define el contrato para las pantallas que deben reaccionar a cambios en los pedidos.
 * Implementación del patrón Observer (lado del Suscriptor).
 */
public interface Observador {

    /**
     * Se ejecuta automáticamente cuando un Pedido cambia de estado en el Modelo.
     * @param idOrden      El número de orden
     * @param nuevoEstado  El estado al que cambió como "Horneando", "listo para enrega"
     * @param cliente      El nombre del cliente asociado a la orden
     * @param detalles     Descripción de la pizza.
     */
    void actualizarOrden(String idOrden, String nuevoEstado, String cliente, String detalles);
}