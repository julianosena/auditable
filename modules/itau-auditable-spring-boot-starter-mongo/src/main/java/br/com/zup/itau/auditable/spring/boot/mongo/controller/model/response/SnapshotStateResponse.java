package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import java.util.Map;

public class SnapshotStateResponse {
    private final Map<String, Object> properties;

    public SnapshotStateResponse(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
