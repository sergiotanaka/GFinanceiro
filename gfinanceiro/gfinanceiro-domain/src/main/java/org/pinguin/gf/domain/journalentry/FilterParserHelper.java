package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.util.function.Function;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Classe de apoio ao {@link FilterParser}.
 */
public class FilterParserHelper {

	private Function<String, Path<?>> pathRetriever;

	public FilterParserHelper() {
	}

	public FilterParserHelper(final Function<String, Path<?>> pathRetriever) {
		this.pathRetriever = pathRetriever;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BooleanExpression buildPredicate(String field, String operator, String value) {
		// PRE-CONDICOES
		if (pathRetriever == null) {
			throw new IllegalStateException("Configuration exception. pathRetriever has not been initialized.");
		}

		final Path<?> path = pathRetriever.apply(field);

		// Verificacao
		if (path == null) {
			throw new IllegalStateException("The field '" + field + "'  is not allowed for filters.");
		}

		if (path instanceof StringPath) {
			final StringPath strPath = (StringPath) path;
			if (operator.equals("==")) {
				return strPath.eq(value);
			} else if (operator.equals("!=")) {
				return strPath.ne(value);
			} else if (operator.equals(">")) {
				return strPath.gt(value);
			} else if (operator.equals("<")) {
				return strPath.lt(value);
			} else if (operator.equals(">=")) {
				return strPath.goe(value);
			} else if (operator.equals("<=")) {
				return strPath.loe(value);
			} else if (operator.equals("=@")) {
				return strPath.containsIgnoreCase(value);
			} else if (operator.equals("!@")) {
				return strPath.containsIgnoreCase(value).not();
			}
			return null;
		} else if (path instanceof NumberPath) {
			final NumberPath numPath = (NumberPath) path;
			if (operator.equals("==")) {
				return numPath.eq(new BigDecimal(value));
			} else if (operator.equals("!=")) {

			} else if (operator.equals(">")) {

			} else if (operator.equals("<")) {

			} else if (operator.equals(">=")) {

			} else if (operator.equals("<=")) {

			} else if (operator.equals("=@")) {

			} else if (operator.equals("!@")) {

			}
		} else if (path instanceof BooleanPath) {
			final BooleanPath boolPath = (BooleanPath) path;
			if (operator.equals("==")) {
				return boolPath.eq(Boolean.valueOf(value));
			} else if (operator.equals("!=")) {
				return boolPath.ne(Boolean.valueOf(value));
			}
		} else if (path instanceof EnumPath) {
			final EnumPath enumPath = (EnumPath) path;
			if (operator.equals("==")) {
				return enumPath.eq(Enum.valueOf(enumPath.getType(), value));
			} else if (operator.equals("!=")) {
				return enumPath.ne(Enum.valueOf(enumPath.getType(), value));
			}
		}

		return null;
	}

}
