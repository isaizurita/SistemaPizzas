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