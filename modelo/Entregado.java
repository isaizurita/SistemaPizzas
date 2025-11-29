package modelo;

/**
 * Estado final. El cliente ya tiene su pedido.
 * Aquí termina el ciclo y ya no debería haber cambios.
 * *  @see EstadoPedido
 */
class Entregado implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                // Estado final, no hay otrro estado después de este
                System.out.println("El pedido ya ha sido entregado.");
            }

        @Override
        public String getNombreEstado() { return "Entregado"; }
    }