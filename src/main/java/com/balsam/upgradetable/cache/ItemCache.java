package com.balsam.upgradetable.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ItemCache<K, V> {

    protected Map<K, V> cache = new ConcurrentHashMap<>();
    protected Map<K, Long> timeCache = new ConcurrentHashMap<>();

    /**
     * 是否需要自动过期
     */
    private boolean needAutoRemove;
    /**
     * 过期延迟时间
     */
    private int delayTimes;

    public ItemCache() {
        this(false, 0);
    }

    public ItemCache(boolean needAutoRemove, int delayTimes) {
        this.needAutoRemove = needAutoRemove;
        this.delayTimes = delayTimes;
    }

    public void setValue(K k, V v){
        cache.put(k, v);
        if (needAutoRemove) {
            timeCache.put(k, System.currentTimeMillis());
            tryClear();
        }
    }

    public V getValue(K k){
        return cache.get(k);
    }

    public void removeValue(K k){
        cache.remove(k);
        if (needAutoRemove) {
            timeCache.remove(k);
        }
    }

    private void tryClear(){
        for (Map.Entry<K, Long> entry : timeCache.entrySet()) {
            if (entry.getValue() > System.currentTimeMillis() + delayTimes){
                removeValue(entry.getKey());
            }
        }
    }
}
