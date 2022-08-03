## 一、实验内容

### 题目1

输入文件为学生成绩信息，包含了必修课与选修课成绩，格式如下：

班级1, 姓名1, 科目1, 必修, 成绩1 `<br>` （注：`<br>`为换行符）

班级2, 姓名2, 科目1, 必修, 成绩2 `<br>`

班级1, 姓名1, 科目2, 选修, 成绩3 `<br>`

………., ………, ………, ………, ………  `<br>`

编写两个Hadoop平台上的MapReduce程序，分别实现如下功能：

1. 计算每个学生必修课的平均成绩。

2. 按科目统计每个班的平均成绩。

### 题目2

输入文件的每一行为具有父子/父女/母子/母女/关系的一对人名，例如：

Tim, Andy `<br>`

Harry, Alice `<br>`

Mark, Louis `<br>`

Andy, Joseph `<br>`

……….., ………… `<br>`

假定不会出现重名现象。

1. 编写Hadoop平台上的MapReduce程序，找出所有具有grandchild-grandparent关系的人名组。

### 题目3

输入文件为学生成绩信息，包含了必修课与选修课成绩，格式如下：

班级1, 姓名1, 科目1, 必修, 成绩1 `<br>` （注：`<br>` 为换行符）

班级2, 姓名2, 科目1, 必修, 成绩2 `<br>`

班级1, 姓名1, 科目2, 选修, 成绩3 `<br>`

………., ………, ………, ………, .........  `<br>`

编写一个Spark程序，同时实现如下功能：

1. 计算每个学生必修课的平均成绩。
2. 统计学生必修课平均成绩在：90~100,80~89,70~79,60~69和60分以下这5个分数段的人数。

## 二、设计思想

### 题目1

#### 1. 计算每个学生必修课的平均成绩

**&emsp;&emsp;Map阶段：**

&emsp;&emsp;(1) 预处理。对txt文档的每一行，先用split函数将其用","分隔成若干字符串，并存于数组splited中。

&emsp;&emsp;(2) 过滤。由于成绩文档的每一行形如“班级,姓名,课程名,性质,分数”，而我们需要统计学生的必修课平均分，因此可以用`splited[3].equals("必修")`的条件过滤掉选修课所在的行。

&emsp;&emsp;(3) 设置Map的输出格式。由于我们是对每个学生求一个平均分，故思路很直接：让Map阶段的输出是形如<"姓名": 成绩>的kv对。其中学生姓名字符串和成绩字符串可分别从splited[1]、splited[4]直接得到。

**&emsp;&emsp;Reduce阶段：**

&emsp;&emsp;(1) 观察Shuffling阶段的输出。根据我们上面设置的Map阶段输出，可以得知：Shuffling后单个学生的所有必修课成绩已被归并至一个列表中，作为以该生的姓名为key的键值对的value。

&emsp;&emsp;(2) 求平均成绩。对于每个学生，我们可以遍历他的value列表并求出他的所有必修课总分。该过程涉及到数据类型的转换，如`sum += Integer.valueOf(grade.toString());`。在得到总分之后，直接除以该生的成绩条目个数（用i自增1得到）即可得到他的平均成绩。

&emsp;&emsp;(3) 设置Reduce的输出格式。这里我简单地用`String.format("%.2f", avg);`设置了输出成绩小数点后保留两位小数。我将Reduce的输出设置为形如<“姓名”: 必修课平均成绩>的格式。

#### 2. 按科目统计每个班的平均成绩

**&emsp;&emsp;Map阶段：**

&emsp;&emsp;(1) 预处理。对txt文档的每一行，先用split函数将其用","分隔成若干字符串，并存于数组splited中。

&emsp;&emsp;(2) 设置Map的输出格式。题目要求按科目统计每个班的平均成绩，只要我们将“科目”和“班级”这两个字段看成是一个字段，就可以直接套用计算学生平均成绩的方法来求解本题。因此，这里我将Map阶段的输出设置为形如<“科目 班级”: 成绩>，其中科目、班级、成绩的字符串均和上面的实验类似，可直接从splited[0]、splited[2]、splited[4]得到。

**&emsp;&emsp;Reduce阶段：**

&emsp;&emsp;(1) 观察Shuffling阶段的输出。根据我们上面设置的Map阶段输出，可以得知：Shuffling后各班各科目的成绩均已被归并至一个列表中，形如<“科目 班级”: [成绩1, 成绩2, ..., 成绩m]>，其中m是该班级考这门课的人数。

&emsp;&emsp;(2) 求平均成绩。对于每个"科目 班级"键，我们可以遍历其value列表来求出该班级所有学生在这门课的总成绩。该过程涉及到数据类型的转换。在得到总分之后，直接除以该班考这门课的学生个数（用i自增1得到）即可得到该班在该科目的平均成绩。

&emsp;&emsp;(3) 设置Reduce的输出格式。这里我简单地用`String.format("%.1f", avg);`设置了输出成绩小数点后保留一位小数。我将Reduce的输出设置为形如<“科目 班级”: 平均成绩>的格式。

### 题目2

**&emsp;&emsp;Map阶段：**

&emsp;&emsp;(1) 预处理。对txt文档的每一行，先用split函数将其用","分隔成若干字符串，并存于数组splited中。splited[0]为父母姓名，splited[1]为子女姓名。

&emsp;&emsp;(2) 设置Map的输出格式。由于我的目的是让任务经过Shuffling阶段后生成类似<"某人姓名": ["父亲姓名", "母亲姓名","儿子姓名"]>这样的输出，因此我将Map阶段的输出设置为对于原始数据的每一行，输出两个kv对：一个是以该行的splited[0]为键，splited[1]为值，这么做可以保证Shuffling之后得到以某人姓名为键，其所有子女姓名为值的kv对；另一个是以该行的splited[1]为键，splited[0]为值（因为一个人他可能既有父母的身份，也有子女的身份），这么做可以使得Shuffling之后得到以某人姓名为键，其父母姓名为值的kv对。因为Shuffling会把所有同键的value聚集到一个列表中，因此不难发现Shuffling后我们可以得到以某人姓名为键，其子女、父母姓名为值的kv对。此外，为了便于Reduce阶段将父母姓名和孩子姓名分别存入列表中，我在Map阶段设置context.write时将孩子姓名开头加上“0”，将父母姓名开头加上“1”。

**&emsp;&emsp;Reduce阶段：**

&emsp;&emsp;(1) 观察Shuffling阶段的输出。根据上述分析可知：Shuffling后我们可以得到**以某人姓名为键，其子女、父母姓名为值的kv对，即形如<"某人姓名": ["父亲姓名", "母亲姓名","儿子姓名"]>的kv对**。

&emsp;&emsp;(2) 构造grandparents和grandchildren列表。遍历当前key的value列表，若姓名以0开头，则将该姓名加入到grandchildren中，否则加入到grandparents中。

&emsp;&emsp;(3) 设置Reduce的输出格式。题目要求我们输出所有具有grandchild-grandparent关系的人名组。因此，只需要写一个二重循环，遍历grandchildren和grandparents列表，输出所有可能的以grandchild姓名为键，grandparent姓名为值的kv对即可，因为可以肯定grandparents列表中的所有人必定是grandchild列表中所有人的祖辈。

### 题目3

&emsp;&emsp;(1) 利用filter方法过滤掉不是必修课的行。

&emsp;&emsp;(2) 利用map方法将一行数据映射为一个<姓名: (成绩, 1)>对，其中用到了split方法分割出若干字段。

&emsp;&emsp;(3) 利用reduceByKey方法将所有同key项聚合起来，聚合方式为所有同key的value的第一分量相加，得到一个学生的总必修成绩，同时将同key的value的第二分量相加，得到该学生修的必修课总数。然后，利用mapValues方法求出每个学生的平均成绩，即：将(总必修成绩, 必修课程数)这一value映射为该学生的平均成绩，方式为用总成绩除以必修课程数。

