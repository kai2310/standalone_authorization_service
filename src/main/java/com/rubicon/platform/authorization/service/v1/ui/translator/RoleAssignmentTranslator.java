package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.cache.AccountGroupObjectCache;
import com.rubicon.platform.authorization.service.cache.AccountObjectCache;
import com.rubicon.platform.authorization.service.cache.BaseRoleObjectCache;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.rubicon.platform.authorization.hyperion.cache.CacheHelper;
import net.sf.ehcache.Ehcache;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION;

public class RoleAssignmentTranslator
        extends DefaultObjectTranslator<RoleAssignment, com.rubicon.platform.authorization.model.data.acm.RoleAssignment>
{
    private BaseRoleObjectCache<com.rubicon.platform.authorization.model.data.acm.Role> roleCache;
    private AccountObjectCache accountCache;
    private AccountGroupObjectCache accountGroupCache;
    private Ehcache deletedAccountCache;

    public RoleAssignmentTranslator()
    {
        super(RoleAssignment.class, com.rubicon.platform.authorization.model.data.acm.RoleAssignment.class);
    }

    public RoleAssignmentTranslator(BaseRoleObjectCache<com.rubicon.platform.authorization.model.data.acm.Role> roleCache,
                                    AccountObjectCache accountCache,
                                    AccountGroupObjectCache accountGroupCache,
                                    Ehcache deleteAccountCache)
    {
        super(RoleAssignment.class, com.rubicon.platform.authorization.model.data.acm.RoleAssignment.class);
        this.roleCache = roleCache;
        this.accountCache = accountCache;
        this.accountGroupCache = accountGroupCache;
        this.deletedAccountCache = deleteAccountCache;
    }

    @Override
    public RoleAssignment convertPersistent(com.rubicon.platform.authorization.model.data.acm.RoleAssignment persistent,
                                            TranslationContext translationContext)
    {
        RoleAssignment roleAssignment = super.convertPersistent(persistent, translationContext);
        RoleTypePermission roleTypePermission =
                (RoleTypePermission) translationContext.getContextItem(TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION);

        // Translate The Role
        com.rubicon.platform.authorization.model.data.acm.Role role = roleCache.getItemById(persistent.getRoleId());
        roleAssignment.setRole(convertPersistentRole(role));

        // Translate the Account
        roleAssignment.setAccountReference(
                convertPersistentAccount(persistent.getAccount(), persistent.getAccountGroupId(), persistent));


        RoleTypeEnum roleType = RoleTypeEnum.getById(role.getRoleTypeId());
        roleAssignment.setEditable(roleTypePermission.isRoleTypeEditable(roleType));

        return roleAssignment;
    }


    protected Reference convertPersistentRole(com.rubicon.platform.authorization.model.data.acm.Role role)
    {
        Reference reference = new Reference();
        reference.setId(role.getId());
        reference.setName(role.getLabel());

        return reference;
    }

    protected AccountReference convertPersistentAccount(String accountId, Long accountGroupId,
                                                        com.rubicon.platform.authorization.model.data.acm.RoleAssignment roleAssignment)
    {
        AccountReference accountReference = new AccountReference();
        // if account group is is null, assume we are dealing with accounts
        if (accountGroupId != null)
        {
            AccountGroup group = accountGroupCache.getItemById(accountGroupId);
            accountReference.setId(group.getId());
            accountReference.setName(group.getLabel());
            accountReference.setType(AccountReferenceTypeEnum.group);
        }
        else
        {
            CompoundId compoundAccountId = new CompoundId(accountId);
            com.rubicon.platform.authorization.model.data.acm.Account account = getAccountData(compoundAccountId);

            accountReference.setId(account.getId());
            accountReference.setName(account.getAccountName());
            accountReference.setType(AccountReferenceTypeEnum.getByIdType(compoundAccountId.getIdType()));
        }

        return accountReference;
    }

    protected com.rubicon.platform.authorization.model.data.acm.Account getAccountData(CompoundId compoundAccountId)
    {
        com.rubicon.platform.authorization.model.data.acm.Account account = accountCache.getByAccountId(compoundAccountId);

        // You need to get the account from the loader if we can not find the account.
        if (account == null)
        {
            account = CacheHelper.get(deletedAccountCache, compoundAccountId);
        }


        return account;
    }

}
