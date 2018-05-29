package org.pinguin.gf.domain.journalentry;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Mapeia os request parameters para filtrar no repositorio.
 */
@Component
public class RequestParamsMapper {

	private Function<String, Path<?>> pathRetriever;

	public void setPathRetriever(Function<String, Path<?>> pathRetriever) {
		this.pathRetriever = pathRetriever;
	}

	/**
	 * Map completo.
	 * 
	 * @param filters
	 * @param sort
	 * @param start
	 * @param limit
	 * @param fields
	 * @return
	 */
	public Result map(final Optional<String> filters, final Optional<String> sort, Optional<String> start,
			Optional<String> limit, Optional<String> fields) {
		try {
			BooleanExpression predicate = null;
			if (filters.isPresent()) {
				final String decoded = URLDecoder.decode(filters.get(), "UTF-8");
				FilterParser<JournalEntry> parser = new FilterParser<>(new StringReader(decoded));
				parser.setHelper(new FilterParserHelper(pathRetriever));
				predicate = parser.expr();
			}
			int pageStart = Integer.parseInt(start.orElse("0"));
			int pageLimit = Integer.parseInt(limit.orElse("1000"));

			return new Result(predicate, sort.isPresent() ? PageRequest.of(pageStart, pageLimit, parseSort(sort.get()))
					: PageRequest.of(pageStart, pageLimit));

		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Sort parseSort(final String sort) {
		final List<Order> orders = new ArrayList<>();
		for (final String field : sort.split(",")) {
			final String trimmed = field.trim();
			if (trimmed.startsWith("-")) {
				orders.add(Order.desc(trimmed.substring(1)));
			} else {
				orders.add(Order.by(trimmed));
			}
		}
		return Sort.by(orders);
	}

	@Immutable
	public static class Result {
		private final Predicate predicate;
		private final Pageable pageable;
		private final List<StringPath> fields = new ArrayList<StringPath>();

		public Result(Predicate predicate, Pageable pageable, StringPath... fields) {
			this.predicate = predicate;
			this.pageable = pageable;
			this.fields.addAll(Arrays.asList(fields));
		}

		public Predicate getPredicate() {
			return predicate;
		}

		public Pageable getPageable() {
			return pageable;
		}

		public List<StringPath> getFields() {
			return Collections.unmodifiableList(fields);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Result [predicate=");
			builder.append(predicate);
			builder.append(", pageable=");
			builder.append(pageable);
			builder.append(", fields=");
			builder.append(fields);
			builder.append("]");
			return builder.toString();
		}
	}
}
