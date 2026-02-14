# 使用 Eclipse Temurin JDK 21 作为基础镜像（官方推荐）
FROM eclipse-temurin:21-jdk-jammy

# 设置工作目录
WORKDIR /app

# 复制编译后的 jar 包
COPY libra-admin/target/libra-admin.jar app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 暴露端口
EXPOSE 8080

# 启动命令（Railway 不需要等待数据库，因为数据库是独立服务）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
