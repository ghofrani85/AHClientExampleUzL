package eu.arrowhead.client.skeleton.common.serviceregistry.standalone;

import java.util.Map;

/**
 * Hold data for systems
 */
public class SystemDefinitionData {
    private String address;
    private int port;
    private boolean tokensecurityfilterEnabled = false;
    private boolean sslEnabled = false;
    private Map<String, ServiceDefinition> services;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isTokensecurityfilterEnabled() {
        return tokensecurityfilterEnabled;
    }

    public void setTokensecurityfilterEnabled(boolean tokensecurityfilterEnabled) {
        this.tokensecurityfilterEnabled = tokensecurityfilterEnabled;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public boolean isSslenabled() {
        return sslEnabled;
    }

    public void setSslenabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public Map<String, ServiceDefinition> getServices() {
        return services;
    }

    public void setServices(Map<String, ServiceDefinition> services) {
        this.services = services;
    }
}
