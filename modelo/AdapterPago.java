package modelo;

/**
 * Adaptador que conecta nuestro sistema con el banco externo.
 * Traduce la llamada simple a la compleja del banco.
 */
public class AdapterPago implements ServicioPago 
    {
        private SistemaBancarioExterno bancoAPI;

        public AdapterPago() 
            {
                this.bancoAPI = new SistemaBancarioExterno();
            }

        @Override
        public boolean procesarPago(double monto) 
            {
                // Generamos un token simulado que en realidad es necesario para el banco
                String tokenSeguridad = "TOKEN-SEC-" + System.currentTimeMillis();
                return bancoAPI.realizarTransaccion(monto, tokenSeguridad);
            }
    }