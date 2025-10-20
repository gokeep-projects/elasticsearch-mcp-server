package org.gokeep.elasticsearch.mcp.server.router;

import org.gokeep.elasticsearch.mcp.server.enmus.HttpMethod;

/**
 * 请求路由
 */
public enum Router {
    /**
     * ping
     */
    PING(HttpMethod.GET, "/"),
    /**
     * 健康检查
     */
    HEALTH(HttpMethod.GET, "/_cluster/health"),
    /**
     * 获取所有索引
     */
    LIST_INDICES(HttpMethod.GET, "/_cat/indices?format=json"),
    /**
     * 获取所有别名
     */
    LIST_ALIASES(HttpMethod.GET, "/_alias"),
    /**
     * 获取索引mapping
     */
    GET_MAPPING(HttpMethod.GET, "/%s/_mapping"),
    /**
     * 查询es文档
     */
    SEARCH(HttpMethod.POST, "/%s/_search"),
    /**
     * 通过SQL查询es文档
     */
    SQL(HttpMethod.POST, "/_sql"),
    /**
     * 根据索引ID查询文档
     */
    GET_BY_ID(HttpMethod.GET, "/%s/_doc/%s");

    Router(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    private final HttpMethod method;

    private final String path;

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
