# 1.主要内容
1.  承接mysql的crash-safe机制：只要有完成的binlog和redolog就能保证crash-saft工作良好，
    这里讨论如何保证这两个日志完整的。

# 2.具体内容
1.  binlog的写入机制
    1.  binlog是transaction单位，无论t多大都不能拆分
    2.  sync_binlog的参数配置
        1.  0：每次提交都write，而不fsync
        2.  1：都会fsync
        3.  n：n个事务后才fsync（当然每个事务提交都会write到os的缓存）
    3.  总结：这就是binlog的刷盘机制了；
        注意各个设置的风险：0和n，在服务器异常断点、异常重启时，都会丢失数据。
        但是请注意，mysql异常重启，则不会丢失数据。因为这部分在os中，os为它保留着呢
    
    4.  binlog的写入时，有三个地方：每个thread一个binlog-cache、page-cache、disk。
        sync_binlog的不同值表示提交几次才来一个fsync，比如0、1、n。

3.  redolog的写入机制
    1.  按照redo-log的写入位置，innod_flush_log_at_trx_commit分为以下三个值
        1.  0-buffer中，每次事务提交，都只写到buffer中就齐了
        2.  1-直接fsync到磁盘
        3.  2-提交时只写到page_cache(os的文件缓存)中
    2.  innodb中有一个1秒的定时线程，把buffer中的内容，先write再fsyc到磁盘
    3.  总结：
        1.  因为1秒定时任务的存在，所以会存在尚未提交的redo-log被写到了disk
        2.  除此之外还有两种情况会把尚未提交的redo-log被写到了disk
            1.  redo-log-buffer空间达到了innodb_log_buffer_size的一半（这时后台线程会写redo-buffer的数据到page-cache）
            2.  组提交
    4.  补充：
        1.  如果innodb_flush_log_at_trx_commit=1,则redo_log在prepare时就要fsync一次磁盘
        2.  redo-log的写入过程也要经历三个空间：buffer、page-cache、disk
        3.  flush_log_at_trx_commit的值表示每次提交，redo-log写到哪里。
            0表示buffer、1表示disk、2表示page-cache
        


4.  综合两个写入机制
    1.  一个事务提交前，需要有两次刷盘，redo-log的prepare和binlog的fsync
    2.  所以wal机制并没有单纯的减少刷盘次数，wal机制的优化点是：
        1.  顺序写
        2.  组提交——在这个机制的辅助下，才达到了减少刷盘次数(得有并发时)

5.  组提交
    1.  实际上，wal机制是这样的写入顺序
        1.  redo-log-prepare：write
        2.  bin-log：write
        3.  redo-log-prepare：fsync —— 这个会组提交; 这个很快，所以给binlog组提交的机会不多。
        4.  binlog-fsync
        5.  redo-log-commit：write —— 事实上这里并没有刷盘，尽管innodb_flush_log_at_trx_commit = 1;
    2.  为了让binlog组提交，有两个参数优化，或的关系
        1.  binlog-group-commit-sync-daley = n， 表示n微妙后(组)提交
        2.  binlog-group-commit-sync-count = m， 表示累积m个提交后fsync
    
6.  实战：如果你的mysql出现了性能瓶颈，且在io上，你怎么提升性能
    1.  binlog组提交：设置binlog-group-commit的两个参数；这里是基于'额外的故意等待'来实现，
        可能会增加语句的响应时间，但不会丢失数据；因为真的在这里服务器宕机了，这是语句执行失败，
        但是不会丢失数据；
    2.  fsync-binlog设置为大于1；风险：主机掉电丢失binlog；mysql异常重启问题
    3.  innodb_flush_log_at_trx_commit设置为2，即redo-log每次提交都写到page-cache就好了
        风险：一样是主机掉电
        不建议：不建议把innodb_flush_log_at_trx_commit=0，因为这样mysql的异常重启都会造成
        数据丢失——与主机掉电比起来，mysql异常重启概率更大；而且写page-cache也是很快的，所以设置为0
        与设置为2是没什么性能差别的，但是安全性高了一些呢。




    