![GitHub last commit](https://img.shields.io/github/last-commit/purang-fintech/seppb)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/purang-fintech/seppb)
![GitHub license](https://img.shields.io/github/license/purang-fintech/seppb)

# Software Engineering Productivity Platform #

### 问题解决步骤：
- 先赏个STAR
- 再加QQ群：362260709
- 再问问题，当然，建议提问之前仔细看完下面的说明

### 启动方式：

- Main Class：com.pr.sepp.SeppApplication（线上可注释掉@EnableSwagger2）
- 本地VM options：-Denv=local
- 注意：<b>本地开发请务必安装好IDE的lombok插件</b>
- 线上部署：
    - rm -rf src/main/resources/spring.properties
    - mvn clean package -Dmaven.test.skip=true
    - mv target/sepp*.jar ${your_path}/sepp.jar
    - java -Dpath=${your_conf_path}/spring.properties -jar ${your_path}/sepp.jar &
- 集群：已测试HAProxy、keepalived，其余未测试

### 组件使用说明
- mariadb（>= 10.3），必须设置lower_case_table_names=1，参考conf/database/my.conf

- FastDfs，与nginx绑定使用，二者配置参考conf/fastdfs/\*.properties、conf/nginx/\*.properties

- octopus监控需要配置octopus.admin.address和context.env

- Quartz请注意spring-boot-starter-quartz和boot版本的兼容性问题，最好自己指定版本（参考pom.xml）

- 邮件服务、Jenkins服务、git仓库服务、sonarQube服务、LDAP认证服务，皆可使用超级管理员进入系统设置进行配置，持久化到数据库，同时Jenkins服务和邮件服务也可以通过配置文件实现，其中Jenkins使用持久化的配置需要配置jenkins.enable-programmatic=false

- 基础设施>>监控中心，请参考：https://github.com/fudax/octopus 工程说明，并且更新spring.properties和前端src/components/monitor/MonitorAdmin.vue文件配置

## License

- 开原协议为MIT，请参考[LICENSE](./LICENSE)

- 本平台已经获得软件著作权（登记号2019SR0572278、2019SR1105515），请使用和传播时审慎思考
