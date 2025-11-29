package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase central que gestiona la información de la orden.
 * <p>Actúa como Contexto para el patrón State y como Sujeto para el patrón Observer.
 */
public class Pedido implements Sujeto 
    {
        private Pizza pizza;
        private EstadoPedido estadoActual;
        private List<Observador> observadores;
        private String nombreCliente;
        private double costoFinal;

        public Pedido(Pizza pizza, String nombreCliente) 
            {
                this.pizza = pizza;
                this.nombreCliente = nombreCliente;
                this.observadores = new ArrayList<>();
                // Estado inicial
                this.estadoActual = new EnPreparacion(); 
                this.costoFinal = pizza.calcularCosto();
            }

        /**
         * Avanza el estado del pedido al siguiente paso lógico.
         * Delega el cambio al objeto de estado actual.
         */
        public void avanzarEstado() 
            {
                this.estadoActual.siguiente(this);
                notificarObservadores();
            }

        public void setEstado(EstadoPedido nuevoEstado) 
            {
                this.estadoActual = nuevoEstado;
            }

        public String getEstadoNombre() 
            {
                return estadoActual.getNombreEstado();
            }

        public double getCostoFinal() 
            {
                return costoFinal;
            }

        public String getNombreCliente() 
            {
                return nombreCliente;
            }

        // Implementación del Sujeto para Observer
        @Override
        public void registrarObservador(Observador o) 
            {
                observadores.add(o);
            }

        @Override
        public void eliminarObservador(Observador o) 
            {
                observadores.remove(o);
            }

        @Override
        public void notificarObservadores() 
            {
                for (Observador o : observadores) 
                    {
                        o.actualizar(estadoActual.getNombreEstado(), pizza.getDescripcion());
                    }
            }
    }
