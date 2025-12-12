package controlador;

import java.util.List;
import javafx.application.Platform;
import modelo.*;
import vista.Observador;
import vista.PantallaCocina;
import vista.PantallaExterna;

public class ControladorGeneral implements Observador 
    {
        private PantallaCocina vistaCocina;
        private PantallaExterna vistaExterna;
        private Cocina cocinaModelo;

        public ControladorGeneral(PantallaCocina vistaCocina, PantallaExterna vistaExterna) 
            {
                this.vistaCocina = vistaCocina;
                this.vistaExterna = vistaExterna;
                this.cocinaModelo = Cocina.getInstancia();
            }

        // Usamos un hilo separado para no congelar la UI mientras la cocina procesa
        public boolean procesarNuevaOrden(String idOrden, String cliente, String masa, String salsa, String queso, String orilla, List<String> ingredientes, String metodoPago, double montoRecibido) 
            {
                // Construcción de la Pizza
                PizzaBuilder builder = new PizzaPersonalizadaBuilder();
                
                builder.buildMasa(masa);
                builder.buildSalsa(salsa);
                builder.buildQueso(queso);
                builder.buildOrilla(orilla);
                
                for (String ing : ingredientes) 
                    {
                        builder.buildIngrediente(ing);
                    }
                
                Pizza pizza = builder.getPizza();

                // Creación del Pedido
                Pedido nuevoPedido = new Pedido(idOrden, pizza, cliente);
                
                // "Suscripción" del observador (pantallas)
                nuevoPedido.registrarObservador(this);

                // Procesamiento del Pago
                ServicioPago servicioPago;
                if (metodoPago.equalsIgnoreCase("TARJETA")) 
                    {
                        servicioPago = new ProxyPago();
                    } 
                else 
                    {
                        servicioPago = new PagoEfectivo();
                    }

                boolean pagoExitoso = servicioPago.procesarPago(nuevoPedido.getCostoFinal());

                if (pagoExitoso) 
                    {
                        System.out.println("Controlador: Pago aprobado. Enviando orden " + idOrden + " a cocina...");
                        new Thread(() -> cocinaModelo.recibirPedido(nuevoPedido)).start();
                        return true;
                    } 
                else 
                    {
                        return false;
                    }
            }

        @Override
        public void actualizar(String idOrden, String estado, String descripcionPedido) 
            {
                Platform.runLater(() -> 
                    {
                        if (vistaCocina != null) 
                            {
                                vistaCocina.actualizarTarjeta(idOrden, descripcionPedido, estado);
                            }

                        if (vistaExterna != null) 
                            {
                                vistaExterna.actualizarEstadoOrden(idOrden, descripcionPedido, estado);
                            }
                    });
            }
    }