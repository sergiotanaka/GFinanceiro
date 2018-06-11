package org.pinguin.pomodoro.application;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.pinguin.pomodoro.domain.task.Task;
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

		injector = Guice.createInjector(new JpaPersistModule("prod"));
		injector.getInstance(PersistService.class).start();

		final EntityManager em = injector.getInstance(EntityManager.class);
		em.getTransaction().begin();

		// FIXME: encontrar uma maneira mais elegante de fechar
		primaryStage.setOnCloseRequest(e -> {
			em.getTransaction().commit();
			System.exit(0);
		});

		final MainPane mainPane = injector.getInstance(MainPane.class);
		final ObservableList<TaskRow> items = FXCollections.observableArrayList();
		findAllTasks().forEach(t -> items.add(new TaskRow(t)));
		mainPane.setItems(items);

		primaryStage.setTitle("Pomodoro Timer");
		primaryStage.setScene(new Scene(mainPane));
		primaryStage.sizeToScene();
		Platform.runLater(primaryStage::centerOnScreen);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public List<Task> findAllTasks() {
		final EntityManager em = injector.getInstance(EntityManager.class);

		CriteriaQuery<Task> cq = em.getCriteriaBuilder().createQuery(Task.class);
		cq.select(cq.from(Task.class));

		return em.createQuery(cq).getResultList();
	}

}
