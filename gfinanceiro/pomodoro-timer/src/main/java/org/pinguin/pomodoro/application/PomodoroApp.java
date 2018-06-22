package org.pinguin.pomodoro.application;

import javax.persistence.EntityManager;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransitionRepository;
import org.pinguin.pomodoro.gui.main.MainPane;
import org.pinguin.pomodoro.gui.main.TaskRow;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PomodoroApp extends Application {

	private Injector injector;

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("PT");

		injector = Guice.createInjector(new JpaPersistModule("prod"));
		injector.getInstance(PersistService.class).start();

		injector.getInstance(EntityManager.class).getTransaction().begin();

		// FIXME: encontrar uma maneira mais elegante de fechar
		primaryStage.setOnCloseRequest(e -> {
			injector.getInstance(EntityManager.class).getTransaction().commit();
			System.exit(0);
		});

		final MainPane mainPane = injector.getInstance(MainPane.class);
		mainPane.setCallFocus(() -> {
			Platform.runLater(() -> {
				primaryStage.setIconified(false);
				primaryStage.requestFocus();
			});
		});
		mainPane.remainingProperty().addListener((r, o, n) -> primaryStage.setTitle("PT " + n));
		final TaskRepository taskRepo = injector.getInstance(TaskRepository.class);
		final ObservableList<TaskRow> items = FXCollections.observableArrayList();
		taskRepo.getAllUndone().forEach(t -> items.add(buildTaskRow(t)));

		mainPane.setItems(items);

		primaryStage.setScene(new Scene(mainPane));
		primaryStage.sizeToScene();
		Platform.runLater(primaryStage::centerOnScreen);
		primaryStage.show();
	}

	private TaskRow buildTaskRow(final Task task) {
		final TaskRow taskRow = new TaskRow(task);
		taskRow.stateProperty().addListener((r, o, n) -> injector.getInstance(TaskStateTransitionRepository.class)
				.add(new TaskStateTransition(task.getId(), o, n)));
		return taskRow;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
