package com.gmats.st.group.entity;

public enum GroupQueueEnum {
    GROUP_DATABASE("sa-group-db.queue", "sa-group-db.queue");

    private final String configKey;
    private final String defaultValue;

    private GroupQueueEnum(final String configKey, final String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
}
