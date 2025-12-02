package modelo;

/**
 * Interface para los observadores que desean recibir notificaciones del pedido.
 * Parte del patrón Observer.
 */
public interface Observador 
    {
        void actualizar(String estado, String descripcionPedido);
    }