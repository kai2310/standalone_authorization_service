package com.rubicon.platform.authorization.translator;

import java.util.HashMap;
import java.util.Map;

public class TranslationContext {
    private Map<String, Object> contextMap = new HashMap();

    public TranslationContext() {
    }

    public void putContextItem(String key, Object value) {
        this.contextMap.put(key, value);
    }

    public Object getContextItem(String key) {
        return this.contextMap.get(key);
    }
}
