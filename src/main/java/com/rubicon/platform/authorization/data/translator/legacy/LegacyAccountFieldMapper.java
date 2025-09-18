package com.rubicon.platform.authorization.data.translator.legacy;

import com.dottydingo.hyperion.api.exception.InternalException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultPropertyChangeEvaluator;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.dottydingo.hyperion.core.translation.PropertyChangeEvaluator;
import com.rubicon.platform.authorization.data.api.legacy.RoleAssignment_v1;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.translator.BaseRoleAssignmentTranslator;
import com.rubicon.platform.authorization.data.translator.IdParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 */
@Component
public class LegacyAccountFieldMapper implements FieldMapper<RoleAssignment_v1,PersistentRoleAssignment>
{
    private IdParser idParser = IdParser.STANDARD_ID_PARSER;
    protected PropertyChangeEvaluator propertyChangeEvaluator = new DefaultPropertyChangeEvaluator();

    @Resource(name="accountGroupMap")
    private Map<String,Long> accountGroupMap;

    public void setAccountGroupMap(Map<String, Long> accountGroupMap)
    {
        this.accountGroupMap = accountGroupMap;
    }

    @Override
    public String getClientFieldName()
    {
        return "account";
    }

    @Override
    public void convertToClient(ObjectWrapper<PersistentRoleAssignment> persistentWrapper,
                                ObjectWrapper<RoleAssignment_v1> clientWrapper, PersistenceContext persistenceContext)
    {
        PersistentRoleAssignment persistent = persistentWrapper.getWrappedObject();
        RoleAssignment_v1 client = clientWrapper.getWrappedObject();
        CompoundId account = persistent.getAccount();
        if(account == null)
            return;

        if(account.getIdType().equals(""))
        {
            PersistentAccountGroup group = persistent.getAccountGroup();
            if(group != null && StringUtils.isNotEmpty(group.getAccountType()))
            {
                client.setAccount(group.getAccountType() + "/*");
            }
        }
        else
            client.setAccount(account.asIdString());

    }

    @Override
    public boolean convertToPersistent(ObjectWrapper<RoleAssignment_v1> clientWrapper,
                                       ObjectWrapper<PersistentRoleAssignment> persistentWrapper,
                                       PersistenceContext persistenceContext)
    {
        RoleAssignment_v1 client = clientWrapper.getWrappedObject();
        if(client.getAccount() == null)
            return false;

        PersistentRoleAssignment persistent = persistentWrapper.getWrappedObject();

        CompoundId id = idParser.parseId(client.getAccount());
        if(!id.hasWildcards())
        {
            CompoundId existing = persistent.getAccount();
            if(propertyChangeEvaluator.hasChanged(existing,id))
            {
                persistent.setAccount(id);
                persistent.setAccountGroupId(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP);
                return true;
            }

            return false;
        }

        Long accountGroupId = accountGroupMap.get(id.getIdType().toLowerCase());

        // this should never happen because of validation
        if(accountGroupId == null)
            throw new InternalException(String.format("Missing default accountGroupId for accountType:%s",id.getIdType()));


        if(propertyChangeEvaluator.hasChanged(persistent.getAccountGroupId(),accountGroupId))
        {
            persistent.setAccountGroupId(accountGroupId);
            persistent.setAccount(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT);
            return true;
        }


        return false;
    }
}
