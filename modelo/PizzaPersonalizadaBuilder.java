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