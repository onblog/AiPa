# 一款小巧、灵活的Java多线程爬虫框架（AiPa）爱爬

## 1.框架简介

AiPa 是一款小巧，灵活，扩展性高的多线程爬虫框架。

AiPa 依赖当下最简单的HTML解析器Jsoup。

AiPa 只需要使用者提供网址集合，即可在多线程下自动爬取，并对一些异常进行处理。

## 2.下载安装

AiPa是一个小巧的、只有390KB的jar包。

下载该Jar包导入到你的项目中即可使用。

jar包存放在Git，下载：[AIPa.jar](AIPa.jar)

## 3.如何使用

先来看下一个简单完整的示例程序：

必须实现的接口
```java
public class MyAiPaWorker implements AiPaWorker {

    @Override
    public String run(Document doc, AiPaUtil util) {
        //使用JSOUP进行HTML解析获取想要的div节点和属性
        //保存在数据库或本地文件中
        //新增aiPaUtil工具类可以再次请求网址
        return doc.title() + doc.body().text();
    }

    @Override
    public Boolean fail(String link) {
        //任务执行失败
        //可以记录失败网址
        //记录日志
        return false;
    }
}
```

Main方法

```java
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
        //准备网址集合
        List<String> linkList = new ArrayList<>();
        linkList.add("http://jb39.com/jibing/FeiQiZhong265988.htm");
        linkList.add("http://jb39.com/jibing/XiaoErGuoDu262953.htm");
        linkList.add("http://jb39.com/jibing/XinShengErShiFei250995.htm");
        linkList.add("http://jb39.com/jibing/GaoYuanFeiShuiZhong260310.htm");
        linkList.add("http://jb39.com/zhengzhuang/LuoYin337449.htm");
        //第一步：新建AiPa实例
        AiPaExecutor aiPaExecutor = AiPa.newInstance(new MyAiPaWorker()).setCharset(Charset.forName("GBK"));
        //第二步：提交任务
        for (int i = 0; i < 10; i++) {
            aiPaExecutor.submit(linkList);
        }
        //第三步：读取返回值
        List<Future> futureList = aiPaExecutor.getFutureList();
        for (int i = 0; i < futureList.size(); i++) {
            //get() 方法会阻塞当前线程直到获取返回值
            System.out.println(futureList.get(i).get());
        }
        //第四步：关闭线程池
        aiPaExecutor.shutdown();
    }
```

通过`AiPa.newInstance()`方法直接创建一个新的AiPa实例，该方法必须要传入 AiPaWorker 接口的实现类。

### 3.1 AiPaWorker接口

AiPaWorker 接口是用户必须要实现的业务类。

该接口方法如下：

```java
public interface AiPaWorker<T,S> {
    /**
     * 如何解析爬下来的HTML文档？
     * @param doc JSOUP提供的文档
     * @param util 爬虫工具类
     * @return
     */
    T run(Document doc, AiPaUtil util);

    /**
     * run方法异常则执行fail方法
     * @param link 网址
     * @return
     */
    S fail(String link);
}
```

`run()`方法是用户自定义处理爬取的HTML内容，一般是利用Jsoup的Document类进行解析，获取节点或属性等，然后保存到数据库或本地文件中。如果在业务方法需要再次请求URL，可以使用工具类Util。

`fail()`方法是当run()方法出现异常或爬取网页时异常，多次处理无效的情况下进入的方法，该方法的参数为此次出错的网址。一般是对其进行日志记录等操作。

### 3.2 解码，最多失败次数，请求头

通过AiPa获取实例后，可以直接在后面跟着设置一大堆属性，比如：setCharset、setThreads、setMaxFailCount等，这些属性啥意思，下面以表格的形式说明一下：

| 方法                | 说明                                                         |
| ------------------- | ------------------------------------------------------------ |
| **setThreads**      | 工作线程数，默认CPU数量+1，你也可以设置CPU*2等等             |
| **setMaxFailCount** | 最大失败次数，也就是爬网站出现异常，再次爬一共尝试多少次，默认5 |
| setCharset          | 网页的编码，碰到乱码设置这个，默认UTF-8                      |
| setHeader           | 设置请求头，只接受Map<String,String>类型，默认null           |
| setMethod           | 设置请求方法，默认Method.GET                                 |
| setTimeout          | 请求解析的等待时间，默认30秒。                               |
| setUserAgent        | 设置请求的UA，默认电脑版。                                   |
| setCookies        | 设置Cookie集合，默认null                                  |

上面的一般情况下够用了，如果对这些不满意，嫌太少啥的，下面给了更优秀的解决方案。

