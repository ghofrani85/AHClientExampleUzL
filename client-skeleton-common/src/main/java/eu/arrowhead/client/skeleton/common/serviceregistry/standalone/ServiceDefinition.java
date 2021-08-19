package eu.arrowhead.client.skeleton.common.serviceregistry.standalone;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Hold service definition
 */
public class ServiceDefinition {
    private String interfaceInsecure = "HTTP-INSECURE-JSON";
    private String interfaceSecure = "HTTP-SECURE-JSON";
    private String serviceUri;
    private int version = 1;
    private HttpMethod httpMethod = HttpMethod.GET;
    private Map<String, String> metadata;

    public String getInterfaceInsecure() {
        return interfaceInsecure;
    }

    public void setInterfaceInsecure(String interfaceInsecure) {
        this.interfaceInsecure = interfaceInsecure;
    }

    public String getInterfaceSecure() {
        return interfaceSecure;
    }

    public void setInterfaceSecure(String interfaceSecure) {
        this.interfaceSecure = interfaceSecure;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
    }

    public Map<String, String> getMetadata() {
        return (metadata.isEmpty() ? null : metadata);
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
