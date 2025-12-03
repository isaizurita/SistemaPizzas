package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; 
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import java.util.Optional;

public class PantallaCaptura {
    private PantallaMenu app;
    private BorderPane view;
    private VBox ticketLayout;
    private Label totalLabel;
    
    // Variables para calcular total real de la órden en cuestión
    private double costoBase = 100.00;
    private double costoExtras = 0.00;

    // Datos de la orden actual
    private TextField txtNombreCliente;
    private Label lblNumeroOrden;
    private String currentOrderId;
    private static int contadorOrdenes = 100;

    public PantallaCaptura(PantallaMenu app) {
        this.app = app;
        this.currentOrderId = "" + contadorOrdenes;
        contadorOrdenes++;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b; -fx-padding: 20;");

        // Inicializamods los controles independientes
        // Debemos crear el TextField ANTES de crear el ticket, porque el ticket se "conecta" a él.
        txtNombreCliente = new TextField();
        txtNombreCliente.setPromptText("Ingrese nombre...");
        txtNombreCliente.setPrefWidth(250);
        txtNombreCliente.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-background-radius: 10; -fx-background-color: white; -fx-text-fill: #152238;");

        // Creación del ticket
        // Ahora sí podemos crear el panel derecho, porque txtNombreCliente ya existe
        view.setRight(crearPanelTicket());

        // Datos del cliente
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label("Nombre Cliente:");
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        lblNumeroOrden = new Label("Número de orden: " + currentOrderId);
        lblNumeroOrden.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");

        topBar.getChildren().addAll(lblNombre, txtNombreCliente, new Separator(), lblNumeroOrden);
        view.setTop(topBar);

        // Scroll con todas las secciones
        VBox mainContent = new VBox(30); 
        mainContent.setPadding(new Insets(0, 20, 20, 0));
        mainContent.setStyle("-fx-background-color: transparent;");

        mainContent.getChildren().add(crearSeccionSimple("TIPO DE MASA", new String[]{"Tradicional", "Crujiente", "Sartén"}, "Masa"));
        mainContent.getChildren().add(crearSeccionSimple("SALSA BASE", new String[]{"Tomate", "BBQ", "Ranch"}, "Salsa"));
        mainContent.getChildren().add(crearSeccionSimple("QUESOS", new String[]{"Mozzarella", "Fontina", "Sin Queso"}, "Queso"));
        mainContent.getChildren().add(crearSeccionIngredientesConImagenes());

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        try {
            if (getClass().getResource("/style.css") != null) {
                scrollPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
        } catch (Exception e) {}
        
        view.setCenter(scrollPane);
    }

    // Métodos para crear las diversas secciiones contenidas en el panel de crear orden

    private VBox crearSeccionSimple(String titulo, String[] items, String categoria) {
        VBox seccion = new VBox(10);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 1);");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(15);
        flow.setVgap(15);

        ToggleGroup group = new ToggleGroup();

        for (String item : items) {
            ToggleButton btn = crearBotonOpcionSimple(item);
            btn.setToggleGroup(group);
            
            btn.setOnAction(e -> {
                actualizarTicketUnico(categoria, item);
                actualizarEstiloBotonSimple(btn);
            });
            btn.selectedProperty().addListener((obs, old, isSelected) -> actualizarEstiloBotonSimple(btn));
            
            if (item.equals(items[0])) {
                btn.setSelected(true);
                actualizarTicketUnico(categoria, item);
            }

            flow.getChildren().add(btn);
        }
        seccion.getChildren().addAll(lblTitulo, flow);
        return seccion;
    }

    private VBox crearSeccionIngredientesConImagenes() {
        VBox seccion = new VBox(15);
        Label lblTitulo = new Label("INGREDIENTES");
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(20);
        flow.setVgap(20);

        // Catálogo de ingredientes: Nombre, Costo, Imagen
        String[][] dataIngredientes = {
            {"Pepperoni", "15", "pepperoni.png"},
            {"Jamón", "15", "jamon-2.png"},
            {"Tocino", "20", "tocino.png"},
            {"Salchicha", "15", "salchicha.png"},
            {"Chorizo", "15", "chorizo.png"},
            {"Salami", "15", "salami.png"},
            {"Pollo", "15", "pollo.png"},
            {"Pimientos", "10", "pimientoVerde.png"},
            {"Champiñones", "12", "champinon.png"},
            {"Albahacar", "12", "albahacar.png"},
            {"Parmesano", "15", "parmesano.png"},
            {"Aceitunas", "12", "aceitunasNegras.png"},
            {"Cebolla", "10", "cebolla.png"},
            {"Maíz", "10", "maiz.png"}
        };

        for (String[] data : dataIngredientes) {
            ToggleButton btn = crearTarjetaIngrediente(data[0], Double.parseDouble(data[1]), data[2]);
            btn.setOnAction(e -> {
                actualizarTicketIngrediente(data[0], Double.parseDouble(data[1]), btn.isSelected());
                actualizarEstiloTarjeta(btn);
            });
            flow.getChildren().add(btn);
        }
        seccion.getChildren().addAll(lblTitulo, flow);
        return seccion;
    }

