package org.example.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.example.grpc.File;
import org.example.grpc.FileServiceGrpc;
import org.example.grpc.FileUploadRequest;
import org.example.grpc.FileUploadResponse;
import org.example.grpc.MetaData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class FileUploadClient {
    FileServiceGrpc.FileServiceStub fileServiceStub;

    public FileUploadClient(ManagedChannel managedChannel) {
        this.fileServiceStub = FileServiceGrpc.newStub(managedChannel);
    }

    public void upload(String fileName) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        // request observer
        StreamObserver<FileUploadRequest> streamObserver = this.fileServiceStub.upload(new FileUploadObserver(latch));

        // input file for testing
        Path path = Paths.get("input/" + fileName);

        // build metadata
        FileUploadRequest metadata = FileUploadRequest.newBuilder()
                .setMetadata(MetaData.newBuilder()
                        .setName("output")
                        .setType("pdf").build())
                .build();
        streamObserver.onNext(metadata);

        // upload file as chunk
        InputStream inputStream = Files.newInputStream(path);
        byte[] bytes = new byte[4096];
        int size;
        while ((size = inputStream.read(bytes)) > 0){
            FileUploadRequest uploadRequest = FileUploadRequest.newBuilder()
                    .setFile(File.newBuilder().setContent(ByteString.copyFrom(bytes, 0 , size)).build())
                    .build();
            streamObserver.onNext(uploadRequest);
        }

        // close the stream
        inputStream.close();
        streamObserver.onCompleted();
        latch.await();
    }
}

class FileUploadObserver implements StreamObserver<FileUploadResponse> {
    private final CountDownLatch latch;
    public FileUploadObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(FileUploadResponse fileUploadResponse) {
        System.out.println(
                "File upload status :: " + fileUploadResponse.getStatus()
        );
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Done");
        this.latch.countDown();
    }
}
