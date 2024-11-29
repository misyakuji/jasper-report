# jasper-report

This is study project with jasper in Springboot.



## 环境准备
- 配置jdk21环境变量
- 配置docker环境
- 配置maven
```shell
# 赋予可执行权限
chmod +x mvnw
# 检证
./mvnw -v
```


### 运行
```shell
./mvnw spring-boot:run
```

### maven打包
```shell
./mvnw clean package '-Dmaven.test.skip=true'
```

### Docker构建并启动
```shell
docker-compose up --build
```

### Docker后台启动
```shell
docker-compose up -d
```


## Docker常用操作

### Docker列出所有容器
```shell
docker ps -a
```

### Docker列出所有镜像
```shell
docker images
```

### Docker删除模糊匹配的容器
```shell
docker rm -f $(docker ps -a --filter "name=jasper" -q)
```

### Docker删除模糊匹配的镜像
```shell
docker rmi -f $(docker images --filter "reference=*jasper*" -q)
```

### Docker删除所有 <none> 标签的镜像
```shell
docker rmi -f $(docker images -f "dangling=true" -q)
```