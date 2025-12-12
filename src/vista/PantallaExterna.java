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

/**
 * Pantalla pública para los clientes.
 * <p>Muestra el estado de las órdenes divididas en dos áreas: "Cocinando" y "Listas para Recoger".
 */
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