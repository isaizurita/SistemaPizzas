package vista;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MenuView {
    private MainApp app;
    private VBox view;

    public MenuView(MainApp app) {
        this.app = app;
        crearInterfaz();
    }

    private void crearInterfaz() {
        view = new VBox(40);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 50;");

        HBox buttonsContainer = new HBox(30);
        buttonsContainer.setAlignment(Pos.CENTER);

        Button btnOrden = crearBotonMenu("Nueva Orden", "📝");
        btnOrden.setOnAction(e -> app.mostrarNuevaOrden());

        Button btnCocina = crearBotonMenu("Pantalla Cocina", "👨‍🍳");
        btnCocina.setOnAction(e -> app.mostrarCocina());

        Button btnCliente = crearBotonMenu("Pantalla Externa", "📺");
        btnCliente.setOnAction(e -> app.mostrarPantallaExterna());

        buttonsContainer.getChildren().addAll(btnOrden, btnCocina, btnCliente);
        view.getChildren().add(buttonsContainer);
    }

    private Button crearBotonMenu(String texto, String emoji) {
        Button btn = new Button(emoji + "\n" + texto);
        // Asegúrate de que esta clase CSS exista en tu style.css o comenta la línea si da error visual
        btn.getStyleClass().add("menu-button"); 
        // Estilo inline de respaldo por si no carga el CSS
        if (btn.getStyleClass().isEmpty()) {
             btn.setStyle("-fx-font-size: 18px; -fx-padding: 20;");
        }
        return btn;
    }

    public VBox getView() { return view; }
}