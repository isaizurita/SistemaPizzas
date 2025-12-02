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