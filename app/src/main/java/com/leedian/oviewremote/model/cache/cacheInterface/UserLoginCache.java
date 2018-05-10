package com.leedian.oviewremote.model.cache.cacheInterface;

import com.leedian.oviewremote.model.dataout.UserInfoModel;

/**
 * User cache interface
 *
 * @author Franco
 */
public interface UserLoginCache {
    UserInfoModel getCache();

    void writeCache(UserInfoModel user);

    void cleanCache();

    boolean isCached();
}