    // Métodos para el diseño de botones

    private ToggleButton crearBotonOpcionSimple(String texto) {
        ToggleButton btn = new ToggleButton(texto);
        btn.setPrefSize(140, 50);
        actualizarEstiloBotonSimple(btn);
        return btn;
    }

    private void actualizarEstiloBotonSimple(ToggleButton btn) {
        String base = "-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;";
        if (btn.isSelected()) {
            btn.setStyle(base + "-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-effect: dropshadow(three-pass-box, rgba(255,165,0,0.5), 10, 0, 0, 0);");
        } else {
            btn.setStyle(base + "-fx-background-color: #203354; -fx-text-fill: white; -fx-border-color: #2e4a7d; -fx-border-radius: 25;");
        }
    }

    private ToggleButton crearTarjetaIngrediente(String nombre, double precio, String imgFileName) {
        ToggleButton btn = new ToggleButton();
        btn.setPrefSize(140, 160); 
        btn.setContentDisplay(ContentDisplay.TOP); 
        
        VBox layout = new VBox(5);
        layout.setAlignment(Pos.CENTER);
        
        Node graphicNode;
        try {
            String path = "/imagenes/" + imgFileName;
            if (getClass().getResource(path) == null) throw new Exception("No img");
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(70); imgView.setFitHeight(70);
            imgView.setPreserveRatio(true);
            imgView.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.2)));
            graphicNode = imgView;
        } catch (Exception e) {
            Label lblNoImg = new Label("?");
            lblNoImg.setStyle("-fx-text-fill: gray; -fx-font-size: 40px; -fx-font-weight: bold;");
            lblNoImg.setMinHeight(70);
            graphicNode = lblNoImg;
        }

        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: inherit;");
        lblNombre.setWrapText(true);
        lblNombre.setTextAlignment(TextAlignment.CENTER);
        
        Label lblPrecio = new Label("+$" + String.format("%.2f", precio));
        lblPrecio.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px; -fx-text-fill: #a0a0a0;"); 

        layout.getChildren().addAll(graphicNode, lblNombre, lblPrecio);
        btn.setGraphic(layout);
        actualizarEstiloTarjeta(btn);
        
        btn.setOnMouseEntered(e -> btn.setEffect(new DropShadow(15, Color.rgb(255, 165, 0, 0.4))));
        btn.setOnMouseExited(e -> btn.setEffect(null));
        return btn;
    }

    private void actualizarEstiloTarjeta(ToggleButton btn) {
        VBox layout = (VBox) btn.getGraphic();
        if (layout.getChildren().size() >= 3) {
            Label lblNombre = (Label) layout.getChildren().get(1);
            Label lblPrecio = (Label) layout.getChildren().get(2);
            String baseStyle = "-fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 10;";
            if (btn.isSelected()) {
                btn.setStyle(baseStyle + "-fx-background-color: #23395d; -fx-border-color: #FFA500; -fx-border-width: 3; -fx-border-radius: 15;");
                lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #FFA500;");
                lblPrecio.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px; -fx-text-fill: white;");
            } else {
                btn.setStyle(baseStyle + "-fx-background-color: #203354; -fx-border-color: transparent; -fx-border-width: 3;");
                lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white;");
                lblPrecio.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px; -fx-text-fill: #a0a0a0;");
            }
        }
    }

    // Creación del ticket

    private Node crearPanelTicket() {
        VBox ticketPanel = new VBox(15);
        ticketPanel.setPadding(new Insets(20));
        ticketPanel.setPrefWidth(340); 
        ticketPanel.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        Label tituloTicket = new Label("RESUMEN DE ORDEN");
        tituloTicket.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #152238;");
        
        Label lblClienteTicket = new Label("Cliente: ---");
        lblClienteTicket.textProperty().bind(javafx.beans.binding.Bindings.concat("Cliente: ", txtNombreCliente.textProperty()));
        lblClienteTicket.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #555; -fx-font-size: 14px;");

        ticketLayout = new VBox(8); 
        
        totalLabel = new Label("TOTAL: $100.00");
        totalLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #152238;");

        Button btnPagar = crearBotonAccion("COBRAR", "#FFA500", "#152238"); 
        btnPagar.setOnAction(e -> mostrarModalPago());

        Button btnCancelar = crearBotonAccion("Cancelar Orden", "#d9534f", "white"); 
        btnCancelar.setOnAction(e -> app.mostrarMenuPrincipal());

        ticketPanel.getChildren().addAll(tituloTicket, lblClienteTicket, new Separator(), ticketLayout, new Separator(), totalLabel, btnPagar, btnCancelar);
        HBox container = new HBox(ticketPanel);
        container.setPadding(new Insets(0,0,0,20));
        return container;
    }

    private void actualizarTicketUnico(String categoria, String item) {
        if (ticketLayout == null) return;
        ticketLayout.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().startsWith(categoria + ":"));
        Label l = new Label(categoria + ": " + item);
        l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333; -fx-font-size: 13px;");
        ticketLayout.getChildren().add(0, l); 
    }

    private void actualizarTicketIngrediente(String item, double precio, boolean agregando) {
        String textoItem = "+ " + item + " ($" + precio + ")";
        if (agregando) {
            costoExtras += precio;
            Label l = new Label(textoItem);
            l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333; -fx-font-size: 13px;");
            ticketLayout.getChildren().add(l);
        } else {
            costoExtras -= precio;
            ticketLayout.getChildren().removeIf(node -> ((Label)node).getText().equals(textoItem));
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = costoBase + costoExtras;
        totalLabel.setText("TOTAL: $" + String.format("%.2f", total));
    }

    private Button crearBotonAccion(String texto, String colorFondo, String colorTexto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: " + colorFondo + "; -fx-text-fill: " + colorTexto + "; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20; -fx-cursor: hand;");
        btn.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.2)));
        return btn;
    }

    // Métodos para simular el pago
    private void mostrarModalPago() {
        if(txtNombreCliente.getText().isEmpty()) {
            mostrarAlertaError("Faltan datos", "Por favor ingresa el nombre del cliente.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("Total a Pagar: " + totalLabel.getText().replace("TOTAL: ", ""));
        estilizarDialogo(dialog);
        ButtonType btnEfectivo = new ButtonType("Efectivo", ButtonBar.ButtonData.LEFT);
        ButtonType btnTarjeta = new ButtonType("Tarjeta", ButtonBar.ButtonData.LEFT);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEfectivo, btnTarjeta, btnCancelar);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnEfectivo) simularPagoEfectivo();
            else if (result.get() == btnTarjeta) simularPagoTarjeta();
        }
    }

    private void simularPagoEfectivo() {
        TextInputDialog td = new TextInputDialog();
        td.setTitle("Pago en Efectivo");
        td.setHeaderText("Ingrese monto recibido:");
        estilizarDialogo(td); 
        td.showAndWait().ifPresent(monto -> {
            try {
                double montoRecibido = Double.parseDouble(monto);
                double total = costoBase + costoExtras;
                if (montoRecibido >= total) {
                    mostrarAlertaExito("Pago Recibido", "Cambio a devolver: $" + String.format("%.2f", (montoRecibido - total)));
                    mostrarExitoOrden();
                } else {
                    mostrarAlertaError("Error", "Monto insuficiente");
                }
            } catch (NumberFormatException e) {
                mostrarAlertaError("Error", "Ingrese un número válido");
            }
        });
    }

    private void simularPagoTarjeta() {
        Alert processing = new Alert(Alert.AlertType.NONE);
        processing.setTitle("Terminal Bancaria");
        processing.setHeaderText("Procesando pago...");
        estilizarDialogo(processing); 
        ProgressBar pb = new ProgressBar(); pb.setProgress(-1); 
        VBox content = new VBox(10, new Label("Contactando al Banco..."), pb);
        content.lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana';"));
        processing.getDialogPane().setContent(content);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        processing.getDialogPane().getButtonTypes().add(cancelar);
        processing.show();
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() -> {
                    processing.setResult(cancelar);
                    processing.close();
                    mostrarExitoOrden();
                });
            }
        }, 3000);
    }
    
    private void mostrarExitoOrden() {
        mostrarAlertaExito("Orden Enviada", "Orden " + currentOrderId + " pagada y enviada a cocina.");
        app.mostrarMenuPrincipal();
    }

    private void mostrarAlertaExito(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        estilizarDialogo(alert);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        estilizarDialogo(alert);
        alert.showAndWait();
    }

    private void estilizarDialogo(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("-fx-background-color: #0f192b; -fx-font-family: 'Verdana';");
        pane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
        pane.lookupAll(".content.label").forEach(node -> node.setStyle("-fx-text-fill: white; -fx-font-size: 14px;"));
        Node headerPanel = pane.lookup(".header-panel");
        if (headerPanel != null) {
            headerPanel.setStyle("-fx-background-color: #203354; -fx-padding: 10;");
            headerPanel.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 14px;"));
        }
        for (ButtonType bt : pane.getButtonTypes()) {
            Node btnNode = pane.lookupButton(bt);
            if (btnNode != null) {
                btnNode.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
                btnNode.setOnMouseEntered(e -> btnNode.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #152238; -fx-background-radius: 15; -fx-font-weight: bold;"));
                btnNode.setOnMouseExited(e -> btnNode.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-background-radius: 15; -fx-font-weight: bold;"));
            }
        }
        if (dialog instanceof TextInputDialog) {
            TextInputDialog tid = (TextInputDialog) dialog;
            TextField tf = tid.getEditor();
            if (tf != null) tf.setStyle("-fx-background-color: white; -fx-text-fill: #152238; -fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-background-radius: 5;");
        }
    }

    public Pane getView() { return view; }
}