### 3.3 自定义爬虫方法

在上面的演示程序中，我们使用了`submit()`方法进行提交任务，默认是使用了Jsoup+上面的那些非加粗属性进行爬取，一般情况下够用，如果要一个一个的扩展Jsoup的方法太累了，于是我想到把爬虫方法提供给用户重，让用户自己去扩展，想用什么爬，想设置什么属性都可以。

下面请看使用Demo：

```java
public class MyAiPaUtil extends AiPaUtil {

    @Override
    public Document getHtmlDocument(String link) throws IOException {
        // 你可以不用JSOUP，可以使用其它方法进行HTTP请求，但最后需要转为Document格式
        // 你也可以使用Jsoup实现定制属性
        Connection connection = Jsoup.connect(link).method(Connection.Method.GET);
        String body = connection.execute().charset("GBK").body();
        
        return Jsoup.parse(body);
    }

}
```

然后，再调用submit方法提交任务，代码示例：

```
aiPaExecutor.submit(linkList, MyAiPaUtil.class);
```

注意：当你重写爬虫方法后，3.2小节的非加粗属性都会失效。

### 3.3 读取返回值与获取线程池

如果你想要读取返回值来看下任务是否执行成功，你可以使用看下上面的程示例序是如何做的。

```
public List<Future> getFutureList()
```

getFutureList()方法会返回任务执行之后的结果集合，集合中的成员都是Future类。调用Future对象的 get() 方法会等待当前任务执行完成再返回结果值，也就是会阻塞当前线程。该类还有很多方法，比如get(long timeout, TimeUnit unit)，设置等待时间等等。

```
public ExecutorService getExecutor()
```

该方法会返回AiPa当前使用的Executor线程池，你获取到该线程池后，需要一些使用线程池的一些方法可以自行使用。

### 3.4 如何应对爬取网页时的异常

对于网页爬取时的异常，这真的是个痛点。原因真的很多，你的网络不行，网站服务器的网络不行，在网上有说把请求头中Connection设置为close，不用keep-alive。这个以我爬取几百兆数据的经验告诉你，然并卵。

于是我想出了一种无赖打法，反复爬。爬一次不行就两次，爬两次不行就三次，只要网页是可以正常响应的，基本这个策略没多少问题。当然，万一真的是某个网页就那么独树一帜呢，所以我们设置一个最大值，对于爬取超过最大值的，放弃记录下来，看看啥子情况。在我的这个框架中，也给出了fail()方法专门处理这个问题。

## 4.测试用例

在Java SE测试中。没有使用数据库等，直接控制台打印是没问题的。

在Spring Boot中写了个测试用例，爬取数据保存到数据库，运行也没问题。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class InterApplicationTests {

    @Autowired
    private DemoResponse demoResponse;

    @Test
    public void context() throws ExecutionException, InterruptedException {
        AiPaExecutor executor = AiPa.newInstance(new AiPaWorker() {
            @Override
            public Boolean run(Document document, AiPaUtil util) {
                String title = document.title();
                demoResponse.save(new DemoEntity(title));
                return true;
            }

            @Override
            public Boolean fail(String s) {
                demoResponse.save(new DemoEntity(s));
                return false;
            }
        }).setCharset(Charset.forName("GBK"));

        List<String> linkList = new ArrayList<>();
        linkList.add("http://jb39.com/jibing/FeiQiZhong265988.htm");
        linkList.add("http://jb39.com/jibing/XiaoErGuoDu262953.htm");
        linkList.add("http://jb39.com/jibing/XinShengErShiFei250995.htm");
        linkList.add("http://jb39.com/jibing/GaoYuanFeiShuiZhong260310.htm");
        linkList.add("http://jb39.com/zhengzhuang/LuoYin337449.htm");
        executor.submit(linkList);

        List<Future> list = executor.getFutureList();
        for (int i = 0; i < list.size(); i++) {
            //get() 方法会阻塞当前线程直到获取返回值
            System.out.println(list.get(i).get());
        }
        executor.shutdown();
    }

}
```

运行结果：

```
Hibernate: insert into demo (title) values (?)
Hibernate: insert into demo (title) values (?)
Hibernate: insert into demo (title) values (?)
Hibernate: insert into demo (title) values (?)
Hibernate: insert into demo (title) values (?)
```

## 5.关于作者

*由于作者水平有限，框架一定存在一些漏洞或不足，希望各位专家、大佬提出批评指正！*

*我的博客：https://yueshutong.cnblogs.com/*

*Github：https://github.com/yueshutong/AIPa*

*Giree：https://gitee.com/zyzpp/AIPa*

交流QQ群：781927207
