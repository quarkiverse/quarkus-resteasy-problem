package com.tietoevry.quarkus.resteasy.problem;

import static jakarta.ws.rs.core.Response.Status.*;

import jakarta.ws.rs.core.Response;

public enum ExtendedStatus implements Response.StatusType {

    /**
     * 207 Multi-Status, see {@link <a href="https://www.rfc-editor.org/rfc/rfc4918#section-11.1">WebDAV documentation</a>}.
     */
    MULTI_STATUS(207, "Multi-Status"),
    /**
     * 422 Unprocessable Entity, see {@link <a href="https://www.rfc-editor.org/rfc/rfc4918#section-11.2">WebDAV
     * documentation</a>}.
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    /**
     * 423 Unprocessable Entity, see {@link <a href="https://www.rfc-editor.org/rfc/rfc4918#section-11.3">WebDAV
     * documentation</a>}.
     */
    LOCKED(423, "Locked"),
    /**
     * 424 Locked, see {@link <a href="https://www.rfc-editor.org/rfc/rfc4918#section-11.4">WebDAV documentation</a>}.
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    /**
     * 507 Insufficient Storage, see {@link <a href="https://www.rfc-editor.org/rfc/rfc4918#section-11.5">WebDAV
     * documentation</a>}.
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage");

    private final int code;
    private final String reason;
    private final Family family;

    ExtendedStatus(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        this.family = Family.familyOf(statusCode);
    }

    /**
     * Get the class of status code.
     *
     * @return the class of status code.
     */
    @Override
    public Family getFamily() {
        return family;
    }

    /**
     * Get the associated status code.
     *
     * @return the status code.
     */
    @Override
    public int getStatusCode() {
        return code;
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String getReasonPhrase() {
        return toString();
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String toString() {
        return reason;
    }

    /**
     * Convert a numerical status code into the corresponding Status.
     *
     * @param statusCode the numerical status code.
     * @return the matching Status or null is no matching Status is defined.
     */
    public static Response.StatusType fromStatusCode(final int statusCode) {
        for (ExtendedStatus s : ExtendedStatus.values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return Response.Status.fromStatusCode(statusCode);
    }
}
