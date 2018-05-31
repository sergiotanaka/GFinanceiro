package org.pinguin.gf.domain.common.impl;

import static com.querydsl.core.types.dsl.Expressions.ONE;
import static com.querydsl.core.types.dsl.Expressions.predicate;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.ReflectionUtils;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;

/**
 * Mapeia os request parameters para filtrar no repositorio.
 *
 * @param <T>
 */
public class RequestParamsMapper<T> {

    /** Parser do filtro do RequestParameter. */
    private final FilterParser parser = new FilterParser();

    /** Funcao para recuperar o {@link Path} atravez do nome do campo. */
    private Function<String, Path< ? >> pathRetriever;

    /**
     * Construtor.
     * 
     * @param entityType
     */
    public RequestParamsMapper(final Class<T> entityType) {
        // Inicializando um pathRetriever generico.
        pathRetriever = new DefaultPathRetriever<>(entityType);
        // Construir um pathRetriever padrao, via reflexao.
        this.parser.setHelper(new FilterParserHelper(pathRetriever));
    }

    /**
     * @param pathRetriever {@link #pathRetriever}.
     */
    public void setPathRetriever(final Function<String, Path< ? >> pathRetriever) {
        this.pathRetriever = pathRetriever;
        this.parser.setHelper(new FilterParserHelper(pathRetriever));
    }

    /**
     * Gera um {@link Result} baseado nos parametros passados.
     * 
     * @param filters
     * @param sort
     * @param page
     * @param pageSize
     * @param fields
     * @return
     */
    public Result map(final Optional<String> filters, final Optional<String> sort, Optional<String> page,
                      Optional<String> pageSize, Optional<String> fields) {
        try {

            Predicate predicate = null;
            if (filters.isPresent()) {
                parser.ReInit(new StringReader(URLDecoder.decode(filters.get(), "UTF-8")));
                predicate = parser.expr();
            } else {
                // passando 1 = 1 para retornar todos os registros.
                predicate = predicate(Ops.EQ, ONE, ONE);
            }

            final int pageStart = Integer.parseInt(page.orElse("0"));
            final int pageLimit = Integer.parseInt(pageSize.orElse("1000"));

            return new Result(predicate, sort.isPresent() ? PageRequest.of(pageStart, pageLimit, parseSort(sort.get()))
                : PageRequest.of(pageStart, pageLimit), parseFields(fields));

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

    private List<Path< ? >> parseFields(Optional<String> fields) {
        if (fields.isPresent()) {
            final List<Path< ? >> fieldList = new ArrayList<>();
            for (final String field : fields.get().split(",")) {
                final Path< ? > path = pathRetriever.apply(field.trim());
                if (path == null) {
                    throw new IllegalStateException("The field '" + field + "'  is not allowed for fields.");
                }
                fieldList.add(path);
            }
            return fieldList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Agrupador de parametros a ser passado para executar filtros, paginacao, ordenacao e retricao de campos a serem
     * retornados.
     */
    @Immutable
    public static class Result {

        private final Predicate predicate;
        private final Pageable pageable;
        private final List<Path< ? >> fields = new ArrayList<>();

        public Result(Predicate predicate, Pageable pageable, List<Path< ? >> fields) {
            this.predicate = predicate;
            this.pageable = pageable;
            this.fields.addAll(fields);
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public Pageable getPageable() {
            return pageable;
        }

        public List<Path< ? >> getFields() {
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

    private static class DefaultPathRetriever<T> implements Function<String, Path< ? >> {

        private final Class<T> entityType;
        private final SimplePath<T> root;
        private final Map<String, Path< ? >> pathMap = new HashMap<>();

        public DefaultPathRetriever(final Class<T> entityType) {
            this.entityType = entityType;
            root = Expressions.path(entityType, entityType.getSimpleName().toLowerCase());
        }

        @Override
        public Path< ? > apply(String field) {
            if (!pathMap.containsKey(field)) {
                final Field classField = ReflectionUtils.findField(entityType, field);
                pathMap.put(field, Expressions.path(classField.getType(), root, field));
            }
            return pathMap.get(field);
        }

    }

}
