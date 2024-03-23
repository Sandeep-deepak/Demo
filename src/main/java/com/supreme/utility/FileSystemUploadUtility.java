//package com.supreme.utility;
//
//import com.supreme.exception.CustomErrorResponseException;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.FileAlreadyExistsException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Component
//public class FileSystemUploadUtility {
//    // Create folder if doesn't exist
//    public void createFolder(Path path) {
//        try {
//            if (!Files.exists(path)) {
//                Files.createDirectories(path);
//            }
//        } catch (IOException e) {
//            throw new CustomErrorResponseException(HttpStatus.BAD_REQUEST, 0, "Could not initialize folder for upload!", "MSG42");
//        }
//    }
//
//    // Copy file with modified name to specified path
//    public void copyFile(MultipartFile file, Path applicantPath, String modifiedName) {
//        try {
//            Files.copy(file.getInputStream(), applicantPath.resolve(modifiedName));
//        } catch (Exception e) {
//            if (e instanceof FileAlreadyExistsException) {
//                throw new CustomErrorResponseException(HttpStatus.FOUND, 0, "A file of that name already exists.", "MSG22");
//            }
//            throw new CustomErrorResponseException(HttpStatus.BAD_REQUEST, 0, e.getMessage(), "MSG56");
//        }
//    }
//
//    // Renaming File
//    public String renameFile(MultipartFile file) {
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
//            throw new CustomErrorResponseException(HttpStatus.NOT_FOUND, 0, "File doesn't exists to rename", "MSG32");
//        }
//        return modifiedName;
//    }
//}
