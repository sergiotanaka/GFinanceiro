package org.pinguin.gtarefa.domain.appointment;

import org.pinguin.core.domain.RepositoryBase;

public class AppointmentRepository extends RepositoryBase<Appointment, Long> {

	@Override
	protected Class<Appointment> getEntityType() {
		return Appointment.class;
	}

}
