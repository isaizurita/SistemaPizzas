

package modelo;

/**
 * Interface que define el comportamiento de los estados del pedido.
 * Parte del patrón State.
 */
public interface EstadoPedido 
    {
        void siguiente(Pedido contexto);
        String getNombreEstado();
    }

package modelo;

/**
 * Interface común para el servicio de pagos.
 */
public interface ServicioPago 
    {
        boolean procesarPago(double monto);
    }

package modelo;

/**
 * Representa el estado inicial del ciclo de vida de un pedido.
 * Significa que la cocina recibió la orden y está armando la pizza.
 * * @see EstadoPedido
 */
class EnPreparacion implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new Horneando());
            }

        @Override
        public String getNombreEstado() { return "En Preparación"; }
    }

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


package modelo;

/**
 * Interface del Sujeto (Observable) en el patrón Observer.
 */
public interface Sujeto 
    {
        void registrarObservador(Observador o);
        void eliminarObservador(Observador o);
        void notificarObservadores();
    }

package modelo;

/**
 * Estado final. El cliente ya tiene su pedido.
 * Aquí termina el ciclo y ya no debería haber cambios.
 * *  @see EstadoPedido
 */
class Entregado implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                // Estado final, no hay otrro estado después de este
                System.out.println("El pedido ya ha sido entregado.");
            }

        @Override
        public String getNombreEstado() { return "Entregado"; }
    }

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

package modelo;

/**
 * Implementación concreta de la fábrica.
 * <p>Encargada de instanciar los ingredientes disponibles en el sistema.
 */
public class FabricaIngredientesConcreta extends FabricaIngredientes 
    {
        @Override
        public Ingrediente crearIngrediente(String tipo) 
            {
                // 1. Normalizamos la entrada
                switch (tipo.toLowerCase()) 
                    {
                        case "pepperoni":
                            return new IngredienteConcreto("Pepperoni", 15.00);
                        case "jamon":
                            return new IngredienteConcreto("Jamón", 15.00);
                        case "tocino":
                            return new IngredienteConcreto("Tocino", 20.00);
                        case "pimientos":
                            return new IngredienteConcreto("Pimientos", 10.00);
                        case "aceitunas":
                            return new IngredienteConcreto("Aceitunas", 12.00);
                        case "champiñones":
                            return new IngredienteConcreto("Champiñones", 12.00);
                        case "albahacar":
                            return new IngredienteConcreto("Albahacar", 15.00);
                        case "queso extra":
                            return new IngredienteConcreto("Queso Extra", 25.00);
                        default:
                            return null;
                    }
            }
    }

package modelo;

import java.util.Arrays;
import java.util.List;

/**
 * Constructor concreto que ensambla la pizza paso a paso.
 * <p>Esta clase implementa el patrón Builder para encapsular la complejidad de
 * crear un objeto Pizza. Incluye validaciones internas para asegurar que
 * los tipos de masa, salsa y orilla sean permitidos por el negocio.
 */
