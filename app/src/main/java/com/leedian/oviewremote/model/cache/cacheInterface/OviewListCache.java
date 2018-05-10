package com.leedian.oviewremote.model.cache.cacheInterface;

import java.util.List;

import com.leedian.oviewremote.model.dataout.OviewListModel;

/**
 * List cache interface for Oview list
 *
 * @author Franco
 */
public interface OviewListCache {
    List<OviewListModel> getCache();

    List<OviewListModel> getCacheWithDeleteItem(String zipKey);

    void addCache(List<OviewListModel> list);

    void writeCache(List<OviewListModel> list);

    void cleanCache();

    void backupCache();

    void restoreCache();
}
