package com.rubicon.platform.authorization.service.persistence;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.PersistenceOperations;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.exception.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class ServiceExceptionMappingDecoratorTest extends TestAbstract
{
    private static final String BAD_REQUEST_MSG = "BAD_REQUEST_MSG";
    private static final String NOT_FOUND_MSG = "NOT_FOUND_MSG";
    private static final String UNAUTHORIZED_MSG = "UNAUTHORIZED_MSG";
    private static final String UNPROCESSABLE_ENTITY_MSG = "UNPROCESSABLE_ENTITY_MSG";
    private static final String SERVICE_UNAVAILABLE_MSG = "SERVICE_UNAVAILABLE_MSG";
    private static final String BAD_GATEWAY_MSG = "BAD_GATEWAY_MSG";
    private static final String INTERNAL_SERVER_ERROR_MSG = "INTERNAL_SERVER_ERROR_MSG";

    PersistenceOperations<FakeApiObject, Long> delegate;
    ServiceExceptionMappingDecorator decorator;
    PersistenceContext context;

    @Before
    public void setup()
    {
        delegate = mock(PersistenceOperations.class);

        decorator = new ServiceExceptionMappingDecorator();
        decorator.setDelegate(delegate);

        context = new PersistenceContext();
    }

    @DataProvider
    public static Object[][] exceptionDataProvider()
    {
        return new Object[][]{
                {new HyperionException(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_MSG),
                        BadRequestException.class, BAD_REQUEST_MSG},
                {new HyperionException(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MSG),
                        NotFoundException.class, NOT_FOUND_MSG},
                {new HyperionException(HttpStatus.UNAUTHORIZED.value(), UNAUTHORIZED_MSG),
                        UnauthorizedException.class, UNAUTHORIZED_MSG},
                {new HyperionException(HttpStatus.UNPROCESSABLE_ENTITY.value(), UNPROCESSABLE_ENTITY_MSG),
                        ValidationException.class, UNPROCESSABLE_ENTITY_MSG},
                {new HyperionException(HttpStatus.SERVICE_UNAVAILABLE.value(), SERVICE_UNAVAILABLE_MSG),
                        ServiceUnavailableException.class, SERVICE_UNAVAILABLE_MSG},
                {new HyperionException(HttpStatus.BAD_GATEWAY.value(), BAD_GATEWAY_MSG),
                        ServiceUnavailableException.class, BAD_GATEWAY_MSG},
                {new HyperionException(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_MSG),
                        ServiceException.class, INTERNAL_SERVER_ERROR_MSG}
        };
    }

    @Test
    public void testFindByIdsNoException()
    {
        decorator.findByIds(null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testFindByIdsException(Exception thrownException,
                                       Class<? extends Exception> expectedExceptionType, String expectedMessage)
    {
        doThrow(thrownException).when(delegate).findByIds(anyListOf(Long.class), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.findByIds(null, null);
    }

    @Test
    public void testQueryNoException()
    {
        decorator.query(null, null, null, null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testQueryException(Exception thrownException,
                                   Class<? extends Exception> expectedExceptionType, String expectedMessage)
    {
        doThrow(thrownException).when(delegate)
                .query(any(Node.class), anyInt(), anyInt(), any(EndpointSort.class), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.query(null, null, null, null, null);
    }

    @Test
    public void testCreateOrUpdateItemException()
    {
        decorator.createOrUpdateItem(null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testCreateOrUpdateItemException(Exception thrownException,
                                                Class<? extends Exception> expectedExceptionType,
                                                String expectedMessage)
    {
        doThrow(thrownException).when(delegate)
                .createOrUpdateItem(any(FakeApiObject.class), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.createOrUpdateItem(null, null);
    }

    @Test
    public void testUpdateItemException()
    {
        decorator.updateItem(null, null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testUpdateItemException(Exception thrownException,
                                        Class<? extends Exception> expectedExceptionType, String expectedMessage)
    {
        doThrow(thrownException).when(delegate)
                .updateItem(anyListOf(Long.class), any(FakeApiObject.class), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.updateItem(null, null, null);
    }

    @Test
    public void testDeleteItemException()
    {
        decorator.deleteItem(null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testDeleteItemException(Exception thrownException,
                                        Class<? extends Exception> expectedExceptionType, String expectedMessage)
    {
        doThrow(thrownException).when(delegate).deleteItem(anyListOf(Long.class), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.deleteItem(null, null);
    }

    @Test
    public void testGetHistoryException()
    {
        decorator.getHistory(null, null, null, null);
    }

    @Test
    @UseDataProvider("exceptionDataProvider")
    public void testGetHistoryException(Exception thrownException,
                                        Class<? extends Exception> expectedExceptionType, String expectedMessage)
    {
        doThrow(thrownException).when(delegate)
                .getHistory(anyLong(), anyInt(), anyInt(), any(PersistenceContext.class));

        expectedException.expect(expectedExceptionType);
        expectedException.expectMessage(expectedMessage);

        decorator.getHistory(null, null, null, null);
    }

    static class FakeApiObject implements ApiObject<Long>
    {
        private Long id = null;

        @Override
        public Long getId()
        {
            return id;
        }

        @Override
        public void setId(Long id)
        {
            this.id = id;
        }
    }
}
