package com.rubicon.platform.authorization;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.api.BaseApiObject;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import com.rubicon.platform.authorization.model.data.acm.*;
import com.rubicon.platform.authorization.model.data.lfo.Authorization;
import com.rubicon.platform.authorization.model.data.lfo.PrincipleTypeEnum;
import com.rubicon.platform.authorization.model.data.lfo.ResourceTypeEnum;
import com.rubicon.platform.authorization.model.data.pmg.Publisher;
import com.rubicon.platform.authorization.model.data.pmg.PublisherStatusEnum;
import com.rubicon.platform.authorization.model.ui.acm.Account;
import com.rubicon.platform.authorization.model.ui.acm.Operation;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import com.rubicon.platform.authorization.service.cache.AccountGroupObjectCache;
import com.rubicon.platform.authorization.service.cache.AccountObjectCache;
import com.rubicon.platform.authorization.service.cache.BaseRoleObjectCache;
import com.rubicon.platform.authorization.service.cache.ServiceRoleAssignment;
import com.rubicon.platform.authorization.service.jobs.DataMarketplaceVendor;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.TranslationContext;
import cz.jirutka.rsql.parser.ast.Node;
import junit.framework.Assert;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class TestAbstract extends Assert
{
    public static final long DATA_SERVICE_ACCOUNT_ID = 225L;
    public static final String DATA_SERVICE_ACCOUNT_COMBO_ID = "publisher/1232";
    public static final String DATA_SERVICE_ACCOUNT_NAME = "Some Random Account Name";
    public static final String DATA_SERVICE_STATUS = "active";

    public static final long DATA_SERVICE_ACCOUNT_FEATURE_ID = 227L;
    public static final String DATA_SERVICE_FEATURE_NAME = "A Random Feature Name";

    public static final long DATA_SERVICE_ACCOUNT_FEATURE_2_ID = 683L;
    public static final String DATA_SERVICE_FEATURE_2_NAME = "A Random Feature 2 Name";

    public static final long DATA_SERVICE_ROLE_ID = 229L;
    public static final String DATA_SERVICE_ROLE_NAME = "A Random Role Name";
    public static final long DATA_SERVICE_ROLE_TYPE_ID = 1L;

    public static final long DATA_SERVICE_ACCOUNT_GROUP_ID = 28193L;
    public static final String DATA_SERVICE_ACCOUNT_GROUP_NAME = "A Random Account Group";

    public static final long DATA_SERVICE_ROLE_ASSIGNMENT_ID = 123291L;

    public static final RoleTypeEnum DATA_SERVICE_ROLE_TYPE = RoleTypeEnum.buyer;
    public static final String DATA_SERVICE_ROLE_TYPE_NAME = "Buyer";

    public static final Long DATA_MARKETPLACE_VENDOR_ID = 9080L;
    public static final String DATA_MARKETPLACE_VENDOR = "A Fake Marketplace Vendor";
    public static final String DATA_MARKETPLACE_STATUS = "deleted";

    public static final String OPERATION_SERVICE = "ServiceName";
    public static final String OPERATION_RESOURCE = "ResourceName";
    public static final String OPERATION_ACTION = "ActionName";
    public static final List<String> OPERATION_PROPERTIES = Arrays.asList("Properties1");
    public static final String OPERATION_2_SERVICE = "ServiceName2";
    public static final String OPERATION_2_RESOURCE = "ResourceName2";
    public static final String OPERATION_2_ACTION = "ActionName2";
    public static final List<String> OPERATION_2_PROPERTIES = Arrays.asList("Properties1", "Properties2");

    public static final String ACCOUNT_REFERENCE_GROUP = "group";
    public static final String ACCOUNT_REFERENCE_ACCOUNT = "account";

    protected static final String USER_ID = "1234";
    protected static final String USER_EMAIL = "test@test.com";
    protected static final CompoundId SUBJECT_ID = new CompoundId("user", USER_ID);
    protected static final String AUTHORIZATION_RESOURCE_ID = "2763";
    protected static final Long AUTHORIZATION_ID = 12345L;

    protected static final Long ROLE_ASSIGNMENT_ID = 267821L;

    protected static final String STREAMING_SEAT_ID = "123";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public RoleTypePermission buildRoleTypePermission(boolean canEditBuyer, boolean canEditInternal,
                                                      boolean canEditSeller, boolean canEditService,
                                                      boolean canEditProduct, boolean canEditMarketplaceVendor,
                                                      boolean canEditStreamingSeat, boolean canEditStreamingBuyer)
    {
        return buildRoleTypePermission(canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer, false, false, false, false,
                false, false, false, false);
    }

    public RoleTypePermission buildRoleTypePermission(boolean canEditBuyer, boolean canEditInternal,
                                                      boolean canEditSeller, boolean canEditService,
                                                      boolean canEditProduct, boolean canEditMarketplaceVendor,
                                                      boolean canEditStreamingSeat, boolean canEditStreamingBuyer,
                                                      boolean canViewBuyer, boolean canViewInternal,
                                                      boolean canViewSeller, boolean canViewService,
                                                      boolean canViewProduct, boolean canViewMarketplaceVendor,
                                                      boolean canViewStreamingSeat, boolean canViewStreamingBuyer)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditBuyer(canEditBuyer);
        roleTypePermission.setEditInternal(canEditInternal);
        roleTypePermission.setEditSeller(canEditSeller);
        roleTypePermission.setEditService(canEditService);
        roleTypePermission.setEditProduct(canEditProduct);
        roleTypePermission.setEditMarketplaceVendor(canEditMarketplaceVendor);
        roleTypePermission.setEditStreamingSeat(canEditStreamingSeat);
        roleTypePermission.setEditStreamingBuyer(canEditStreamingBuyer);
        roleTypePermission.setViewBuyer(canViewBuyer);
        roleTypePermission.setViewInternal(canViewInternal);
        roleTypePermission.setViewSeller(canViewSeller);
        roleTypePermission.setViewService(canViewService);
        roleTypePermission.setViewProduct(canViewProduct);
        roleTypePermission.setViewMarketplaceVendor(canViewMarketplaceVendor);
        roleTypePermission.setViewStreamingSeat(canViewStreamingSeat);
        roleTypePermission.setViewStreamingBuyer(canViewStreamingBuyer);

        return roleTypePermission;
    }


    protected TranslationContext buildTranslationContext(boolean canEditBuyer, boolean canEditInternal,
                                                         boolean canEditSeller, boolean canEditService,
                                                         boolean canEditProduct, boolean canEditMarketplaceVendor,
                                                         boolean canEditStreamingSeat, boolean canEditStreamingBuyer)
    {
        return buildTranslationContext(canEditBuyer, canEditInternal, canEditSeller, canEditService,
                canEditProduct, canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer, false, false,
                false, false, false, false, false, false);
    }

    protected TranslationContext buildTranslationContext(boolean canEditBuyer, boolean canEditInternal,
                                                         boolean canEditSeller, boolean canEditService,
                                                         boolean canEditProduct, boolean canEditMarketplaceVendor,
                                                         boolean canEditStreamingSeat, boolean canEditStreamingBuyer,
                                                         boolean canViewBuyer, boolean canViewInternal,
                                                         boolean canViewSeller, boolean canViewService,
                                                         boolean canViewProduct, boolean canViewMarketplaceVendor,
                                                         boolean canViewStreamingSeat, boolean canViewStreamingBuyer)
    {
        RoleTypePermission roleTypePermission = buildRoleTypePermission(
                canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer, canViewBuyer, canViewInternal,
                canViewSeller, canViewService, canViewProduct,
                canViewMarketplaceVendor, canViewStreamingSeat, canViewStreamingBuyer);

        TranslationContext context = new TranslationContext();
        context.putContextItem(TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION, roleTypePermission);

        return context;
    }

    protected TranslationContext buildViewTranslationContext(boolean canViewBuyer, boolean canViewInternal,
                                                             boolean canViewSeller, boolean canViewService,
                                                             boolean canViewProduct, boolean canViewMarketplaceVendor,
                                                             boolean canViewStreamingSeat,
                                                             boolean canViewStreamingBuyer)
    {
        return buildTranslationContext(false, false, false, false, false, false, false, false, canViewBuyer,
                canViewInternal, canViewSeller, canViewService, canViewProduct, canViewMarketplaceVendor,
                canViewStreamingSeat, canViewStreamingBuyer);
    }


    protected com.rubicon.platform.authorization.model.data.acm.Account getDataServiceAccount()
    {
        com.rubicon.platform.authorization.model.data.acm.Account account =
                new com.rubicon.platform.authorization.model.data.acm.Account();

        account.setId(DATA_SERVICE_ACCOUNT_ID);
        account.setAccountId(DATA_SERVICE_ACCOUNT_COMBO_ID);
        account.setAccountName(DATA_SERVICE_ACCOUNT_NAME);
        account.setStatus(DATA_SERVICE_STATUS);

        account.setAccountFeatureIds(new HashSet<>(Arrays.asList(DATA_SERVICE_ACCOUNT_FEATURE_ID)));

        return account;
    }

    protected com.rubicon.platform.authorization.model.data.acm.AccountFeature getDataServiceAccountFeature()
    {
        return getDataServiceAccountFeature(DATA_SERVICE_ACCOUNT_FEATURE_ID, DATA_SERVICE_FEATURE_NAME);
    }

    protected com.rubicon.platform.authorization.model.data.acm.AccountFeature getDataServiceAccountFeature2()
    {
        return getDataServiceAccountFeature(DATA_SERVICE_ACCOUNT_FEATURE_2_ID, DATA_SERVICE_FEATURE_2_NAME);
    }

    protected com.rubicon.platform.authorization.model.data.acm.AccountFeature getDataServiceAccountFeature(Long id, String name)
    {
        com.rubicon.platform.authorization.model.data.acm.AccountFeature accountFeature =
                new com.rubicon.platform.authorization.model.data.acm.AccountFeature();


        List<com.rubicon.platform.authorization.model.data.acm.Operation> allowedOperations = new ArrayList<>();
        allowedOperations.add(getDataServiceOperation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES));

        List<com.rubicon.platform.authorization.model.data.acm.Operation> deniedOperations = new ArrayList<>();
        deniedOperations.add(getDataServiceOperation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES));

        accountFeature.setId(id);
        accountFeature.setLabel(name);
        accountFeature.setAllowedOperations(allowedOperations);
        accountFeature.setDeniedOperations(deniedOperations);

        return accountFeature;
    }

    protected com.rubicon.platform.authorization.model.data.acm.Operation getDataServiceOperation(String service,
                                                                                                  String resource,
                                                                                                  String action,
                                                                                                  List<String> properties)
    {
        com.rubicon.platform.authorization.model.data.acm.Operation operation =
                new com.rubicon.platform.authorization.model.data.acm.Operation();
        operation.setService(service);
        operation.setResource(resource);
        operation.setAction(action);
        operation.setProperties(properties);

        return operation;
    }

    protected com.rubicon.platform.authorization.model.data.acm.Role getDataServiceRole()
    {
        return getDataServiceRole(DATA_SERVICE_ROLE_TYPE_ID);
    }

    protected com.rubicon.platform.authorization.model.data.acm.Role getDataServiceRole(Long roleTypeId)
    {
        com.rubicon.platform.authorization.model.data.acm.Role role = new com.rubicon.platform.authorization.model.data.acm.Role();

        List<com.rubicon.platform.authorization.model.data.acm.Operation> allowedOperations = new ArrayList<>();
        allowedOperations.add(getDataServiceOperation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES));

        List<com.rubicon.platform.authorization.model.data.acm.Operation> deniedOperations = new ArrayList<>();
        deniedOperations.add(getDataServiceOperation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES));

        role.setId(DATA_SERVICE_ROLE_ID);
        role.setLabel(DATA_SERVICE_ROLE_NAME);
        role.setRoleTypeId(roleTypeId);
        role.setAllowedOperations(allowedOperations);
        role.setDeniedOperations(deniedOperations);

        return role;
    }

    protected AccountGroup getDataServiceAccountGroup()
    {
        AccountGroup accountGroup = new AccountGroup();

        accountGroup.setId(DATA_SERVICE_ACCOUNT_GROUP_ID);
        accountGroup.setLabel(DATA_SERVICE_ACCOUNT_GROUP_NAME);

        return accountGroup;
    }

    protected RoleAssignment getDataServiceRoleAssignment()
    {
        return getDataServiceRoleAssignment(ACCOUNT_REFERENCE_ACCOUNT);
    }

    protected RoleAssignment getDataServiceRoleAssignment(
            String accountReferenceType)
    {
        RoleAssignment roleAssignment = new RoleAssignment();

        roleAssignment.setId(DATA_SERVICE_ROLE_ASSIGNMENT_ID);
        roleAssignment.setRoleId(DATA_SERVICE_ROLE_ID);
        roleAssignment.setSubject(SUBJECT_ID.asIdString());
        if (ACCOUNT_REFERENCE_GROUP.equals(accountReferenceType))
        {
            roleAssignment.setAccountGroupId(DATA_SERVICE_ACCOUNT_GROUP_ID);

        }
        else
        {

            roleAssignment.setAccount(DATA_SERVICE_ACCOUNT_COMBO_ID);
        }

        return roleAssignment;
    }

    protected RoleType getDataServiceRoleType()
    {
        return getDataServiceRoleType(DATA_SERVICE_ROLE_TYPE, DATA_SERVICE_ROLE_TYPE_NAME);
    }

    protected RoleType getDataServiceRoleType(RoleTypeEnum roleTypeEnum,
                                                                                          String name)
    {
        return getDataServiceRoleType(roleTypeEnum.getRoleTypeEnumId(), name);
    }

    protected RoleType getDataServiceRoleType(Long roleTypeId, String name)
    {
        RoleType roleType = new RoleType();
        roleType.setId(roleTypeId);
        roleType.setLabel(name);

        return roleType;
    }



    protected BaseRoleObjectCache<Role> getRoleObjectCache()
    {
        BaseRoleObjectCache<com.rubicon.platform.authorization.model.data.acm.Role> roleObjectCache = spy(
                new BaseRoleObjectCache<>(null));

        doReturn(getDataServiceRole(DATA_SERVICE_ROLE_TYPE_ID)).when(roleObjectCache).getItemById(anyLong());

        return roleObjectCache;
    }

    protected AccountObjectCache getAccountObjectCache()
    {
        AccountObjectCache accountObjectCache = spy(new AccountObjectCache(null));

        doReturn(getDataServiceAccount()).when(accountObjectCache).getByAccountId((CompoundId) any());

        return accountObjectCache;
    }

    protected AccountGroupObjectCache getAccountGroupObjectCache()
    {
        AccountGroupObjectCache accountGroupObjectCache = spy(new AccountGroupObjectCache(null));

        doReturn(getDataServiceAccountGroup()).when(accountGroupObjectCache).getItemById(anyLong());

        return accountGroupObjectCache;
    }


    protected Ehcache getDeletedAccountCache()
    {
        Ehcache cache = Mockito.mock(Ehcache.class);

        Mockito.when(cache.get(Mockito.anyLong())).thenReturn(new Element(1, getDataServiceAccount(), 1));

        return cache;
    }


    protected <D extends BaseApiObject> ServiceExceptionMappingDecorator setupUiServicePersistenceOperations(
            QueryResult<D> queryResults, List<D> itemList)
    {
        return setupUiServicePersistenceOperations(queryResults, itemList, 1, null);
    }

    protected <D extends BaseApiObject> ServiceExceptionMappingDecorator setupUiServicePersistenceOperations(
            QueryResult<D> queryResults, List<D> itemList, Integer deletedItems, D createdItem)
    {
        ServiceExceptionMappingDecorator persistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(persistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(queryResults);


        when(persistenceOperations.findByIds((List) any(), (PersistenceContext) any()))
                .thenReturn(itemList);

        when(persistenceOperations.deleteItem((List) any(), (PersistenceContext) any())).thenReturn(deletedItems);

        when(persistenceOperations.createOrUpdateItem((D) any(), (PersistenceContext) any())).thenReturn(createdItem);

        when(persistenceOperations.updateItem((List) any(), (D) any(), (PersistenceContext) any()))
                .thenReturn(createdItem);

        return persistenceOperations;
    }

    protected <D extends BaseApiObject> ServiceExceptionMappingDecorator setupCreationUiServicePersistenceOperations(D savedItem)
    {
        ServiceExceptionMappingDecorator persistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);

        when(persistenceOperations.createOrUpdateItem(any(ApiObject.class), any(PersistenceContext.class)))
                .thenReturn(savedItem);

        return persistenceOperations;
    }

    protected BaseRoleObjectCache<AccountFeature> getMockAccountFeatureCache()
    {
        BaseRoleObjectCache<AccountFeature> accountFeatureCache = Mockito.mock(BaseRoleObjectCache.class);
        when(accountFeatureCache.getItemById(anyLong())).thenAnswer(new Answer<AccountFeature>()
        {
            @Override
            public AccountFeature answer(
                    InvocationOnMock invocation) throws Throwable
            {
                AccountFeature result = null;
                long featureId = (Long) invocation.getArguments()[0];
                if (DATA_SERVICE_ACCOUNT_FEATURE_ID == featureId)
                {
                    result = getDataServiceAccountFeature();
                }
                else if (DATA_SERVICE_ACCOUNT_FEATURE_2_ID == featureId)
                {
                    result = getDataServiceAccountFeature2();
                }
                return result;
            }
        });
        return accountFeatureCache;
    }

    protected PersistenceContext getPersistentContext()
    {
        PersistenceContext context = new PersistenceContext();

        DataUserContext dataUserContext = new DataUserContext(null, "aRandomAccessToken", "aRandomCID");
        EntityPlugin entityPlugin = new EntityPlugin();

        context.setUserContext(dataUserContext);
        context.setEntityPlugin(new EntityPlugin());


        return context;
    }

    protected Publisher getPublisher()
    {
        Publisher publisher = new Publisher();
        publisher.setId(DATA_SERVICE_ACCOUNT_ID);
        publisher.setName(DATA_SERVICE_ACCOUNT_NAME);
        publisher.setStatus(PublisherStatusEnum.active);

        return publisher;
    }

    protected static Authorization getAuthorization()
    {
        Authorization authorization = new Authorization();
        authorization.setId(AUTHORIZATION_ID);
        authorization.setResourceId(AUTHORIZATION_RESOURCE_ID);
        authorization.setResourceType(ResourceTypeEnum.account);
        authorization.setPrincipleId(Long.valueOf(USER_ID));
        authorization.setPrincipleType(PrincipleTypeEnum.user);

        return authorization;
    }

    protected PersistentRoleAssignment getPersistentRoleAssignment(Long accountGroupId, CompoundId account)
    {
        PersistentRoleAssignment roleAssignment = new PersistentRoleAssignment();
        roleAssignment.setId(ROLE_ASSIGNMENT_ID);
        roleAssignment.setSubject(new CompoundId("user", USER_ID));
        roleAssignment.setAccountGroupId(accountGroupId);
        roleAssignment.setAccount(account);

        return roleAssignment;
    }

    protected ServiceRoleAssignment getServiceRoleAssignment(Long id)
    {
        ServiceRoleAssignment roleAssignment = new ServiceRoleAssignment();
        roleAssignment.setId(id);

        return roleAssignment;
    }

    protected static Operation getUiOperation(boolean distinctFromExisting, boolean missingService,
                                              boolean missingResource, boolean missingAction, boolean missingProperties)
    {
        Operation result = new Operation();
        if (distinctFromExisting)
        {
            result.setService(missingService ? null : OPERATION_2_SERVICE);
            result.setResource(missingResource ? null : OPERATION_2_RESOURCE);
            result.setAction(missingAction ? null : OPERATION_2_ACTION);
            result.setProperties(missingProperties ? null : OPERATION_2_PROPERTIES);
        }
        else
        {
            result.setService(missingService
                              ? null
                              : OPERATION_SERVICE);
            result.setResource(missingResource
                               ? null
                               : OPERATION_RESOURCE);
            result.setAction(missingAction
                             ? null
                             : OPERATION_ACTION);
            result.setProperties(missingProperties
                                 ? null
                                 : OPERATION_PROPERTIES);
        }
        return result;
    }

    public Account getUiAccount()
    {
        CompoundId accountCompoundId = new CompoundId(DATA_SERVICE_ACCOUNT_COMBO_ID);

        Account account = new Account();
        account.setId(DATA_SERVICE_ACCOUNT_ID);
        account.setName(DATA_SERVICE_ACCOUNT_NAME);
        account.setContextId(Long.parseLong(accountCompoundId.getId()));
        account.setContextType(accountCompoundId.getIdType());
        account.setStatus(DATA_SERVICE_STATUS);

        return account;
    }

    protected DataMarketplaceVendor getDataMarketplaceVendor()
    {
        return new DataMarketplaceVendor(DATA_MARKETPLACE_VENDOR_ID, DATA_MARKETPLACE_VENDOR, DATA_MARKETPLACE_STATUS);
    }


}
