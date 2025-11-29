package modelo;

/**
 * Implementación simple para pagos en efectivo.
 * No requiere validaciones de seguridad ni conexión con bancos externos.
 */
public class PagoEfectivo implements ServicioPago 
    {
        @Override
        public boolean procesarPago(double monto) 
            {
                // Solo se confirma la recepción del dinero
                System.out.println("[Caja] Pago en efectivo recibido por la cantidad de: $" + monto);
                return true; 
            }
    }