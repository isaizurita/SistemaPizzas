package modelo;

/**
 * Simulación de una API bancaria externa (legacy o de terceros).
 * Esta clase no implementa nuestra interface directamente.
 */
class SistemaBancarioExterno 
    {
        public boolean realizarTransaccion(double cantidad, String token) 
            {
                System.out.println("[BancoAPI] Procesando transacción de $" + cantidad + " con token " + token);
                return true; // Simulación de éxito
            }
    }
