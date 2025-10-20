package org.gokeep.elasticsearch.mcp.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.util.Optional;

@ApplicationScoped
public class McpServerConfig {

    /**
     * 请求默认超时渐渐
     */
    public static final int DEFAULT_REQUEST_TIMEOUT = 60;

    /**
     * Content-Type
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    /**
     * application/json
     */
    public static final String APPLICATION_JSON_UTF8_JSON = "application/json;charset=utf-8";

    public static final String DEFAULT_ELASTIC_HOST_ADDRESS = "http://127.0.0.1:9200";

    private static final Logger log = LoggerFactory.getLogger(McpServerConfig.class);

    /**
     * Elasticsearch配置
     */
    @ConfigMapping(prefix = "elasticsearch")
    public interface ElasticsearchConfig {
        /**
         * Elasticsearch 服务地址,默认缺省为 http://localhost:9200
         *
         * @return Optional<String>
         */
        Optional<String> host();

        /**
         * Elasticsearch   用户名
         *
         * @return Optional<String>
         */
        Optional<String> username();

        /**
         * Elasticsearch   密码
         *
         * @return Optional<String>
         */
        Optional<String> password();

        /**
         * Elasticsearch      超时时间， 默认为60
         *
         * @return Optional<String>
         */
        Optional<Request> request();

        interface Request {
            Optional<Timeout> timeout();
        }

        interface Timeout {
            /**
             * ES请求超时时间，默认缺省为 60
             * @return Optional<Integer>
             */
            Optional<Integer> second();
        }

        /**
         * 安全的获取请求超时时间参数
         *
         * @return Optional<Integer>
         */
        default Optional<Integer> getRequestTimeout() {
            if (this.request().isPresent() && this.request().get().timeout().isPresent() && this.request().get().timeout().get().second().isPresent()) {
                return this.request().get().timeout().get().second();
            } else {
                return Optional.empty();
            }
        }
    }

    @Inject
    ElasticsearchConfig elasticsearchConfig;

    /**
     * HttpClient
     *
     * @return HttpClient
     */
    @Singleton
    public HttpClient httpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        if (elasticsearchConfig.username().isPresent() && elasticsearchConfig.password().isPresent()) {
            builder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            elasticsearchConfig.username().orElse(null),
                            elasticsearchConfig.password().orElse("").toCharArray()
                    );
                }
            });
        }
        builder.version(HttpClient.Version.HTTP_1_1);
        return builder.build();
    }

    /**
     * json工具
     *
     * @return ObjectMapper
     */
    @Singleton
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
    }

}
