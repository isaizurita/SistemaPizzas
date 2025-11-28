package modelo;

/**
 * Proxy de seguridad para validar el pago antes de llamar al adaptador.
 * Actúa como intermediario para validaciones previas.
 */
public class ProxyPago implements ServicioPago 
    {
        private ServicioPago servicioReal;

        public ProxyPago() 
            {
                // El proxy contiene al adaptador (que es el servicio real en nuestro contexto)
                this.servicioReal = new AdapterPago();
            }

        @Override
        public boolean procesarPago(double monto) 
            {
                // 1. Validaciones de seguridad previas
                if (monto <= 0) 
                    {
                        System.out.println("Error: Monto inválido.");
                        return false;
                    }

                System.out.println("[Proxy] Validando datos de la cuenta...");
                
                // 2. Delegamos al servicio real si pasa la validacion
                return servicioReal.procesarPago(monto);
            }
    }