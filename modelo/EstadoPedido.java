package modelo;

/**
 * Interface que define el comportamiento de los estados del pedido.
 * Parte del patrón State.
 */
public interface EstadoPedido 
    {
        void siguiente(Pedido contexto);
        String getNombreEstado();
    }