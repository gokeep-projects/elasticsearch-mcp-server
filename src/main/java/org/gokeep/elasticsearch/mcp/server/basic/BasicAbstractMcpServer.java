package org.gokeep.elasticsearch.mcp.server.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.gokeep.elasticsearch.mcp.server.enmus.HttpMethod;
import org.gokeep.elasticsearch.mcp.server.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static org.gokeep.elasticsearch.mcp.server.config.McpServerConfig.*;

/**
 * MCP Server
 */
@ApplicationScoped
public class BasicAbstractMcpServer {

    static {
        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
    }

    private static final Logger log = LoggerFactory.getLogger(BasicAbstractMcpServer.class);
    /**
     * HTTP请求客户端
     */
    @Inject
    HttpClient httpClient;

    /**
     * JSON转换工具
     */
    @Inject
    ObjectMapper objectMapper;

    /**
     * Elasticsearch配置参数
     */
    @Inject
    ElasticsearchConfig elasticsearchConfig;

    /**
     * 默认HttpClient
     *
     * @return HttpClient
     */
    public HttpRequest.Builder defaultRequestBuilder() {
        return HttpRequest.newBuilder()
                .header(CONTENT_TYPE_HEADER, APPLICATION_JSON_UTF8_JSON)
                .timeout(Duration.ofSeconds(elasticsearchConfig.getRequestTimeout().orElse(DEFAULT_REQUEST_TIMEOUT)));
    }

    /**
     * HttpRequest构造器
     *
     * @param method 请求方法
     * @param path   请求路径
     * @param body   请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(HttpMethod method, String path, Map<String, Object> body) throws JsonProcessingException {
        String url = elasticsearchConfig.host().orElse(DEFAULT_ELASTIC_HOST_ADDRESS) + path;
        HttpRequest.Builder builder = defaultRequestBuilder().uri(URI.create(url));
        if (body != null && !body.isEmpty()) {
            String payload = this.objectMapper.writeValueAsString(body);
            builder.method(method.name(), HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8));
        } else {
            builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
        }
        return builder.build();
    }

    /**
     * 通过路由以及参数构建请求
     *
     * @param router 路由
     * @param body   请求体
     * @param params 请求参数
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(Router router, Map<String, Object> body, Object... params) throws JsonProcessingException {
        String path = params == null || params.length == 0 ? router.getPath() : router.getPath().formatted(params);
        return this.buildRequest(router.getMethod(), path, body);
    }


    /**
     * 通过路由以及参数构建请求
     *
     * @param router 路由
     * @param params 请求参数
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(Router router, Object... params) throws JsonProcessingException {
        String path = params == null || params.length == 0 ? router.getPath() : router.getPath().formatted(params);
        return this.buildRequest(router.getMethod(), path);
    }


    /**
     * 通过路由以及参数构建请求
     *
     * @param router 路由
     * @param body   请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(Router router, Map<String, Object> body) throws JsonProcessingException {
        return this.buildRequest(router.getMethod(), router.getPath(), body);
    }

    /**
     * 通过路由以及参数构建请求
     *
     * @param router 路由
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(Router router) throws JsonProcessingException {
        return this.buildRequest(router.getMethod(), router.getPath());
    }


    /**
     * HttpRequest构造器
     *
     * @param method 请求方法
     * @param path   请求路径
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildRequest(HttpMethod method, String path) throws JsonProcessingException {
        return buildRequest(method, path, null);
    }

    /**
     * HttpRequest GET构造器
     *
     * @param path 请求路径
     * @param body 请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildGetRequest(String path, Map<String, Object> body) throws JsonProcessingException {
        return this.buildRequest(HttpMethod.GET, path, body);
    }

    /**
     * HttpRequest POST构造器
     *
     * @param path 请求路径
     * @param body 请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildPostRequest(String path, Map<String, Object> body) throws JsonProcessingException {
        return this.buildRequest(HttpMethod.POST, path, body);
    }

    /**
     * HttpRequest PUT构造器
     *
     * @param path 请求路径
     * @param body 请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildPutRequest(String path, Map<String, Object> body) throws JsonProcessingException {
        return this.buildRequest(HttpMethod.PUT, path, body);
    }

    /**
     * HttpRequest DELETE构造器
     *
     * @param path 请求路径
     * @param body 请求体
     * @return HttpRequest
     * @throws JsonProcessingException 异常
     */
    public HttpRequest buildDeleteRequest(String path, Map<String, Object> body) throws JsonProcessingException {
        return this.buildRequest(HttpMethod.DELETE, path, body);
    }


    /**
     * 执行HttpRequest请求
     *
     * @param request 请求体
     * @return HttpResponse 请求相应内容
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    public String callResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = call(request);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            log.error("Response exception: {}", response);
            throw new IOException("Response exception: " + response);
        }
    }

    /**
     * 执行HttpRequest请求
     *
     * @param request 请求体
     * @return HttpResponse 请求相应内容
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    public HttpResponse<String> call(HttpRequest request) throws IOException, InterruptedException {
        log.info("Request: {}", request);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
}