&emsp;&emsp;(4) 通过上述步骤我们已经求得了每个学生的必修课均分。先将各学生的<"姓名": 必修课均分>kv对用map方法映射为<“分段”: 1>，然后再用reduceByKey方法将所有同分段的学生聚集起来，求出各分段的人数。

## 三、主要步骤和实验结果

### 1. 实验环境搭建

&emsp;&emsp;(1) 安装 Docker 引擎和 WSL2 Linux 内核更新包。 Docker 安装完毕后在设置界面配置国内镜像仓库地址。

&emsp;&emsp;(2) 导入包含实验环境的 Docker 镜像：(1) 利用`docker load --input hadoopsparkv2.tar`将压缩包 hadoopsparkv2.tar 中的 Dockers 镜像 ubuntu-jdk8-hadoop-spark:v2 镜像导入的本机的 Docker 引擎中。(2) 将 hadoopspark.zip 中的内容解压到 xxxx/hadoopspark 目录下，然后在命令终端模式下将当前目录切换到 xxxx/hadoopspark。

&emsp;&emsp;(3) 启动实验环境：首先开启 Docker Desktop 。将当前目录切换到 xxxx/hadoopspark ，然后运行命令`docker-compose up -d`和`docker ps`，可以看到基于 ubuntu-jdk8-hadoop-spark:v2 镜像的 Docker 容器（虚拟机）在运行：

