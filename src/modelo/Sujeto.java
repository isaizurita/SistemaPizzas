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