package org.pinguin.pomodoro.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;

public class ReportService {
	@Inject
	private EntityManager em;

	/**
	 * Retorna o relatorio de Atividades por dia.
	 * 
	 * @return
	 */
	public List<DailyReportItem> retrieveDailyReport() {
		final Map<LocalDate, Map<Long, List<Period>>> map = new HashMap<>();

		for (TaskStateTransition transition : getHistory()) {
			if (transition.getTaskId() == null || transition.getTimeStamp() == null) {
				continue;
			}
			final LocalDate date = LocalDate.from(transition.getTimeStamp());
			if (!map.containsKey(date)) {
				map.put(date, new HashMap<>());
			}
			final Map<Long, List<Period>> dayMap = map.get(date);
			if (!dayMap.containsKey(transition.getTaskId())) {
				dayMap.put(transition.getTaskId(), new ArrayList<>());
			}
			final List<Period> periodList = dayMap.get(transition.getTaskId());
			switch (transition.getAfter()) {
			case DONE:
			case STOPPED:
				if (periodList.isEmpty()) {
					periodList.add(new Period());
				}
				final Period last = periodList.get(periodList.size() - 1);
				if (last.getEnd() != null) {
					System.out.println("warn! final do periodo preenchido.");
				} else {
					last.setEnd(transition.getTimeStamp());
				}
				break;
			case EXECUTING:
				final Period period = new Period();
				period.setStart(transition.getTimeStamp());
				periodList.add(period);
				break;
			default:
				break;
			}

		}

		final List<DailyReportItem> items = new ArrayList<>();

		for (Entry<LocalDate, Map<Long, List<Period>>> entry : map.entrySet()) {
			final DailyReportItem dateItem = new DailyReportItem(entry.getKey());
			items.add(dateItem);
			long dayEffort = 0L;
			for (Entry<Long, List<Period>> subEntry : entry.getValue().entrySet()) {
				final DailyReportItem taskItem = new DailyReportItem(subEntry.getKey());
				dateItem.getSubItems().add(taskItem);

				long taskEffort = 0L;
				for (Period period : subEntry.getValue()) {
					final DailyReportItem item = new DailyReportItem(period);
					taskItem.getSubItems().add(item);
					if (item.getDuration() != null) {
						taskEffort += item.getDuration();
					}
				}
				taskItem.setDuration(taskEffort);
				dayEffort += taskEffort;
			}
			dateItem.setDuration(dayEffort);
		}

		return items;
	}

	public List<TaskStateTransition> getHistory() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TaskStateTransition> cq = cb.createQuery(TaskStateTransition.class);
		final Root<TaskStateTransition> taskTransition = cq.from(TaskStateTransition.class);
		cq.select(taskTransition).orderBy(cb.asc(taskTransition.get("timeStamp")));
		return em.createQuery(cq).getResultList();
	}

	public static class DailyReportItem {
		private LocalDate date;
		private Long taskId;
		private LocalDateTime start;
		private LocalDateTime end;
		private Long duration;
		private List<DailyReportItem> subItems = new ArrayList<>();

		public DailyReportItem(Long taskId) {
			this.taskId = taskId;
		}

		public DailyReportItem(LocalDate date) {
			this.date = date;
		}

		public DailyReportItem(Period period) {
			this.start = period.getStart();
			this.end = period.getEnd();
			if (this.start != null && this.end != null) {
				this.duration = this.start.until(this.end, ChronoUnit.MINUTES);
			}
		}

		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		public Long getTaskId() {
			return taskId;
		}

		public void setTaskId(Long taskId) {
			this.taskId = taskId;
		}

		public LocalDateTime getStart() {
			return start;
		}

		public void setStart(LocalDateTime start) {
			this.start = start;
		}

		public LocalDateTime getEnd() {
			return end;
		}

		public void setEnd(LocalDateTime end) {
			this.end = end;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public List<DailyReportItem> getSubItems() {
			return subItems;
		}

		public void setSubItems(List<DailyReportItem> subItems) {
			this.subItems = subItems;
		}

		@Override
		public String toString() {
			return "DailyReportItem [date=" + date + ", taskId=" + taskId + ", start=" + start + ", end=" + end + "]";
		}
	}

	public static class Period {
		private LocalDateTime start;
		private LocalDateTime end;

		public LocalDateTime getStart() {
			return start;
		}

		public void setStart(LocalDateTime start) {
			this.start = start;
		}

		public LocalDateTime getEnd() {
			return end;
		}

		public void setEnd(LocalDateTime end) {
			this.end = end;
		}

		public long duration() {
			if (start == null) {
				return 0;
			}
			if (end == null) {
				return ChronoUnit.SECONDS.between(start, LocalDateTime.now());
			}
			return ChronoUnit.SECONDS.between(start, end);
		}

		@Override
		public String toString() {
			return "Period [start=" + start + ", end=" + end + "]";
		}

	}

}
