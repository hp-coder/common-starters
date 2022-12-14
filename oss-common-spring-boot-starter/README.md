# 使用说明
- Amazon S3 OSS common client
  - 引入maven依赖之后，使用idea build，或maven compile之后，yaml配置文件能正确提示配置项
  - 按配置项配置完成后，在需要使用的类中注入OssClient对象即可 （spring context中） 
  - 注意需要在springboot配置文件中配置 oss.enable=true 才能正确使用springboot完成自动化配置，管理对象

# 文件迁移
- 文件上很容易，但是表数据上相对处理起来较复杂，需要在设计时多考虑；
  - 表数据迁移不再考虑范围内
  - 文件迁移： Minio + Rclone
    - minio安装步骤
      - 略
    - Rclone 安装步骤
      - 使用包管理器直接安装即可，如 CentOS7
        - ```shell
            yum install rclone
          ``` 
      - 配置
        - 方式1：键入 rclone config，然后按照提示配置地址，地区，账号，密码等
        - 方式2：直接修改配置文件：包管理器安装的默认在 /root/.config/rclone/rclone.conf
          - 可以配置多个实例，做到跨服务器迁移文件夹的目的
          - ```shell
            [minio]
            type = s3
            provider = Minio
            env_auth = false
            access_key_id = root
            secret_access_key = 12345678
            region =
            endpoint = http://127.0.0.1:9900
            ```
      - 使用
        - rclone -h 看说明
        - rclone lsd minio: (这里的minio对应配置文件[minio]) 查看该实例下的所有桶
        - rclone ls minio:[bucket]
        - rclone copy minio:[bucket] minio2:[bucket] 迁移 一般用这个就够了
        - rclone copy [dir] [dir] 
        - rclone sync 慎用，会将目标桶与源桶中不同的内容清空， 请使用 --dry-run 先测试看看效果

