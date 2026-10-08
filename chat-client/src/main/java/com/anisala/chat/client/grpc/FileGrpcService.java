package com.anisala.chat.client.grpc;

import com.anisala.chat.client.view.ChatView;
import com.anisala.chat.proto.*;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.io.*;

public class FileGrpcService {
    private final FileTransferServiceGrpc.FileTransferServiceStub fileStub;
    private final ChatView view;
    private static final int CHUNK_SIZE = 256 * 1024;
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "ChatApp" + File.separator;

    public FileGrpcService(ManagedChannel channel, ChatView view) {
        this.fileStub = FileTransferServiceGrpc.newStub(channel);
        this.view = view;
        new File(DOWNLOAD_DIR).mkdirs(); 
    }

    public void sendTransferRequest(String fileName, String targetUserId, String transferId) {
        RequestFileTransferRequest request = RequestFileTransferRequest.newBuilder()
                .setFileName(fileName)
                .setUserId(targetUserId)
                .setTransferId(transferId)
                .build();

        fileStub.requestFileTransfer(request, new StreamObserver<RequestFileTransferResponse>() {
            @Override
            public void onNext(RequestFileTransferResponse response) {
                if (response.getSuccess()) {
                    view.showSystemMessage("File request sent successfully. Waiting for acceptance...");
                } else {
                    view.showSystemMessage("Failed to send file request.");
                }
            }
            @Override
            public void onError(Throwable t) { view.showSystemMessage("Error sending request: " + t.getMessage()); }
            @Override
            public void onCompleted() {}
        });
    }

    public void startReceiving(String transferId, String fileName) {
        DownloadRequest req = DownloadRequest.newBuilder().setTransferId(transferId).setFileName(fileName).build();
        
        fileStub.downloadFile(req, new StreamObserver<FileChunk>() {
            FileOutputStream fos;
            @Override
            public void onNext(FileChunk chunk) {
                try {
                    if (chunk.getChunkNumber() == 1) {
                        fos = new FileOutputStream(DOWNLOAD_DIR + chunk.getFileName());
                    }
                    if (fos != null) fos.write(chunk.getData().toByteArray());
                    
                    int progress = (int) (((double) chunk.getChunkNumber() / chunk.getTotalChunks()) * 100);
                    view.showFileTransferProgress(chunk.getFileName(), progress);
                    
                    if (chunk.getChunkNumber() == chunk.getTotalChunks()) {
                        view.showFileTransferComplete(chunk.getFileName());
                        close();
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
            @Override
            public void onError(Throwable t) { view.showSystemMessage("Transfer failed."); close(); }
            @Override
            public void onCompleted() {}
            private void close() { try { if (fos != null) fos.close(); } catch (Exception e){} }
        });
    }

    public void startSending(String filePath, String transferId) {
        File file = new File(filePath);
        long fileSize = file.length();
        long totalChunks = (long) Math.ceil((double) fileSize / CHUNK_SIZE);

        StreamObserver<FileChunk> reqObserver = fileStub.uploadFile(new StreamObserver<UploadResponse>() {
            @Override public void onNext(UploadResponse ur) {}
            @Override public void onError(Throwable t) { view.showSystemMessage("Upload failed."); }
            @Override public void onCompleted() {}
        });
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            long chunkNum = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                reqObserver.onNext(FileChunk.newBuilder()
                        .setTransferId(transferId)
                        .setFileName(file.getName())
                        .setFileSize(fileSize)
                        .setChunkSize(bytesRead)
                        .setChunkNumber(chunkNum)
                        .setTotalChunks(totalChunks)
                        .setData(ByteString.copyFrom(buffer, 0, bytesRead))
                        .setLastChunk(chunkNum == totalChunks)
                        .build()
                );
                
                view.showFileTransferProgress(file.getName(), (int) (((double) chunkNum / totalChunks) * 100));
                if (chunkNum == totalChunks) view.showFileTransferComplete(file.getName());
                chunkNum++;
            }
            reqObserver.onCompleted();
        } catch (Exception e) { reqObserver.onError(e); }
    }
}
