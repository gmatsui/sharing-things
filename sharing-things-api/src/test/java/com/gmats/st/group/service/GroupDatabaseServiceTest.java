package com.gmats.st.group.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GroupDatabaseServiceTest {

    @Mock
    private SQLClient sqlClientMock;

    @Mock
    private SQLConnection sqlConnectionMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.sqlClientMock.getConnection(any())).thenReturn(this.sqlClientMock);
    }

    @Test
    public void test_constructor_callFuture() {

        final Handler handler = mock(Handler.class);

        GroupDatabaseService instance = new GroupDatabaseServiceImpl(this.sqlClientMock, handler);
        assertNotNull(instance);

        ArgumentCaptor<AsyncResult> argumentCaptor = ArgumentCaptor.forClass(AsyncResult.class);
        verify(handler, only()).handle(argumentCaptor.capture());

        AsyncResult future = argumentCaptor.getValue();
        assertNotNull(future.result());
        assertTrue(future.result() instanceof GroupDatabaseServiceImpl);
    }

    @Test
    public void test_fetchAll_callFuture() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Handler<AsyncResult<SQLConnection>> callback = (Handler<AsyncResult<SQLConnection>>) invocationOnMock.getArguments()[0];
                AsyncResult<SQLConnection> result = new AsyncResult<SQLConnection>() {

                    @Override
                    public SQLConnection result() {
                        return sqlConnectionMock;
                    }

                    @Override
                    public Throwable cause() {
                        return null;
                    }

                    @Override
                    public boolean succeeded() {
                        return true;
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                };
                callback.handle(result);
                return null;
            }
        }).when(this.sqlClientMock).getConnection(any());


        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Handler<AsyncResult<ResultSet>> callback = (Handler<AsyncResult<ResultSet>>) invocationOnMock.getArguments()[1];
                AsyncResult<ResultSet> result = new AsyncResult<ResultSet>() {

                    @Override
                    public ResultSet result() {
                        ResultSet resultSet = new ResultSet();
                        List<JsonArray> rowList = new ArrayList<>();
                        List<String> columnNameList = new ArrayList<>();

                        columnNameList.add("name");
                        JsonArray oneRow = new JsonArray();
                        oneRow.add("test name");
                        rowList.add(oneRow);

                        resultSet.setColumnNames(columnNameList);
                        resultSet.setResults(rowList);
                        return resultSet;
                    }

                    @Override
                    public Throwable cause() {
                        return null;
                    }

                    @Override
                    public boolean succeeded() {
                        return true;
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                };
                callback.handle(result);
                return null;
            }
        }).when(this.sqlConnectionMock).query(anyString(), any());

        ArgumentCaptor<String> sqlCapture = ArgumentCaptor.forClass(String.class);


        new GroupDatabaseServiceImpl(this.sqlClientMock, instanceAsync -> {
            instanceAsync.result().fetchAll(jsonArrayAsyncResult -> {
                if(jsonArrayAsyncResult.succeeded()) {

                    JsonArray rows = jsonArrayAsyncResult.result();
                    assertNotNull(rows);
                    JsonObject row = rows.getJsonObject(0);
                    assertEquals("test name", row.getString("name"));

                    verify(this.sqlConnectionMock).query(sqlCapture.capture(), any());
                    String query = sqlCapture.getValue();
                    assertEquals("SELECT * FROM collaboration_group", query);

                }  else {
                    fail(jsonArrayAsyncResult.cause().getMessage());
                }
            });
        });

    }
}
