package no.utgdev.nfpa;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import no.utgdev.nfpa.model.ApplicationState;
import no.utgdev.nfpa.model.Division;
import no.utgdev.nfpa.model.DivisionOption;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

public class DiamondViewer extends StackPane {
    private final ApplicationState state;
    private final DiamondDrawer diamondDrawer;

    public DiamondViewer(ApplicationState state) {
        super();
        this.state = state;
        this.getStyleClass().addAll("diamondviewer");
        this.setPadding(new Insets(0));
        this.setMinSize(475, 480);
        this.diamondDrawer = new DiamondDrawer();
        this.getChildren().addAll(diamondDrawer);

        this.state.addListener((divisionId, optionIndex) -> diamondDrawer.draw());
    }

    public void exportImageTo(File file) throws IOException {
        WritableImage wim = new WritableImage(475, 480);
        diamondDrawer.snapshot(null, wim);

        ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
    }

    private class DiamondDrawer extends Canvas {
        private final GraphicsContext gc;
        private static final double SIZE = 150;
        private final double centerW;
        private final double centerH;
        private final double diagonal;

        public DiamondDrawer() {
            super(475, 480);
            gc = getGraphicsContext2D();
            centerW = getWidth() / 2;
            centerH = getHeight() / 2;
            diagonal = Math.sqrt(2 * Math.pow(SIZE, 2));
            draw();
        }

        private void draw() {
            reset();
            drawSkeleton();
            drawData();
        }

        private void reset() {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, getWidth(), getHeight());
        }

        private void drawSkeleton() {
            gc.save();
            gc.setTransform(new Affine(new Rotate(45, centerW, centerH)));

            //BACKGROUND
            gc.setFill(Paint.valueOf("#FD6666"));//Red
            gc.fillRect(centerW - SIZE, centerH - SIZE, SIZE, SIZE);

            gc.setFill(Paint.valueOf("#6690FE"));//Blue
            gc.fillRect(centerW - SIZE, centerH, SIZE, SIZE);

            gc.setFill(Paint.valueOf("#FAFD66"));//Yellow
            gc.fillRect(centerW, centerH - SIZE, SIZE, SIZE);

            //Border
            gc.setStroke(Color.BLACK);
            gc.strokeRect(centerW - SIZE, centerH - SIZE, 2 * SIZE, 2 * SIZE);
            gc.strokeLine(centerW, centerH - SIZE, centerW, centerH + SIZE);
            gc.strokeLine(centerW - SIZE, centerH, centerW + SIZE, centerH);

            gc.restore();
        }

        private void drawData() {
            gc.setFill(Color.BLACK);
            Font arial = Font.font("Arial", FontWeight.BOLD, 34);
            gc.setFont(arial);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            for (Entry<Division, DivisionOption> entry : state.getState().entrySet()) {
                Division division = entry.getKey();
                DivisionOption divisionOption = entry.getValue();

                double x = centerW - division.x * diagonal / 2 + division.y * diagonal / 2;
                double y = centerH - diagonal / 2 + division.y * diagonal / 2 + division.x * diagonal / 2;

                gc.fillText(divisionOption.code.toUpperCase(), x, y);
            }

        }
    }
}
