package org.gokeep.elasticsearch.mcp.server.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gokeep.elasticsearch.mcp.server.config.McpServerConfig.DEFAULT_ELASTIC_HOST_ADDRESS;
import static org.gokeep.elasticsearch.mcp.server.config.McpServerConfig.DEFAULT_REQUEST_TIMEOUT;

@ApplicationScoped
public class ApplicationRunner {
    Logger log = LoggerFactory.getLogger(ApplicationRunner.class);

    @Inject
    McpServerConfig.ElasticsearchConfig elasticsearchConfig;

    /**
     * 打印Elasticsearch配置
     */
    void printElasticsearchConfig() {
        log.info("- Elasticsearch host={}", elasticsearchConfig.host().orElse(DEFAULT_ELASTIC_HOST_ADDRESS));
        log.info("- Elasticsearch username={}", elasticsearchConfig.username().orElse(null));
        log.info("- Elasticsearch password={}", null == elasticsearchConfig.password().orElse(null) ? null : "hidden password! ******");
        log.info("- Elasticsearch request timeout={}", elasticsearchConfig.getRequestTimeout().orElse(DEFAULT_REQUEST_TIMEOUT));
    }

    void onStartup(@Observes StartupEvent event) {
        printElasticsearchConfig();
    }
}
