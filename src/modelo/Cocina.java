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
                new Thread(() -> procesarPedidos(pedido)).start();;
            }

        /**
         * Simula el ciclo de vida de preparación de los pedidos en cola.
         */
        private void procesarPedidos(Pedido p) 
            {
                try {
                    //Tiempo en "En Preparación"
                    Thread.sleep(5000); // 5 segundos
                    p.avanzarEstado(); // Pasa a Horneando

                    //Tiempo en "Horneando"
                    Thread.sleep(7000); // 7 segundos
                    p.avanzarEstado(); // Pasa a Listo

                    //Tiempo en "Listo para Entrega"
                    Thread.sleep(5000); // 5 segundos
                    p.avanzarEstado(); // Pasa a Entregado (y desaparece de toda pantalal)

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }