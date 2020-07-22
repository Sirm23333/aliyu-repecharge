package schedulerproto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: scheduler.proto")
public final class SchedulerGrpc {

  private SchedulerGrpc() {}

  public static final String SERVICE_NAME = "schedulerproto.Scheduler";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.AcquireContainerRequest,
      schedulerproto.SchedulerOuterClass.AcquireContainerReply> getAcquireContainerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AcquireContainer",
      requestType = schedulerproto.SchedulerOuterClass.AcquireContainerRequest.class,
      responseType = schedulerproto.SchedulerOuterClass.AcquireContainerReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.AcquireContainerRequest,
      schedulerproto.SchedulerOuterClass.AcquireContainerReply> getAcquireContainerMethod() {
    io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.AcquireContainerRequest, schedulerproto.SchedulerOuterClass.AcquireContainerReply> getAcquireContainerMethod;
    if ((getAcquireContainerMethod = SchedulerGrpc.getAcquireContainerMethod) == null) {
      synchronized (SchedulerGrpc.class) {
        if ((getAcquireContainerMethod = SchedulerGrpc.getAcquireContainerMethod) == null) {
          SchedulerGrpc.getAcquireContainerMethod = getAcquireContainerMethod = 
              io.grpc.MethodDescriptor.<schedulerproto.SchedulerOuterClass.AcquireContainerRequest, schedulerproto.SchedulerOuterClass.AcquireContainerReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "schedulerproto.Scheduler", "AcquireContainer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  schedulerproto.SchedulerOuterClass.AcquireContainerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  schedulerproto.SchedulerOuterClass.AcquireContainerReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerMethodDescriptorSupplier("AcquireContainer"))
                  .build();
          }
        }
     }
     return getAcquireContainerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.ReturnContainerRequest,
      schedulerproto.SchedulerOuterClass.ReturnContainerReply> getReturnContainerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReturnContainer",
      requestType = schedulerproto.SchedulerOuterClass.ReturnContainerRequest.class,
      responseType = schedulerproto.SchedulerOuterClass.ReturnContainerReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.ReturnContainerRequest,
      schedulerproto.SchedulerOuterClass.ReturnContainerReply> getReturnContainerMethod() {
    io.grpc.MethodDescriptor<schedulerproto.SchedulerOuterClass.ReturnContainerRequest, schedulerproto.SchedulerOuterClass.ReturnContainerReply> getReturnContainerMethod;
    if ((getReturnContainerMethod = SchedulerGrpc.getReturnContainerMethod) == null) {
      synchronized (SchedulerGrpc.class) {
        if ((getReturnContainerMethod = SchedulerGrpc.getReturnContainerMethod) == null) {
          SchedulerGrpc.getReturnContainerMethod = getReturnContainerMethod = 
              io.grpc.MethodDescriptor.<schedulerproto.SchedulerOuterClass.ReturnContainerRequest, schedulerproto.SchedulerOuterClass.ReturnContainerReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "schedulerproto.Scheduler", "ReturnContainer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  schedulerproto.SchedulerOuterClass.ReturnContainerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  schedulerproto.SchedulerOuterClass.ReturnContainerReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerMethodDescriptorSupplier("ReturnContainer"))
                  .build();
          }
        }
     }
     return getReturnContainerMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SchedulerStub newStub(io.grpc.Channel channel) {
    return new SchedulerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SchedulerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SchedulerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SchedulerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SchedulerFutureStub(channel);
  }

  /**
   */
  public static abstract class SchedulerImplBase implements io.grpc.BindableService {

    /**
     */
    public void acquireContainer(schedulerproto.SchedulerOuterClass.AcquireContainerRequest request,
        io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.AcquireContainerReply> responseObserver) {
      asyncUnimplementedUnaryCall(getAcquireContainerMethod(), responseObserver);
    }

    /**
     */
    public void returnContainer(schedulerproto.SchedulerOuterClass.ReturnContainerRequest request,
        io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.ReturnContainerReply> responseObserver) {
      asyncUnimplementedUnaryCall(getReturnContainerMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAcquireContainerMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                schedulerproto.SchedulerOuterClass.AcquireContainerRequest,
                schedulerproto.SchedulerOuterClass.AcquireContainerReply>(
                  this, METHODID_ACQUIRE_CONTAINER)))
          .addMethod(
            getReturnContainerMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                schedulerproto.SchedulerOuterClass.ReturnContainerRequest,
                schedulerproto.SchedulerOuterClass.ReturnContainerReply>(
                  this, METHODID_RETURN_CONTAINER)))
          .build();
    }
  }

  /**
   */
  public static final class SchedulerStub extends io.grpc.stub.AbstractStub<SchedulerStub> {
    private SchedulerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerStub(channel, callOptions);
    }

    /**
     */
    public void acquireContainer(schedulerproto.SchedulerOuterClass.AcquireContainerRequest request,
        io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.AcquireContainerReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAcquireContainerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void returnContainer(schedulerproto.SchedulerOuterClass.ReturnContainerRequest request,
        io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.ReturnContainerReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReturnContainerMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SchedulerBlockingStub extends io.grpc.stub.AbstractStub<SchedulerBlockingStub> {
    private SchedulerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerBlockingStub(channel, callOptions);
    }

    /**
     */
    public schedulerproto.SchedulerOuterClass.AcquireContainerReply acquireContainer(schedulerproto.SchedulerOuterClass.AcquireContainerRequest request) {
      return blockingUnaryCall(
          getChannel(), getAcquireContainerMethod(), getCallOptions(), request);
    }

    /**
     */
    public schedulerproto.SchedulerOuterClass.ReturnContainerReply returnContainer(schedulerproto.SchedulerOuterClass.ReturnContainerRequest request) {
      return blockingUnaryCall(
          getChannel(), getReturnContainerMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SchedulerFutureStub extends io.grpc.stub.AbstractStub<SchedulerFutureStub> {
    private SchedulerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<schedulerproto.SchedulerOuterClass.AcquireContainerReply> acquireContainer(
        schedulerproto.SchedulerOuterClass.AcquireContainerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAcquireContainerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<schedulerproto.SchedulerOuterClass.ReturnContainerReply> returnContainer(
        schedulerproto.SchedulerOuterClass.ReturnContainerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReturnContainerMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ACQUIRE_CONTAINER = 0;
  private static final int METHODID_RETURN_CONTAINER = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SchedulerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SchedulerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ACQUIRE_CONTAINER:
          serviceImpl.acquireContainer((schedulerproto.SchedulerOuterClass.AcquireContainerRequest) request,
              (io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.AcquireContainerReply>) responseObserver);
          break;
        case METHODID_RETURN_CONTAINER:
          serviceImpl.returnContainer((schedulerproto.SchedulerOuterClass.ReturnContainerRequest) request,
              (io.grpc.stub.StreamObserver<schedulerproto.SchedulerOuterClass.ReturnContainerReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class SchedulerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SchedulerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return schedulerproto.SchedulerOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Scheduler");
    }
  }

  private static final class SchedulerFileDescriptorSupplier
      extends SchedulerBaseDescriptorSupplier {
    SchedulerFileDescriptorSupplier() {}
  }

  private static final class SchedulerMethodDescriptorSupplier
      extends SchedulerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SchedulerMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SchedulerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SchedulerFileDescriptorSupplier())
              .addMethod(getAcquireContainerMethod())
              .addMethod(getReturnContainerMethod())
              .build();
        }
      }
    }
    return result;
  }
}
