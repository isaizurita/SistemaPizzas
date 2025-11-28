package modelo;

/**
 * Interface común para el servicio de pagos.
 */
public interface ServicioPago 
    {
        boolean procesarPago(double monto);
    }