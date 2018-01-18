package io.nuls.cache.service.intf;

import io.nuls.cache.entity.CacheElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Niels
 * @date 2017/10/18
 */
public interface CacheService<K, V> {

    /**
     * create a cache named title
     *
     * @param title
     */
    void createCache(String title,int heapMb);

    /**
     * create a cache named title by configurations
     *
     * @param title
     * @param initParams
     */
    void createCache(String title, Map<String, Object> initParams);

    void createCache(String title, int heapMb, int timeToLiveSeconds, int timeToIdleSeconds);


    /**
     * remove a cache by title
     *
     * @param title
     */
    void removeCache(String title);

    /**
     * put data to a cache
     *
     * @param cacheTitle
     * @param key
     * @param value
     */
    void putElement(String cacheTitle, K key, Object value);

    /**
     * put data to a cache
     *
     * @param element
     */
    void putElement(CacheElement element);

    /**
     * get data from the cache named cacheTitle
     *
     * @param cacheTitle
     * @param key
     * @return
     */
    V getElement(String cacheTitle, K key);

    List<V> getElementList(String cacheTitle);

    /**
     * remove an element from the cache named cacheTitle
     *
     * @param cacheTitle
     * @param key
     */
    void removeElement(String cacheTitle, K key);

    /**
     * @param title
     */
    void clearCache(String title);

    List<String> getCacheTitleList();


    boolean containsKey(String cacheTitle, K key);

    Set<K> keySet(String cacheTitle);
}
