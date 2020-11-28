package net.silthus.rccities.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

    public CaseInsensitiveMap() {

    }

    public CaseInsensitiveMap(int initialCapacity) {

        super(initialCapacity);
    }

    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {

        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveMap(Map<? extends String, ? extends V> m) {

        super(m.size());
        putAll(m);
    }

    @Override
    public V put(String key, V value) {

        return super.put(key.toLowerCase(), value);
    }

    @Override
    public V get(Object key) {

        return super.get(key == null ? null : key.toString().toLowerCase());
    }

    @Override
    public boolean containsKey(Object key) {

        return super.containsKey(key == null ? null : key.toString().toLowerCase());
    }

    @Override
    public V remove(Object key) {

        return super.remove(key == null ? null : key.toString().toLowerCase());
    }
}
