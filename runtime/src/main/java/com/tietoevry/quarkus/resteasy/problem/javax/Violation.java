package com.tietoevry.quarkus.resteasy.problem.javax;

public final class Violation {

    public final String field;
    public final String message;

    /**
     * Deprecated, use message instead.
     */
    @Deprecated
    public final String error;

    public Violation(String message, String field) {
        this.field = field;
        this.message = message;

        this.error = message;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "message='" + message + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
