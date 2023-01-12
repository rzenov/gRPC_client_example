package org.example.client;

import io.grpc.ManagedChannel;
import org.example.grpc.GreetingServiceGrpc;
import org.example.grpc.GreetingServiceOuterClass;

import java.util.Iterator;

public class GreetingClient {
    private final GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;

    public GreetingClient(ManagedChannel managedChannel) {
        this.blockingStub = GreetingServiceGrpc.newBlockingStub(managedChannel);
    }

    public void greeting() {
        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest.newBuilder()
                .setName("Rostislav")
                .build();

        GreetingServiceOuterClass.HelloResponse response = blockingStub.greeting(request);
        System.out.println(response);
    }

    public void greetingRepeatedly(int count) {
        GreetingServiceOuterClass.MultipleHelloRequest multipleHelloRequest = GreetingServiceOuterClass.MultipleHelloRequest.newBuilder()
                .setName("Rostislav")
                .setResponseCount(count)
                .build();
        Iterator<GreetingServiceOuterClass.HelloResponse> greetings = blockingStub.greetings(multipleHelloRequest);
        while (greetings.hasNext())
            System.out.println(greetings.next());
    }
}
