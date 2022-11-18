package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.GreetingServiceGrpc;
import org.example.grpc.GreetingServiceOuterClass;

import java.util.Iterator;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:8080")
                .usePlaintext()
                .build();

        GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub = GreetingServiceGrpc.newBlockingStub(managedChannel);
        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest.newBuilder()
                .setName("Rostislav")
                .build();

        GreetingServiceOuterClass.HelloResponse response = blockingStub.greeting(request);
        System.out.println(response);

        GreetingServiceOuterClass.MultipleHelloRequest multipleHelloRequest = GreetingServiceOuterClass.MultipleHelloRequest.newBuilder()
                .setName("Rostislav")
                .setResponseCount(5)
                .build();
        Iterator<GreetingServiceOuterClass.HelloResponse> greetings = blockingStub.greetings(multipleHelloRequest);
        while (greetings.hasNext())
            System.out.println(greetings.next());

        managedChannel.shutdownNow();
    }
}
