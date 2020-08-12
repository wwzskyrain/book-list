# 1.导读
1.  mysql主从复制的交互过程？这一点是和redis整体上是一致的
2.  mysql的binary-log的三种格式

# 2.mysql主从复制过程
1.  其实和redis是一样的，当我发现的时候也是'意料之外'又在'情理之中'；
2.  首先也是推的模式；
3.  文中没有讲清楚存量和增量数据的同步细节，但是大概也清楚了。
4.  当一主多从时，主从延迟严重也是很正常的情况，因为master需要负责发送那么多binlog给多个从服务器。

# 3.mysql的bin-log格式
1.  这个在理解上是很简单的，可能实现上会很细节化；
2.  binlog就2+1中格式：statement、row、statement+row=mixed
3.  statement格式很容易导致主备不一致。
4.  注意：
    1.  row格式时，同步的是数据数据本身；这一点，和redis是一样的。而且，redis操作的和同步的都是数据本身，不带条件的。
    2.  语句中的now()函数怎么同步？比如`insert into t values(1,1,now())`
        不用担心，会在这个句子之前设置一个变量`set timestamp=1546103491`
        ，然后紧跟的这个句子再执行时，now()就是timestamp的值了。
