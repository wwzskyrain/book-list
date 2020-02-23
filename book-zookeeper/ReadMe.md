# 1.问题列表
1.  zk中的三个状态
2.  zk中1个client是一个session吗?同一个client的多线程对
    同一个path操作，其listener是谁调用呢？
3.  获取数据的前必须先建立和这个path的"链接"吗？不用，这个和session
    不一样，session对应的是底层网络链路，是tcp的那一套。而已经和主机
    对接上之后，在具体方位某个'path'时，是不需要建立链接的。如果需要
    那么链接维护就很耗费资源了。没有必要
4.  永久结点会持久化吗?当服务器重启后，该节点还在吗？
5.  操作都要有stat对象，这个技巧可以很好的避免并发问题
6.  



# 2.总结
1.  链接创建：链接创建是一个异步过程，创建结果需要等异步回调。正常情况可以用 CountDownLatch来做回话创建的同步化。
2.  结点创建：
    1.  有同步创建和异步创建两种。都有创建失败的可能。
        同步时失败以异常形式表现，异步时以回调函数参数code表现
3.  watch机制
    1.  watch是和event纠缠在一起的。
    2.  watch有多种，
        1.  链接创建结果watch，这时候更像一种异步结果回调
        2.  结点数据变更watch，
        3.  子结点变更watch
    3.  watch注册和消费机制
        1.  一次注册一次消费；如果还继续关心，就继续注册
        2.  一个客户端可以对一个path多重注册，消费时会被重复串行效用
    4.  watch和事件
    
4.  读取和设值和删除
    1.  设值时，即使新旧值一样，数据版本也是要更新的，因此相关watch就会被触发。
5.  

# 3.编程实践
## 3.1  创建结点
1.  无论是持久结点还是临时结点，当它已经存在的情况下，创建结点都会不成功的；
    1.  创建结果的返回根据'同步'和'异步'的不同而不同。一个是异常的形式，一个是回调的result_code的形式
    2.  安全的创建结点的方式
        1.  先检查在同步创建
            大部分情况足够用了。在高并发场景不适用。
        2.  异步创建
2.  

## 3.2 读取结点值
1.  读取结点有异步操作吗？
    这不是一个大问题：读取本身肯定是同步返回的，但是有可能发生客户端异常和服务端异常。所以，无论哪个异常都需要把读取代码包在try中。
    如果想进一步处理异常，那就进一步处理好了。
    异步读取在这里有优势吗？微乎其微，只是另外一种服务端错误相应的一种处理形式罢了。

2.  读取结点的问题都清楚了吧。

## 3.3 NodeCache的使用以及结点监听


    