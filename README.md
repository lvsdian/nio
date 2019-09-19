## 同步、异步、阻塞、非阻塞
  - 同步：A请求B，A在收到B的响应结果前不返回。
  - 异步：A请求B，B立即回应已收到结果，A返回，B的响应结果出来后再通知A。
  - 阻塞：调用结果返回前，当前线程会让出CPU并处于阻塞状态。
  - 非阻塞：调用结果返回前，当前线程不会阻塞。
  - 同步、异步表示用户线程与内核交互方式；阻塞非阻塞表示线程的执行状态。
## IO模型
1. 阻塞IO模型
  - 用户线程发出IO请求后，交出CPU，自身变为阻塞态，直到内核返回请求结果。
2. 非阻塞IO模型
  - 用户线程发出read操作后，内核立马返回结果，如果结果是error，表示数据没准备好，用户线程就会再次发送read操作。直到内核把数据准备好并且收到了用户线程
    的请求。这个过程不会交出CPU。
3. 多路复用IO模型
  - 内核态会有一个线程轮询多个socket，发现socket有读写事件时，就会进行读写操作。
  - 实现方式：
    - select：轮询所有流，找出能读写的流，进行操作。有最大连接数限制。时间复杂度O(n)。
    - poll：链表实现，无最大连接数限制。其余类似select。
    - epoll：基于事件驱动，知道具体哪个流发生读写操作。时间复杂度O(1)。linux提供的epoll相关函数：
      ```c++
      int epoll_create(int size);
      int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
      int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout);
      ```
      epoll_create：用于创建epoll句柄，调用成功，返回epoll句柄描述符，失败返回-1。  
      epoll_ctl：注册要监听的事件类型。  
      epoll_write：等待事件的就绪，成功时返回就绪的事件数目，调用失败时返回 -1，等待超时返回 0。  
      工作模式：水平触发、边缘触发。
        - 水平触发：默认工作模式，当epoll_wait检测到某描述符事件就绪并通知应用程序时，应用程序可以不立即处理该事件；下次调用epoll_wait时，会再次通知此事件。  
        - 边缘触发： 当epoll_wait检测到某描述符事件就绪并通知应用程序时，应用程序必须立即处理该事件。如果不处理，下次调用epoll_wait时，不会再次通知此事件。
4. 信号驱动IO模型
  - 用户线程发出IO请求后，会给对应的socket注册一个信号函数，内核准备好数据后，会通知用户线程，用户线程再调用信号函数进行读写操作。
5. 异步IO模型
  - 用户线程发起read操作后即返回。内核准备好数据并拷贝到用户线程后会通知用户线程。
## NIO
传统IO基于流，而NIO基于Channel和Buffer，面向缓冲区。数据总是从Channel读到Buffer或从Buffer写到Channel。Selector用于监听Channel。
- Channel  
  双向的，可读可写；流是单向的。
- Buffer  
  是一个连续数组，子类有ByteBuffer、IntBuffer...
- Selector  
  Selector能够检测多个注册通道上是否有事件发生，如果有，就针对每个事件进行处理。单个线程就可以管理多个通道，即多个连接。

