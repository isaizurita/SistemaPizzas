package vista;

import controlador.ControladorGeneral;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.plaf.synth.Region;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import modelo.CargadorPedidos;
import modelo.Cocina;
import modelo.Pedido;

/**
 * Clase principal de la UI
 * <p>Inicializa la aplicación JavaFX, configura el contenedor principal y
 * establece la inyección de dependencias entre las Vistas y el Controlador.
 * Actúa como el contenedor de navegación entre las distintas pantallas.
 */
public class PantallaMenu extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    private PantallaCocina pantallaCocina;
    private PantallaExterna pantallaExterna;
    private ControladorGeneral controlador;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PizzaFactory POS System");

        this.pantallaCocina = new PantallaCocina(this);
        this.pantallaExterna = new PantallaExterna(this);

        // Inicialización del Controlador
        this.controlador = new ControladorGeneral(pantallaCocina, pantallaExterna);

        initRootLayout();
        mostrarMenuPrincipal();

        CargadorPedidos cargador = new CargadorPedidos();
        List<Pedido> historicos = cargador.cargarInfo("ordenes.csv");

        Cocina cocina = Cocina.getInstancia();

        new Thread(() -> {
            for(modelo.Pedido p : historicos) {
                try {
                    p.registrarObservador(this.controlador);
                    Thread.sleep(1500); 
                    cocina.recibirPedido(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #0f192b; -fx-padding: 15; -fx-border-color: #23395d; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label logoLbl = new Label("pizzaFactory"); 
        logoLbl.setStyle("-fx-font-family: 'Century Gothic'; -fx-font-weight: bold; -fx-font-size: 28px; -fx-text-fill: white;");
        
        Label welcomeLbl = new Label("| Bienvenido, isaizurita ");
        welcomeLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");

        // Reloj
        Label clockLbl = new Label();
        clockLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-family: 'Verdana'; -fx-font-size: 14px;");
        
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoLbl, welcomeLbl, spacer, clockLbl);
        rootLayout.setTop(header);

        Scene scene = new Scene(rootLayout, 1000, 700);
        try {
            if (getClass().getResource("/style.css") != null)
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) { }
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void mostrarMenuPrincipal() {
        VBox menuView = new VBox(40);
        menuView.setAlignment(Pos.CENTER);
        menuView.setStyle("-fx-padding: 50; -fx-background-color: #0f192b;");

        // Logo
        try {
            Image imgLogo = new Image(getClass().getResourceAsStream("/imagenes/logo_pizza.jpg"));
            ImageView imgView = new ImageView(imgLogo);
            imgView.setFitHeight(220); 
            imgView.setPreserveRatio(true);
            Rectangle clip = new Rectangle(220, 220); 
            clip.setArcWidth(30); clip.setArcHeight(30);
            imgView.setClip(clip);
            imgView.setEffect(new DropShadow(30, Color.BLACK));
            menuView.getChildren().add(imgView);
        } catch (Exception e) {
            Label lblTitulo = new Label("PIZZA FACTORY");
            lblTitulo.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 60px; -fx-text-fill: white;");
            menuView.getChildren().add(lblTitulo);
        }

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

    private Button crearBotonMenu(String texto) {
        Button btn = new Button(texto);
        btn.setTextAlignment(TextAlignment.CENTER);
        String estiloNormal = "-fx-background-color: #203354; -fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2e4a7d; -fx-border-radius: 15; -fx-border-width: 1; -fx-cursor: hand;";
        btn.setStyle(estiloNormal);
        btn.setPrefSize(220, 180); 
        btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #23395d; -fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 19px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #FFA500; -fx-border-radius: 15; -fx-border-width: 3; -fx-cursor: hand;");
            btn.setEffect(new DropShadow(25, Color.rgb(255, 165, 0, 0.5))); 
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(estiloNormal);
            btn.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        });
        return btn;
    }

    public void mostrarNuevaOrden() {
        // Creamos la pantalla de captura nueva inyectándole el controlador (inyección de instancias)
        PantallaCaptura orderView = new PantallaCaptura(this, controlador);
        rootLayout.setCenter(orderView.getView());
    }

    public void mostrarCocina() {
        rootLayout.setCenter(pantallaCocina.getView());
    }

    public void mostrarPantallaExterna() {
        rootLayout.setCenter(pantallaExterna.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// export PATH_TO_FX=/Users/isaizurita/Documents/javafx-sdk-21.0.9/lib (Ruta del sdk de JavaFX)
// javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml src/modelo/*.java src/vista/*java src/controlador/*.java 
// java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp src vista.PantallaMenu