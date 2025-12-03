package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class PantallaCocina {
    private PantallaMenu app;
    private BorderPane view;
    private HBox kanbanBoard;

    public PantallaCocina(PantallaMenu app){
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b;"); //Fondo oscuro
        
        // Cabecera
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button btnBack = new Button("⬅ Volver");
        btnBack.setStyle(
            "-fx-background-color: #FFA500;" + // Naranja
            "-fx-text-fill: #152238;" +  // Azul Oscuro
            "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnBack.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.2)));
        
        btnBack.setOnMouseEntered(e -> btnBack.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #152238; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20; -fx-cursor: hand;"));
        btnBack.setOnMouseExited(e -> btnBack.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20; -fx-cursor: hand;"));

        btnBack.setOnAction(e -> app.mostrarMenuPrincipal());

        Label title = new Label("MONITOR DE COCINA");
        title.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(btnBack, title);
        view.setTop(header);

        // Tablero
        kanbanBoard = new HBox(15);
        kanbanBoard.setPadding(new Insets(20));
        
        ScrollPane scroll = new ScrollPane(kanbanBoard);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 0;"); 
        
        view.setCenter(scroll);

        // Datos de prueba)
        agregarTicketMock("ORDEN 101", "Juan P.", "En Preparación", "#FFA500"); // Naranja
        agregarTicketMock("ORDEN 102", "Ana G.", "Horneando", "#d9534f"); // Rojo
        agregarTicketMock("ORDEN 103", "Luis R.", "Listo", "#28a745"); // Verde
    }

    private void agregarTicketMock(String orden, String cliente, String estado, String colorHex) {
        VBox ticket = new VBox(10);
        ticket.setPrefWidth(260);
        ticket.setPrefHeight(320);
        ticket.setPadding(new Insets(15));
        
        ticket.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        
        // Barra de color superior
        Region colorBar = new Region();
        colorBar.setPrefHeight(8);
        colorBar.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 5 0 0;");

        Label lblOrden = new Label(orden);
        lblOrden.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #152238;");
        
        Label lblCliente = new Label("Cliente: " + cliente);
        lblCliente.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-text-fill: #555;");
        
        Label lblDetalle = new Label("- Pizza Grande\n- Masa Sartén\n- Pepperoni\n- Extra Queso");
        lblDetalle.setWrapText(true);
        lblDetalle.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333; -fx-font-size: 14px;"); 
        
        Label lblEstado = new Label(estado.toUpperCase());
        lblEstado.setStyle(
            "-fx-background-color: " + colorHex + ";" + 
            "-fx-text-fill: white;" + 
            "-fx-font-family: 'Verdana'; -fx-font-weight: bold;" +
            "-fx-padding: 8;" + 
            "-fx-background-radius: 5;" + 
            "-fx-alignment: center;"
        );
        lblEstado.setMaxWidth(Double.MAX_VALUE);
        lblEstado.setAlignment(Pos.CENTER);
        
        VBox.setVgrow(lblDetalle, Priority.ALWAYS);
        
        ticket.getChildren().addAll(colorBar, lblOrden, lblCliente, new javafx.scene.control.Separator(), lblDetalle, new Region(), lblEstado);
        
        kanbanBoard.getChildren().add(ticket);
    }

    public Pane getView() { return view; }
}