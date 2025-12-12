package vista;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Vista del monitor de cocina (inspirado en un POS de una cocina en la que trabaje, o bueno, como me hubiese gustado que fuera).
 * <p>Muestra las órdenes activas y se actualiza en tiempo real mediante el patrón Observer.
 * Las tarjetas se mueven o eliminan según el estado del pedido.
 */
public class PantallaCocina {
    private PantallaMenu app;
    private BorderPane view;
    private VBox kanbanBoard;

    public PantallaCocina(PantallaMenu app){
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b;");
        
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button btnBack = new Button("⬅ Volver");
        btnBack.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-weight: bold; -fx-background-radius: 20;");
        btnBack.setOnAction(e -> app.mostrarMenuPrincipal());

        Label title = new Label("MONITOR DE COCINA");
        title.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(btnBack, title);
        view.setTop(header);

        kanbanBoard = new VBox(15);
        kanbanBoard.setPadding(new Insets(20));
        
        ScrollPane scroll = new ScrollPane(kanbanBoard);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        
        view.setCenter(scroll);
    }

    public void actualizarTarjeta(String idOrden, String descripcion, String estado) {
        String color = "#FFA500"; 
        if(estado.equalsIgnoreCase("Horneando")) color = "#d9534f";
        if(estado.equalsIgnoreCase("Listo para Entrega")) color = "#28a745";
        
        // Si ya se entregó la orden, la quitamos de la pantalla de cocina
        if(estado.equalsIgnoreCase("Entregado")) {
            Platform.runLater(() -> eliminarTarjeta(idOrden));
            return;
        }

        String finalColor = color;

        Platform.runLater(() -> {
            eliminarTarjeta(idOrden);
            agregarTicketVisual(idOrden, descripcion, estado, finalColor);
        });
    }

    private void eliminarTarjeta(String idOrden) {
        kanbanBoard.getChildren().removeIf(node -> 
            idOrden.equals(node.getUserData())
        );
    }

    private void agregarTicketVisual(String idOrden, String descripcion, String estado, String colorHex) {
        String titulo = "ORDEN " + idOrden;
        
        VBox ticket = new VBox(10);
        ticket.setPrefWidth(260);
        ticket.setPadding(new Insets(15));
        ticket.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        
        ticket.setUserData(idOrden); 

        Region colorBar = new Region();
        colorBar.setPrefHeight(8);
        colorBar.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 5 0 0;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #152238;");
        
        Label lblDetalle = new Label(descripcion);
        lblDetalle.setWrapText(true);
        lblDetalle.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333; -fx-font-size: 12px;"); 
        
        Label lblEstado = new Label(estado.toUpperCase());
        lblEstado.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 5; -fx-alignment: center;");
        lblEstado.setMaxWidth(Double.MAX_VALUE);
        lblEstado.setAlignment(Pos.CENTER);
        
        ticket.getChildren().addAll(colorBar, lblTitulo, new javafx.scene.control.Separator(), lblDetalle, new Region(), lblEstado);
        
        // Agregamos al principio de la lista
        kanbanBoard.getChildren().add(0, ticket);
    }

    public Pane getView() { return view; }
}