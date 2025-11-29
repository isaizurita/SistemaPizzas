package modelo;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Singleton que gestiona la cola de pedidos y simula la preparación.
 * Garantiza una única instancia gestionando la sincronización de órdenes.
 */
public class Cocina 
    {
        private static Cocina instancia;
        private Queue<Pedido> colaPedidos;

        private Cocina() 
            {
                this.colaPedidos = new LinkedList<>();
            }

        public static synchronized Cocina getInstancia() 
            {
                if (instancia == null) 
                    {
                        instancia = new Cocina();
                    }
                return instancia;
            }

        public void recibirPedido(Pedido pedido) 
            {
                System.out.println("Cocina: Pedido recibido de " + pedido.getNombreCliente());
                pedido.notificarObservadores();
                colaPedidos.add(pedido);
                procesarPedidos();
            }

        /**
         * Simula el ciclo de vida de preparación de los pedidos en cola.
         */
        private void procesarPedidos() 
            {
                // En un sistema real esto sería un hilo separado, aquí lo simulamos secuencial
                while (!colaPedidos.isEmpty()) 
                    {
                        Pedido p = colaPedidos.poll();
                        
                        try 
                            {
                                // Simulación: Transición del estado En Preparación -> a Horneando
                                Thread.sleep(1000); 
                                p.avanzarEstado(); 

                                // Simulación: Transición de Horneando -> Listo
                                Thread.sleep(1000); 
                                p.avanzarEstado();

                                // Simulación: Transición de Listo -> Entregado
                                Thread.sleep(1000); 
                                p.avanzarEstado();

                            } catch (InterruptedException e) 
                            {
                                e.printStackTrace();
                            }
                    }
            }
    }