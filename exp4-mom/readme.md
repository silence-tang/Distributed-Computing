# 基于MOM的分布式随机信号分析系统设计说明



## 一、实验要求

#### 利用MOM消息队列技术实现一个分布式随机信号分析系统，具体要求：

##### 1.随机信号产生器微服务每隔100毫秒左右就产生一个正态分布的随机数字，并作为一个消息发布。

##### 2.一个随机信号统计分析微服务，对信号进行如下分析：

&emsp;&emsp;(1) 计算过去N个随机信号的均值和方差（N为常量，可设置）；

&emsp;&emsp;(2) 计算所有历史数据中的最大值和最小值；

&emsp;&emsp;(3) 定时地将分析结果打包成一个新消息并通过MOM发布出去。

##### 3.一个实时数据显示微服务：

&emsp;&emsp;(1) 实时绘制过去一段时间内随机信号的折线图；

&emsp;&emsp;(2) 实时显示随机信号统计分析结果。



## 二、设计说明

#### 1. 面向消息的中间件MOM

&emsp;&emsp;MOM(Message Oriented Middleware)是面向消息的中间件，使用消息提供者来协调消息传送操作。这种松耦合的通信机制有助于降低客户端和远程服务之间的依赖性。

&emsp;&emsp;生产者负责向分布式消息队列中发送消息，消费者从队列中取出消息。生产者不必关心消费者是谁，反之亦然，这降低了分布式节点之间的耦合度，提升了系统的通信效率。当消费者未上线或者消费者速度慢于生产者时，生产者可以将消息先缓存于消息队列中，待消费者上线后，消费者可以继续从队首取出消息，这种模式提高了系统的容错能力。

#### 2. ActiveMQ

&emsp;&emsp;ActiveMQ是Apache软件基金会所研发的开放源代码消息中间件，完全兼容JMS(Java Message Service)，能够为多种编程语言提供客户端API。本实验主要基于ActiveMQ和JMS实现了题目相关要求。

#### 3. 基于ActiveMQ的随机信号分析系统

#### (1) 设计思想

&emsp;&emsp; (a) **随机信号发生微服务作为生产者**，每隔100ms生成一个高斯分布随机值并且作为消息发布出去，主题名为`RandGaussian`。

&emsp;&emsp; (b) **信号分析微服务作为消费者**，订阅主题名为`RandGaussian`的消息，每次从消息队列中取出一个随机信号加入自己维护的数组中，然后对数组中的元素进行处理，得到信号的统计分析结果（均值、方差、最小值、最大值等等）。**与此同时，该分析器还作为生产者**，每隔2s将先前得到的信号分析结果以空格隔开，打包成字符串形式的消息发布出去，主题名为`AnalysisRes`。

&emsp;&emsp; (c) **分析结果可视化微服务同样作为消费者**。由于题目要求将过去一段时间的信号动态显示出来，也要求把信号分析结果显示出来，因此我让信号分析微服务在发送消息时，把当前的信号值也封装在消息中，这样可视化微服务只需要订阅主题为`AnalysisRes`的消息即可获取全部所需数据。在获取到所需数据后，我利用`tablesaw`可视化工具提供的折线图绘制功能将数据可视化到了网页页面中，一幅折线图中集成了当前信号值、过去N个数的均值以及历史最大最小值，且用不同颜色加以区分，可以达到一个比较好的视觉效果。

#### (2) 具体实现步骤

&emsp;**&emsp;为了具体实现题目要求，我编写了3个.java文件，分别为`Publisher.java`、`Analyzer.java`和`Visualizer.java`，这3个模块分别负责实现随机信号产生器微服务、随机信号统计分析微服务和实时数据显示微服务。**

&emsp;&emsp;**(a)** `Publisher.java`文件中只有一个Publisher类。首先定义实例构造函数`Pulisher(String topicName)`，通过该函数我们可以实例化一个新的、绑定名为`topicName`（本实验中即为`RandGaussian`）的主题的publisher对象并且启动与MOM的连接。其次，我定义了一个`sendNum(double mu ,double sigma)`函数，功能是根据用户输入的高斯分布的均值和方差产生一个符合该分布的随机数值，并利用producer.send()将该数值的字符形式当成消息发布出去。最后，还定义了一个`sendAnalysis(int num, int N, double value, double mean, double var, double min, double max)`函数，该函数负责将信号分析结果封装成以空格分隔的字符串消息并发布出去。在Publisher类的主方法中，首先需要用户输入高斯分布的均值和方差，然后程序循环生成随机数并发布，间隔100毫秒后发布下一条消息。

