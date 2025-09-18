package com.rubicon.platform.authorization.service.persistence;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.api.HistoryEntry;
import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.PersistenceOperations;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.service.exception.*;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

public class ServiceExceptionMappingDecorator<C extends ApiObject, ID extends Serializable>
        implements PersistenceOperations<C, ID>
{
    private PersistenceOperations<C,ID> delegate;

    public void setDelegate(PersistenceOperations<C, ID> delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public List<C> findByIds(List<ID> ids, PersistenceContext context)
    {
        try
        {
            return delegate.findByIds(ids, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    @Override
    public QueryResult<C> query(Node query, Integer start, Integer limit, EndpointSort sort, PersistenceContext context)
    {
        try
        {
            return delegate.query(query, start, limit, sort, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    @Override
    public C createOrUpdateItem(C item, PersistenceContext context)
    {
        try
        {
            return delegate.createOrUpdateItem(item, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    @Override
    public C updateItem(List<ID> ids, C item, PersistenceContext context)
    {
        try
        {
            return delegate.updateItem(ids, item, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    @Override
    public int deleteItem(List<ID> ids, PersistenceContext context)
    {
        try
        {
            return delegate.deleteItem(ids, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    @Override
    public QueryResult<HistoryEntry> getHistory(ID id, Integer start, Integer limit, PersistenceContext context)
    {
        try
        {
            return delegate.getHistory(id, start, limit, context);
        }
        catch (HyperionException e)
        {
            throw convertHyperionException(e);
        }
    }

    private RuntimeException convertHyperionException(HyperionException e) throws ServiceException
    {
        RuntimeException result;
        switch (HttpStatus.valueOf(e.getStatusCode()))
        {
            case BAD_REQUEST:
                result = new BadRequestException(e.getMessage(), e);
                break;
            case NOT_FOUND:
                result = new NotFoundException(e.getMessage());
                break;
            case UNAUTHORIZED:
                result = new UnauthorizedException(e.getMessage());
                break;
            case UNPROCESSABLE_ENTITY:
                result = new ValidationException(e.getMessage());
                break;
            case SERVICE_UNAVAILABLE:
                result = new ServiceUnavailableException(e.getMessage());
                break;
            case BAD_GATEWAY:
                result = new ServiceUnavailableException(e.getMessage());
                break;
            case INTERNAL_SERVER_ERROR:
                result = new ServiceException(e.getStatusCode(), e.getMessage(), e);
                break;
            default:
                result = new ServiceException(e.getStatusCode(), e.getMessage(), e);
        }
        return result;
    }
}
