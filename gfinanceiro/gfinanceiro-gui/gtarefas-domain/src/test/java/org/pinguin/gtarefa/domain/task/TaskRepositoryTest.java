package org.pinguin.gtarefa.domain.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pinguin.core.domain.Parameter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

import junit.framework.Assert;

public class TaskRepositoryTest {

	private Injector injector;

	public TaskRepositoryTest() {
		initResources();
	}

	private void initResources() {
		injector = Guice.createInjector(new JpaPersistModule("main"));
		injector.getInstance(PersistService.class).start();
	}

	@Before
	public void clearTaskTable() {
		TaskRepository repo = injector.getInstance(TaskRepository.class);
		List<Task> all = repo.retrieveAll();
		for (Task item : all) {
			repo.delete(item);
		}
	}

	@Test
	public void testRetrieveParentTasks() {
		TaskRepository repo = injector.getInstance(TaskRepository.class);
		Task task = new Task("Pai");
		Task subTask = new Task("Filho 1");
		Task subTask1 = new Task("Filho 2");
		Task subTask2 = new Task("Neto");
		task.getSubTasks().add(subTask);
		task.getSubTasks().add(subTask1);
		subTask.getSubTasks().add(subTask2);

		Task task2 = new Task("Pai2");

		repo.create(task);
		repo.create(task2);

		List<Task> retrieved = repo.retrieveAll();
		Assert.assertEquals(5, retrieved.size());

		// So' esta' filtrando os que nao tem filho!
		List<Task> retrieved2 = repo.retrieveByQuery("select t from Task t where t not in (select st from Task t2 inner join t2.subTasks st)");
		Assert.assertEquals(2, retrieved2.size());
		Set<String> names = new HashSet<>();
		for (Task item : retrieved2) {
			names.add(item.getName());
		}
		Assert.assertTrue(names.contains("Pai"));
		Assert.assertTrue(names.contains("Pai2"));
	}

	@Test
	public void testRetrieveRootTask() {
		TaskRepository repo = injector.getInstance(TaskRepository.class);
		Task task = new Task("Pai");
		Task subTask = new Task("Filho");
		Task subTask2 = new Task("Neto");
		task.getSubTasks().add(subTask);
		subTask.getSubTasks().add(subTask2);

		Task task2 = new Task("Pai2");

		repo.create(task);
		repo.create(task2);

		List<Task> retrieved = repo.retrieveAll();
		Assert.assertEquals(4, retrieved.size());

		List<Task> retrieved2 = repo.retrieveByQuery("select t from Task t where t not in (select st from Task t2 inner join t2.subTasks st)",
				new Parameter<Task>("subTask", subTask2));
		Assert.assertEquals(1, retrieved2.size());
		Assert.assertEquals("Pai", retrieved2.get(0).getName());

	}

}
