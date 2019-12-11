# Software Engineering Productivity Platform #

### 启动方式：

- Main Class：com.pr.sepp.SeppApplication（线上可注释掉@EnableSwagger2）
- 本地VM options：-Denv=local
- 线上部署：
    - rm -rf src/main/resources/spring.properties
    - mvn clean package -Dmaven.test.skip=true
    - java -Dpath=/opt/seppb/conf/spring.properties -jar /opt/seppb/sepp.jar &
- 集群：已测试HAProxy、keepalived，其余未测试

### 组件使用说明
- mariadb（>= 10.3），必须设置lower_case_table_names=1，参考conf/database/my.conf

- FastDfs，与nginx绑定使用，二者配置参考conf/fastdfs/\*.properties、conf/nginx/\*.properties

- octopus监控需要配置octopus.admin.address和context.env

- Quartz请注意spring-boot-starter-quartz和boot版本的兼容性问题，最好自己指定版本（参考pom.xml）

- 邮件服务、Jenkins服务、git仓库服务、sonarQube服务、LDAP认证服务，皆可使用超级管理员进入系统设置进行配置，持久化到数据库，同时Jenkins服务和邮件服务也可以通过配置文件实现，其中Jenkins使用持久化的配置需要配置jenkins.enable-programmatic=false

## License

- The code are licensed under the MIT license;

- All rights of other parts, but not limited to the icons, images, and UI designs are reserved.