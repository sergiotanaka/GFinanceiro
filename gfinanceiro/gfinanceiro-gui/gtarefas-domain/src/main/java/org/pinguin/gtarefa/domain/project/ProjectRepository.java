package org.pinguin.gtarefa.domain.project;

import org.pinguin.core.domain.RepositoryBase;

public class ProjectRepository extends RepositoryBase<Project, Long> {

	@Override
	protected Class<Project> getEntityType() {
		return Project.class;
	}

}
