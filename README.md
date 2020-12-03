# ConcurrentTicketing
一个用于模拟售卖后车票的系统，系统主要关注于并发场景下的性能。

[TOC]

## Mainly Data Structure
### Route Map
这是系统最基础的数据结构，它使用JDK提供的`ConcurrentHashMap`实现，尽管它其实不会发生并发读写的情况，但这里为了严谨还是采用了线程安全的数据结构，同时其`get`方法拥有与`HashMap`相同的性能。

它主要用于存储车次到`Route`的映射关系，是在系统启动时便生成好的，后续操作时仅会对其进行查询。

### Route

使用`CopyOnWriteArrayList<CopyOnWriteArrayList<AtomicInteger>>`这个二维数组来存储`Route`到`Seat`的映射，同时这两个数组也是仅在创建时对其进行初始化，之后仅会进行查询操作。

### Seat

使用`AtomicLong`来存储这个座位在不同站点的售出情况，按照站点序号把Long的对应位置置为1，当查询时可通过&操作获取结果，售票和退票时先读取当前值，再通过`compareAndSet`操作来保证不会冲突。

### Ticket

使用`AtomicInteger`来生成基础的`TicketSeed`，同时为避免因所有车次共用一个寄存器而造成性能瓶颈，所以为每一个`Route`创建一个 `AtomicInteger`来将负载均衡。

真正的`TicketID`则采用64位的结构，其中后27位为获取到的`TicketSeed`，而高37位则由时间、车次等信息构成。
$$
[^{63}<- 日期(14,4,5),车次(14) ->^{27}][^{26}<- \qquad TicketSeed \qquad  ->^{0}]
$$
