package vista;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class KitchenView {
    private MainApp app;
    private BorderPane view;
    private HBox kanbanBoard;

    public KitchenView(MainApp app) {
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        
        HBox header = new HBox(20);
        header.setPadding(new Insets(10));
        Button btnBack = new Button("⬅ Volver");
        btnBack.setOnAction(e -> app.mostrarMenuPrincipal());
        Label title = new Label("MONITOR DE COCINA");
        // Título blanco grande
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        header.getChildren().addAll(btnBack, title);
        view.setTop(header);

        kanbanBoard = new HBox(15);
        kanbanBoard.setPadding(new Insets(20));
        
        ScrollPane scroll = new ScrollPane(kanbanBoard);
        scroll.setFitToHeight(true);
        // Fondo transparente para que se vea el azul de la app detrás
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        
        view.setCenter(scroll);

        // Mocks
        agregarTicketMock("ORD-101", "Juan P.", "En Preparación", "#FFA500");
        agregarTicketMock("ORD-102", "Ana G.", "Horneando", "#d9534f"); 
    }

    private void agregarTicketMock(String orden, String cliente, String estado, String colorHex) {
        VBox ticket = new VBox(10);
        ticket.setPrefWidth(260);
        ticket.setPrefHeight(320);
        ticket.setPadding(new Insets(15));
        
        // IMPORTANTE: Clase CSS que define fondo blanco y texto oscuro
        ticket.getStyleClass().add("kitchen-ticket"); 
        
        // Barra de color superior
        Region colorBar = new Region();
        colorBar.setPrefHeight(8);
        colorBar.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 5 0 0;");

        // Info (Estilos inline para asegurar visibilidad sobre blanco)
        Label lblOrden = new Label(orden);
        lblOrden.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #152238;");
        
        Label lblCliente = new Label("Cliente: " + cliente);
        lblCliente.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        
        Label lblDetalle = new Label("- Pizza Grande\n- Masa Sartén\n- Pepperoni\n- Extra Queso");
        lblDetalle.setWrapText(true);
        lblDetalle.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;"); // Texto gris oscuro
        
        Label lblEstado = new Label(estado.toUpperCase());
        lblEstado.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 3; -fx-font-weight: bold; -fx-alignment: center;");
        lblEstado.setMaxWidth(Double.MAX_VALUE);
        
        VBox.setVgrow(lblDetalle, Priority.ALWAYS);
        
        ticket.getChildren().addAll(colorBar, lblOrden, lblCliente, new javafx.scene.control.Separator(), lblDetalle, new Region(), lblEstado);
        
        kanbanBoard.getChildren().add(ticket);
    }

    public Pane getView() { return view; }
}