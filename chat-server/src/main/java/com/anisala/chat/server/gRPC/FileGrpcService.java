package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.DownloadRequest;
import com.anisala.chat.proto.UploadResponse;
import com.anisala.chat.proto.FileChunk;
import com.anisala.chat.proto.FileTransferServiceGrpc;
import com.anisala.chat.proto.RequestFileTransferRequest;
import com.anisala.chat.proto.RequestFileTransferResponse;

import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.model.Message;

// import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

import io.grpc.stub.StreamObserver;

public class FileGrpcService extends FileTransferServiceGrpc.FileTransferServiceImplBase {

    private final ChatService chatService;
    // private final ConcurrentHashMap<String, StreamObserver<FileChunk> downloads = new ConcurrentHashMap<>();
    private StreamObserver<FileChunk> receiverStream = null;
    public FileGrpcService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void requestFileTransfer(RequestFileTransferRequest request, StreamObserver<RequestFileTransferResponse> responseObserver) {
        String transferId = request.getTransferId();
        String fileName = request.getFileName();
        String receiverID = request.getUserId();

        try {
            Message downloadNoti = new Message(
                    "System", 
                    receiverID, 
                    "[FILE_REQ:" + transferId + ":" + fileName + "]", 
                    Instant.now()
            );

            chatService.sendMessage(downloadNoti);

            responseObserver.onNext(RequestFileTransferResponse.newBuilder()
                    .setSuccess(true)
                    .setUserId(receiverID)
                    .setTransferId(transferId)
                    .build());
        } catch (Exception e) {
            responseObserver.onNext(RequestFileTransferResponse.newBuilder()
                    .setSuccess(false)
                    .build());
        }
        responseObserver.onCompleted();
    }


    @Override
    public void downloadFile(DownloadRequest request, StreamObserver<FileChunk> responseObserver) {
        // downloads.put(request.getTransferId(), responseObserver);
        receiverStream = responseObserver;
    }

    @Override
    public StreamObserver<FileChunk> uploadFile(StreamObserver<UploadResponse> responseObserver) {
        return new StreamObserver<FileChunk>() {
            private String currentTransferId = null;
            @Override
            public void onNext(FileChunk fileChunk) {
                if (currentTransferId == null) {
                    currentTransferId = fileChunk.getTransferId();
                }
                // StreamObserver<FileChunk> receiver = downloads.get(currentTransferId);
                
                // if (receiver != null) {
                //     receiver.onNext(fileChunk);
                // }
                // 
                if (receiverStream != null) {
                    receiverStream.onNext(fileChunk);
                }
                else {
                    System.out.println("No receiver for transferId: " + currentTransferId);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (receiverStream != null) {
                    receiverStream.onError(t);
                    receiverStream = null;
                }
            }

            @Override
            public void onCompleted() {
                if (receiverStream != null) {
                    receiverStream.onCompleted();
                    receiverStream = null;
                }

                UploadResponse response = UploadResponse.newBuilder()
                    .setSuccess(true)
                    .setTransferId(currentTransferId == null ? "" : currentTransferId)
                    .setMessage("File transfer completed")
                    .build();
                responseObserver.onNext(response);
                currentTransferId = null;
                responseObserver.onCompleted();
            }
        };
    }
}
