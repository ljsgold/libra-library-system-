# 第一阶段：构建项目
FROM maven:3.9-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /build

# 复制整个项目（Maven 会利用缓存优化）
COPY pom.xml .
COPY libra-admin/pom.xml libra-admin/
COPY libra-common/pom.xml libra-common/
COPY libra-framework/pom.xml libra-framework/

# 先下载依赖（利用 Docker 缓存层）
RUN echo "开始下载 Maven 依赖..." && \
    mvn dependency:go-offline -B || echo "依赖下载完成（部分失败可忽略）"

# 复制源代码
COPY libra-admin/src libra-admin/src
COPY libra-common/src libra-common/src
COPY libra-framework/src libra-framework/src

# 构建项目（-e 参数显示错误堆栈，-B 批处理模式，输出构建信息）
RUN echo "开始构建项目..." && \
    mvn clean package -DskipTests -B -e && \
    echo "构建完成！" && \
    ls -lh libra-admin/target/*.jar || (echo "构建失败！" && exit 1)

# 第二阶段：运行应用
FROM eclipse-temurin:21-jdk-jammy

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/libra-admin/target/libra-admin.jar app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
