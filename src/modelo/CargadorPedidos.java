package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CargadorPedidos 
    {
        public List<Pedido> cargarInfo(String rutaArchivo)
            {
                List<Pedido> pedidosCargados = new ArrayList<>();

                try(BufferedReader br = new BufferedReader(new FileReader(rutaArchivo)))
                    {
                        String linea;
                        br.readLine();

                        while ((linea = br.readLine()) != null)
                        {
                            if (linea.trim().isEmpty()) continue;

                            String[] datos = linea.split(",");

                            if(datos.length < 7) continue;

                            PizzaBuilder builder = new PizzaPersonalizadaBuilder();
                            builder.buildMasa(datos[2].trim());
                            builder.buildSalsa(datos[3].trim());
                            builder.buildQueso(datos[4].trim());
                            builder.buildOrilla(datos[5].trim());

                            String[] ings = datos[6].trim().split("-");
                            for(String ing : ings)
                            {
                                builder.buildIngrediente(ing);
                            }

                            Pizza pizzaReconstruida = builder.getPizza();

                            Pedido p = new Pedido(datos[0], pizzaReconstruida, datos[1].trim());

                            pedidosCargados.add(p);
                        }
                    } catch(IOException e)
                        {
                            System.err.println("Error al leer archivo: " + e.getMessage());
                        }
            
                return pedidosCargados;
            }
    }
