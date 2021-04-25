package com.tietoevry.quarkus.resteasy.problem.javax;

import java.util.Locale;

public final class Violation {

    public enum In {
        QUERY,
        PATH,
        HEADER,
        FORM,
        BODY;

        public Violation violation(String message, String field) {
            return new Violation(message, field, this);
        }
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