&emsp;&emsp;**(b)** `Analyzer.java`文件中有两个类，第一个是`public class Analyzer`类，主要实现对消费者对象的初始化（绑定主题`RandGaussian`）、获取用户输入的N值以及设置消息监听器，最后启动与MOM的连接。另一个类是`MyListener1`，即消息监听类。在该类中我重写了`onMessage`函数，使其能够实现题目的要求：开辟一个动态数组，每隔一段时间从消息队列中取得一个随机数并加入该数组，然后利用该数组即可求出过去N个信号的均值和方差，也可以求出全局的最小值和最大值。在得到一次分析的所有结果后，只需实例化一个publisher2对象，再调用Publisher类提供的`sendAnalysis`方法即可将分析结果打包成字符串发布出去（主题设为`AnalysisRes`）。

&emsp;&emsp;**(c)** `Visualizer.java`文件中有两个类，`public class Visualizer`类负责初始化消费者对象（订阅主题为`AnalysisRes`的消息）、设置监听器以及开启连接。另一个类是`MyListener2`，即消息监听类。在`onMessage`函数中，首先需要对得到的消息进行解包，这里我利用的是`String.split(" ")`函数对字符串进行分解，再将结果传入analysis数组中。解包之后，便可以将各类统计数据分别add到相应的动态数组中保存，便于后续的可视化操作。最后，设置一个`Thread.sleep(2000)`，即实现每隔2s从消息队列中取一个随机信号进行分析。

&emsp;&emsp;**(d)** 在可视化模块，我采用的是`tech.tablesaw`工具包中的`Plot.show(LinePlot)`函数。由于我想在同一幅图中将若干统计结果一起绘制出来，则需要将各个统计结果的横轴数值数组、纵轴数值数组和类型名数组分别合并到3个大数组中，这导致有一部分的代码显得比较冗长。由于在`Visualizer.java`中设置了`Thread.sleep(2000)`，因此在可视化模块中只需要不断地取出统计分析结果，即可实现每隔2s显示可视化结果。如果sleep时间设置得太短，则会导致瞬间产生很多统计图表，人眼看不过来，所以为了简洁，我将sleep时间设为了2s，可以起到接近实时的效果。



## 三、实验结果

**&emsp;&emsp;1. 先在`D:\apache-activemq-5.16.1\bin`路径下用cmd输入`activemq start`开启服务，此时可以看到历史上的所有主题，包括实验中涉及到的RandGaussian和AnalysisRes，且这两个主题的消费者数均为0，已入队和出队的消息数均为0。**

[![vZElCR.png](https://s1.ax1x.com/2022/08/03/vZElCR.png)](https://imgtu.com/i/vZElCR)

**&emsp;&emsp;2. 在项目文件根目录下用cmd输入`mvn clean`及`mvn compile`，再依次执行Publisher、Analyzer、Visualizer3个主类。先在Publisher主类的命令行中输入高斯分布的均值和方差，再在Analyzer主类的命令行中输入N值，最后执行Visualizer主类开启可视化微服务。此时打开ActiveMQ控制页面，可以发现各主题的状态（消费者数、入队消息数、出队消息数）发生了变化：**

[![vZEUVe.png](https://s1.ax1x.com/2022/08/03/vZEUVe.png)](https://imgtu.com/i/vZEUVe)

**&emsp;&emsp;3. 在Visualizer主类的命令行中，是实时显示的统计分析结果（过去N个信号的均值、方差和全局最小值最大值等）：**

[![vZEd5d.png](https://s1.ax1x.com/2022/08/03/vZEd5d.png)](https://imgtu.com/i/vZEd5d)

**&emsp;&emsp;4. 浏览器页面在不断实时显示随机信号统计分析的可视化折线图，各种统计量的变化一目了然：**

[![vZED2t.png](https://s1.ax1x.com/2022/08/03/vZED2t.png)](https://imgtu.com/i/vZED2t)

**补充说明：可视化部分我采用的工具tablesaw在渲染HTML页面时调用的是https://cdn.plot.ly/plotly-latest.min.js提供的JavaScript代码，而该CDN节点有时存在不稳定的情况，可能导致页面渲染失败，但仍能从网页源代码处看到图表背后的数据，例如：**

[![vZE6r8.png](https://s1.ax1x.com/2022/08/03/vZE6r8.png)](https://imgtu.com/i/vZE6r8)
