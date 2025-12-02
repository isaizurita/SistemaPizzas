package vista;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MainApp extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PizzaFactory POS System");

        initRootLayout();
        mostrarMenuPrincipal();
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        
        // --- HEADER ---
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #0f192b; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);

        // Logo (Texto simulado, aquí iría tu ImageView)
        Label logoLbl = new Label("pizzaFactory"); 
        logoLbl.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 30px; -fx-text-fill: white;");
        
        Label welcomeLbl = new Label("|  Bienvenido, Operador");
        welcomeLbl.getStyleClass().add("sub-header");

        // Reloj en tiempo real
        Label clockLbl = new Label();
        clockLbl.getStyleClass().add("sub-header");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Espaciador para empujar el reloj a la derecha
        HBox spacer = new HBox();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(logoLbl, welcomeLbl, spacer, clockLbl);
        rootLayout.setTop(header);

        Scene scene = new Scene(rootLayout, 1280, 720);
        // Asegúrate de tener el style.css en la carpeta correcta o comenta esta línea para probar sin estilos
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) { System.out.println("No se encontró style.css, usando estilos por defecto."); }
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- NAVEGACIÓN ---

    public void mostrarMenuPrincipal() {
        MenuView menu = new MenuView(this);
        rootLayout.setCenter(menu.getView());
    }

    public void mostrarNuevaOrden() {
        OrderView orderView = new OrderView(this);
        rootLayout.setCenter(orderView.getView());
    }

    public void mostrarCocina() {
        KitchenView kitchenView = new KitchenView(this);
        rootLayout.setCenter(kitchenView.getView());
    }

    public void mostrarPantallaExterna() {
        // En una app real, esto abriría un segundo Stage (ventana)
        ExternalView externalView = new ExternalView(this);
        rootLayout.setCenter(externalView.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}