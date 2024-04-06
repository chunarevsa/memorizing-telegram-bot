package com.memorizing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationEnvironmentConfig {
    private final String host;
    private final String coreServiceName;
    private final String gatewayPort;

    public ApplicationEnvironmentConfig(
            @Value("${api-gateway.port}")
            String gatewayPort,
            @Value("${api-gateway.name}")
            String gatewayServiceName,
            @Value("${core-service.name}")
            String coreServiceName) {
        this.coreServiceName = coreServiceName;
        this.host = gatewayServiceName;
        this.gatewayPort = gatewayPort;
    }

    public String getHost() {
        return host;
    }

    public String getCoreServiceName() {
        return coreServiceName;
    }

    public String getGatewayPort() {
        return gatewayPort;
    }
}
