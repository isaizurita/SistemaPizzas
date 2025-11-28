package modelo;

/**
 * Interface del Builder para la construcción de pizzas.
 */
public interface PizzaBuilder 
    {
        void buildMasa(String tipoMasa);
        void buildSalsa(String tipoSalsa);
        void buildOrilla(String tipoOrilla);
        void buildIngrediente(String tipoIngrediente);
        Pizza getPizza();
    }