package com.supreme.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Date;

@Configuration
public class S3Util {

    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.accessKey}")
    private String accessKey;
    @Value("${aws.s3.secretKey}")
    private String secretKey;
    @Value("${aws.s3.region}")
    private String region;
    private S3Client s3Client;

    @PostConstruct
    private void initializeAmazon() {
        this.s3Client = S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .region(Region.of(region))   // Region.US_EAST_1
                .build();
    }

    // Post API
    public String uploadFile(String folderName, MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = folderName + generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    } // 1708319436061-JappTech_Logo.jpeg

//    public String generateFileName(MultipartFile file) {
//        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
//        String modifiedName = null;
//        if (!file.isEmpty()) {
//            modifiedName = file.getOriginalFilename().replaceAll(" ", "_");
//            modifiedName = modifiedName.substring(0, file.getOriginalFilename().lastIndexOf("."))
//                    .concat("_" + currentDateTime
//                            + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
//            // JappTech_Logo_2024-02-13_180503.jpeg
//        } else {
////            throw new ErrorResponse(HttpStatus.NOT_FOUND.value(), "File Not found Exception", "MSG19");
//            throw new CustomErrorResponseException(HttpStatus.NOT_FOUND, "File doesn't exists to rename", "MSG32");
//        }
//        return modifiedName;
//    }

    private void uploadFileTos3bucket(String fileName, File file) {
        // Upload the file to S3
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(fileName)
//                .acl(ObjectCannedACL.PUBLIC_READ)
//                .acl("public-read")
                .build();
        s3Client.putObject(request, file.toPath());
    }

    public void deleteFileFromS3Bucket(String folderName, String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(folderName + fileName).build();
        s3Client.deleteObject(request);
    }

    public ResponseEntity<byte[]> getImageFromS3Bucket(String folderName, String fileName) throws IOException {
        // Remove Ngrok header during deployment
        HttpHeaders headers = new HttpHeaders();
        headers.set("ngrok-skip-browser-warning", "1231");

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(this.bucketName)
                .key(folderName + fileName)
                .build();

//        try (ResponseInputStream<?> responseInputStream = s3Client.getObject(request)) {
//            byte[] imageBytes = responseInputStream.readAllBytes();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.IMAGE_JPEG); // Set appropriate content type based on image format
//            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//            // return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
//        }
        try (ResponseInputStream<GetObjectResponse> s3ObjectResponseInputStream = s3Client.getObject(request)) {
            byte[] imageData = s3ObjectResponseInputStream.readAllBytes();
            String contentType = URLConnection.guessContentTypeFromName(fileName);
            if (contentType == null) {
                contentType = MediaType.IMAGE_JPEG_VALUE; // Default to image/jpeg if content type cannot be guessed
            }
            return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType(contentType)).body(imageData);
        } catch (IOException e) { // Handle IO exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
