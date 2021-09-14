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
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main do Pomodoro Timer.
 */
public class PomodoroApp extends Application {

	private Injector injector;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		injector = Guice.createInjector(new JpaPersistModule("prod"));

		// Inicializando a persistencia
		final PersistService ps = injector.getInstance(PersistService.class);
		ps.start();
		final EntityManager em = injector.getInstance(EntityManager.class);
		em.getTransaction().begin();

		// Configurando a janela principal
		primaryStage.setTitle("PT");
		primaryStage.getIcons().add(new Image(MainPane.class.getResourceAsStream("/META-INF/256x256bb.jpg")));
		primaryStage.setOnCloseRequest(e -> {
			em.getTransaction().commit();
			ps.stop();
		});

		// Inicializando o painel principal
		final MainPane mainPane = injector.getInstance(MainPane.class);
		mainPane.setCallFocus(new FocusRequester(primaryStage));
		mainPane.remainingProperty().addListener((r, o, n) -> primaryStage.setTitle("PT " + n));
		final ObservableList<TaskRow> items = FXCollections.observableArrayList();
		injector.getInstance(TaskRepository.class).getAllUndone()
				.forEach(t -> items.add(buildTaskRow(t, mainPane::updateExecutingTasks, mainPane)));
		mainPane.setItems(items);
		mainPane.updateExecutingTasks();

		// Posicionando e visualizando
		primaryStage.setScene(new Scene(mainPane));
		primaryStage.sizeToScene();
		Platform.runLater(primaryStage::centerOnScreen);
		primaryStage.show();
	}

	private TaskRow buildTaskRow(final Task task, final Runnable notifyChangeState, MainPane mainPane) {
		final TaskRow taskRow = new TaskRow(task);
		taskRow.stateProperty().addListener((r, o, n) -> {
			injector.getInstance(TaskStateTransitionRepository.class).add(new TaskStateTransition(task.getId(), o, n));
			notifyChangeState.run();
		});
		taskRow.estimatedProperty().addListener((r, o, n) -> {
			mainPane.updateParentEstimated(taskRow);
		});
		return taskRow;
	}

	private static class FocusRequester implements Runnable {

		private final Stage stage;

		public FocusRequester(final Stage stage) {
			this.stage = stage;
		}

		@Override
		public void run() {
			Platform.runLater(() -> {
				stage.setIconified(false);
				stage.requestFocus();
			});
		}

	}

}
