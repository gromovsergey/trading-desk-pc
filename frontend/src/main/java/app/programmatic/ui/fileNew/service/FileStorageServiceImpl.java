package app.programmatic.ui.fileNew.service;

import app.programmatic.ui.fileNew.config.FileStorageProperties;
import app.programmatic.ui.fileNew.exception.FileStorageException;
import app.programmatic.ui.fileNew.exception.MoveFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService{

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            if (Files.notExists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String parentDir) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(parentDir.isEmpty() ? file.getOriginalFilename() : parentDir + "/" + file.getOriginalFilename());
        //String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public void moveFile(String fromFile, String toFile) {
        Path source = fileStorageLocation.resolve(Paths.get(fromFile)).normalize();
        Path target = fileStorageLocation.resolve(Paths.get(toFile)).normalize();

        try {
            Files.createDirectories(target.getParent());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MoveFileException("File not moved");
        }
    }

    @Override
    public Path getFullPath(String path)
    {
        return fileStorageLocation.resolve(Paths.get(path)).normalize();
    }
}
