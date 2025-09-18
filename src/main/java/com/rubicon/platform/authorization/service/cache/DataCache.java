package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.BaseApiObject;

import java.io.Serializable;

/**
 * User: mhellkamp
 * Date: 10/25/12
 */
public interface DataCache<T extends BaseApiObject, CID extends Serializable>
{
	T getItemById(CID id);
}
