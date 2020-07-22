package com.aliyun.mini.resourcemanager;

import resourcemanagerproto.ResourceManagerOuterClass;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: resource_manager.proto")
public final class ResourceManagerGrpc {

  private ResourceManagerGrpc() {}

  public static final String SERVICE_NAME = "resourcemanagerproto.ResourceManager";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReserveNodeRequest,
      ResourceManagerOuterClass.ReserveNodeReply> getReserveNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReserveNode",
      requestType = ResourceManagerOuterClass.ReserveNodeRequest.class,
      responseType = ResourceManagerOuterClass.ReserveNodeReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReserveNodeRequest,
      ResourceManagerOuterClass.ReserveNodeReply> getReserveNodeMethod() {
    io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReserveNodeRequest, ResourceManagerOuterClass.ReserveNodeReply> getReserveNodeMethod;
    if ((getReserveNodeMethod = ResourceManagerGrpc.getReserveNodeMethod) == null) {
      synchronized (ResourceManagerGrpc.class) {
        if ((getReserveNodeMethod = ResourceManagerGrpc.getReserveNodeMethod) == null) {
          ResourceManagerGrpc.getReserveNodeMethod = getReserveNodeMethod = 
              io.grpc.MethodDescriptor.<ResourceManagerOuterClass.ReserveNodeRequest, ResourceManagerOuterClass.ReserveNodeReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resourcemanagerproto.ResourceManager", "ReserveNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.ReserveNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.ReserveNodeReply.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceManagerMethodDescriptorSupplier("ReserveNode"))
                  .build();
          }
        }
     }
     return getReserveNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReleaseNodeRequest,
      ResourceManagerOuterClass.ReleaseNodeReply> getReleaseNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReleaseNode",
      requestType = ResourceManagerOuterClass.ReleaseNodeRequest.class,
      responseType = ResourceManagerOuterClass.ReleaseNodeReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReleaseNodeRequest,
      ResourceManagerOuterClass.ReleaseNodeReply> getReleaseNodeMethod() {
    io.grpc.MethodDescriptor<ResourceManagerOuterClass.ReleaseNodeRequest, ResourceManagerOuterClass.ReleaseNodeReply> getReleaseNodeMethod;
    if ((getReleaseNodeMethod = ResourceManagerGrpc.getReleaseNodeMethod) == null) {
      synchronized (ResourceManagerGrpc.class) {
        if ((getReleaseNodeMethod = ResourceManagerGrpc.getReleaseNodeMethod) == null) {
          ResourceManagerGrpc.getReleaseNodeMethod = getReleaseNodeMethod = 
              io.grpc.MethodDescriptor.<ResourceManagerOuterClass.ReleaseNodeRequest, ResourceManagerOuterClass.ReleaseNodeReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resourcemanagerproto.ResourceManager", "ReleaseNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.ReleaseNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.ReleaseNodeReply.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceManagerMethodDescriptorSupplier("ReleaseNode"))
                  .build();
          }
        }
     }
     return getReleaseNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ResourceManagerOuterClass.GetNodesUsageRequest,
      ResourceManagerOuterClass.GetNodesUsageReply> getGetNodesUsageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNodesUsage",
      requestType = ResourceManagerOuterClass.GetNodesUsageRequest.class,
      responseType = ResourceManagerOuterClass.GetNodesUsageReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ResourceManagerOuterClass.GetNodesUsageRequest,
      ResourceManagerOuterClass.GetNodesUsageReply> getGetNodesUsageMethod() {
    io.grpc.MethodDescriptor<ResourceManagerOuterClass.GetNodesUsageRequest, ResourceManagerOuterClass.GetNodesUsageReply> getGetNodesUsageMethod;
    if ((getGetNodesUsageMethod = ResourceManagerGrpc.getGetNodesUsageMethod) == null) {
      synchronized (ResourceManagerGrpc.class) {
        if ((getGetNodesUsageMethod = ResourceManagerGrpc.getGetNodesUsageMethod) == null) {
          ResourceManagerGrpc.getGetNodesUsageMethod = getGetNodesUsageMethod = 
              io.grpc.MethodDescriptor.<ResourceManagerOuterClass.GetNodesUsageRequest, ResourceManagerOuterClass.GetNodesUsageReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resourcemanagerproto.ResourceManager", "GetNodesUsage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.GetNodesUsageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResourceManagerOuterClass.GetNodesUsageReply.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceManagerMethodDescriptorSupplier("GetNodesUsage"))
                  .build();
          }
        }
     }
     return getGetNodesUsageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ResourceManagerStub newStub(io.grpc.Channel channel) {
    return new ResourceManagerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ResourceManagerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ResourceManagerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ResourceManagerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ResourceManagerFutureStub(channel);
  }

  /**
   */
  public static abstract class ResourceManagerImplBase implements io.grpc.BindableService {

