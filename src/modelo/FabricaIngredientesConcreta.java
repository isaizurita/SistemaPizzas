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