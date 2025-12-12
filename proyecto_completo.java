

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

import vista.Observador;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase central que gestiona la información de la orden.
 * <p>Actúa como Contexto para el patrón State y como Sujeto para el patrón Observer.
 */
public class Pedido implements Sujeto 
    {
        private String id;
        private Pizza pizza;
        private EstadoPedido estadoActual;
        private List<Observador> observadores;
        private String nombreCliente;
        private double costoFinal;

        public Pedido(String id, Pizza pizza, String nombreCliente) 
            {
                this.id = id;
                this.pizza = pizza;
                this.nombreCliente = nombreCliente;
                this.observadores = new ArrayList<>();
                this.estadoActual = new EnPreparacion(); 
                this.costoFinal = pizza.calcularCosto();
            }

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
        
        public String getId() 
            {
                return id;
            }

        // Implementación del sujeto para Observer
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
                        o.actualizar(this.id, estadoActual.getNombreEstado(), pizza.getDescripcion());
                    }
            }
    }

package modelo;

import vista.Observador; //Importamos la interfaz de la capa de la vista

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
 * <p>Actualizada con el catálogo completo de la interfaz gráfica.
 */
public class FabricaIngredientesConcreta extends FabricaIngredientes 
    {
        @Override
        public Ingrediente crearIngrediente(String tipo) 
            {
                if (tipo == null) return null;
                
                // Normalizamos entrada
                switch (tipo.toLowerCase()) 
                    {
                        // Carnes
                        case "pepperoni":
                            return new IngredienteConcreto("Pepperoni", 15.00);
                        case "jamón": //Abarcamos casos con o sin tilde
                        case "jamon":
                            return new IngredienteConcreto("Jamón", 15.00);
                        case "tocino":
                            return new IngredienteConcreto("Tocino", 20.00);
                        case "salchicha":
                            return new IngredienteConcreto("Salchicha", 15.00);
                        case "chorizo":
                            return new IngredienteConcreto("Chorizo", 15.00);
                        case "salami":
                            return new IngredienteConcreto("Salami", 15.00);
                        case "pollo":
                            return new IngredienteConcreto("Pollo", 15.00);
                        
                        case "pimientos":
                            return new IngredienteConcreto("Pimientos", 10.00);
                        case "champiñones":
                            return new IngredienteConcreto("Champiñones", 12.00);
                        case "albahacar":
                        case "albahaca":
                            return new IngredienteConcreto("Albahaca", 12.00);
                        case "cebolla":
                            return new IngredienteConcreto("Cebolla", 10.00);
                        case "maíz":
                        case "maiz":
                            return new IngredienteConcreto("Maíz", 10.00);
                        case "aceitunas":
                            return new IngredienteConcreto("Aceitunas", 12.00);

                        case "parmesano":
                            return new IngredienteConcreto("Parmesano", 15.00);
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
                while (!colaPedidos.isEmpty()) 
                    {
                        Pedido p = colaPedidos.poll();
                        
                        try 
                            {
                                // Simulación: Transición del estado En Preparación -> a Horneando
                                Thread.sleep(10000); // 10 segundos
                                p.avanzarEstado(); 

                                // Simulación: Transición de Horneando -> Listo
                                Thread.sleep(10000); 
                                p.avanzarEstado();

                                // Simulación: Transición de Listo -> Entregado
                                Thread.sleep(10000); 
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

package vista;

import controlador.ControladorGeneral;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PantallaMenu extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    private PantallaCocina pantallaCocina;
    private PantallaExterna pantallaExterna;
    private ControladorGeneral controlador;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PizzaFactory POS System");

        this.pantallaCocina = new PantallaCocina(this);
        this.pantallaExterna = new PantallaExterna(this);

        // Inicialización del Controlador
        this.controlador = new ControladorGeneral(pantallaCocina, pantallaExterna);

        initRootLayout();
        mostrarMenuPrincipal();
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #0f192b; -fx-padding: 15; -fx-border-color: #23395d; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label logoLbl = new Label("pizzaFactory"); 
        logoLbl.setStyle("-fx-font-family: 'Century Gothic'; -fx-font-weight: bold; -fx-font-size: 28px; -fx-text-fill: white;");
        
        Label welcomeLbl = new Label("| Bienvenido, isaizurita ");
        welcomeLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");

        // Reloj
        Label clockLbl = new Label();
        clockLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");
        
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoLbl, welcomeLbl, spacer, clockLbl);
        rootLayout.setTop(header);

        Scene scene = new Scene(rootLayout, 1000, 700);
        try {
            if (getClass().getResource("/style.css") != null)
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) { }
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void mostrarMenuPrincipal() {
        VBox menuView = new VBox(40);
        menuView.setAlignment(Pos.CENTER);
        menuView.setStyle("-fx-padding: 50; -fx-background-color: #0f192b;");

        // Logo
        try {
            Image imgLogo = new Image(getClass().getResourceAsStream("/imagenes/logo_pizza.jpg"));
            ImageView imgView = new ImageView(imgLogo);
            imgView.setFitHeight(220); 
            imgView.setPreserveRatio(true);
            Rectangle clip = new Rectangle(220, 220); 
            clip.setArcWidth(30); clip.setArcHeight(30);
            imgView.setClip(clip);
            imgView.setEffect(new DropShadow(30, Color.BLACK));
            menuView.getChildren().add(imgView);
        } catch (Exception e) {
            Label lblTitulo = new Label("PIZZA FACTORY");
            lblTitulo.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 60px; -fx-text-fill: white;");
            menuView.getChildren().add(lblTitulo);
        }

        HBox buttonsContainer = new HBox(40); 
        buttonsContainer.setAlignment(Pos.CENTER);

        Button btnOrden = crearBotonMenu("NUEVA\nORDEN");
        btnOrden.setOnAction(e -> mostrarNuevaOrden());

        Button btnCocina = crearBotonMenu("PANTALLA\nCOCINA");
        btnCocina.setOnAction(e -> mostrarCocina());

        Button btnCliente = crearBotonMenu("PANTALLA\nEXTERNA");
        btnCliente.setOnAction(e -> mostrarPantallaExterna());

        buttonsContainer.getChildren().addAll(btnOrden, btnCocina, btnCliente);
        menuView.getChildren().add(buttonsContainer);

        rootLayout.setCenter(menuView);
    }

    private Button crearBotonMenu(String texto) {
        Button btn = new Button(texto);
        btn.setTextAlignment(TextAlignment.CENTER);
        String estiloNormal = "-fx-background-color: #203354; -fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2e4a7d; -fx-border-radius: 15; -fx-border-width: 1; -fx-cursor: hand;";
        btn.setStyle(estiloNormal);
        btn.setPrefSize(220, 180); 
        btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #23395d; -fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 19px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #FFA500; -fx-border-radius: 15; -fx-border-width: 3; -fx-cursor: hand;");
            btn.setEffect(new DropShadow(25, Color.rgb(255, 165, 0, 0.5))); 
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(estiloNormal);
            btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        });
        return btn;
    }

    public void mostrarNuevaOrden() {
        // Creamos la pantalla de captura nueva inyectándole el controlador (inyección de instancias)
        PantallaCaptura orderView = new PantallaCaptura(this, controlador);
        rootLayout.setCenter(orderView.getView());
    }

    public void mostrarCocina() {
        rootLayout.setCenter(pantallaCocina.getView());
    }

    public void mostrarPantallaExterna() {
        rootLayout.setCenter(pantallaExterna.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package vista;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PantallaCocina {
    private PantallaMenu app;
    private BorderPane view;
    private VBox kanbanBoard;

    public PantallaCocina(PantallaMenu app){
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b;");
        
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button btnBack = new Button("⬅ Volver");
        btnBack.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-weight: bold; -fx-background-radius: 20;");
        btnBack.setOnAction(e -> app.mostrarMenuPrincipal());

        Label title = new Label("MONITOR DE COCINA");
        title.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(btnBack, title);
        view.setTop(header);

        kanbanBoard = new VBox(15);
        kanbanBoard.setPadding(new Insets(20));
        
        ScrollPane scroll = new ScrollPane(kanbanBoard);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        
        view.setCenter(scroll);
    }

    public void actualizarTarjeta(String idOrden, String descripcion, String estado) {
        String color = "#FFA500"; 
        if(estado.equalsIgnoreCase("Horneando")) color = "#d9534f";
        if(estado.equalsIgnoreCase("Listo para Entrega")) color = "#28a745";
        
        // Si ya se entregó la orden, la quitamos de la pantalla de cocina
        if(estado.equalsIgnoreCase("Entregado")) {
            Platform.runLater(() -> eliminarTarjeta(idOrden));
            return;
        }

        String finalColor = color;

        Platform.runLater(() -> {
            eliminarTarjeta(idOrden);
            agregarTicketVisual(idOrden, descripcion, estado, finalColor);
        });
    }

    private void eliminarTarjeta(String idOrden) {
        kanbanBoard.getChildren().removeIf(node -> 
            idOrden.equals(node.getUserData())
        );
    }

    private void agregarTicketVisual(String idOrden, String descripcion, String estado, String colorHex) {
        String titulo = "ORDEN " + idOrden;
        
        VBox ticket = new VBox(10);
        ticket.setPrefWidth(260);
        ticket.setPadding(new Insets(15));
        ticket.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        
        ticket.setUserData(idOrden); 

        Region colorBar = new Region();
        colorBar.setPrefHeight(8);
        colorBar.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 5 0 0;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #152238;");
        
        Label lblDetalle = new Label(descripcion);
        lblDetalle.setWrapText(true);
        lblDetalle.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333; -fx-font-size: 12px;"); 
        
        Label lblEstado = new Label(estado.toUpperCase());
        lblEstado.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 5; -fx-alignment: center;");
        lblEstado.setMaxWidth(Double.MAX_VALUE);
        lblEstado.setAlignment(Pos.CENTER);
        
        ticket.getChildren().addAll(colorBar, lblTitulo, new javafx.scene.control.Separator(), lblDetalle, new Region(), lblEstado);
        
        // Agregamos al principio de la lista
        kanbanBoard.getChildren().add(0, ticket);
    }

    public Pane getView() { return view; }
}

package vista;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaExterna {
    private PantallaMenu app;
    private SplitPane view;
    
    private FlowPane contenedorListos;
    private FlowPane contenedorCocinando;

    public PantallaExterna(PantallaMenu app) {
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new SplitPane();
        
        VBox leftPane = new VBox(30); 
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #0f192b;");

        try {
            Image imgLogo = new Image(getClass().getResourceAsStream("/imagenes/logo_pizza.jpg"));
            ImageView imgView = new ImageView(imgLogo);
            imgView.setFitWidth(300); imgView.setPreserveRatio(true);
            leftPane.getChildren().add(imgView);
        } catch (Exception e) {
            Label placeholder = new Label("PizzaFactory");
            placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
            leftPane.getChildren().add(placeholder);
        }

        Button btnVolver = new Button("⬅ Volver");
        btnVolver.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-weight: bold; -fx-background-radius: 25;");
        btnVolver.setOnAction(e -> app.mostrarMenuPrincipal());
        leftPane.getChildren().add(btnVolver);

        VBox rightPane = new VBox();
        rightPane.setStyle("-fx-background-color: #eef2f5;"); 
        
        HBox headerList = new HBox();
        headerList.setStyle("-fx-background-color: #23395d; -fx-padding: 20;");
        headerList.setAlignment(Pos.CENTER);
        Label statusTitle = new Label("ESTADO DE ÓRDENES");
        statusTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        headerList.getChildren().add(statusTitle);

        VBox statusContent = new VBox(40); 
        statusContent.setPadding(new Insets(30));
        
        contenedorListos = new FlowPane(); 
        contenedorListos.setHgap(20); contenedorListos.setVgap(20);
        
        contenedorCocinando = new FlowPane();
        contenedorCocinando.setHgap(20); contenedorCocinando.setVgap(20);

        statusContent.getChildren().add(crearSeccionEstructura("¡LISTAS PARA RECOGER!", "#28a745", contenedorListos));
        statusContent.getChildren().add(crearSeccionEstructura("COCINANDO", "#152238", contenedorCocinando));
        
        ScrollPane scroll = new ScrollPane(statusContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        rightPane.getChildren().addAll(headerList, scroll);
        view.getItems().addAll(leftPane, rightPane);
        view.setDividerPositions(0.35); 
    }

    private VBox crearSeccionEstructura(String titulo, String color, FlowPane contenedor) {
        VBox seccion = new VBox(15);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-font-family: 'Verdana';");
        seccion.getChildren().addAll(lblTitulo, contenedor);
        return seccion;
    }

    public void actualizarEstadoOrden(String idOrden, String descripcion, String estado) {
        Platform.runLater(() -> {
            contenedorListos.getChildren().removeIf(node -> idOrden.equals(node.getUserData()));
            contenedorCocinando.getChildren().removeIf(node -> idOrden.equals(node.getUserData()));

            if (estado.equalsIgnoreCase("Entregado")) {
                return;
            }

            StackPane tarjeta = crearTarjetaOrden(idOrden, estado);
            
            tarjeta.setUserData(idOrden);

            if (estado.equalsIgnoreCase("Listo para Entrega")) {
                contenedorListos.getChildren().add(tarjeta);
            } else {
                contenedorCocinando.getChildren().add(tarjeta);
            }
        });
    }

    private StackPane crearTarjetaOrden(String texto, String estado) {
        StackPane tarjeta = new StackPane();
        tarjeta.setPrefSize(180, 100); 
        
        String color = "#152238";
        boolean relleno = false;
        
        if(estado.equalsIgnoreCase("Listo para Entrega")) {
            color = "#28a745";
            relleno = true;
        }

        Label lbl = new Label(texto); 
        lbl.setWrapText(true);
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        if (relleno) {
            tarjeta.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        } else {
            tarjeta.setStyle("-fx-background-color: white; -fx-border-color: " + color + "; -fx-border-width: 3; -fx-background-radius: 15; -fx-border-radius: 15;");
            lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 42px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        }
        tarjeta.getChildren().add(lbl);
        return tarjeta;
    }

    public SplitPane getView() { return view; }
}

package vista;

/**
 * Define el contrato para las pantallas que deben reaccionar a cambios en los pedidos.
 * Implementación del patrón Observer (lado del Suscriptor).
 */
public interface Observador {

    /**
     * Se ejecuta automáticamente cuando un Pedido cambia de estado en el Modelo.
     * @param idOrden      El número de orden
     * @param estado       El estado al que cambió
     * @param detalles     Descripción de la pizza
     */
    void actualizar(String idOrden, String estado, String detalles);
}

package vista;

import controlador.ControladorGeneral;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; 
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PantallaCaptura {
    private PantallaMenu app;
    private ControladorGeneral controlador;
    private BorderPane view;
    private VBox ticketLayout;
    private Label totalLabel;
    
    private double costoBase = 100.00;
    private double costoExtras = 0.00;

    private TextField txtNombreCliente;
    private Label lblNumeroOrden;
    private String currentOrderId;
    private static int contadorOrdenes = 100;

    private String selMasa = "Tradicional";
    private String selSalsa = "Tomate";
    private String selQueso = "Mozzarella";
    private String selOrilla = "Normal";
    private List<String> selIngredientes = new ArrayList<>();

    public PantallaCaptura(PantallaMenu app, ControladorGeneral controlador) {
        this.app = app;
        this.controlador = controlador;
        this.currentOrderId = "" + contadorOrdenes;
        contadorOrdenes++;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b; -fx-padding: 20;");

        txtNombreCliente = new TextField();
        txtNombreCliente.setPromptText("Ingrese nombre...");
        txtNombreCliente.setPrefWidth(250);
        txtNombreCliente.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-background-radius: 10; -fx-background-color: white; -fx-text-fill: #152238;");

        view.setRight(crearPanelTicket());

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblNombre = new Label("Nombre Cliente:");
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        lblNumeroOrden = new Label("Número de orden: " + currentOrderId);
        lblNumeroOrden.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");

        topBar.getChildren().addAll(lblNombre, txtNombreCliente, new Separator(), lblNumeroOrden);
        view.setTop(topBar);

        VBox mainContent = new VBox(30); 
        mainContent.setPadding(new Insets(0, 20, 20, 0));
        mainContent.setStyle("-fx-background-color: transparent;");

        mainContent.getChildren().add(crearSeccionSimple("TIPO DE MASA", new String[]{"Tradicional", "Crujiente", "Sarten", "Delgada"}, "Masa"));
        mainContent.getChildren().add(crearSeccionSimple("SALSA BASE", new String[]{"Tomate", "BBQ", "Ranch", "Picante"}, "Salsa"));
        mainContent.getChildren().add(crearSeccionSimple("QUESOS", new String[]{"Mozzarella", "Parmesano", "Cheddar", "Sin Queso"}, "Queso"));
        mainContent.getChildren().add(crearSeccionSimple("ORILLA", new String[]{"Normal", "Rellena de Queso", "Sin Orilla"}, "Orilla"));
        mainContent.getChildren().add(crearSeccionIngredientesConImagenes());

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        view.setCenter(scrollPane);
    }

    private VBox crearSeccionSimple(String titulo, String[] items, String categoria) {
        VBox seccion = new VBox(10);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(15); flow.setVgap(15);
        ToggleGroup group = new ToggleGroup();

        for (String item : items) {
            ToggleButton btn = crearBotonOpcionSimple(item);
            btn.setToggleGroup(group);
            
            btn.setOnAction(e -> {
                actualizarTicketUnico(categoria, item);
                actualizarEstiloBotonSimple(btn);
            });
            btn.selectedProperty().addListener((obs, old, isSelected) -> actualizarEstiloBotonSimple(btn));
            
            if (item.equalsIgnoreCase(items[0])) {
                btn.setSelected(true);
                if(categoria.equals("Masa")) selMasa = item;
                if(categoria.equals("Salsa")) selSalsa = item;
                if(categoria.equals("Queso")) selQueso = item;
                if(categoria.equals("Orilla")) selOrilla = item;
            }
            flow.getChildren().add(btn);
        }
        seccion.getChildren().addAll(lblTitulo, flow);
        return seccion;
    }

    private VBox crearSeccionIngredientesConImagenes() {
        VBox seccion = new VBox(15);
        Label lblTitulo = new Label("INGREDIENTES EXTRA");
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(20); flow.setVgap(20);

        String[][] dataIngredientes = {
            {"Pepperoni", "15", "pepperoni.png"},
            {"Jamón", "15", "jamon.png"},
            {"Tocino", "20", "tocino.png"},
            {"Salchicha", "15", "salchicha.png"},
            {"Pimientos", "10", "pimientoVerde.png"},
            {"Champiñones", "12", "champinon.png"},
            {"Albahaca", "12", "albahacar.png"},
            {"Aceitunas", "12", "aceitunasNegras.png"},
            {"Cebolla", "10", "cebolla.png"},
            {"Maíz", "10", "maiz.png"},
            {"Queso Extra", "25", "parmesano.png"}
        };

        for (String[] data : dataIngredientes) {
            ToggleButton btn = crearTarjetaIngrediente(data[0], Double.parseDouble(data[1]), data[2]);
            btn.setOnAction(e -> {
                actualizarTicketIngrediente(data[0], Double.parseDouble(data[1]), btn.isSelected());
                actualizarEstiloTarjeta(btn);
            });
            flow.getChildren().add(btn);
        }
        seccion.getChildren().addAll(lblTitulo, flow);
        return seccion;
    }

    private ToggleButton crearBotonOpcionSimple(String texto) {
        ToggleButton btn = new ToggleButton(texto);
        btn.setPrefSize(140, 50);
        actualizarEstiloBotonSimple(btn);
        return btn;
    }

    private void actualizarEstiloBotonSimple(ToggleButton btn) {
        String base = "-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;";
        if (btn.isSelected()) {
            btn.setStyle(base + "-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-effect: dropshadow(three-pass-box, rgba(255,165,0,0.5), 10, 0, 0, 0);");
        } else {
            btn.setStyle(base + "-fx-background-color: #203354; -fx-text-fill: white; -fx-border-color: #2e4a7d; -fx-border-radius: 25;");
        }
    }

    private ToggleButton crearTarjetaIngrediente(String nombre, double precio, String imgFileName) {
        ToggleButton btn = new ToggleButton();
        btn.setPrefSize(140, 160); 
        btn.setContentDisplay(ContentDisplay.TOP); 
        
        VBox layout = new VBox(5);
        layout.setAlignment(Pos.CENTER);
        Node graphicNode;
        try {
            Image img = new Image(getClass().getResourceAsStream("/imagenes/" + imgFileName));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(70); imgView.setFitHeight(70); imgView.setPreserveRatio(true);
            graphicNode = imgView;
        } catch (Exception e) {
            Label lblNoImg = new Label("?");
            lblNoImg.setStyle("-fx-text-fill: gray; -fx-font-size: 40px;");
            graphicNode = lblNoImg;
        }
        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: inherit;");
        Label lblPrecio = new Label("+$" + String.format("%.2f", precio));
        lblPrecio.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px; -fx-text-fill: #a0a0a0;"); 

        layout.getChildren().addAll(graphicNode, lblNombre, lblPrecio);
        btn.setGraphic(layout);
        actualizarEstiloTarjeta(btn);
        return btn;
    }

    private void actualizarEstiloTarjeta(ToggleButton btn) {
        VBox layout = (VBox) btn.getGraphic();
        if (layout.getChildren().size() >= 3) {
            Label lblNombre = (Label) layout.getChildren().get(1);
            if (btn.isSelected()) {
                btn.setStyle("-fx-background-color: #23395d; -fx-border-color: #FFA500; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15;");
                lblNombre.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: #203354; -fx-border-color: transparent; -fx-background-radius: 15;");
                lblNombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
    }

    private Node crearPanelTicket() {
        VBox ticketPanel = new VBox(15);
        ticketPanel.setPadding(new Insets(20));
        ticketPanel.setPrefWidth(340); 
        ticketPanel.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 10;");

        Label tituloTicket = new Label("RESUMEN DE ORDEN");
        tituloTicket.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #152238;");
        Label lblClienteTicket = new Label("Cliente: ---");
        lblClienteTicket.textProperty().bind(javafx.beans.binding.Bindings.concat("Cliente: ", txtNombreCliente.textProperty()));

        ticketLayout = new VBox(8); 
        totalLabel = new Label("TOTAL: $100.00");
        totalLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #152238;");

        Button btnPagar = new Button("COBRAR"); 
        btnPagar.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20;");
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> mostrarModalPago());

        Button btnCancelar = new Button("Cancelar Orden"); 
        btnCancelar.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20;");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> app.mostrarMenuPrincipal());

        ticketPanel.getChildren().addAll(tituloTicket, lblClienteTicket, new Separator(), ticketLayout, new Separator(), totalLabel, btnPagar, btnCancelar);
        return new HBox(ticketPanel);
    }

    private void actualizarTicketUnico(String categoria, String item) {
        if(categoria.equals("Masa")) selMasa = item;
        if(categoria.equals("Salsa")) selSalsa = item;
        if(categoria.equals("Queso")) selQueso = item;
        if(categoria.equals("Orilla")) selOrilla = item;

        if (ticketLayout == null) return;
        ticketLayout.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().startsWith(categoria + ":"));
        Label l = new Label(categoria + ": " + item);
        l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333;");
        ticketLayout.getChildren().add(0, l); 
    }

    private void actualizarTicketIngrediente(String item, double precio, boolean agregando) {
        String textoItem = "+ " + item + " ($" + precio + ")";
        if (agregando) {
            selIngredientes.add(item);
            costoExtras += precio;
            Label l = new Label(textoItem);
            l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333;");
            ticketLayout.getChildren().add(l);
        } else {
            selIngredientes.remove(item);
            costoExtras -= precio;
            ticketLayout.getChildren().removeIf(node -> ((Label)node).getText().equals(textoItem));
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = costoBase + costoExtras;
        totalLabel.setText("TOTAL: $" + String.format("%.2f", total));
    }

    private void mostrarModalPago() {
        if(txtNombreCliente.getText().isEmpty()) {
            mostrarAlertaError("Faltan datos", "Ingrese el nombre del cliente.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("Total a Pagar: " + totalLabel.getText().replace("TOTAL: ", ""));
        
        ButtonType btnEfectivo = new ButtonType("Efectivo", ButtonBar.ButtonData.LEFT);
        ButtonType btnTarjeta = new ButtonType("Tarjeta", ButtonBar.ButtonData.LEFT);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEfectivo, btnTarjeta, btnCancelar);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnEfectivo) simularPagoEfectivo();
            else if (result.get() == btnTarjeta) simularPagoTarjeta();
        }
    }

    private void simularPagoEfectivo() {
        TextInputDialog td = new TextInputDialog();
        td.setTitle("Pago en Efectivo");
        td.setHeaderText("Ingrese monto recibido:");
        td.showAndWait().ifPresent(montoStr -> {
            try {
                double monto = Double.parseDouble(montoStr);
                // PASAMOS EL ID AL CONTROLADOR
                boolean exito = controlador.procesarNuevaOrden(
                    currentOrderId,
                    txtNombreCliente.getText(), selMasa, selSalsa, selQueso, selOrilla, selIngredientes,
                    "EFECTIVO", monto
                );

                if (exito) mostrarExitoOrden();
                else mostrarAlertaError("Pago rechazado", "Monto insuficiente o error de sistema.");

            } catch (NumberFormatException e) {
                mostrarAlertaError("Error", "Ingrese un número válido");
            }
        });
    }

    private void simularPagoTarjeta() {
        // Configuración de la ventana de carga del paog
        Alert processing = new Alert(Alert.AlertType.NONE);
        processing.setTitle("Terminal Bancaria");
        processing.setHeaderText("Conectando con el Servicio Bancario...");
        
        // Botón de seguridad
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        processing.getDialogPane().getButtonTypes().add(btnCancelar);
        
        ProgressBar pb = new ProgressBar(); 
        pb.setProgress(-1); 
        
        VBox content = new VBox(10, new Label("Validando fondos y seguridad..."), pb);
        content.setStyle("-fx-padding: 10;");
        processing.getDialogPane().setContent(content);
        
        processing.show();

        // Simulamos el tiempo de espera del banco
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        
        pause.setOnFinished(e -> {
            processing.setResult(btnCancelar);
            processing.close();

            javafx.application.Platform.runLater(() -> {
                boolean exito = false;
                try {
                    exito = controlador.procesarNuevaOrden(
                        currentOrderId,
                        txtNombreCliente.getText(), selMasa, selSalsa, selQueso, selOrilla, selIngredientes,
                        "TARJETA", 0.0
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (exito) {
                    mostrarExitoOrden(); // Esto muestra la alerta de éxito y regresa al menú
                } else {
                    mostrarAlertaError("Transacción Fallida", "La tarjeta fue rechazada por el banco.");
                }
            });
        });
        
        pause.play();
    }
    
    private void mostrarExitoOrden() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Orden Enviada");
        alert.setHeaderText("¡Éxito!");
        alert.setContentText("Orden enviada a cocina.");
        alert.showAndWait();
        app.mostrarMenuPrincipal();
    }

    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public Pane getView() { return view; }
}

