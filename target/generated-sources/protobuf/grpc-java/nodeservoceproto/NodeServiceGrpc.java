package nodeservoceproto;

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
    comments = "Source: node_service.proto")
public final class NodeServiceGrpc {

  private NodeServiceGrpc() {}

  public static final String SERVICE_NAME = "nodeservoceproto.NodeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.ReserveRequest,
      nodeservoceproto.NodeServiceOuterClass.ReserveReply> getReserveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Reserve",
      requestType = nodeservoceproto.NodeServiceOuterClass.ReserveRequest.class,
      responseType = nodeservoceproto.NodeServiceOuterClass.ReserveReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.ReserveRequest,
      nodeservoceproto.NodeServiceOuterClass.ReserveReply> getReserveMethod() {
    io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.ReserveRequest, nodeservoceproto.NodeServiceOuterClass.ReserveReply> getReserveMethod;
    if ((getReserveMethod = NodeServiceGrpc.getReserveMethod) == null) {
      synchronized (NodeServiceGrpc.class) {
        if ((getReserveMethod = NodeServiceGrpc.getReserveMethod) == null) {
          NodeServiceGrpc.getReserveMethod = getReserveMethod = 
              io.grpc.MethodDescriptor.<nodeservoceproto.NodeServiceOuterClass.ReserveRequest, nodeservoceproto.NodeServiceOuterClass.ReserveReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "nodeservoceproto.NodeService", "Reserve"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.ReserveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.ReserveReply.getDefaultInstance()))
                  .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("Reserve"))
                  .build();
          }
        }
     }
     return getReserveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest,
      nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> getCreateContainerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateContainer",
      requestType = nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest.class,
      responseType = nodeservoceproto.NodeServiceOuterClass.CreateContainerReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest,
      nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> getCreateContainerMethod() {
    io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest, nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> getCreateContainerMethod;
    if ((getCreateContainerMethod = NodeServiceGrpc.getCreateContainerMethod) == null) {
      synchronized (NodeServiceGrpc.class) {
        if ((getCreateContainerMethod = NodeServiceGrpc.getCreateContainerMethod) == null) {
          NodeServiceGrpc.getCreateContainerMethod = getCreateContainerMethod = 
              io.grpc.MethodDescriptor.<nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest, nodeservoceproto.NodeServiceOuterClass.CreateContainerReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "nodeservoceproto.NodeService", "CreateContainer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.CreateContainerReply.getDefaultInstance()))
                  .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("CreateContainer"))
                  .build();
          }
        }
     }
     return getCreateContainerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest,
      nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> getRemoveContainerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveContainer",
      requestType = nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest.class,
      responseType = nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest,
      nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> getRemoveContainerMethod() {
    io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest, nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> getRemoveContainerMethod;
    if ((getRemoveContainerMethod = NodeServiceGrpc.getRemoveContainerMethod) == null) {
      synchronized (NodeServiceGrpc.class) {
        if ((getRemoveContainerMethod = NodeServiceGrpc.getRemoveContainerMethod) == null) {
          NodeServiceGrpc.getRemoveContainerMethod = getRemoveContainerMethod = 
              io.grpc.MethodDescriptor.<nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest, nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "nodeservoceproto.NodeService", "RemoveContainer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply.getDefaultInstance()))
                  .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("RemoveContainer"))
                  .build();
          }
        }
     }
     return getRemoveContainerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest,
      nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> getInvokeFunctionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InvokeFunction",
      requestType = nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest.class,
      responseType = nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest,
      nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> getInvokeFunctionMethod() {
    io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest, nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> getInvokeFunctionMethod;
    if ((getInvokeFunctionMethod = NodeServiceGrpc.getInvokeFunctionMethod) == null) {
      synchronized (NodeServiceGrpc.class) {
        if ((getInvokeFunctionMethod = NodeServiceGrpc.getInvokeFunctionMethod) == null) {
          NodeServiceGrpc.getInvokeFunctionMethod = getInvokeFunctionMethod = 
              io.grpc.MethodDescriptor.<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest, nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "nodeservoceproto.NodeService", "InvokeFunction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply.getDefaultInstance()))
                  .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("InvokeFunction"))
                  .build();
          }
        }
     }
     return getInvokeFunctionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.GetStatsRequest,
      nodeservoceproto.NodeServiceOuterClass.GetStatsReply> getGetStatsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStats",
      requestType = nodeservoceproto.NodeServiceOuterClass.GetStatsRequest.class,
      responseType = nodeservoceproto.NodeServiceOuterClass.GetStatsReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.GetStatsRequest,
      nodeservoceproto.NodeServiceOuterClass.GetStatsReply> getGetStatsMethod() {
    io.grpc.MethodDescriptor<nodeservoceproto.NodeServiceOuterClass.GetStatsRequest, nodeservoceproto.NodeServiceOuterClass.GetStatsReply> getGetStatsMethod;
    if ((getGetStatsMethod = NodeServiceGrpc.getGetStatsMethod) == null) {
      synchronized (NodeServiceGrpc.class) {
        if ((getGetStatsMethod = NodeServiceGrpc.getGetStatsMethod) == null) {
          NodeServiceGrpc.getGetStatsMethod = getGetStatsMethod = 
              io.grpc.MethodDescriptor.<nodeservoceproto.NodeServiceOuterClass.GetStatsRequest, nodeservoceproto.NodeServiceOuterClass.GetStatsReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "nodeservoceproto.NodeService", "GetStats"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.GetStatsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  nodeservoceproto.NodeServiceOuterClass.GetStatsReply.getDefaultInstance()))
                  .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("GetStats"))
                  .build();
          }
        }
     }
     return getGetStatsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NodeServiceStub newStub(io.grpc.Channel channel) {
    return new NodeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NodeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NodeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class NodeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void reserve(nodeservoceproto.NodeServiceOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.ReserveReply> responseObserver) {
      asyncUnimplementedUnaryCall(getReserveMethod(), responseObserver);
    }

    /**
     */
    public void createContainer(nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateContainerMethod(), responseObserver);
    }

    /**
     */
    public void removeContainer(nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> responseObserver) {
      asyncUnimplementedUnaryCall(getRemoveContainerMethod(), responseObserver);
    }

    /**
     */
    public void invokeFunction(nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> responseObserver) {
      asyncUnimplementedUnaryCall(getInvokeFunctionMethod(), responseObserver);
    }

    /**
     */
    public void getStats(nodeservoceproto.NodeServiceOuterClass.GetStatsRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.GetStatsReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetStatsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReserveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                nodeservoceproto.NodeServiceOuterClass.ReserveRequest,
                nodeservoceproto.NodeServiceOuterClass.ReserveReply>(
                  this, METHODID_RESERVE)))
          .addMethod(
            getCreateContainerMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest,
                nodeservoceproto.NodeServiceOuterClass.CreateContainerReply>(
                  this, METHODID_CREATE_CONTAINER)))
          .addMethod(
            getRemoveContainerMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest,
                nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply>(
                  this, METHODID_REMOVE_CONTAINER)))
          .addMethod(
            getInvokeFunctionMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest,
                nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply>(
                  this, METHODID_INVOKE_FUNCTION)))
          .addMethod(
            getGetStatsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                nodeservoceproto.NodeServiceOuterClass.GetStatsRequest,
                nodeservoceproto.NodeServiceOuterClass.GetStatsReply>(
                  this, METHODID_GET_STATS)))
          .build();
    }
  }

  /**
   */
  public static final class NodeServiceStub extends io.grpc.stub.AbstractStub<NodeServiceStub> {
    private NodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceStub(channel, callOptions);
    }

    /**
     */
    public void reserve(nodeservoceproto.NodeServiceOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.ReserveReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createContainer(nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateContainerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removeContainer(nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRemoveContainerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void invokeFunction(nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getInvokeFunctionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getStats(nodeservoceproto.NodeServiceOuterClass.GetStatsRequest request,
        io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.GetStatsReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetStatsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NodeServiceBlockingStub extends io.grpc.stub.AbstractStub<NodeServiceBlockingStub> {
    private NodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public nodeservoceproto.NodeServiceOuterClass.ReserveReply reserve(nodeservoceproto.NodeServiceOuterClass.ReserveRequest request) {
      return blockingUnaryCall(
          getChannel(), getReserveMethod(), getCallOptions(), request);
    }

    /**
     */
    public nodeservoceproto.NodeServiceOuterClass.CreateContainerReply createContainer(nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateContainerMethod(), getCallOptions(), request);
    }

    /**
     */
    public nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply removeContainer(nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest request) {
      return blockingUnaryCall(
          getChannel(), getRemoveContainerMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply> invokeFunction(
        nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getInvokeFunctionMethod(), getCallOptions(), request);
    }

    /**
     */
    public nodeservoceproto.NodeServiceOuterClass.GetStatsReply getStats(nodeservoceproto.NodeServiceOuterClass.GetStatsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetStatsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NodeServiceFutureStub extends io.grpc.stub.AbstractStub<NodeServiceFutureStub> {
    private NodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nodeservoceproto.NodeServiceOuterClass.ReserveReply> reserve(
        nodeservoceproto.NodeServiceOuterClass.ReserveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nodeservoceproto.NodeServiceOuterClass.CreateContainerReply> createContainer(
        nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateContainerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply> removeContainer(
        nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRemoveContainerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<nodeservoceproto.NodeServiceOuterClass.GetStatsReply> getStats(
        nodeservoceproto.NodeServiceOuterClass.GetStatsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetStatsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RESERVE = 0;
  private static final int METHODID_CREATE_CONTAINER = 1;
  private static final int METHODID_REMOVE_CONTAINER = 2;
  private static final int METHODID_INVOKE_FUNCTION = 3;
  private static final int METHODID_GET_STATS = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RESERVE:
          serviceImpl.reserve((nodeservoceproto.NodeServiceOuterClass.ReserveRequest) request,
              (io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.ReserveReply>) responseObserver);
          break;
        case METHODID_CREATE_CONTAINER:
          serviceImpl.createContainer((nodeservoceproto.NodeServiceOuterClass.CreateContainerRequest) request,
              (io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.CreateContainerReply>) responseObserver);
          break;
        case METHODID_REMOVE_CONTAINER:
          serviceImpl.removeContainer((nodeservoceproto.NodeServiceOuterClass.RemoveContainerRequest) request,
              (io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.RemoveContainerReply>) responseObserver);
          break;
        case METHODID_INVOKE_FUNCTION:
          serviceImpl.invokeFunction((nodeservoceproto.NodeServiceOuterClass.InvokeFunctionRequest) request,
              (io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.InvokeFunctionReply>) responseObserver);
          break;
        case METHODID_GET_STATS:
          serviceImpl.getStats((nodeservoceproto.NodeServiceOuterClass.GetStatsRequest) request,
              (io.grpc.stub.StreamObserver<nodeservoceproto.NodeServiceOuterClass.GetStatsReply>) responseObserver);
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

  private static abstract class NodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NodeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return nodeservoceproto.NodeServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NodeService");
    }
  }

  private static final class NodeServiceFileDescriptorSupplier
      extends NodeServiceBaseDescriptorSupplier {
    NodeServiceFileDescriptorSupplier() {}
  }

  private static final class NodeServiceMethodDescriptorSupplier
      extends NodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NodeServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (NodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NodeServiceFileDescriptorSupplier())
              .addMethod(getReserveMethod())
              .addMethod(getCreateContainerMethod())
              .addMethod(getRemoveContainerMethod())
              .addMethod(getInvokeFunctionMethod())
              .addMethod(getGetStatsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
