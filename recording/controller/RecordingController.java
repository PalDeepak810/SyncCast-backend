package com.Syncast.recording.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.io.FileWriter;

@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor
public class RecordingController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadRecording(
            @RequestParam("file")
            MultipartFile file
    )throws IOException {

        //Create uploads folder

        File uploadDir=new File("uploads");

        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }

        //Save recording
        String fileName=System.currentTimeMillis()
                +"-"
                +file.getOriginalFilename();

        File destination = new File(
                uploadDir,
                fileName
        );

        file.transferTo(destination);

        return ResponseEntity.ok(
                "Recording uploaded successfully"
        );
    }

    private final String UPLOAD_BASE_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomCode") String roomCode,
            @RequestParam("username") String username,
            @RequestParam("chunkIndex") Integer chunkIndex
    ) throws IOException {

        File userDir = new File(UPLOAD_BASE_DIR + File.separator + roomCode + File.separator + username);

        if (!userDir.exists()) {

            userDir.mkdirs();
        }

    /*
      Chunk filename
     */
        File chunkFile =
                new File(

                        userDir,

                        "chunk-"
                                + chunkIndex
                                + ".webm"
                );

        System.out.println("Received chunk " + chunkIndex + " for user " + username + " in room " + roomCode);

        file.transferTo(chunkFile);

        System.out.println("Chunk " + chunkIndex + " saved successfully");

        return ResponseEntity.ok(
                "Chunk uploaded"
        );
    }

    @PostMapping("/merge")
    public ResponseEntity<String> mergeRecording(

            @RequestParam("roomCode")
            String roomCode,

            @RequestParam("username")
            String username

    ) throws IOException, InterruptedException {

    /*
      Chunk directory
     */
        File userDir =
                new File(
                        "uploads/"
                                + roomCode
                                + "/"
                                + username
                );

        if (!userDir.exists()) {

            return ResponseEntity.badRequest()
                    .body("Recording directory not found");
        }

    /*
      Create concat file
     */
        File concatFile =
                new File(
                        userDir,
                        "chunks.txt"
                );

        FileWriter writer =
                new FileWriter(concatFile);

        File[] chunks =
                userDir.listFiles(
                        (dir, name) ->
                                name.endsWith(".webm")
                );

        if (chunks == null || chunks.length == 0) {

            return ResponseEntity.badRequest()
                    .body("No chunks found");
        }

    /*
      Sort chunks
     */
        /*
          Numeric Sort chunks (chunk-0.webm, chunk-1.webm, chunk-2.webm...)
         */
        Arrays.sort(chunks, (f1, f2) -> {
            try {
                int n1 = Integer.parseInt(f1.getName().replace("chunk-", "").replace(".webm", ""));
                int n2 = Integer.parseInt(f2.getName().replace("chunk-", "").replace(".webm", ""));
                return Integer.compare(n1, n2);
            } catch (Exception e) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        for (File chunk : chunks) {

            writer.write(
                    "file '"
                            + chunk.getAbsolutePath()
                            .replace("\\", "/")
                            + "'\n"
            );
        }

        writer.close();

    /*
      Final output
     */
        File outputFile =
                new File(
                        userDir,
                        "final-recording.webm"
                );

    /*
      FFmpeg command using absolute path
     */
        String ffmpegPath = "C:\\Users\\dp220\\Downloads\\ffmpeg-8.1.1-essentials_build\\ffmpeg-8.1.1-essentials_build\\bin\\ffmpeg.exe";

        ProcessBuilder processBuilder =
                new ProcessBuilder(

                        ffmpegPath,

                        "-f",
                        "concat",

                        "-safe",
                        "0",

                        "-i",
                        concatFile.getAbsolutePath(),

                        "-c",
                        "copy",

                        "-y", // Overwrite output file if exists

                        outputFile.getAbsolutePath()
                );

        processBuilder.redirectErrorStream(true);

        Process process =
                processBuilder.start();

        int exitCode =
                process.waitFor();

        if (exitCode != 0) {

            return ResponseEntity.internalServerError()
                    .body("FFmpeg merge failed");
        }

        return ResponseEntity.ok(
                "Recording merged successfully"
        );
    }
}
