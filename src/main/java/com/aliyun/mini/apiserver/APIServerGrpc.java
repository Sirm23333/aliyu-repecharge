package com.aliyun.mini.apiserver;

import apiserverproto.Apiserver;

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
    comments = "Source: apiserver.proto")
public final class APIServerGrpc {

  private APIServerGrpc() {}

  public static final String SERVICE_NAME = "apiserverproto.APIServer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Apiserver.InvokeFunctionRequest,
      Apiserver.InvokeFunctionReply> getInvokeFunctionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InvokeFunction",
      requestType = Apiserver.InvokeFunctionRequest.class,
      responseType = Apiserver.InvokeFunctionReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Apiserver.InvokeFunctionRequest,
      Apiserver.InvokeFunctionReply> getInvokeFunctionMethod() {
    io.grpc.MethodDescriptor<Apiserver.InvokeFunctionRequest, Apiserver.InvokeFunctionReply> getInvokeFunctionMethod;
    if ((getInvokeFunctionMethod = APIServerGrpc.getInvokeFunctionMethod) == null) {
      synchronized (APIServerGrpc.class) {
        if ((getInvokeFunctionMethod = APIServerGrpc.getInvokeFunctionMethod) == null) {
          APIServerGrpc.getInvokeFunctionMethod = getInvokeFunctionMethod = 
              io.grpc.MethodDescriptor.<Apiserver.InvokeFunctionRequest, Apiserver.InvokeFunctionReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "apiserverproto.APIServer", "InvokeFunction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Apiserver.InvokeFunctionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Apiserver.InvokeFunctionReply.getDefaultInstance()))
                  .setSchemaDescriptor(new APIServerMethodDescriptorSupplier("InvokeFunction"))
                  .build();
          }
        }
     }
     return getInvokeFunctionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Apiserver.ListFunctionsRequest,
      Apiserver.ListFunctionsReply> getListFunctionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListFunctions",
      requestType = Apiserver.ListFunctionsRequest.class,
      responseType = Apiserver.ListFunctionsReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Apiserver.ListFunctionsRequest,
      Apiserver.ListFunctionsReply> getListFunctionsMethod() {
    io.grpc.MethodDescriptor<Apiserver.ListFunctionsRequest, Apiserver.ListFunctionsReply> getListFunctionsMethod;
    if ((getListFunctionsMethod = APIServerGrpc.getListFunctionsMethod) == null) {
      synchronized (APIServerGrpc.class) {
        if ((getListFunctionsMethod = APIServerGrpc.getListFunctionsMethod) == null) {
          APIServerGrpc.getListFunctionsMethod = getListFunctionsMethod = 
              io.grpc.MethodDescriptor.<Apiserver.ListFunctionsRequest, Apiserver.ListFunctionsReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "apiserverproto.APIServer", "ListFunctions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Apiserver.ListFunctionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Apiserver.ListFunctionsReply.getDefaultInstance()))
                  .setSchemaDescriptor(new APIServerMethodDescriptorSupplier("ListFunctions"))
                  .build();
          }
        }
     }
     return getListFunctionsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static APIServerStub newStub(io.grpc.Channel channel) {
    return new APIServerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static APIServerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new APIServerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static APIServerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new APIServerFutureStub(channel);
  }

  /**
   */
  public static abstract class APIServerImplBase implements io.grpc.BindableService {

    /**
     */
    public void invokeFunction(Apiserver.InvokeFunctionRequest request,
                               io.grpc.stub.StreamObserver<Apiserver.InvokeFunctionReply> responseObserver) {
      asyncUnimplementedUnaryCall(getInvokeFunctionMethod(), responseObserver);
    }

    /**
     */
    public void listFunctions(Apiserver.ListFunctionsRequest request,
                              io.grpc.stub.StreamObserver<Apiserver.ListFunctionsReply> responseObserver) {
      asyncUnimplementedUnaryCall(getListFunctionsMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInvokeFunctionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                Apiserver.InvokeFunctionRequest,
                Apiserver.InvokeFunctionReply>(
                  this, METHODID_INVOKE_FUNCTION)))
          .addMethod(
            getListFunctionsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                Apiserver.ListFunctionsRequest,
                Apiserver.ListFunctionsReply>(
                  this, METHODID_LIST_FUNCTIONS)))
          .build();
    }
  }

  /**
   */
  public static final class APIServerStub extends io.grpc.stub.AbstractStub<APIServerStub> {
    private APIServerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private APIServerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected APIServerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new APIServerStub(channel, callOptions);
    }

    /**
     */
    public void invokeFunction(Apiserver.InvokeFunctionRequest request,
                               io.grpc.stub.StreamObserver<Apiserver.InvokeFunctionReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInvokeFunctionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listFunctions(Apiserver.ListFunctionsRequest request,
                              io.grpc.stub.StreamObserver<Apiserver.ListFunctionsReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListFunctionsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class APIServerBlockingStub extends io.grpc.stub.AbstractStub<APIServerBlockingStub> {
    private APIServerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private APIServerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected APIServerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new APIServerBlockingStub(channel, callOptions);
    }

    /**
     */
    public Apiserver.InvokeFunctionReply invokeFunction(Apiserver.InvokeFunctionRequest request) {
      return blockingUnaryCall(
          getChannel(), getInvokeFunctionMethod(), getCallOptions(), request);
    }

    /**
     */
    public Apiserver.ListFunctionsReply listFunctions(Apiserver.ListFunctionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListFunctionsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class APIServerFutureStub extends io.grpc.stub.AbstractStub<APIServerFutureStub> {
    private APIServerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private APIServerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected APIServerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new APIServerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Apiserver.InvokeFunctionReply> invokeFunction(
        Apiserver.InvokeFunctionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInvokeFunctionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Apiserver.ListFunctionsReply> listFunctions(
        Apiserver.ListFunctionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListFunctionsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INVOKE_FUNCTION = 0;
  private static final int METHODID_LIST_FUNCTIONS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final APIServerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(APIServerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INVOKE_FUNCTION:
          serviceImpl.invokeFunction((Apiserver.InvokeFunctionRequest) request,
              (io.grpc.stub.StreamObserver<Apiserver.InvokeFunctionReply>) responseObserver);
          break;
        case METHODID_LIST_FUNCTIONS:
          serviceImpl.listFunctions((Apiserver.ListFunctionsRequest) request,
              (io.grpc.stub.StreamObserver<Apiserver.ListFunctionsReply>) responseObserver);
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

  private static abstract class APIServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    APIServerBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Apiserver.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("APIServer");
    }
  }

  private static final class APIServerFileDescriptorSupplier
      extends APIServerBaseDescriptorSupplier {
    APIServerFileDescriptorSupplier() {}
  }

  private static final class APIServerMethodDescriptorSupplier
      extends APIServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    APIServerMethodDescriptorSupplier(String methodName) {
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
      synchronized (APIServerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new APIServerFileDescriptorSupplier())
              .addMethod(getInvokeFunctionMethod())
              .addMethod(getListFunctionsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
