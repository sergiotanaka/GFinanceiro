package org.pinguin.gf.domain.common.impl;

import static com.querydsl.core.types.dsl.Expressions.constant;
import static com.querydsl.core.types.dsl.Expressions.predicate;

import java.math.BigDecimal;
import java.util.function.Function;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;

/**
 * Classe de apoio ao {@link FilterParser}.
 */
public class FilterParserHelper {

    /**
     * Funcao para recuperar o {@link Path} do field (String) passado.
     */
    private Function<String, Path< ? >> pathRetriever;

    /**
     * Construtor.
     */
    public FilterParserHelper() {
    }

    /**
     * Construtor.
     * 
     * @param pathRetriever {@link #pathRetriever}.
     */
    public FilterParserHelper(final Function<String, Path< ? >> pathRetriever) {
        this.pathRetriever = pathRetriever;
    }

    /**
     * Constroi um {@link Predicate} baseado em uma comparacao (campo operador valor).
     * 
     * @param field
     * @param operator
     * @param value
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public BooleanExpression buildPredicate(final String field, final String operator, final String value) {

        // Verificacao de pre-condicoes
        if (pathRetriever == null) {
            throw new IllegalStateException("Configuration exception. pathRetriever has not been initialized.");
        }

        final Path< ? > path = pathRetriever.apply(field);

        // Verificar se consegue recuperar um Path atravez do field.
        if (path == null) {
            throw new IllegalStateException("The field '" + field + "'  is not allowed for filters.");
        }

        // Tratamento para caracter de escape
        final String treated = value.replaceAll("\\\\;", ";").replaceAll("\\\\,", ",").replaceAll("\\\\\\(", "(")
            .replaceAll("\\\\\\)", ")");

        Ops op = null;
        if (operator.equals("==")) {
            op = Ops.EQ;
        } else if (operator.equals("!=")) {
            op = Ops.NE;
        } else if (operator.equals(">")) {
            op = Ops.GT;
        } else if (operator.equals("<")) {
            op = Ops.LT;
        } else if (operator.equals(">=")) {
            op = Ops.GOE;
        } else if (operator.equals("<=")) {
            op = Ops.LOE;
        } else if (operator.equals("=@")) {
            op = Ops.STRING_CONTAINS_IC;
        } else if (operator.equals("!@")) {
            op = Ops.STRING_CONTAINS_IC;
        } else {
            throw new IllegalStateException("Operador nao esperado: " + operator);
        }

        if (path.getType().equals(String.class)) {
            return predicate(op, path, constant(treated));
        } else if (Number.class.isAssignableFrom(path.getType())) {
            return predicate(op, path, constant(new BigDecimal(treated)));
        } else if (Boolean.class.isAssignableFrom(path.getType())) {
            return predicate(op, path, constant(Boolean.valueOf(treated)));
        } else if (Enum.class.isAssignableFrom(path.getType())) {
            return predicate(op, path, constant(Enum.valueOf(((EnumPath) path).getType(), treated)));
        } else {
            throw new IllegalStateException("Tipo nao tratado: " + path.getClass().getName());
        }
    }

}
