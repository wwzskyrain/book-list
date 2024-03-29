# 1.基本内容介绍
1.  kill query(默认) thread_id 和 kill connection thread_id 的作用原理
2.  客户端误解1：库里的表特别多，链接就很慢
3.  客户端误解2：链接单数 -quick 
4.  自我勘误：
    1.  之前一直我认为`kill query pid`中的query可以省略，其实记错了，是`kill connection pid`
        可以省略，即`kill pid`是后者的简写，而不是前者； 
    2.  再次补充一下：`kill query pid`是让线程不要在执行接下来的语句了；
        而`kill pid`则会关闭链接，进而导致回滚事务。而kill query是不能回滚事务的，如果实物中持有了锁也是不能释放掉的
# 2.关于kill
1.  一个kill调的例子：sessionB（简称Sb）被blocked于Sa，这时候Sc开kill threadB（简称Tb）；
    可以看到Sb的客户端立刻被kill了。

2.  一个kill不掉的例子：先把innodb的并发设置为2，然后S1、S2都睡觉，S3就blocked，S4去kill C，S5去kill connection C；
    可以看到，S4的kill 是没用的，而S5的kill connection是有用的。
    这时候执行show processlist，会看到S3的状态是killed，但是却还在。

3.  解密：
    1.  首先要明白kill线程做的工作：
        1.  把线程状态设置为query-killed
        2.  发一个信号给线程
    2.  其次要知道被kill的线程会怎么反应：
        1.  首先将被kill的线程，并不一定非得是阻塞状态，也可能是正常的执行流程中。
        2.  如果是阻塞中，该阻塞也不是不可中断，即不是不可接受信号的；
        3.  如果是正常执行流程也会有很多'埋点'以相应信号
        4.  相应信号：检查当前线程的状态发现是query-kill/connection-kill，就开始做kill收尾工作：
            1.  回滚事务
            2.  删除中间表
            3.  释放锁
            4.  自动退出线程
    3.  以上就是第一个正常kill的例子的说明
    4.  但是第二个不能kill的例子怎么说呢？
        1.  因为innodb的并发就只有2，所以S3都没有进入innodb中，就没开始执行，所以也就不能被S4kill调。
        2.  为什么S5可以kill掉呢，其实S5kill的是链接，而S3线程还是在等待中。等S1或者S2完了，S3一单有机会执行
            S3就能完成kill——一生即死。
        3.  关于这事的show processlist：特殊处理，被kill调链接的线程，会显示killed状态。
        4.  还有什么情况会显示killed状态？
            1.  在信号处理中，都会显示killed状态，特别是当信号处理的内容耗时比较多时。
    5.  客户端被堵住时候，直接简单的粗暴的ctrl+c就算退出了？
        1.  当然不是，ctrl+c时，客户端其实是又开了一个链接，并且发送了一个kill query Tid(ctrl+c之前的那个链接对应的线程id)
        

# 3.关于客户端的两个误解
1.  库里表比较多，kill的链接就很慢?
    1.  其实链接要做的工作：tcp链接、用户校验、获取权限；
    2.  这三步都跟库里表多少无关
    3.  与库里表有关的是客户端提供的'自动补全'功能，为了支持该功能，
        客户端就要来show database、show tables，然后根据这两部的返回创建一个hash表
    4.  so，这个慢，不在服务端，在客户端
    5.  可以用-A参数关闭该功能，从而快速"链接"。
    6.  问题出现的场景，库中有上万张表（分表啦）
2.  关于链接参数-quick；快在哪里？
    1.  跳过自动补全功能
    2.  使用mysql_use_result：客户端不适用缓冲，而是读一个处理一个
    3.  不在把执行的命令记录到本地的历史文件中


        