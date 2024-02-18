package org.memorizing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationEnvironmentConfig {

    private final String localhost;
    private final String coreServiceName;
    private final String gatewayPort;

    public ApplicationEnvironmentConfig(
            @Value("${localhost}")
            String localhost,
            @Value("${core-service.name}")
            String serviceName,
            @Value("${api-gateway.port}")
            String gatewayPort) {
        this.localhost = localhost;
        this.coreServiceName = serviceName;
        this.gatewayPort = gatewayPort;
    }

    public String getLocalhost() {
        return localhost;
    }

    public String getCoreServiceName() {
        return coreServiceName;
    }

    public String getGatewayPort() {
        return gatewayPort;
    }
}
