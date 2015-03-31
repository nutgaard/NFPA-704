package no.utgdev.nfpa;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import no.utgdev.nfpa.model.ApplicationState;
import no.utgdev.nfpa.model.Division;
import no.utgdev.nfpa.model.DivisionOption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiamondSettingsPanel extends VBox {
    private ApplicationState state;
    private Map<Integer, ToggleGroup> groups;

    public DiamondSettingsPanel(List<Division> divisions, ApplicationState state) {
        super(0);
        this.groups = new HashMap<>();
        this.state = state;
        this.state.addListener(this::selectByState);
        initializeComponents(divisions);
    }

    private void initializeComponents(List<Division> divisions) {
        boolean isFirst = true;
        for (Division division : divisions) {
            this.getChildren().add(createLabel(division, isFirst));
            isFirst = false;

            FlowPane flowLayout = new FlowPane();
            ToggleGroup group = new ToggleGroup();
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                state.update(division.id, ((DivisionOption) newValue.getUserData()));
            });
            this.groups.put(division.id, group);

            for (DivisionOption option : division.options) {
                ToggleButton button = createRadiobutton(option, group);
                button.setPrefWidth(80);
                button.setToggleGroup(group);
                button.setAlignment(Pos.CENTER);
                flowLayout.getChildren().addAll(button);
            }

            if (!division.options.isEmpty()) {
                group.selectToggle(group.getToggles().get(0));
            }

            this.getChildren().addAll(flowLayout);
        }
    }

    private void selectByState(int divisionId, int optionIndex) {
        ToggleGroup group = groups.get(divisionId);
        int currentIndex = group.getToggles().indexOf(group.getSelectedToggle());

        if (currentIndex != optionIndex) {
            group.selectToggle(group.getToggles().get(optionIndex));
            state.update(divisionId, optionIndex);
        }
    }

    private Label createLabel(Division division, boolean first) {
        Label label = new Label(division.name);
        label.setTooltip(new Tooltip(division.description));
        if (!first) {
            label.getStyleClass().addAll("notfirst");
        }
        return label;
    }

    private ToggleButton createRadiobutton(DivisionOption option, ToggleGroup group) {
        ToggleButton toggleButton = new ToggleButton(option.code);
        Tooltip tooltip = new Tooltip(option.description);
        toggleButton.setTooltip(tooltip);
        toggleButton.setUserData(option);
        toggleButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (toggleButton.equals(group.getSelectedToggle())) {
                event.consume();
            }
        });

        return toggleButton;
    }
}
