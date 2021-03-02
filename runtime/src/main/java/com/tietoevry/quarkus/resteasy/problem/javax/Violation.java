package com.tietoevry.quarkus.resteasy.problem.javax;

public class Violation {

    public final String error;
    public final String field;

    public Violation(String error, String field) {
        this.error = error;
        this.field = field;
    }

}
