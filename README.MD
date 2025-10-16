<p align="center">
  <img src="https://www.elastic.co/favicon.svg" alt="elasticsearch-mcp-server" hight="15%" width="15%">
</p>
<h1 align="center">Elasticsearch MCP Server<h1>
<h4 align="center">极易部署 • 高性能 • 低内存占用 • 云原生支持 •Java版本的Elasticsearch MCP服务</h4>
[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.27.0-blue.svg)](https://quarkus.io/)
[![MCP Server](https://img.shields.io/badge/MCP-1.6.1-green.svg)](https://quarkiverse.github.io/quarkiverse-docs/quarkus-mcp-server/dev/index.html)
![MCP Server](https://img.shields.io/badge/License-MIT-yellow.svg)

**[📖 项目文档](#-项目介绍) • [🔗 MCP连接](#-MCP连接)  [🚀 快速开始](#-快速开始) • [📦 项目构建](#-项目构建) • [🛠️ 项目部署](#-项目部署) • [🔧 二次开发](#-二次开发)**

---

## 📖 项目介绍

<div>

### 🌟 基于Quarkus的轻量级Elasticsearch MCP服务

Elasticsearch MCP Server 是一个基于 [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) 标准的高性能服务器实现，专门为Elasticsearch数据源提供AI助手交互能力。本项目采用 **Quarkus框架** 构建，具有以下核心优势：

### 🚀 核心特性

| 特性 | 描述 |
|------|------|
| **⚡ 快速启动** | 基于Quarkus原生编译，毫秒级快速启动 |
| **🧠 低内存占用** | 相比传统Spring Boot应用，内存使用减少85%，原生程序运行仅需要9MB，非原生13MB |
| **🔄 双协议支持** | 同时支持SSE和Streamable HTTP协议，也可以支持studio |
| **🛠️ 丰富的工具集** | 提供7种核心Elasticsearch查询工具 |
| **🌐 云原生设计** | 专为Kubernetes和容器化环境优化，如果非云原生，直接部署也是非常容易的 |
| **📦 快速打包** | 支持JVM和原生二进制两种部署模式 |

### 🎯 支持的工具功能

- **🔍 健康检查** - 查询Elasticsearch集群健康状态
- **📋 索引管理** - 列出所有索引和别名
- **🗺️ 映射查询** - 获取索引的mapping信息
- **📝 SQL查询** - 通过SQL语句查询Elasticsearch文档
- **🔎 DSL搜索** - 使用Elasticsearch DSL进行高级搜索
- **📄 文档查询** - 通过ID获取特定文档
- 后续功能根据实际需求和issue持续更新...

---

## 🚀 快速开始

### 📋 环境要求

- **Java 17+** - 推荐使用JDK 17或更高
- **Maven 3.8+** - 项目构建工具
- **Elasticsearch 7.x/8.x/9.x** - 支持的Elasticsearch版本

### 🏃‍♂️ 立即运行

- 默认会Elasticsearch连接http://localhost:9200, 如需要修改请参考：[🔧 快速配置](#-快速配置)

```bash
# 克隆项目
git clone https://github.com/gokeep-projects/elasticsearch-mcp-server.git
cd elasticsearch-mcp-server

# 开发模式运行
./mvnw quarkus:dev

# 打包应用
./mvnw package
```

### 🔧 快速配置

在 `src/main/resources/application.properties` 中配置Elasticsearch连接：

```properties
# Elasticsearch连接配置, host缺省为http://localhost:9200，username和password如果需要可以配置
elasticsearch.host=http://localhost:9200
elasticsearch.username=<Your username>
elasticsearch.password=<Your password>
```
### 🔗 MCP连接

- **streamable:** http://localhost:19000/mcp

- **sse:** http://localhost:19000/mcp/sse

  

---

## 📦 项目构建

### 🏗️ 构建选项

本项目基于 **Quarkus** 构建，提供两种构建模式：

#### 1. JVM模式构建（常用构建方式，无任何工具依赖）

```bash
# 清理并打包
./mvnw clean package
或者
mvn clean package

# 生成的文件
# target/elasticsearch-mcp-server-1.0.0-runner.jar
# target/lib/ - 依赖库目录
```

#### 2. 原生模式构建（如果需要云原生或对性能有极致要求）

```bash
# 原生镜像构建（需要安装GraalVM）
./mvnw package -Dnative
或者
mvn package -Dnative

# 生成的文件
# target/elasticsearch-mcp-server-1.0.0-runner
# 特点：启动更快，内存占用更低
```

### 📊 性能对比

| 构建模式 | 启动时间 | 内存占用 | 文件大小 | 适用场景 |
|---------|---------|---------|---------|---------|
| **JVM模式** | 2-3秒 | ≈10MB | ≈22MB | 开发环境/生产环境 |
| **原生模式** | 1秒 | ≈5MB | ≈70MB | 开发环境/生产环境 |

### 🔧 构建配置

在 `pom.xml` 中可以调整构建参数：

```xml
<!-- 原生编译配置 -->
<profile>
    <id>native</id>
    <properties>
        <quarkus.native.enabled>true</quarkus.native.enabled>
        <quarkus.native.additional-build-args>
            -H:ResourceConfigurationFiles=resources-config.json
        </quarkus.native.additional-build-args>
    </properties>
</profile>
```

---

## 🛠️ 项目部署

### 🐳 Docker部署

#### 1. 创建Dockerfile

```dockerfile
FROM quay.io/quarkus/ubi-quarkus-native-image:22.3-java17 AS build
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
USER quarkus
WORKDIR /code
RUN ./mvnw package -Dnative

FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY --from=build /code/target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

#### 2. 构建并运行

```bash
# 构建Docker镜像
docker build -t elasticsearch-mcp-server .

# 运行容器
docker run -i --rm \
  -p 8080:8080 \
  -e ELASTICSEARCH_HOST=your-es-server-address \
  -e ELASTICSEARCH_USERNAME=your-es-useranme \
  -e ELASTICSEARCH_PASSWORD=your_password \
  elasticsearch-mcp-server
```

### ☸️ Kubernetes部署

#### 1. 创建部署文件

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: elasticsearch-mcp-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: elasticsearch-mcp-server
  template:
    metadata:
      labels:
        app: elasticsearch-mcp-server
    spec:
      containers:
        - name: elasticsearch-mcp-server
          image: elasticsearch-mcp-server:latest
          ports:
            - containerPort: 8080
          env:
            - name: ELASTICSEARCH_HOST
              value: "elasticsearch-service"
            - name: ELASTICSEARCH_PORT
              value: "9200"
          resources:
            requests:
              memory: "64Mi"
              cpu: "250m"
            limits:
              memory: "128Mi"
              cpu: "500m"
```

#### 2. 部署到集群

```bash
# 应用配置
kubectl apply -f k8s-deployment.yaml

# 检查状态
kubectl get pods -l app=elasticsearch-mcp-server
```

### 🖥️ 传统部署

#### 1. JVM模式部署

```bash
# 上传jar包和lib目录到服务器
scp target/elasticsearch-mcp-server-1.0.0-runner.jar user@server:/opt/
scp -r target/lib user@server:/opt/

# 在服务器上运行
java -jar /opt/elasticsearch-mcp-server-1.0.0-runner.jar
```

#### 2. 原生模式部署

```bash
# 上传原生可执行文件
scp target/elasticsearch-mcp-server-1.0.0-runner user@server:/opt/

# 在服务器上运行
./elasticsearch-mcp-server-1.0.0-runner
```

---

## 🔧 二次开发

### 📁 项目结构

```
elasticsearch-mcp-server/
├── src/main/java/org/gokeep/elasticsearch/mcp/server/
│   ├── basic/           # 基础抽象类
│   ├── config/          # 配置管理
│   ├── enmus/           # 枚举定义
│   ├── router/          # 路由定义
│   └── ElasticsearchMcpServer.java  # 主服务类
├── src/main/resources/
│   └── application.properties       # 配置文件
├── pom.xml              # Maven配置文件
└── README.md            # 项目文档
```

### 🛠️ 添加新工具

要添加新的Elasticsearch操作工具，只需在 `ElasticsearchMcpServer.java` 中添加新方法：

```java
@Tool(description = "CN: 自定义Elasticsearch操作\nEN: Custom elasticsearch operation")
public ToolResponse customOperation(
        @ToolArg(description = "参数描述") String param) throws Exception {
    // 构建请求
    HttpRequest request = buildRequest(Router.CUSTOM_ENDPOINT, param);
    String response = call(request);
    return ToolResponse.success(response);
}
```

然后在 `router.Router` 类中添加对应的路由常量。

### 🔄 扩展示例

#### 添加聚合查询工具

```java
@Tool(description = "CN: 执行Elasticsearch聚合查询\nEN: Execute elasticsearch aggregation")
public ToolResponse aggregate(
        @ToolArg(description = "索引名") String indexName,
        @ToolArg(description = "聚合查询语句") Map<String, Object> aggregation) throws Exception {
    HttpRequest request = buildRequest(Router.AGGREGATE, aggregation, indexName);
    String response = call(request);
    return ToolResponse.success(response);
}
```

#### 添加索引管理工具

```java
@Tool(description = "CN: 创建Elasticsearch索引\nEN: Create elasticsearch index")
public ToolResponse createIndex(
        @ToolArg(description = "索引名") String indexName,
        @ToolArg(description = "索引配置") Map<String, Object> settings) throws Exception {
    HttpRequest request = buildRequest(Router.CREATE_INDEX, settings, indexName);
    String response = call(request);
    return ToolResponse.success(response);
}
```

### 📚 二次开发文档

#### 核心依赖

- **Quarkus 3.27.0** - 核心框架
- **quarkus-mcp-server-sse 1.6.1** - MCP服务器实现
- **quarkus-rest-client-jackson** - REST客户端
- **Java 17** - 编程语言

#### 配置参考

```properties
# 服务器配置
quarkus.http.port=8080

# Elasticsearch配置
elasticsearch.host=http://localhost:9200
elasticsearch.username=
elasticsearch.password=
```

#### 测试工具

```bash
# 运行测试
./mvnw test

# 集成测试
./mvnw verify

# 开发模式（支持热部署）
./mvnw quarkus:dev
```

---

## 📄 许可证

本项目采用 [MIT]([MIT License](https://mit-license.org/)) 许可证。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目！

---

<div align="center">

### ⭐ 如果这个项目对你有帮助，请给个Star！

**[🔝 回到顶部](#-elasticsearch-mcp-server)**

</div>