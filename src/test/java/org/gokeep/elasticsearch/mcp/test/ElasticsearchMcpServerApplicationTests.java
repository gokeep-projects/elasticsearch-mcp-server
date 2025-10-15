package org.gokeep.elasticsearch.mcp.test;

import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.gokeep.elasticsearch.mcp.server.ElasticsearchMcpServer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@QuarkusTest
class ElasticsearchMcpServerApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchMcpServerApplicationTests.class);
    @Inject
    ElasticsearchMcpServer elasticsearchMcpServer;

    @Test
    void testHealth() throws Exception {
        ToolResponse response = elasticsearchMcpServer.health();
        log.info("ToolResponse: {}", response);
    }

    @Test
    void testListIndices() throws Exception {
        ToolResponse response = elasticsearchMcpServer.listIndices();
        log.info("ToolResponse: {}", response);
    }

    @Test
    void testListAliases() throws Exception {
        ToolResponse response = elasticsearchMcpServer.listAliases();
        log.info("ToolResponse: {}", response);
    }

    @Test
    void testGetMapping() throws Exception {
        ToolResponse response = elasticsearchMcpServer.getMappings("index_name");
        log.info("ToolResponse: {}", response);
    }

    @Test
    void testSQL() throws Exception {
        ToolResponse response = elasticsearchMcpServer.sql("select * from index_name limit 5");
        log.info("ToolResponse: {}", response);
    }


    @Test
    void testSearch() throws Exception {
        ToolResponse response = elasticsearchMcpServer.search("index_name", Map.of("fieldName", "fieldValue"));
        log.info("ToolResponse: {}", response);
    }

}