public class PizzaPersonalizadaBuilder implements PizzaBuilder 
    {
        private Pizza pizza;
        private FabricaIngredientes fabrica;

        // Definimos las listas de opciones válidas (Reglas de Negocio)
        private static final List<String> masasValidas = Arrays.asList("tradicional", "crujiente", "de sarten", "delgada");
        private static final List<String> salsasValidas = Arrays.asList("tomate", "bbq", "ranch", "picante");
        private static final List<String> orillasValidas = Arrays.asList("normal", "rellena de queso", "sin orilla");
        private static final List<String> quesosValidos = Arrays.asList("mozzarella", "parmesano", "cheddar", "sin queso");

        /**
         * Inicializa el builder creando una nueva instancia de Pizza y la fábrica de ingredientes.
         */
        public PizzaPersonalizadaBuilder() 
            {
                this.pizza = new Pizza();
                // Delegamos la creación de ingredientes a la fábrica concreta
                this.fabrica = new FabricaIngredientesConcreta();
            }

        /**
         * Configura el tipo de masa de la pizza.
         * <p>Valida que la entrada exista en la lista de masas permitidas.
         * Si la masa no es válida, asigna "Tradicional" por defecto.
         * * @param tipoMasa Nombre de la masa (ej. "Crujiente").
         */
        @Override
        public void buildMasa(String tipoMasa) 
            {
                if (tipoMasa != null && masasValidas.contains(tipoMasa.toLowerCase())) 
                    {
                        pizza.setMasa(tipoMasa);
                    } 
                else 
                    {
                        System.out.println(">> Aviso: Masa '" + tipoMasa + "' no reconocida. Se usará 'Tradicional'.");
                        pizza.setMasa("Tradicional");
                    }
            }

        /**
         * Configura el tipo de salsa base.
         * <p>Valida que la entrada exista en la lista de salsas permitidas.
         * Si la salsa no es válida, asigna "Tomate" por defecto.
         * * @param tipoSalsa Nombre de la salsa (ej. "BBQ").
         */
        @Override
        public void buildSalsa(String tipoSalsa) 
            {
                if (tipoSalsa != null && salsasValidas.contains(tipoSalsa.toLowerCase())) 
                    {
                        pizza.setSalsa(tipoSalsa);
                    } 
                else 
                    {
                        System.out.println(">> Aviso: Salsa '" + tipoSalsa + "' no reconocida. Se usará 'Tomate'.");
                        pizza.setSalsa("Tomate");
                    }
            }

        /**
         * Configura el tipo de orilla.
         * <p>Valida que la entrada exista en la lista de orillas permitidas.
         * Si la orilla no es válida, asigna "Normal" por defecto.
         * * @param tipoOrilla Descripción de la orilla (ej. "Rellena de queso").
         */
        @Override
        public void buildOrilla(String tipoOrilla) 
            {
                if (tipoOrilla != null && orillasValidas.contains(tipoOrilla.toLowerCase())) 
                    {
                        pizza.setOrilla(tipoOrilla);
                    } 
                else 
                    {
                        System.out.println(">> Aviso: Orilla '" + tipoOrilla + "' no reconocida. Se usará 'Normal'.");
                        pizza.setOrilla("Normal");
                    }
            }

        /**
         * Configura el tipo de queso.
         * <p>Valida que la entrada exista en la lista de quesos válidos.
         * Si lel tipo de queso no es válido, asigna "Normal" por defecto.
         * * @param tipoQueso Descripción del tipo de queso.
         */
        @Override
        public void buildQueso(String tipoQueso) 
            {
                if (tipoQueso != null && quesosValidos.contains(tipoQueso.toLowerCase())) 
                    {
                        pizza.setTipoQueso(tipoQueso);
                    } 
                else 
                    {
                        System.out.println(">> Aviso: Queso '" + tipoQueso + "' no reconocido. Se usará 'Mozzarella'.");
                        pizza.setTipoQueso("Mozzarella");
                    }
            }

        /**
         * Agrega un ingrediente a la pizza utilizando la Fábrica.
         * <p>Este método convierte el String de solicitud en un objeto Ingrediente real.
         * Si la fábrica no reconoce el ingrediente, este se omite.
         * * @param tipoIngrediente Nombre del ingrediente a agregar.
         */
        @Override
        public void buildIngrediente(String tipoIngrediente) 
            {
                if (tipoIngrediente == null) return;

                // Delegamos la creación a la fábrica (Factory Method)
                Ingrediente ing = fabrica.crearIngrediente(tipoIngrediente);
                
                if (ing != null) 
                    {
                        pizza.agregarIngrediente(ing);
                    }
                else
                    {
                        System.out.println(">> Aviso: El ingrediente '" + tipoIngrediente + "' no está disponible en inventario.");
                    }
            }

        /**
         * Finaliza la construcción y devuelve el producto terminado.
         * * @return La instancia de Pizza configurada.
         */
        @Override
        public Pizza getPizza() 
            {
                return this.pizza;
            }
    }

package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que representa el producto complejo a construir.
 * Contiene atributos como masa, salsa y la lista de ingredientes.
 */
public class Pizza 
    {
        private String masa;
        private String salsa;
        private String orilla;
        private String tipoQueso;
        private List<Ingrediente> ingredientes;

        public Pizza() 
            {
                this.ingredientes = new ArrayList<>();
            }

        // Setters para el Builder
        public void setMasa(String masa) { this.masa = masa; }
        public void setSalsa(String salsa) { this.salsa = salsa; }
        public void setOrilla(String orilla) { this.orilla = orilla; }
        public void setTipoQueso(String tipoQueso) { this.tipoQueso = tipoQueso;}

        public void agregarIngrediente(Ingrediente ingrediente) 
            {
                this.ingredientes.add(ingrediente);
            }

        /**
         * Calcula el costo total de la pizza sumando base + ingredientes.
         * @return Costo total en formato double.
         */
        public double calcularCosto() 
            {
                // Costo base por masa/tamaño
                double total = 100.00; 
                
                for (Ingrediente ing : ingredientes) 
                    {
                        total += ing.getCosto();
                    }
                return total;
            }

        public String getDescripcion() 
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\nTipo de masa: ").append(masa)
                  .append("\nTipo de salsa: ").append(salsa)
                  .append("\nTipo de orilla: ").append(orilla)
                  .append("\nTipo de queso: ").append(tipoQueso)
                  .append("\nIngredientes: ");
                
                if (ingredientes.isEmpty()) sb.append("Solo queso base.");
                
                for (Ingrediente ing : ingredientes) 
                    {
                        sb.append(ing.getNombre()).append(", ");
                    }
                return sb.toString();
            }
    }

