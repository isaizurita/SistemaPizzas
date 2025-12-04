package vista;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PantallaMenu extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PizzaFactory POS System");

        initRootLayout();
        mostrarMenuPrincipal(); // Llamamos directamente al método interno
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #0f192b; -fx-padding: 15; -fx-border-color: #23395d; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label logoLbl = new Label("pizzaFactory"); 
        logoLbl.setStyle("-fx-font-family: 'Century Gothic'; -fx-font-weight: bold; -fx-font-size: 28px; -fx-text-fill: white;");
        
        Label welcomeLbl = new Label("|  Bienvenido, isaizurita");
        welcomeLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");

        // Reloj
        Label clockLbl = new Label();
        clockLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");
        
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Espaciador para "empujar" el reloj a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoLbl, welcomeLbl, spacer, clockLbl);
        rootLayout.setTop(header);

        Scene scene = new Scene(rootLayout, 900, 600);
        try {
            if (getClass().getResource("/style.css") != null)
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) { /* Ignorar si no hay css */ }
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    // Métodos para la creación del menú principal
    public void mostrarMenuPrincipal() {
        VBox menuView = new VBox(40);
        menuView.setAlignment(Pos.CENTER);
        
        menuView.setStyle("-fx-padding: 50; -fx-background-color: #0f192b;");

        // Logo de pizzaFactory
        try {
            Image imgLogo = new Image(getClass().getResourceAsStream("/imagenes/logo_pizza.jpg"));
            ImageView imgView = new ImageView(imgLogo);
            
            imgView.setFitHeight(220); 
            imgView.setPreserveRatio(true);
            
            Rectangle clip = new Rectangle(220, 220); 
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            imgView.setClip(clip);

            imgView.setEffect(new DropShadow(30, Color.BLACK));

            menuView.getChildren().add(imgView);

        } catch (Exception e) {
            // Fallback si no se encuentra la imagen
            Label lblTitulo = new Label("PIZZA FACTORY");
            lblTitulo.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 60px; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");
            menuView.getChildren().add(lblTitulo);
        }

        // Contenedfor de botones
        HBox buttonsContainer = new HBox(40); 
        buttonsContainer.setAlignment(Pos.CENTER);

        Button btnOrden = crearBotonMenu("NUEVA\nORDEN");
        btnOrden.setOnAction(e -> mostrarNuevaOrden());

        Button btnCocina = crearBotonMenu("PANTALLA\nCOCINA");
        btnCocina.setOnAction(e -> mostrarCocina());

        Button btnCliente = crearBotonMenu("PANTALLA\nEXTERNA");
        btnCliente.setOnAction(e -> mostrarPantallaExterna());

        buttonsContainer.getChildren().addAll(btnOrden, btnCocina, btnCliente);
        menuView.getChildren().add(buttonsContainer);

        rootLayout.setCenter(menuView);
    }

    /**
     * Fábrica de botones estilo tarjeta
     */
    private Button crearBotonMenu(String texto) {
        Button btn = new Button(texto);
        btn.setTextAlignment(TextAlignment.CENTER);
        
        String estiloNormal = 
            "-fx-background-color: #203354;" + 
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Verdana', sans-serif;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #2e4a7d;" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;";

        btn.setStyle(estiloNormal);
        btn.setPrefSize(220, 180); 
        btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #23395d;" + 
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Verdana', sans-serif;" +
                "-fx-font-size: 19px;" + 
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #FFA500;" + // Borde Naranja
                "-fx-border-radius: 15;" +
                "-fx-border-width: 3;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(new DropShadow(25, Color.rgb(255, 165, 0, 0.5))); 
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(estiloNormal);
            btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        });

        return btn;
    }

    public void mostrarNuevaOrden() {
        PantallaCaptura orderView = new PantallaCaptura(this);
        rootLayout.setCenter(orderView.getView());
    }

    public void mostrarCocina() {
        PantallaCocina kitchenView = new PantallaCocina(this);
        rootLayout.setCenter(kitchenView.getView());
    }

    public void mostrarPantallaExterna() {
        PantallaExterna externalView = new PantallaExterna(this);
        rootLayout.setCenter(externalView.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}