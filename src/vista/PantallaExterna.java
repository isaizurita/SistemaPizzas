package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaExterna {
    private PantallaMenu app;
    private SplitPane view;

    public PantallaExterna(PantallaMenu app) {
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new SplitPane();
        
        //Panel izquierdo
        VBox leftPane = new VBox(30); 
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #0f192b;");

        try {
            Image imgLogo = new Image(getClass().getResourceAsStream("/imagenes/logo_pizza.jpg"));
            ImageView imgView = new ImageView(imgLogo);
            imgView.setFitWidth(300);
            imgView.setPreserveRatio(true);
            leftPane.getChildren().add(imgView);
        } catch (Exception e) {
            Label placeholder = new Label("PizzaFactory");
            placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
            leftPane.getChildren().add(placeholder);
        }

        // Botón de regreso
        Button btnVolver = new Button("⬅ Volver");
        
        btnVolver.setStyle(
            "-fx-background-color: #FFA500;" +   
            "-fx-text-fill: #152238;" +         
            "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 16px;" +
            "-fx-background-radius: 25;" +      
            "-fx-padding: 10 30;" +             
            "-fx-cursor: hand;" +               
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);" 
        );
        
        // Efecto Hover
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle(
            "-fx-background-color: #FFD700;" + 
            "-fx-text-fill: #152238;" +
            "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 16px;" +
            "-fx-background-radius: 25;" +
            "-fx-padding: 10 30;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 5);"
        ));

        btnVolver.setOnMouseExited(e -> btnVolver.setStyle(
            "-fx-background-color: #FFA500;" + 
            "-fx-text-fill: #152238;" +
            "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 16px;" +
            "-fx-background-radius: 25;" +
            "-fx-padding: 10 30;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
        ));

        btnVolver.setOnAction(e -> app.mostrarMenuPrincipal());
        leftPane.getChildren().add(btnVolver);

        // Panel de estados
        VBox rightPane = new VBox();
        rightPane.setStyle("-fx-background-color: #eef2f5;"); 
        
        // Cabecera
        HBox headerList = new HBox();
        headerList.setStyle("-fx-background-color: #23395d; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        headerList.setAlignment(Pos.CENTER);
        Label statusTitle = new Label("ESTADO DE ÓRDENES");
        statusTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        headerList.getChildren().add(statusTitle);

        // Contenedor principal
        VBox statusContent = new VBox(40); 
        statusContent.setPadding(new Insets(30));
        
        // Sección de órdenes "Listas para recoger"
        VBox seccionListos = crearSeccionVisual(
            "¡LISTAS PARA RECOGER!", 
            new String[]{"101"}, 
            "#28a745", 
            true 
        );

        // Sección de órdenes "En espera"
        VBox seccionCocinando = crearSeccionVisual(
            "COCINANDO", 
            new String[]{"102", "103", "104", "105"}, 
            "#152238", 
            false
        );

        statusContent.getChildren().addAll(seccionListos, seccionCocinando);
        
        ScrollPane scroll = new ScrollPane(statusContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        rightPane.getChildren().addAll(headerList, scroll);

        view.getItems().addAll(leftPane, rightPane);
        view.setDividerPositions(0.35); 
    }

    /**
     * Crea toda la sección visual (Título + Contenedor de "cajitas")
     */
    private VBox crearSeccionVisual(String titulo, String[] ordenes, String colorTema, boolean destacado) {
        VBox seccion = new VBox(15);
        
        // Título de la sección
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + colorTema + "; -fx-font-family: 'Verdana';");
        
        // Contenedor de cada orden
        FlowPane contenedorCajitas = new FlowPane();
        contenedorCajitas.setHgap(20);
        contenedorCajitas.setVgap(20);
        
        for (String orden : ordenes) {
            StackPane tarjeta = crearTarjetaOrden(orden, colorTema, destacado);
            contenedorCajitas.getChildren().add(tarjeta);
        }

        seccion.getChildren().addAll(lblTitulo, contenedorCajitas);
        return seccion;
    }

    /**
     * Crea la "cajita" individual para cada número
     */
    private StackPane crearTarjetaOrden(String numero, String color, boolean rellenoSolido) {
        StackPane tarjeta = new StackPane();
        tarjeta.setPrefSize(180, 100); 
        
        String estiloCSS;
        Label lblNumero = new Label(numero);
        
        if (rellenoSolido) {
            // Estilo para "LISTO"
            estiloCSS = "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);";
            lblNumero.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        } else {
            // Estilo para "En espera"
            estiloCSS = "-fx-background-color: white;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 3;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);";
            lblNumero.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 42px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        }

        tarjeta.setStyle(estiloCSS);
        tarjeta.getChildren().add(lblNumero);
        
        return tarjeta;
    }

    public SplitPane getView() { return view; }
}