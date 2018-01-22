package org.pinguin.gtarefa.domain.task;

import org.pinguin.core.domain.RepositoryBase;

public class TaskStatusChangeRepository extends RepositoryBase<TaskStatusChange, Long> {

	@Override
	protected Class<TaskStatusChange> getEntityType() {
		return TaskStatusChange.class;
	}

}
