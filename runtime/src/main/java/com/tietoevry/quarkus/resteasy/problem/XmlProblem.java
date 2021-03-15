package com.tietoevry.quarkus.resteasy.problem;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.net.URI;
import java.util.Map;
import org.zalando.problem.Problem;

/**
 * Example from RFC doc:
 *
 * <problem xmlns="urn:ietf:rfc:7807">
 * <type>https://example.com/probs/out-of-credit</type>
 * <title>You do not have enough credit.</title>
 * <detail>Your current balance is 30, but that costs 50.</detail>
 * <instance>https://example.net/account/12345/msgs/abc</instance>
 * <balance>30</balance>
 * <accounts>
 * <i>https://example.net/account/12345</i>
 * <i>https://example.net/account/67890</i>
 * </accounts>
 * </problem>
 */
@JacksonXmlRootElement(localName = "problem")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XmlProblem {

    private static final URI DEFAULT_URI = URI.create("about:blank");

    public final String type;
    public final String status;
    public final String title;
    public final String detail;
    public final String instance;
    private final Map<String, Object> parameters;

    private XmlProblem(Problem problem) {
        if (problem.getType() != null && !problem.getType().equals(DEFAULT_URI)) {
            this.type = problem.getType().toASCIIString();
        } else {
            this.type = null;
        }

        this.status = (problem.getStatus() != null) ? String.valueOf(problem.getStatus().getStatusCode()) : null;
        this.title = problem.getTitle();
        this.detail = problem.getDetail();
        this.instance = problem.getInstance() != null ? problem.getInstance().toASCIIString() : null;
        this.parameters = problem.getParameters();
    }

    public static String serialize(Problem problem) {
        XmlMapper mapper = new XmlMapper();
        try {
            return mapper.writeValueAsString(new XmlProblem(problem));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
