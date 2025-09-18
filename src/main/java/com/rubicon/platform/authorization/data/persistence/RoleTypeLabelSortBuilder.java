package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.jpa.persistence.sort.JpaEntitySortBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;

public class RoleTypeLabelSortBuilder implements JpaEntitySortBuilder
{
    @Override
    public List<Order> buildOrder(boolean desc, CriteriaBuilder cb, From root)
    {
        Path id = root.get("roleType").get("label");

        List<Order> list = new ArrayList<>();
        list.add(desc ? cb.desc(id) : cb.asc(id));
        return list;
    }
}
