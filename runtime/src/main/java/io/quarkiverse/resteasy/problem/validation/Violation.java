package io.quarkiverse.resteasy.problem.validation;

public final class Violation {

    public enum In {
        query,
        path,
        header,
        form,
        body,
        unknown() {
            @Override
            protected String serialize() {
                return "?";
            }
        };

        public MessageSupplier field(String field) {
            return message -> new Violation(field, this.serialize(), message);
        }

        protected String serialize() {
            return name();
        }
    }

    public interface MessageSupplier {
        Violation message(String message);
    }

    public final String field;
    public final String in;
    public final String message;

    private Violation(String field, String in, String message) {
        this.field = field;
        this.in = in;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "field='" + field + '\'' +
                ", in='" + in + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
