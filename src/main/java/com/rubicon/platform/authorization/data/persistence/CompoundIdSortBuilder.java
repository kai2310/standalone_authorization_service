package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.jpa.persistence.sort.JpaEntitySortBuilder;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class CompoundIdSortBuilder implements JpaEntitySortBuilder
{
    private String propertyName;

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    @Override
    public List<Order> buildOrder(boolean desc, CriteriaBuilder cb, From root)
    {
        Path path = root.get(propertyName);
        Path id = root.get(propertyName + "Numeric");

        List<Order> list = new ArrayList<Order>();
        list.add(desc ? cb.desc(path.get("idType")) : cb.asc(path.get("idType")));
        list.add(desc ? cb.desc(id) : cb.asc(id));
        return list;
    }
}
