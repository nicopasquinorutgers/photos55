package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the Photo App application.
 * <p>
 * This class launches the JavaFX application by loading the login view from FXML.
 * It also sets up the primary stage and defines the default close behavior.
 * </p>
 *
 * @author Sara Annamraju
 * @version 1.0
 */
public class Photos extends Application {
    /**
     * Starts the JavaFX application.
     * Loads the login FXML view, sets the scene, and shows the primary stage.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Photo App Login");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            // Ensure that the application exits cleanly when the primary stage is closed.
            javafx.application.Platform.exit();
        });
        primaryStage.show();
    }

    /**
     * The main method which launches the Photo App application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}