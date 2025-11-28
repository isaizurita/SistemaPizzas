package modelo;

/**
 * Clase principal para probar la integración de todos los patrones del Modelo.
 */
public class Main 
    {
        public static void main(String[] args) 
            {
                System.out.println("=== SISTEMA DE PIZZAS PERSONALIZADAS (DEMO MODELO) ===\n");

                // 1. Configuración del Builder para crear una pizza
                PizzaBuilder builder = new PizzaPersonalizadaBuilder();
                
                builder.buildMasa("coliflor");
                builder.buildSalsa("bbq");
                builder.buildOrilla("sin orilla");
                
                // Agregamos ingredientes vía Factory (interna en el builder)
                builder.buildIngrediente("Tocino");
                builder.buildIngrediente("Queso Extra");
                builder.buildIngrediente("Pimientos");

                Pizza pizzaTerminada = builder.getPizza();
                System.out.println("Pizza construida: " + pizzaTerminada.getDescripcion());
                System.out.println("Costo calculado: $" + pizzaTerminada.calcularCosto());

                // 2. Creación del Pedido
                Pedido pedidoJuan = new Pedido(pizzaTerminada, "Juan Pérez");

                // 3. Simulación de la Pantalla de Seguimiento (Observer)
                Observador pantallaCocina = new Observador() 
                    {
                        @Override
                        public void actualizar(String estado, String desc) 
                            {
                                System.out.println(">> [NOTIFICACIÓN UI] Estado actualizado a: " + estado.toUpperCase());
                            }
                    };
                
                pedidoJuan.registrarObservador(pantallaCocina);

                // 4. Proceso de Pago (Proxy -> Adapter)
                ServicioPago sistemaPago = new ProxyPago();
                boolean pagado = sistemaPago.procesarPago(pedidoJuan.getCostoFinal());

                if (pagado) 
                    {
                        System.out.println("\nPago exitoso. Enviando orden a cocina...\n");
                        
                        // 5. Envío a Cocina (Singleton y State automático)
                        Cocina cocina = Cocina.getInstancia();
                        cocina.recibirPedido(pedidoJuan);
                    } 
                else 
                    {
                        System.out.println("Error en el pago. Pedido cancelado.");
                    }
                
                System.out.println("\n=== FIN DE LA DEMO ===");
            }
    }