package nimblix.in.HealthCareHub.helper;

/*
import lombok.extern.slf4j.Slf4j;
import nimblix.in.HealthCareHub.constants.HealthCareConstants;
import nimblix.in.HealthCareHub.response.MultipleImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UploadImageHelper {

    @Value("${assignment.upload.path}")
    private String uploadPath;

    public MultipleImageResponse uploadImages(List<MultipartFile> pictures) throws Exception {
        List<String> uploadedFileNames = new ArrayList<>();
        List<String> failedFileNames = new ArrayList<>();

        if (pictures == null || pictures.isEmpty()) {
            return new MultipleImageResponse(HealthCareConstants.STATUS_ERORR, "No files provided", Collections.emptyList());
        }

        for (MultipartFile file : pictures) {
            if (file == null || file.isEmpty()) {
                failedFileNames.add(file != null ? file.getOriginalFilename() : "Unknown file");
                continue;
            }

            String fileName = System.currentTimeMillis() + "_"+file.getOriginalFilename();
            // Optional: sanitize file name
            // fileName = fileName.replaceAll("[^a-zA-Z0-9.\\-]", "_");

            // Ensure upload directory exists
            if (!uploadPath.endsWith(File.separator)) {
                uploadPath += File.separator;
            }

            File directory = new File(uploadPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    log.info("Directory created at: {}", uploadPath);
                } else {
                    log.error("Failed to create directory at: {}", uploadPath);
                }
            }

            Path path = Paths.get(uploadPath + fileName);
            byte[] fileData = file.getBytes();

            try {
                // ✅ Save file in original (non-encrypted) format
                Files.write(path, fileData);

                log.info("File saved successfully: {}", fileName);
                uploadedFileNames.add(fileName);

            } catch (Exception e) {
                log.error("Error saving file: {}, error: {}", fileName, e.getMessage(), e);
                failedFileNames.add(fileName);
            }
        }

        if (uploadedFileNames.isEmpty()) {
            String failedFilesMessage = "Image upload failed for the following files: " + String.join(", ", failedFileNames);
            return new MultipleImageResponse(HealthCareConstants.STATUS_ERORR, failedFilesMessage, Collections.emptyList());
        }

        return new MultipleImageResponse(HealthCareConstants.STATUS_SUCCESS, "Image upload successful", uploadedFileNames);
    }

}
*/