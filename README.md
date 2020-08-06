#### 8.5
1.5282
- 请求处理串行化，为了方便写策略和控制container的创建
    - 请求来了入队
    - 一次出队，根据最先匹配算法选择一个可用的container并返回
    - 没有可用的container则马上创建一个
    - 在创建过程中如果request被消费，则放弃创建，为了节约资源
    - 创建时使用的node选择也是使用最先匹配原则
- 提前创建好MAX_NODE_NUM个node（系统启动后40s才来第一个请求，利用这些时间创建了node）
- 加了container的清理，用了LRU算法，把最长时间没用的container删掉，空出可以创建新的container的空间,使用LinkedHashMap构建了一个container的LRU队列,在创建container时如果node空间不够，则清理一遍container

#### 8.6
1.2372
- 还是恢复了并行处理请求
    - 在acquireContainer中，请求来了如果有可用的container，按照最先匹配原则分配container
    - 如果没有可用的container，则马上申请创建container，创建过程不会被打断，中间这个request可能被消费，但创建container会直到成功为止（因为发现响应得分更加重要，如果来了request不能马上被消费，说明container不够，就去申请，以应付下一次并发来的时候可以及时使用）
- 优化了container清理的算法，分了优先级，对于平均执行时间小于100ms的container长期保存不清理（对于以下小函数，执行时间可能就几ms，如果清理后，下一次冷启动对响应得分影响较大）
- 发现cpu密集型的函数在同一个node上十分影响响应得分，在创建container的node选择上，采用了策略，优先选择没有这个function的container，即同一种container尽量不放在同一个node上，效果显著，明天继续挖掘cpu