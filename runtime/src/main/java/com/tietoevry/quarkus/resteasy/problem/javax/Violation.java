package com.tietoevry.quarkus.resteasy.problem.javax;

import java.util.Locale;

public final class Violation {

    public static Violation inQuery(String message, String field) {
        return new Violation(message, field, In.QUERY);
    }

    public static Violation inPath(String message, String field) {
        return new Violation(message, field, In.PATH);
    }

    public static Violation inHeader(String message, String field) {
        return new Violation(message, field, In.HEADER);
    }

    public static Violation inBody(String message, String field) {
        return new Violation(message, field, In.BODY);
    }

    private enum In {
        QUERY,
        PATH,
        HEADER,
        BODY
    }

    public final String message;
    public final String field;
    public final String in;

    private Violation(String message, String field, In in) {
        this.message = message;
        this.field = field;
        this.in = in.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return "Violation{" +
                "message='" + message + '\'' +
                ", field='" + field + '\'' +
                ", in='" + in + '\'' +
                '}';
    }
}
