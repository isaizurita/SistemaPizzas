package vista;

/**
 * Define el contrato para las pantallas que deben reaccionar a cambios en los pedidos.
 * Implementación del patrón Observer (lado del Suscriptor).
 */
public interface Observador {

    /**
     * Se ejecuta automáticamente cuando un Pedido cambia de estado en el Modelo.
     * * @param idOrden      El número o ID de la orden (ej. "105").
     * @param nuevoEstado  El estado al que cambió (ej. "Horneando", "Listo").
     * @param cliente      El nombre del cliente asociado.
     * @param detalles     Descripción de la pizza (útil para la cocina, ej. "Grande, Pepperoni").
     */
    void actualizarOrden(String idOrden, String nuevoEstado, String cliente, String detalles);
}