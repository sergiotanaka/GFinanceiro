package org.pinguin.pomodoro.gui.description;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.pinguin.pomodoro.domain.task.Task;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class DescriptionPane extends BorderPane {

	@Inject
	private EntityManager em;

	private final Property<Task> taskProp = new SimpleObjectProperty<>();
	private Consumer<Void> closeAction;

	public DescriptionPane() {
		try {
			this.setPadding(new Insets(10.0));
			final TextArea text = new TextArea();
			final Button saveBtn = new Button("Salvar");
			final Button clearBtn = new Button("Limpar");
			final HBox buttons = new HBox(10.0);
			buttons.setPadding(new Insets(10.0));
			buttons.getChildren().addAll(saveBtn, clearBtn);
			this.setCenter(text);
			this.setBottom(buttons);

			taskProp.addListener((r, o, n) -> {
				text.setText(n.getDescription());
			});

			saveBtn.setOnAction(e -> {
				final Task task = taskProp.getValue();
				task.setDescription(text.getText());
				em.persist(task);
				if (closeAction != null) {
					closeAction.accept(null);
				}
			});
			clearBtn.setOnAction(e -> text.setText(""));

		} catch (Exception e) {
			throw e;
		}
	}

	public Property<Task> taskProperty() {
		return taskProp;
	}

	public void setCloseAction(Consumer<Void> closeAction) {
		this.closeAction = closeAction;
	}
}
