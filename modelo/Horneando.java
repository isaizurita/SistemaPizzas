package modelo;

/**
 * Representa que la pizza está cocinándose en el horno.
 * Es un estado de espera antes de que salga.
 */
class Horneando implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new ListoParaEntrega());
            }

        @Override
        public String getNombreEstado() { return "Horneando"; }
    }