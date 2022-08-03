# 基于PRC技术的书籍信息管理系统——设计说明



## 一、实验要求

&emsp;&emsp;利用RPC技术实现一个书籍信息管理系统，具体要求：

&emsp;&emsp;1.客户端实现用户交互，服务器端实现书籍信息存储和管理。客户端与服务器端利用RPC机制进行协作。中间件任选。

&emsp;&emsp;2.服务器端至少暴露如下RPC接口∶
   &emsp;&emsp;**·** bool add(Book b) 添加一个书籍对象。
   &emsp;&emsp;**·** Book queryByID(int bookID) 查询指定ID号的书籍对象。
   &emsp;&emsp;**·** BookList queryByName(String name) 按书名查询符合条件的书籍对象列表，支持模糊查询。
   &emsp;&emsp;**·** bool delete(int bookID) 删除指定ID号的书籍对象。

## 二、设计说明

### 1. RPC 中间件的选取

&emsp;&emsp;本次实验我采用java作为编程语言，因此我选用了Java RMI作为RPC机制的中间件。

&emsp;&emsp;Java RMI，即远程方法调用(Remote Method Invocation)，是一种用于实现远程过程调用(RPC)的Java API，可用于构建分布式应用程序，能直接传输序列化后的Java对象并提供分布式垃圾收集服务。它的实现依赖于Java虚拟机(JVM)，因此它仅支持从一个JVM到另一个JVM的调用，实现了Java程序之间跨JVM的远程通信。

### 2. RMI 过程

&emsp;&emsp;一个RMI过程主要涉及三类对象：RMI客户端、RMI服务端、Registry注册中心。RMI客户端主要是调用服务端提供的远程方法；RMI服务端是远程调用方法对象的提供者，也是代码真正执行的地方，执行结束会返回给客户端一个方法执行的结果；注册中心本质是一个map，相当于是字典一样，向客户端提供客户端要调用的方法的引用。

&emsp;&emsp;RMI架构图如下：

