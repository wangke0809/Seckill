# Jenkins 配置

- 需要安装插件
  - Git Parameter Plug-In
  - Maven Integration plugin
  - Publish Over SSH

- 配置远程服务器连接权限

- 配置 Maven

- 配置打包命令

  - clean package -Dmaven.test.skip=true -Pprod

  - **/web.war 发送到 Tomcat 服务器

  - ```bash
    #!/bin/sh
    
    tomcat_pid=`lsof -n -P -t -i :8080`
    if [ -n "$tomcat_pid" ];then
    echo "kill tomcat";
    sudo kill -9 $tomcat_pid
    sleep 1
    else 
    echo "no kill tomcat";
    fi
    
    sudo rm -rf  /opt/logs/
    sudo mkdir /opt/logs/
    sudo rm -rf /usr/local/tomcat8_8080/webapps/ROOT/
    sudo mkdir /usr/local/tomcat8_8080/webapps/ROOT/     
    sudo unzip ~/web/target/web.war -d /usr/local/tomcat8_8080/webapps/ROOT/
    sudo sh /usr/local/tomcat8_8080/bin/startup.sh
    ```