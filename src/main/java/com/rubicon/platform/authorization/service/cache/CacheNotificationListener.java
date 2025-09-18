package com.rubicon.platform.authorization.service.cache;

import java.io.Serializable;

/**
 * User: mhellkamp
 * Date: 10/18/12
 */
public interface CacheNotificationListener<ID extends Serializable>
{
	void onCreate(ID id);
	void onUpdate(ID id);
	void onDelete(ID id);
}
