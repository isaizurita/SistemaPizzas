package modelo;

/**
 * La pizza ya está cocinada y empacada.
 * Solo espera a que el cliente la recoja.
 */
class ListoParaEntrega implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new Entregado());
            }

        @Override
        public String getNombreEstado() { return "Listo para Entrega"; }
    }