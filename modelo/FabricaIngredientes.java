package modelo;

/**
 * Clase base para la fábrica de ingredientes.
 * Nos deja pedir ingredientes por nombre sin preocuparnos de qué clase exacta usar.
 */
public abstract class FabricaIngredientes 
    {
        public abstract Ingrediente crearIngrediente(String tipo);
    }