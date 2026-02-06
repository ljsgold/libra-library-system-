# 使用 JRE 17 作为基础镜像
FROM openjdk:21-jdk-slim

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

# 启动命令
ENTRYPOINT ["bash", "-lc", "/wait-for-it.sh libra-db 3306 --timeout=60 --strict -- java $JAVA_OPTS -jar app.jar"]
