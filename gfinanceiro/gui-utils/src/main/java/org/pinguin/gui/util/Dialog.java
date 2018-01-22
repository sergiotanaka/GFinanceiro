package org.pinguin.gui.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Dialog extends Stage {

	private final Button buttonOk = new Button("Ok");
	private final Button buttonCancel = new Button("Cancel");
	private int result = 0;

	public Dialog(Window owner, String message) {
		initModality(Modality.WINDOW_MODAL);
		initOwner(owner);
		VBox vbox = new VBox(15);
		vbox.getChildren().add(new Label(message));
		HBox hBox = new HBox(15);
		hBox.setAlignment(Pos.TOP_RIGHT);
		hBox.getChildren().add(buttonOk);
		hBox.getChildren().add(buttonCancel);
		vbox.getChildren().add(hBox);

		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(15));

		setScene(new Scene(vbox));
	}

	public void setOnOkAction(EventHandler<ActionEvent> handler) {
		buttonOk.setOnAction(handler);
	}

	public void setOnCancelAction(EventHandler<ActionEvent> handler) {
		buttonCancel.setOnAction(handler);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public static int showQuestionDialog(Window owner, String message) {
		final Dialog dialog = new Dialog(owner, message);

		dialog.setOnOkAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent evt) {
				dialog.setResult(0);
				dialog.close();
			}
		});
		dialog.setOnCancelAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent evt) {
				dialog.setResult(-1);
				dialog.close();
			}
		});
		dialog.showAndWait();

		return dialog.getResult();
	}

}
