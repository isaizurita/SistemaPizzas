package vista;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class ExternalView {
    private MainApp app;
    private SplitPane view;

    public ExternalView(MainApp app) {
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new SplitPane();
        
        // --- LADO IZQUIERDO: LOGO ---
        VBox leftPane = new VBox(20);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #0f192b;"); // Fondo oscuro
        
        // Simulación de Logo Gráfico con Texto
        Label logoIcon = new Label("🍕");
        logoIcon.setStyle("-fx-font-size: 100px;");
        
        Label logoText = new Label("pizzaFactory");
        logoText.setStyle("-fx-text-fill: white; -fx-font-family: 'Impact'; -fx-font-size: 60px;");

        Button btnVolver = new Button("Volver (Admin)");
        btnVolver.setOnAction(e -> app.mostrarMenuPrincipal());
        
        leftPane.getChildren().addAll(logoIcon, logoText, btnVolver);

        // --- LADO DERECHO: ESTADO (Números Grandes) ---
        VBox rightPane = new VBox();
        rightPane.setStyle("-fx-background-color: #f4f4f4;"); // Fondo claro
        
        HBox headerList = new HBox();
        headerList.setStyle("-fx-background-color: #23395d; -fx-padding: 20;");
        headerList.setAlignment(Pos.CENTER);
        Label statusTitle = new Label("ESTADO DE SU ORDEN");
        statusTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        headerList.getChildren().add(statusTitle);

        VBox statusContent = new VBox(20);
        statusContent.setStyle("-fx-padding: 30;");
        
        // Listas con fuente más grande
        statusContent.getChildren().add(crearSeccionEstado("COCINANDO 👨‍🍳", new String[]{"ORD-102  (Ana)", "ORD-103  (Luis)"}, "#152238"));
        statusContent.getChildren().add(crearSeccionEstado("¡LISTO! ✅", new String[]{"ORD-101  (Juan)"}, "#28a745"));

        rightPane.getChildren().addAll(headerList, statusContent);

        view.getItems().addAll(leftPane, rightPane);
        view.setDividerPositions(0.4); 
    }

    private VBox crearSeccionEstado(String titulo, String[] ordenes, String colorTitulo) {
        VBox box = new VBox(10);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + colorTitulo + ";");
        
        ListView<String> lista = new ListView<>();
        lista.setPrefHeight(200);
        // Estilo específico para aumentar letra dentro de la lista
        lista.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-control-inner-background: #f4f4f4;");
        
        for (String o : ordenes) lista.getItems().add(o);
        
        box.getChildren().addAll(lblTitulo, lista);
        return box;
    }

    public SplitPane getView() { return view; }
}