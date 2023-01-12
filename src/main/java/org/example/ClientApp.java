package org.example;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.client.FileUploadClient;
import org.example.client.GreetingClient;

import java.io.IOException;

/**
 * Hello world!
 */
public class ClientApp {

    public static final int DEFAULT_GRPC_PORT = 6565;

    public static void main(String[] args) {
        ManagedChannel managedChannel = startChannel();

        GreetingClient greetingClient = new GreetingClient(managedChannel);
        greetingClient.greeting();

        try {
            FileUploadClient fileUploadClient = new FileUploadClient(managedChannel);
            fileUploadClient.upload("cupboard_schema.pdf");
        } catch (IOException exception) {
            System.out.println("File reading error: " + exception);
        } catch (InterruptedException exception) {
            System.out.println("Something get wrong: " + exception);
        }

        managedChannel.shutdownNow();
    }

    private static ManagedChannel startChannel() {
        return ManagedChannelBuilder.forAddress("localhost", DEFAULT_GRPC_PORT)
                .usePlaintext()
                .build();
    }
}
