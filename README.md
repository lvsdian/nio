## 同步、异步、阻塞、非阻塞
  - 同步：A请求B，A在收到B的响应结果前不返回。
  - 异步：A请求B，B立即回应已收到结果，A返回，B的响应结果出来后再通知A。
  - 阻塞：调用结果返回前，当前线程会让出CPU并处于阻塞状态。
  - 非阻塞：调用结果返回前，当前线程不会阻塞。
  - 同步、异步表示用户线程与内核交互方式；阻塞非阻塞表示线程的执行状态。
## IO模型
1. 阻塞IO模型
  
  - 用户线程发出IO请求后，交出CPU，自身变为阻塞态，直到内核准备好数据并拷贝到用户线程才返回。
2. 非阻塞IO模型
  - 用户线程发出IO请求后会立即返回，不会阻塞，然后不停的轮询内核态，直到内核把数据准备好并且拷贝到用户线程
3. 多路复用IO模型
  - 内核态会有一个线程轮询多个socket，发现socket有读写事件时，就会进行读写操作。**单个线程通过记录跟踪每一个I/O流的状态来同时管理多个I/O流的读写**
  
  - 实现方式：
    
    - select：将文件描述符集合(`fd_set`)从用户态拷贝到内核态，由内核态来遍历集合判断是否有事件触发，如果遍历完集合还没有读写事件，就进入睡眠，如果有，就将`fd`置位，即标识为有数据要来了，`select`就返回，再进行第二次遍历，找到哪个置位了的fd，取它上面的数据进行处理
    
      缺点：
    
      	1. 用户态与内核态之间的拷贝非常消耗资源
       	2. 进行线性扫描，时间复杂度是`O(n)`
       	3. 有最大连接数限制
    
    - poll：类似select，但它是用链表作为存放`fd`的集合，无最大连接数限制。
    
    - epoll：通过用户态和内核态共用一块内存来实现，提高了性能。基于事件驱动(每个事件都关联`fd`)，知道具体哪个流发生读写操作。时间复杂度O(1)。linux提供的epoll相关函数：
      ```c++
      int epoll_create(int size);
      int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
      int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout);
      ```
      epoll_create：用于创建epoll句柄，调用成功，返回epoll句柄描述符，失败返回-1。  
      epoll_ctl：注册要监听的事件类型。  
      epoll_wait：等待事件的就绪，成功时返回就绪的事件数目，调用失败时返回 -1，等待超时返回 0。 
    
      工作模式：水平触发、边缘触发。
    
        - 水平触发：默认工作模式，当epoll_wait检测到某描述符事件就绪并通知应用程序时，应用程序可以不立即处理该事件；下次调用epoll_wait时，会再次通知此事件。  
        - 边缘触发： 当epoll_wait检测到某描述符事件就绪并通知应用程序时，应用程序必须立即处理该事件。如果不处理，下次调用epoll_wait时，不会再次通知此事件。
4. 信号驱动IO模型
  
  - 用户线程发出IO请求后，会给对应的socket注册一个信号函数，内核准备好数据后，会通知用户线程，用户线程再调用信号函数进行读写操作。
5. 异步IO模型
  
  - 用户线程发起IO请求后返回。内核准备好数据并拷贝到用户线程后会通知用户线程。
## NIO
- 传统IO基于流，是同步阻塞，在读写动作完成之前，线程会一直阻塞，它们之间的调用是可靠的线性顺序

- NIO是`java1.4`，是同步非阻塞IO，基于Channel和Buffer，面向缓冲区。数据总是从Channel读到Buffer或从Buffer写到Channel。Selector用于监听Channel。
  - Channel 
    双向的，可读可写；流是单向的。
  - Buffer 
    是一个连续数组，子类有ByteBuffer、IntBuffer...
  - Selector 
    Selector能够检测多个注册通道上是否有事件发生，如果有，就针对每个事件进行处理。单个线程就可以管理多个通道，即多个连接。
- AIO是`java1.7`引入的，是异步IO，是基于事件和回调机制实现的，也就是应用操作之后会直接返回，不会阻塞，当后台处理完成，操作系统会通知相应的线程进行后续的操作。

