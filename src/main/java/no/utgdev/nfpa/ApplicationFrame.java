package no.utgdev.nfpa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import no.utgdev.nfpa.model.ApplicationState;
import no.utgdev.nfpa.model.Division;
import no.utgdev.nfpa.utils.CodesReader;
import no.utgdev.nfpa.utils.FileWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.function.Function;

import static javafx.application.Platform.runLater;

public class ApplicationFrame extends Application {
    public static final int SPACING = 5;
    public static final String CODES_XML = "codes.xml";
    public static final String STYLES_CSS = "styles.css";
    private List<Division> divisions;
    private StackPane root;
    private ApplicationState state;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("NFPA 704 creator");
        root = new StackPane();
        root.setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));


        updateXMLCodes();
        updateStyling();

        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(900);
        primaryStage.setHeight(560);
        primaryStage.setResizable(false);
        primaryStage.show();

        startFileWatcher();
    }

    private FileWatcher startFileWatcher() throws IOException, InterruptedException {
        return new FileWatcher(FileSystems.getDefault().getPath("./"), new Function<String, Void>() {
            @Override
            public Void apply(String o) {
                if (CODES_XML.equals(o)) {
                    updateXMLCodes();
                }
                if (STYLES_CSS.equals(o)) {
                    updateStyling();
                }
                return null;
            }
        });
    }

    private void updateXMLCodes() {
        runLater(() -> {
            divisions = CodesReader.readFromFile(new File(CODES_XML));
            state = new ApplicationState(divisions);
            root.getChildren().clear();

            DiamondViewer diamondViewer = new DiamondViewer(state);
            DiamondSettingsPanel diamondSettingsPanel = new DiamondSettingsPanel(divisions, state);
            Button saveAsImageButton = createSaveButton(diamondViewer);

            HBox horizontalGrouping = new HBox(SPACING, diamondViewer, diamondSettingsPanel);
            VBox group = new VBox(SPACING, horizontalGrouping, saveAsImageButton);

            root.getChildren().add(group);
        });
    }

    private void updateStyling() {
        runLater(() -> {
            root.getStylesheets().clear();
            root.getStylesheets().add(new File(STYLES_CSS).toURI().toString());
        });
    }

    private Button createSaveButton(DiamondViewer viewer) {
        Button button = new Button("Save image as PNG");
        button.setOnAction((ActionEvent) -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Save as");
            fc.setInitialDirectory(new File("."));
            fc.setInitialFileName("NFPA-704");
            FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("PNG", "*.png");
            fc.getExtensionFilters().addAll(png);
            fc.setSelectedExtensionFilter(png);

            File file = fc.showSaveDialog(this.primaryStage);
            try {
                viewer.exportImageTo(file);
            } catch (Exception ignored) {}
        });
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
