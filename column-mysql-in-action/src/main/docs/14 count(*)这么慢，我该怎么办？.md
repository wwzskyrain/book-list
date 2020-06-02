
# 1.主要内容
1.  顺着count(*)为何越来越慢的问题，介绍了MyISAM和InnoDB对count(*)的实现，   
    接着分析了如何自己实现计数，其中着重讨论了使用Redis来计数的逻辑不一致性；
    然后也顺带解释了count(字段)、count(主键)、count(1)、count(*)的区别
    

# 2.主要知识点
1.  MyISAM和InnoDB实现count(*)的不同，为什么
    1.  MyISAM是另外存放了count
    2.  InnoDB却不能这样干。因为MyISAM在并发场景，MyISAM表不支持行锁，所以到MyISAM表这里都是串行执行的，所以每一个时刻对于当前回话确实有一个
        明确的count；但是在支持行锁的InnoDB表中，表是被并发访问的，在一个时间点上，可能有多个回话；在RR的隔离级别上，不同的回话当然各自有各自的
        count；所以不能用一个count来保存

2.  用redis来维护行数可以吗？
    1.  不可以，无论先写db后写redis，还是反过来，都会有数据不一致的场景
    2.  问题的症结是不能保持'分布式事务'
    3.  可以用InnoDB来维护这个count，而且再事务里面进行插入数据和更新count，保持了读写的原子性。

3.  count(字段)< count(primary key) < count(1) 大约= count(*)

# 3.拓展
1.  如何保持redis缓存和db的一致性，这是一个常见面试题？明天回答