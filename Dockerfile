# 第一阶段：构建项目
FROM eclipse-temurin:21-jdk-jammy AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件
COPY pom.xml .
COPY libra-admin/pom.xml libra-admin/
COPY libra-common/pom.xml libra-common/
COPY libra-framework/pom.xml libra-framework/

# 复制源代码
COPY libra-admin/src libra-admin/src
COPY libra-common/src libra-common/src
COPY libra-framework/src libra-framework/src

# 构建项目
RUN mvn clean package -DskipTests

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