![img](https://upload-images.jianshu.io/upload_images/12696746-07f8e95ec97df9dd.png?imageMogr2/auto-orient/strip|imageView2/2/w/656/format/webp)

&emsp;&emsp;**RMI底层通讯采用了Stub和Skeleton机制，RMI调用远程方法的过程大致如下：**

&emsp;&emsp;(1) RMI客户端在调用远程方法时先创建Stub。

&emsp;&emsp;(2) Stub会将Remote对象传递给远程引用层并创建远程调用对象。

&emsp;&emsp;(3) RemoteCall序列化RMI服务名称、Remote对象。

&emsp;&emsp;(4) RMI客户端的远程引用层传输RemoteCall序列化后的请求信息通过Socket连接的方式（传输层）传输到RMI服务端的远程引用层。

&emsp;&emsp;(5) RMI服务端的远程引用层收到请求会请求传递给Skeleton。

&emsp;&emsp;(6) Skeleton调用RemoteCall反序列化RMI客户端传过来的请求信息。

&emsp;&emsp;(7) Skeleton处理客户端请求：bind、list、lookup、rebind、unbind，如果是lookup则查找RMI服务名绑定的接口对象，序列化该对象并通过RemoteCall传输到客户端。

&emsp;&emsp;(8) RMI客户端反序列化服务端结果，获取远程对象的引用。

&emsp;&emsp;(9) RMI客户端调用远程方法，RMI服务端反射调用RMI服务实现类的对应方法并序列化执行结果返回给客户端。

&emsp;&emsp;(10) RMI客户端反序列化RMI远程方法调用结果，即为最终结果。

### 3. 基于Java RMI的书籍信息管理系统

#### (1) 设计思想

&emsp;&emsp;根据Java RMI的框架，本次实验中涉及到服务端、客户端、注册中心三方角色。为了满足题目要求，服务端需实现的功能为：(a) 将自己提供的远程服务注册到注册中心，(b) 存储、维护和管理书籍信息系统的数据结构；客户端需实现的功能为：(a) 查询注册中心获得远程方法的引用，(b) 调用远程方法实现与书籍管理系统的信息交互，(c) 提供简洁的用户交互界面，以处理用户的各种操作请求与输入输出。

&emsp;&emsp;本次实验要求实现书籍管理系统并提供增删查等操作接口，为了便于存储与维护书籍对象，我采用了Java中的ArrayList作为书籍对象的存储数据结构。ArrayList有如下几个优点：支持自动改变大小（动态数组）、适合随机查找和遍历。此外，对于书籍对象，需要设计一个book类以定义其主要属性如ID、书名等，并在类中实现一些实用方法便于后续的调用。

#### (2) 具体实现步骤

&emsp;&emsp;(a) 定义书籍类Book，在其中定义书籍类属性bookID和bookName，接着定义实例的构造方法（即传入书籍ID和书名创建书籍对象），最后定义一个BookInfo()方法用来打印某个书籍对象的信息。

&emsp;&emsp;(b) 定义接口BookSystemInt，在其中声明系统需要用到的所有方法，且各方法均需要抛出RemoteException异常。

&emsp;&emsp;(c) 定义远程对象实现类BookSystemImpl，在其中实现书籍信息管理系统的数据结构定义以及数据操纵函数，包括定义存储书籍对象的ArrayList动态数组，定义具体的增删改查实现函数。对增删改查各函数功能的具体实现步骤说明如下：

&emsp;&emsp;**·** **bool add(Book b)**：先遍历bookList看是否存在同ID书籍，若存在则拒绝添加，返回false；否则使用ArrayList自带的add()方法添加新书籍并返回true。

&emsp;&emsp;**·** **Book queryByID(int bookID)**：遍历bookList看是否存在ID=bookID的书籍，若存在则表示查询成功，返回true；否则返回false。

&emsp;&emsp;**·** **BookList queryByName(String name)**：遍历bookList看是否存在bookName能和name模糊匹配成功的书籍，若存在则表示查询成功，返回true；否则返回false。通过判断b.bookName.indexOf(name, 0)的返回值是否为-1来确定字符串之间的模糊匹配是否成功。

&emsp;&emsp;**· bool delete(int bookID)**：遍历bookList看是否存在ID=bookID的书籍，若存在则使用ArrayList自带的remove()方法删除指定ID的书籍并返回true；否则返回false，表示未找到ID=bookID的书籍，无法删除。

&emsp;&emsp;**· bool alter(int bookID)**：遍历bookList看是否存在ID=bookID的书籍，若存在则用b.bookName = newName修改指定ID书籍的书名为新的书名并返回true；否则返回false，表示未找到ID=bookID的书籍，无法对其进行修改。

&emsp;&emsp;**· StringBuilder printBookList()**：先用bookList.size() == 0判断书籍列表是否为空，若为空则直接返回"none"，否则遍历书籍列表并不断用StringBuilder.append()方法将当前书籍的信息字符串添加到存储总书籍信息的StringBuilder对象中，最后再返回该StringBuilder对象。

&emsp;&emsp;(d) 定义服务器实现类RMIServer，先定义RMI服务器IP地址、监听端口号、RMI服务名称，再创建注册中心，使其监听来自指定端口的请求，最后将RMI服务名绑定至远程方法实现类上。

&emsp;&emsp;(e) 定义客户端实现类RMIClient，在其中实现简单的用户交互界面的搭建。主要包括显示所有的功能列表，根据用户输入的功能号调用相应的远程方法进行对书籍的增删改查，再根据远程调用返回的结果进行后续处理等等。首先要向注册中心look up RMI服务名，获取远程对象的引用，再去根据引用调用远程方法实现功能。

#### (3) 注意事项&要点记录

&emsp;&emsp;(a) Book类必须使用Serializable接口，类中的所有属性及方法都应定义为public公共对象，否则在其他类中无法访问Book实例的属性，也无法使用方法。

&emsp;&emsp;(b) 接口必须定义为public类型并且继承Remote类作为远程对象，否则客户端在尝试加载实现远程接口的远程对象时会出错。

&emsp;&emsp;(c) 远程对象实现类必须继承UnicastRemoteObject类，必须覆写接口中的全部抽象方法。在其中定义书籍列表对象时一定要写全new ArrayList<Book>()，若只定义ArrayList<Book> list而不给它赋值，则代表仅仅创建了一个list对象的引用，而对象本身还没有初始化，因此会引发空指针异常。此外，在实现类中所有的函数均应有返回值，以便客户端根据返回值进行后续处理。若在这些函数中使用了System.out.println()，则会在服务器端打印信息，因为实现类中的对象都是在服务端的JVM中运行维护的，默认的输出环境也是在服务器端。

&emsp;&emsp;(d) Java中字符串之间是否相等应该用str1.equal(str2)，而不能用==。

&emsp;&emsp;(e) C语言中的goto语句在Java中可以通过while(true)搭配continue和break实现。
