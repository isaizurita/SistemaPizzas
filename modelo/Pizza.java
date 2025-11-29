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