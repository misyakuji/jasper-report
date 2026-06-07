# jasper-report

This is study project with jasper in Springboot.

## 环境准备
- 配置jdk21环境变量
- 配置mariadb数据库
- 配置docker环境
- 配置maven

## 🚀 启动·指南

### 1. maven运行
```shell
#导入src/main/resources/ddl目录下的sql文件到数据库
./mvnw spring-boot:run --jasypt.encryptor.password=SecretKey_jasper_mariadb
```

### 2. 通过docker启动
```shell
# 步骤 1: 使用maven构建项目
chmod +x mvnw && ./mvnw clean package '-Dmaven.test.skip=true'

# 步骤 2: 创建环境变量文件, 以cp为例,复制simple.env到实际的.env
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

⚠️ 重要注意事项
1. 安全性
   ✅ .env 文件已加入 .gitignore，不会提交到 Git
   ✅ 数据库端口仅绑定到 127.0.0.1，外部无法访问
   ✅ 使用环境变量管理敏感信息，避免硬编码
2. 数据持久化
   ✅ MariaDB 数据存储在 mariadb_data 卷中，即使删除容器数据也不会丢失
   ✅ 应用日志挂载到 ./logs 目录，便于查看和备份
3. 初始化脚本执行顺序
   Docker 会按文件名排序执行 /docker-entrypoint-initdb.d/ 中的脚本：
   data.sql - 插入测试数据
   init.sql - 用户授权
   schema.sql - 创建表结构
   注意: 如果数据库已存在（数据卷中有数据），初始化脚本不会再次执行。