[![vZmyWj.png](https://s1.ax1x.com/2022/08/03/vZmyWj.png)](https://imgtu.com/i/vZmyWj)

&emsp;&emsp;(4) 用`ssh -p 2222 root@localhost`命令通过 ssh 协议从本机（宿主机）远程登陆到 `hadoopspark_singlenode` 虚拟机

内部，密码为123456。

[![vZmvTO.png](https://s1.ax1x.com/2022/08/03/vZmvTO.png)](https://imgtu.com/i/vZmvTO)

&emsp;&emsp;(5) 用 `start-dfs.sh` 命令启动 HDFS 分布式文件系统。

&emsp;&emsp;(6) 若要关闭实验环境，先确保当前虚拟机中所有job均已完成，然后在命令行中输入 `logout` 登出虚拟机。最后在 xxxx/hadoopspark 目录下使用 `docker-compose down `关闭实验环境。

### 2. 上传txt文件

&emsp;&emsp;(1) 启动 HDFS 分布式文件系统：`start-dfs.sh`。

&emsp;&emsp;(2) 在 hadoop平台新建目录：`hdfs dfs -mkdir -p /user。`

&emsp;&emsp;(3) 将本地目录切换到实验所在的文件夹，然后输入以下命令上传 `grades.txt` 和 `child-parent.txt` 文件：`hadoop fs -put ./grades.txt /user`，`hadoop fs -put ./child-parent.txt /user`。

### 3. 执行程序

#### 实验1：

&emsp;&emsp;(1) 在本地使用`mvn clean` + `mvn package`命令将程序打成 jar 包。

&emsp;&emsp;(2) 在平台执行功能1的 MapReduce 程序：`hadoop jar ./target/AvgGradeStudent.jar com.org.xidian.MapReduceAvgGradeStudent /user/grades.txt /output1`。

&emsp;&emsp;(2) 在平台执行功能2的 MapReduce 程序：`hadoop jar ./target/AvgGradeClass.jar com.org.xidian.MapReduceAvgGradeClass /user/grades.txt /output2`。

#### 实验二：

&emsp;&emsp;(1) 在本地使用`mvn clean` + `mvn package`命令将程序打成jar包。

&emsp;&emsp;(2) 在平台执行MapReduce程序：`hadoop jar ./target/AvgGradeClass.jar com.org.xidian.MapReduceAvgGradeClass /user/grades.txt /output2`。

#### 实验三：

&emsp;&emsp;(1) 将本地目录切换到/share下，执行`spark-submit sparkexp.py`即可执行程序。

## 四、遇到的问题及解决方法

1. 一开始我尝试使用第一种方法搭建实验环境，前面的步骤都很顺利，但是ssh连接远程主机sandbox一直提示connection refused或者是Connection closed by remote host。由于一开始不知道是client-node频繁重启造成的原因，所以去检查本机的ssh服务等是否有问题。我尝试ssh连接自己的主机，但是发现连接不上，搜了解决方案后安装了Windows的OpenSSH服务器和客户端，并且在系统的“服务”中设置了OpenSSH SSH Server的启动类型为自动，这才解决了SSH连接方面的问题。后来导入了老师发的clientnode镜像文件，解决了client-node频繁重启的问题，但由于这时候方法二已经跑通了，故放弃了继续进行方法一的环境搭建。

2. 在hadoopspark目录下执行docker-compose up -d失败，原因是本地未开启docker服务，开启后成功解决。

3. 在分布式文件系统 HDFS 中创建子目录时，使用老师文档中给的`hadoop fs –mkdir test`提示`hdfs://localhost:9000/user/root': No such file or directory`。后来多次尝试后发现在要创建的子目录名前加"/"即可成功创建：

   [![vZmzkD.png](https://s1.ax1x.com/2022/08/03/vZmzkD.png)](https://imgtu.com/i/vZmzkD)

4. 在写MapReduce程序时，我先是对着老师写的例程观摩了一番，然后准备在此基础上进行修改，以使之符合实验要求。一开始我想将context.write的value的类型改成Text（字符串），但是发现有多处报错，后来查阅资料得知需要相应地修改Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>类的out的数据类型和job.setMapOutputValueClass();的参数，使其均与Text类型一致才行。

5. 在编写寻找祖孙关系的程序时，我查看了child-parent.txt文档，发现假设第一列是父母的话，会出现一个人有五六个父母的情况，这是不现实的，因此我将第二列当成父母，第一列当成子女进行了程序编写。

6. 在编写pyspark程序时，由于一开始还不太能掌握编写技巧，因此我先对着网上的例程学习了基本的filter、map、mapValues、reduceByKey等方法的使用要点，并且逐步将这些方法运用在测试数据上，然后观察hadoop平台的运行结果，再根据结果不断调整代码，最终完成了程序编写。

7. 在提交程序至hadoop平台运行之前，若输入的命令中把要处理的文档写错了（比如child-parent.txt写成grades.txt）则会导致输出的文档大小为0B，因此在输入命令时一定要小心而细致。

## 五、心得体会

1. 通过本次实验，我深入理解了分布式并行计算的宏观思想，对于MapReduce并行计算模型的Map、Shuffling、Reduce阶段的详细执行过程有了感性和直观的认识。
2. 通过本次实验，我掌握了MapReduce程序的编写思想和实操技巧，对Mapper类、Reducer类、Client类的各个参数和负责的功能有了一定的认识，理解了什么叫做“平台负责处理和业务无关的通用业务，应用程序专注于业务逻辑”。
3. 通过本次实验，我加深了对Spark分布式计算平台的计算特点的认识，包括RDD对象的生成、转换、恢复机制以及Transformation算子的作用。我学会了利用pyspark编写简单的Spark程序，同时对于map、reduceByKey、mapValue、sortByKey等操作RDD的方法的使用细节有了一定的认识。
4. 在实验过程中，我也遇到了不少的困难，包括ssh连接失败、hadoop命令执行失败等问题，但最终通过不断尝试以及和同学讨论解决了这些问题。经过本次实验的多次操作，我提高了命令行操作的熟练度，对于文件系统的路径书写方式更清楚了。
5. 在编写程序的过程中，一开始我是通过观摩老师写的例程来揣摩、学习MapReduce和Spark程序的编写思想，然后在看不懂的地方去搜一下具体在做什么事。在初步琢磨明白一个完整程序的实现过程后，我再开始自己着手写实验题目，这样写起来也是比较快的，错误也比较少。我遇到报错了会先根据命令行返回的错误信息去排查，如果看不出来错在哪再去对比例程，看是不是哪里的细节出错了。
