# qof-spring-boot-starter

使用方式

1. 添加项目的依赖

   ```xml
    <dependency>
        <groupId>io.github.codeyunze</groupId>
        <artifactId>qof-spring-boot-starter</artifactId>
        <version>0.0.1</version>
    </dependency>
   ```

2. 选择对应的文件存储模式

   | 存储模式 | 说明                                                         | 是否支持 |
   | -------- | ------------------------------------------------------------ | -------- |
   | local    | 本地存储，将文件存储在服务器本地。                           | 支持     |
   | cos      | 腾讯云的COS对象存储，将文件上传至腾讯云的COS对象存储服务里。 | 支持     |
   | oss      | 阿里云的OSS对象存储，将文件上传至阿里云的OSS对象存储服务里。 | 待开发   |

3. 添加对应存储模式的配置信息

   

4. 啊



对象存储分为如下两个维度
1. 第一级为存储模式( `mode` )，如 `local` （本地存储）、 `cos` （腾讯云对象存储）、 `oss` （阿里云对象存储）等

2. 第二级为存储站( `station` )，存储站的定义为一个存储模式下可以有多个存储站点；

   **local模式** 案例:

   ​	同一个模式local下，一部分文件要存C盘，一部分文件要存D盘。

   ​	这时候就可以使用存储站，第一个存储站station-c，存储路径指向C盘，第二个存储站station-d，存储路径指向D盘。

   **cos模式** 案例:

   ​	同一个模式cos下，一部分文件要存放在ap-guangzhou（广州地区）的存储桶bucket-gz，另一部分文件要存放在ap-beijing（北京地区）的存储桶bucket-bj。

   ​	这时候就可以使用存储站，第一个存储站gz-station，存储路径指向存储桶bucket-gz，第二个存储站bj-station指向bucket-bj。



