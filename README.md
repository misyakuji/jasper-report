# jasper-report

This is study project with jasper in Springboot.

## 环境准备
- JDK 25 (Temurin)
- MariaDB 11.8
- Redis 8
- Docker
- Maven
- 技术栈: Spring Boot 4.0.6 + Jetty + JasperReports 7.0.7 + Lombok + JWT (jjwt 0.13.0)

## 🚀 启动·指南

### 1. maven运行
```shell
# 1.创建环境变量文件.env，参照.env.example

# 2.导入src/main/resources/ddl目录下的sql文件到数据库

# 3.maven运行应用
./mvnw spring-boot:run
```

### 2. 通过docker启动
```shell
# 步骤 1: 使用maven构建项目
chmod +x mvnw && ./mvnw clean package '-Dmaven.test.skip=true'

# 步骤 2: 创建环境变量文件.env, 此处以cp为例
cp .env.example .env

# 步骤 3: 构建并启动服务
docker compose up -d --build

# 步骤 4: 查看服务状态
docker compose ps

# 步骤 5: 查看实时日志
docker compose logs -f

```
### 3. 常用命令速查
```shell
# 验证配置文件
docker compose config
# 拉取最新镜像
docker compose pull
# 后台启动
docker compose up -d
# 停止服务
docker compose stop
# 重启服务
docker compose restart
# 重新创建容器（保留数据卷）
docker compose up -d --force-recreate
# 停止所有运行的服务，并删除默认网络和容器
docker compose down
# 停止所有运行的服务，并删除默认网络和容器以及数据卷
docker compose down -v
# 停止并完全移除 Docker Compose 项目创建的所有资源(容器、网络、数据卷、镜像)
docker compose down -v --rmi all --remove-orphans

# 进入指定运行中的容器
docker compose exec mariadb bash
# 数据库操作
docker compose exec mariadb mariadb -u jasper_user -p${DB_PASSWORD} ${DB_NAME}
docker compose exec mariadb mysqldump -u root -p${DB_ROOT_PASSWORD} ${DB_NAME} > backup.sql

# 监控容器资源使用情况
docker compose stats
# 查看容器内正在运行的进程
docker compose top
# 查看容器详细状态
docker compose ps -a
# 检查指定容器健康状态
docker inspect mariadb-jasper | grep Health
# 查看所有服务日志
docker compose logs -f
# 查看指定运行中的容器日志
docker compose logs -f springboot-app
# 查看特定时间段日志
docker compose logs --since 10m mariadb
docker compose logs --tail 100 springboot-app

```

