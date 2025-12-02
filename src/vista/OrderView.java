package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Optional;

public class OrderView {
    private MainApp app;
    private BorderPane view;
    private VBox ticketLayout;
    private Label totalLabel;

    // Datos de la orden actual
    private TextField txtNombreCliente;
    private Label lblNumeroOrden;
    private String currentOrderId;
    private static int contadorOrdenes=100;

    public OrderView(MainApp app) {
        this.app = app;
        // Generamos un ID aleatorio para simular
        this.currentOrderId = "ORD-" + contadorOrdenes;
        contadorOrdenes++;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setPadding(new Insets(20));

        // --- TOP: DATOS DEL CLIENTE ---
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(0, 0, 20, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label("Nombre Cliente:");
        txtNombreCliente = new TextField();
        txtNombreCliente.setPromptText("Ingrese nombre...");
        txtNombreCliente.setPrefWidth(250);
        txtNombreCliente.setStyle("-fx-font-size: 14px; -fx-text-fill: black;"); // Texto negro en input

        lblNumeroOrden = new Label("Orden #: " + currentOrderId);
        lblNumeroOrden.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");

        topBar.getChildren().addAll(lblNombre, txtNombreCliente, new Separator(), lblNumeroOrden);
        view.setTop(topBar);

        // --- CENTER: SELECCIÓN DE INGREDIENTES ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // true = selección única, false = selección múltiple
        tabPane.getTabs().add(crearTab("Masas", new String[]{"Tradicional", "Crujiente", "Sartén"}, true));
        tabPane.getTabs().add(crearTab("Salsas", new String[]{"Tomate", "BBQ", "Ranch"}, true));
        tabPane.getTabs().add(crearTab("Quesos", new String[]{"Mozzarella", "Parmesano", "Sin Queso"}, true));
        tabPane.getTabs().add(crearTab("Ingredientes", new String[]{"Pepperoni", "Jamón", "Pimientos", "Champiñones", "Aceitunas", "Tocino"}, false));

        view.setCenter(tabPane);

        // --- RIGHT: RESUMEN DE ORDEN (TICKET) ---
        VBox ticketPanel = new VBox(15);
        ticketPanel.setPadding(new Insets(20));
        ticketPanel.setPrefWidth(320);
        ticketPanel.getStyleClass().add("ticket-pane"); // Fondo blanco definido en CSS

        Label tituloTicket = new Label("RESUMEN DE ORDEN");
        tituloTicket.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #152238;");
        
        Label lblClienteTicket = new Label("Cliente: ---");
        // Binding para que se actualice al escribir
        lblClienteTicket.textProperty().bind(javafx.beans.binding.Bindings.concat("Cliente: ", txtNombreCliente.textProperty()));
        lblClienteTicket.setStyle("-fx-text-fill: #555;");

        ticketLayout = new VBox(5); // Aquí irán los items
        
        totalLabel = new Label("TOTAL: $100.00");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #152238;");

        // Botones de acción (Definidos UNA SOLA VEZ)
        Button btnPagar = new Button("COBRAR");
        btnPagar.getStyleClass().add("action-button"); // Estilo naranja
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> mostrarModalPago());

        Button btnCancelar = new Button("Cancelar Orden");
        btnCancelar.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> app.mostrarMenuPrincipal());

        // Agregamos todo al panel del ticket
        ticketPanel.getChildren().addAll(
            tituloTicket, 
            lblClienteTicket, 
            new Separator(), 
            ticketLayout, 
            new Separator(), 
            totalLabel, 
            btnPagar, 
            btnCancelar
        );
        
