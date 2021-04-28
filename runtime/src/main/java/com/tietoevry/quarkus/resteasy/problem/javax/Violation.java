package com.tietoevry.quarkus.resteasy.problem.javax;

public final class Violation {

    public enum In {
        query,
        path,
        header,
        form,
        body,
        unknown;

        public MessageSupplier violation(String field) {
            return message -> new Violation(message, field, this);
        }
    }

    public interface MessageSupplier {
        Violation message(String message);
    }

    public final String message;
    public final String field;
    public final In in;

    private Violation(String message, String field, In in) {
        this.message = message;
        this.field = field;
        this.in = in;
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
