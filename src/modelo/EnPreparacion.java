package modelo;

/**
 * Representa el estado inicial del ciclo de vida de un pedido.
 * Significa que la cocina recibió la orden y está armando la pizza.
 * * @see EstadoPedido
 */
class EnPreparacion implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new Horneando());
            }

        @Override
        public String getNombreEstado() { return "En Preparación"; }
    }