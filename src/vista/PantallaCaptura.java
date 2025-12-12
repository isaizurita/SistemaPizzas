package vista;

import controlador.ControladorGeneral;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PantallaCaptura {
    private PantallaMenu app;
    private ControladorGeneral controlador;
    private BorderPane view;
    private VBox ticketLayout;
    private Label totalLabel;
    
    private double costoBase = 100.00;
    private double costoExtras = 0.00;

    private TextField txtNombreCliente;
    private Label lblNumeroOrden;
    private String currentOrderId;
    private static int contadorOrdenes = 100;

    private String selMasa = "Tradicional";
    private String selSalsa = "Tomate";
    private String selQueso = "Mozzarella";
    private String selOrilla = "Normal";
    private List<String> selIngredientes = new ArrayList<>();

    public PantallaCaptura(PantallaMenu app, ControladorGeneral controlador) {
        this.app = app;
        this.controlador = controlador;
        this.currentOrderId = "" + contadorOrdenes;
        contadorOrdenes++;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #0f192b; -fx-padding: 20;");

        txtNombreCliente = new TextField();
        txtNombreCliente.setPromptText("Ingrese nombre...");
        txtNombreCliente.setPrefWidth(250);
        txtNombreCliente.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-background-radius: 10; -fx-background-color: white; -fx-text-fill: #152238;");

        view.setRight(crearPanelTicket());

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblNombre = new Label("Nombre Cliente:");
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        lblNumeroOrden = new Label("Número de orden: " + currentOrderId);
        lblNumeroOrden.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");

        topBar.getChildren().addAll(lblNombre, txtNombreCliente, new Separator(), lblNumeroOrden);
        view.setTop(topBar);

        VBox mainContent = new VBox(30); 
        mainContent.setPadding(new Insets(0, 20, 20, 0));
        mainContent.setStyle("-fx-background-color: transparent;");

        mainContent.getChildren().add(crearSeccionSimple("TIPO DE MASA", new String[]{"Tradicional", "Crujiente", "Sarten", "Delgada"}, "Masa"));
        mainContent.getChildren().add(crearSeccionSimple("SALSA BASE", new String[]{"Tomate", "BBQ", "Ranch", "Picante"}, "Salsa"));
        mainContent.getChildren().add(crearSeccionSimple("QUESOS", new String[]{"Mozzarella", "Parmesano", "Cheddar", "Sin Queso"}, "Queso"));
        mainContent.getChildren().add(crearSeccionSimple("ORILLA", new String[]{"Normal", "Rellena de Queso", "Sin Orilla"}, "Orilla"));
        mainContent.getChildren().add(crearSeccionIngredientesConImagenes());

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        view.setCenter(scrollPane);
    }

    private VBox crearSeccionSimple(String titulo, String[] items, String categoria) {
        VBox seccion = new VBox(10);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(15); flow.setVgap(15);
        ToggleGroup group = new ToggleGroup();

        for (String item : items) {
            ToggleButton btn = crearBotonOpcionSimple(item);
            btn.setToggleGroup(group);
            
            btn.setOnAction(e -> {
                actualizarTicketUnico(categoria, item);
                actualizarEstiloBotonSimple(btn);
            });
            btn.selectedProperty().addListener((obs, old, isSelected) -> actualizarEstiloBotonSimple(btn));
            
            if (item.equalsIgnoreCase(items[0])) {
                btn.setSelected(true);
                if(categoria.equals("Masa")) selMasa = item;
                if(categoria.equals("Salsa")) selSalsa = item;
                if(categoria.equals("Queso")) selQueso = item;
                if(categoria.equals("Orilla")) selOrilla = item;
            }
            flow.getChildren().add(btn);
        }
        seccion.getChildren().addAll(lblTitulo, flow);
        return seccion;
    }

    private VBox crearSeccionIngredientesConImagenes() {
        VBox seccion = new VBox(15);
        Label lblTitulo = new Label("INGREDIENTES EXTRA");
        lblTitulo.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        
        FlowPane flow = new FlowPane();
        flow.setHgap(20); flow.setVgap(20);

        String[][] dataIngredientes = {
            {"Pepperoni", "15", "pepperoni.png"},
            {"Jamón", "15", "jamon.png"},
            {"Tocino", "20", "tocino.png"},
            {"Salchicha", "15", "salchicha.png"},
            {"Pimientos", "10", "pimientoVerde.png"},
            {"Champiñones", "12", "champinon.png"},
            {"Albahaca", "12", "albahacar.png"},
            {"Aceitunas", "12", "aceitunasNegras.png"},
            {"Cebolla", "10", "cebolla.png"},
            {"Maíz", "10", "maiz.png"},
            {"Queso Extra", "25", "parmesano.png"}
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
            Image img = new Image(getClass().getResourceAsStream("/imagenes/" + imgFileName));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(70); imgView.setFitHeight(70); imgView.setPreserveRatio(true);
            graphicNode = imgView;
        } catch (Exception e) {
            Label lblNoImg = new Label("?");
            lblNoImg.setStyle("-fx-text-fill: gray; -fx-font-size: 40px;");
            graphicNode = lblNoImg;
        }
        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: inherit;");
        Label lblPrecio = new Label("+$" + String.format("%.2f", precio));
        lblPrecio.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px; -fx-text-fill: #a0a0a0;"); 

        layout.getChildren().addAll(graphicNode, lblNombre, lblPrecio);
        btn.setGraphic(layout);
        actualizarEstiloTarjeta(btn);
        return btn;
    }

    private void actualizarEstiloTarjeta(ToggleButton btn) {
        VBox layout = (VBox) btn.getGraphic();
        if (layout.getChildren().size() >= 3) {
            Label lblNombre = (Label) layout.getChildren().get(1);
            if (btn.isSelected()) {
                btn.setStyle("-fx-background-color: #23395d; -fx-border-color: #FFA500; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15;");
                lblNombre.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: #203354; -fx-border-color: transparent; -fx-background-radius: 15;");
                lblNombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
    }

    private Node crearPanelTicket() {
        VBox ticketPanel = new VBox(15);
        ticketPanel.setPadding(new Insets(20));
        ticketPanel.setPrefWidth(340); 
        ticketPanel.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 10;");

        Label tituloTicket = new Label("RESUMEN DE ORDEN");
        tituloTicket.setStyle("-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #152238;");
        Label lblClienteTicket = new Label("Cliente: ---");
        lblClienteTicket.textProperty().bind(javafx.beans.binding.Bindings.concat("Cliente: ", txtNombreCliente.textProperty()));

        ticketLayout = new VBox(8); 
        totalLabel = new Label("TOTAL: $100.00");
        totalLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #152238;");

        Button btnPagar = new Button("COBRAR"); 
        btnPagar.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #152238; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20;");
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> mostrarModalPago());

        Button btnCancelar = new Button("Cancelar Orden"); 
        btnCancelar.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20;");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> app.mostrarMenuPrincipal());

        ticketPanel.getChildren().addAll(tituloTicket, lblClienteTicket, new Separator(), ticketLayout, new Separator(), totalLabel, btnPagar, btnCancelar);
        return new HBox(ticketPanel);
    }

    private void actualizarTicketUnico(String categoria, String item) {
        if(categoria.equals("Masa")) selMasa = item;
        if(categoria.equals("Salsa")) selSalsa = item;
        if(categoria.equals("Queso")) selQueso = item;
        if(categoria.equals("Orilla")) selOrilla = item;

        if (ticketLayout == null) return;
        ticketLayout.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().startsWith(categoria + ":"));
        Label l = new Label(categoria + ": " + item);
        l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333;");
        ticketLayout.getChildren().add(0, l); 
    }

    private void actualizarTicketIngrediente(String item, double precio, boolean agregando) {
        String textoItem = "+ " + item + " ($" + precio + ")";
        if (agregando) {
            selIngredientes.add(item);
            costoExtras += precio;
            Label l = new Label(textoItem);
            l.setStyle("-fx-font-family: 'Verdana'; -fx-text-fill: #333;");
            ticketLayout.getChildren().add(l);
        } else {
            selIngredientes.remove(item);
            costoExtras -= precio;
            ticketLayout.getChildren().removeIf(node -> ((Label)node).getText().equals(textoItem));
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = costoBase + costoExtras;
        totalLabel.setText("TOTAL: $" + String.format("%.2f", total));
    }

    private void mostrarModalPago() {
        if(txtNombreCliente.getText().isEmpty()) {
            mostrarAlertaError("Faltan datos", "Ingrese el nombre del cliente.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("Total a Pagar: " + totalLabel.getText().replace("TOTAL: ", ""));
        
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
        td.showAndWait().ifPresent(montoStr -> {
            try {
                double monto = Double.parseDouble(montoStr);
                // PASAMOS EL ID AL CONTROLADOR
                boolean exito = controlador.procesarNuevaOrden(
                    currentOrderId,
                    txtNombreCliente.getText(), selMasa, selSalsa, selQueso, selOrilla, selIngredientes,
                    "EFECTIVO", monto
                );

                if (exito) mostrarExitoOrden();
                else mostrarAlertaError("Pago rechazado", "Monto insuficiente o error de sistema.");

            } catch (NumberFormatException e) {
                mostrarAlertaError("Error", "Ingrese un número válido");
            }
        });
    }

    private void simularPagoTarjeta() {
        // Configuración de la ventana de carga del paog
        Alert processing = new Alert(Alert.AlertType.NONE);
        processing.setTitle("Terminal Bancaria");
        processing.setHeaderText("Conectando con el Servicio Bancario...");
        
        // Botón de seguridad
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        processing.getDialogPane().getButtonTypes().add(btnCancelar);
        
        ProgressBar pb = new ProgressBar(); 
        pb.setProgress(-1); 
        
        VBox content = new VBox(10, new Label("Validando fondos y seguridad..."), pb);
        content.setStyle("-fx-padding: 10;");
        processing.getDialogPane().setContent(content);
        
        processing.show();

        // Simulamos el tiempo de espera del banco
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        
        pause.setOnFinished(e -> {
            processing.setResult(btnCancelar);
            processing.close();

            javafx.application.Platform.runLater(() -> {
                boolean exito = false;
                try {
                    exito = controlador.procesarNuevaOrden(
                        currentOrderId,
                        txtNombreCliente.getText(), selMasa, selSalsa, selQueso, selOrilla, selIngredientes,
                        "TARJETA", 0.0
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (exito) {
                    mostrarExitoOrden(); // Esto muestra la alerta de éxito y regresa al menú
                } else {
                    mostrarAlertaError("Transacción Fallida", "La tarjeta fue rechazada por el banco.");
                }
            });
        });
        
        pause.play();
    }
    
    private void mostrarExitoOrden() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Orden Enviada");
        alert.setHeaderText("¡Éxito!");
        alert.setContentText("Orden enviada a cocina.");
        alert.showAndWait();
        app.mostrarMenuPrincipal();
    }

    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public Pane getView() { return view; }
}