        // Envolvemos el ticket en un contenedor para darle margen a la izquierda
        HBox rightContainer = new HBox(ticketPanel);
        rightContainer.setPadding(new Insets(0, 0, 0, 20));
        view.setRight(rightContainer);
    }

    private Tab crearTab(String titulo, String[] items, boolean seleccionUnica) {
        Tab tab = new Tab(titulo);
        FlowPane flow = new FlowPane();
        flow.setHgap(15);
        flow.setVgap(15);
        flow.setPadding(new Insets(20));

        ToggleGroup group = seleccionUnica ? new ToggleGroup() : null;

        for (String item : items) {
            ToggleButton btn = new ToggleButton(item);
            btn.getStyleClass().add("ingredient-toggle");
            btn.setWrapText(true);

            if (seleccionUnica) {
                btn.setToggleGroup(group);
                // Listener para actualizar ticket en selección única
                btn.setOnAction(e -> actualizarTicketUnico(titulo, item));
            } else {
                // Listener para selección múltiple
                btn.setOnAction(e -> actualizarTicketMultiple(item, btn.isSelected()));
            }
            
            flow.getChildren().add(btn);
        }
        tab.setContent(flow);
        return tab;
    }

    // Lógica para Masa, Salsa, Queso (Selección Única)
    private void actualizarTicketUnico(String categoria, String item) {
        // Buscar si ya hay algo de esta categoría y borrarlo para reemplazar
        ticketLayout.getChildren().removeIf(node -> 
            node instanceof Label && ((Label)node).getText().startsWith(categoria + ":"));
        
        Label l = new Label(categoria + ": " + item);
        l.setStyle("-fx-text-fill: #333;"); // Texto oscuro forzoso
        ticketLayout.getChildren().add(l);
    }

    // Lógica para Ingredientes (Selección Múltiple)
    private void actualizarTicketMultiple(String item, boolean agregando) {
        if (agregando) {
            Label l = new Label("+ " + item);
            l.setStyle("-fx-text-fill: #333;");
            ticketLayout.getChildren().add(l);
        } else {
            ticketLayout.getChildren().removeIf(node -> ((Label)node).getText().equals("+ " + item));
        }
    }

    // --- MÉTODOS DE PAGO ---

    private void mostrarModalPago() {
        if(txtNombreCliente.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Faltan datos");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingresa el nombre del cliente.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("Seleccione método de pago - Total: $100.00");
        
        ButtonType btnEfectivo = new ButtonType("Efectivo", ButtonBar.ButtonData.LEFT);
        ButtonType btnTarjeta = new ButtonType("Tarjeta", ButtonBar.ButtonData.LEFT);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(btnEfectivo, btnTarjeta, btnCancelar);

        // Estilos básicos al dialogo para que se vea bien
        dialog.getDialogPane().setStyle("-fx-background-color: #f4f4f4; -fx-font-size: 14px;");
        // Forzamos texto negro en el contenido del dialogo
        dialog.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: black;");

        // Manejo de la respuesta
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnEfectivo) {
                simularPagoEfectivo();
            } else if (result.get() == btnTarjeta) {
                simularPagoTarjeta();
            }
        }
    }

    private void simularPagoEfectivo() {
        TextInputDialog td = new TextInputDialog("200");
        td.setTitle("Pago en Efectivo");
        td.setHeaderText("Ingrese monto recibido:");
        td.setContentText("Monto:");
        
        // Estilo para que se vea bien
        td.getDialogPane().setStyle("-fx-background-color: #f4f4f4;");
        
        td.showAndWait().ifPresent(monto -> {
            try {
                double montoRecibido = Double.parseDouble(monto);
                double total = 100.00; // Hardcodeado por ahora
                if (montoRecibido >= total) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pago Exitoso");
                    alert.setHeaderText("Cambio a devolver: $" + (montoRecibido - total));
                    alert.setContentText("Enviando orden a cocina...");
                    alert.showAndWait();
                    app.mostrarMenuPrincipal(); 
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Monto insuficiente");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Manejo de error si meten letras
            }
        });
    }

    private void simularPagoTarjeta() {
        // Simulación visual de Proxy/Adapter
        Alert processing = new Alert(Alert.AlertType.NONE);
        processing.setTitle("Terminal Bancaria");
        processing.setHeaderText("Procesando pago con tarjeta...");
        processing.setContentText("Por favor espere...");
        
        // Simulador de animación
        ProgressBar pb = new ProgressBar();
        pb.setProgress(-1); // Indeterminado (animación de carga)
        
        VBox content = new VBox(10, new Label("Contactando al Banco..."), pb);
        processing.getDialogPane().setContent(content);
        
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        processing.getDialogPane().getButtonTypes().add(cancelar);
        
        processing.show();
        
        // Simulación timer de 3 segundos
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        processing.setResult(cancelar);
                        processing.close();
                        mostrarExitoTarjeta();
                    });
                }
            }, 
            3000 
        );
    }
    
    private void mostrarExitoTarjeta() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Orden Enviada");
        alert.setHeaderText("Orden " + currentOrderId + " pagada y enviada.");
        alert.showAndWait();
        app.mostrarMenuPrincipal();
    }

    public Pane getView() { return view; }
}