    /**
     */
    public void reserveNode(ResourceManagerOuterClass.ReserveNodeRequest request,
                            io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReserveNodeReply> responseObserver) {
      asyncUnimplementedUnaryCall(getReserveNodeMethod(), responseObserver);
    }

    /**
     */
    public void releaseNode(ResourceManagerOuterClass.ReleaseNodeRequest request,
                            io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReleaseNodeReply> responseObserver) {
      asyncUnimplementedUnaryCall(getReleaseNodeMethod(), responseObserver);
    }

    /**
     */
    public void getNodesUsage(ResourceManagerOuterClass.GetNodesUsageRequest request,
                              io.grpc.stub.StreamObserver<ResourceManagerOuterClass.GetNodesUsageReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetNodesUsageMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReserveNodeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ResourceManagerOuterClass.ReserveNodeRequest,
                ResourceManagerOuterClass.ReserveNodeReply>(
                  this, METHODID_RESERVE_NODE)))
          .addMethod(
            getReleaseNodeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ResourceManagerOuterClass.ReleaseNodeRequest,
                ResourceManagerOuterClass.ReleaseNodeReply>(
                  this, METHODID_RELEASE_NODE)))
          .addMethod(
            getGetNodesUsageMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ResourceManagerOuterClass.GetNodesUsageRequest,
                ResourceManagerOuterClass.GetNodesUsageReply>(
                  this, METHODID_GET_NODES_USAGE)))
          .build();
    }
  }

  /**
   */
  public static final class ResourceManagerStub extends io.grpc.stub.AbstractStub<ResourceManagerStub> {
    private ResourceManagerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceManagerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ResourceManagerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceManagerStub(channel, callOptions);
    }

    /**
     */
    public void reserveNode(ResourceManagerOuterClass.ReserveNodeRequest request,
                            io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReserveNodeReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReserveNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void releaseNode(ResourceManagerOuterClass.ReleaseNodeRequest request,
                            io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReleaseNodeReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReleaseNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getNodesUsage(ResourceManagerOuterClass.GetNodesUsageRequest request,
                              io.grpc.stub.StreamObserver<ResourceManagerOuterClass.GetNodesUsageReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetNodesUsageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ResourceManagerBlockingStub extends io.grpc.stub.AbstractStub<ResourceManagerBlockingStub> {
    private ResourceManagerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceManagerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ResourceManagerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceManagerBlockingStub(channel, callOptions);
    }

    /**
     */
    public ResourceManagerOuterClass.ReserveNodeReply reserveNode(ResourceManagerOuterClass.ReserveNodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getReserveNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public ResourceManagerOuterClass.ReleaseNodeReply releaseNode(ResourceManagerOuterClass.ReleaseNodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getReleaseNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public ResourceManagerOuterClass.GetNodesUsageReply getNodesUsage(ResourceManagerOuterClass.GetNodesUsageRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetNodesUsageMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ResourceManagerFutureStub extends io.grpc.stub.AbstractStub<ResourceManagerFutureStub> {
    private ResourceManagerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceManagerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ResourceManagerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceManagerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ResourceManagerOuterClass.ReserveNodeReply> reserveNode(
        ResourceManagerOuterClass.ReserveNodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReserveNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ResourceManagerOuterClass.ReleaseNodeReply> releaseNode(
        ResourceManagerOuterClass.ReleaseNodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReleaseNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ResourceManagerOuterClass.GetNodesUsageReply> getNodesUsage(
        ResourceManagerOuterClass.GetNodesUsageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetNodesUsageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RESERVE_NODE = 0;
  private static final int METHODID_RELEASE_NODE = 1;
  private static final int METHODID_GET_NODES_USAGE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ResourceManagerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ResourceManagerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RESERVE_NODE:
          serviceImpl.reserveNode((ResourceManagerOuterClass.ReserveNodeRequest) request,
              (io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReserveNodeReply>) responseObserver);
          break;
        case METHODID_RELEASE_NODE:
          serviceImpl.releaseNode((ResourceManagerOuterClass.ReleaseNodeRequest) request,
              (io.grpc.stub.StreamObserver<ResourceManagerOuterClass.ReleaseNodeReply>) responseObserver);
          break;
        case METHODID_GET_NODES_USAGE:
          serviceImpl.getNodesUsage((ResourceManagerOuterClass.GetNodesUsageRequest) request,
              (io.grpc.stub.StreamObserver<ResourceManagerOuterClass.GetNodesUsageReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ResourceManagerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ResourceManagerBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ResourceManagerOuterClass.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ResourceManager");
    }
  }

  private static final class ResourceManagerFileDescriptorSupplier
      extends ResourceManagerBaseDescriptorSupplier {
    ResourceManagerFileDescriptorSupplier() {}
  }

  private static final class ResourceManagerMethodDescriptorSupplier
      extends ResourceManagerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ResourceManagerMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ResourceManagerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ResourceManagerFileDescriptorSupplier())
              .addMethod(getReserveNodeMethod())
              .addMethod(getReleaseNodeMethod())
              .addMethod(getGetNodesUsageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
