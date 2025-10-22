package org.gokeep.elasticsearch.mcp.server;

import io.quarkiverse.mcp.server.*;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;
import org.gokeep.elasticsearch.mcp.server.basic.BasicAbstractMcpServer;
import org.gokeep.elasticsearch.mcp.server.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.util.Map;

/**
 * Elasticsearch mcp server
 */
@ApplicationScoped
public class ElasticsearchMcpServer extends BasicAbstractMcpServer implements QuarkusApplication {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchMcpServer.class);

    /**
     * Elasticsearch健康检查
     *
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 查询Elasticsearch健康状态
            EN: search elasticsearch health status
            """)
    public ToolResponse health() throws Exception {
        HttpRequest request = buildRequest(Router.HEALTH);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }

    /**
     * 列出所有索引
     *
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 查询Elasticsearch索引列表
            EN: search elasticsearch indices
            """)
    public ToolResponse listIndices() throws Exception {
        HttpRequest request = buildRequest(Router.LIST_INDICES);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }

    /**
     * 列出所有索引别名
     *
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 查询Elasticsearch所有别名
            EN: search elasticsearch alias
            """)
    public ToolResponse listAliases() throws Exception {
        HttpRequest request = buildRequest(Router.LIST_ALIASES);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }


    /**
     * 查询索引mapping
     *
     * @param indexName 索引名
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 查询Elasticsearch索引mapping
            EN: search elasticsearch index mapping
            """)
    public ToolResponse getMappings(@ToolArg(description = "索引名") String indexName) throws Exception {
        HttpRequest request = buildRequest(Router.GET_MAPPING, indexName);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }

    /**
     * 通过sql查询es文档
     *
     * @param sql elasticsearch sql语句
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 通过SQL查询Elasticsearch文档
            EN: search elasticsearch document by sql
            """)
    public ToolResponse sql(@ToolArg(description = "sql语句，用于elasticsearch查询") String sql) throws Exception {
        log.info("Elasticsearch search by sql: {}", sql);
        HttpRequest request = buildRequest(Router.SQL, Map.of("query", sql));
        String response = callResponse(request);
        return ToolResponse.success(response);
    }


    /**
     * DSL查询
     *
     * @param indexName 索引名
     * @param dsl       DSL查询语句
     * @return ToolResponse
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 通过DSL查询Elasticsearch文档
            EN: search elasticsearch document by dsl
            """)
    public ToolResponse search(@ToolArg(description = "索引名") String indexName,
                               @ToolArg(description = "dsl查询语句") Map<String, Object> dsl) throws Exception {
        HttpRequest request = buildRequest(Router.SEARCH, dsl, indexName);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }

    /**
     * 通过文档ID查询索引文档信息
     *
     * @param indexName 索引名
     * @param id        文档ID
     * @return ToolResponse 响应
     * @throws Exception 异常
     */
    @Tool(description = """
            CN: 通过文档ID查询索引文档信息
            EN: get document by id from index
            """)
    public ToolResponse getById(
            @ToolArg(description = "索引名") String indexName,
            @ToolArg(description = "文档ID") String id) throws Exception {
        HttpRequest request = buildRequest(Router.GET_BY_ID, indexName, id);
        String response = callResponse(request);
        return ToolResponse.success(response);
    }

    @Override
    public int run(String... args) throws Exception {
        return 0;
    }


    /**
     * 主应用启动，适用于本地开发模式
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