package modelo;

/**
 * Implementación concreta de un ingrediente.
 */
public class IngredienteConcreto extends Ingrediente 
    {
        public IngredienteConcreto(String nombre, double costo) 
            {
                super(nombre, costo);
            }
    }

package modelo;

/**
 * Representa un ingrediente genérico que puede ser agregado a la pizza.
 * <p>Esta clase abstracta es el producto que genera la {@link FabricaIngredientes}.
 */
public abstract class Ingrediente 
    {
        protected String nombre;
        protected double costo;

        public Ingrediente(String nombre, double costo) 
            {
                this.nombre = nombre;
                this.costo = costo;
            }

        public String getNombre() 
            {
                return nombre;
            }

        public double getCosto() 
            {
                return costo;
            }
    }

package modelo;

/**
 * Interface del Builder para la construcción de pizzas.
 */
public interface PizzaBuilder 
    {
        void buildMasa(String tipoMasa);
        void buildSalsa(String tipoSalsa);
        void buildOrilla(String tipoOrilla);
        void buildQueso(String tipoQueso);
        void buildIngrediente(String tipoIngrediente);
        Pizza getPizza();
    }

package modelo;

/**
 * Clase principal para probar la integración de todos los patrones del Modelo.
 */
public class Main 
    {
        public static void main(String[] args) 
            {
                System.out.println("\n*** SISTEMA DE PIZZAS PERSONALIZADAS (CLASE DE PRUEBA) ***");

                // 1. Configuración del Builder para crear una pizza
                PizzaBuilder builder = new PizzaPersonalizadaBuilder();
                
                builder.buildMasa("de sarten");
                builder.buildSalsa("bbq");
                builder.buildOrilla("sin orilla");
                builder.buildQueso("mozzarella");
                
                // Agregamos ingredientes vía Factory (interna en el builder)
                builder.buildIngrediente("Tocino");
                builder.buildIngrediente("Queso Extra");
                builder.buildIngrediente("Pimientos");
                builder.buildIngrediente("Champiñones");
                builder.buildIngrediente("Albahacar");

                Pizza pizzaTerminada = builder.getPizza();
                System.out.println("\nPizza construida: " + pizzaTerminada.getDescripcion());
                System.out.println("\nCosto calculado: $" + pizzaTerminada.calcularCosto());

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

                // 4. Selección de Método de Pago
                ServicioPago sistemaPago;
                
                String metodoSeleccionado = "efectivo"; 

                if (metodoSeleccionado.equalsIgnoreCase("tarjeta")) 
                    {
                        // Usa el Proxy -> Adapter -> Banco (con validación estricta de proxy + adapter)
                        sistemaPago = new ProxyPago();
                    } 
                else 
                    {
                        // Usa la clase simple (cobro directo sin validación bancaria)
                        sistemaPago = new PagoEfectivo();
                    }

                // Procesamos el pago independientemente del método (polimorifsmo)
                boolean pagado = sistemaPago.procesarPago(pedidoJuan.getCostoFinal());

                if (pagado) 
                    {
                        System.out.println("\nPago exitoso. Enviando orden a cocina...\n");
                        
                        // 5. Envío a Cocina (Singleton y State automático)
                        // Nota: La notificación de "EN PREPARACIÓN" ya se hace dentro de Cocina.recibirPedido
                        Cocina cocina = Cocina.getInstancia();
                        cocina.recibirPedido(pedidoJuan);
                    } 
                else 
                    {
                        System.out.println("Error en el pago. Pedido cancelado.");
                    }
            }
    }

package modelo;

/**
 * Clase base para la fábrica de ingredientes.
 * Nos deja pedir ingredientes por nombre sin preocuparnos de qué clase exacta usar.
 */
public abstract class FabricaIngredientes 
    {
        public abstract Ingrediente crearIngrediente(String tipo);
    }

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

package modelo;

/**
 * Representa que la pizza está cocinándose en el horno.
 * Es un estado de espera antes de que salga.
 */
class Horneando implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new ListoParaEntrega());
            }

        @Override
        public String getNombreEstado() { return "Horneando"; }
    }

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

package modelo;

/**
 * La pizza ya está cocinada y empacada.
 * Solo espera a que el cliente la recoja.
 */
class ListoParaEntrega implements EstadoPedido 
    {
        @Override
        public void siguiente(Pedido contexto) 
            {
                contexto.setEstado(new Entregado());
            }

        @Override
        public String getNombreEstado() { return "Listo para Entrega"; }
    }

package modelo;

/**
 * Interface para los observadores que desean recibir notificaciones del pedido.
 * Parte del patrón Observer.
 */
public interface Observador 
    {
        void actualizar(String estado, String descripcionPedido);
    }

