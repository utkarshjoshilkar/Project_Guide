package com.studentguide.platform.github.scanner.service;

import com.studentguide.platform.github.scanner.model.RepositoryFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryScannerService {

    private final LanguageDetectionService languageDetectionService;

    public List<RepositoryFile> scanRepository(String repositoryPath) {

        File repositoryDirectory = new File(repositoryPath);

        if (!repositoryDirectory.exists()
                || !repositoryDirectory.isDirectory()) {

            throw new IllegalArgumentException(
                    "Repository directory does not exist: "
                            + repositoryPath);
        }

        List<RepositoryFile> files = new ArrayList<>();

        scanDirectory(
                repositoryDirectory,
                repositoryDirectory,
                files);

        return files;
    }

    private boolean shouldIgnore(File file) {

        String name = file.getName();

        return name.equals(".git")
                || name.equals("target")
                || name.equals("node_modules")
                || name.equals("build")
                || name.equals("dist");
    }

    private void scanDirectory(
            File rootDirectory,
            File currentDirectory,
            List<RepositoryFile> files) {

        File[] children = currentDirectory.listFiles();

        if (children == null) {
            return;
        }

        for (File child : children) {

            if (shouldIgnore(child)) {
                continue;
            }

            String relativePath = rootDirectory.toPath()
                    .relativize(child.toPath())
                    .toString();

            if (child.isDirectory()) {

                files.add(new RepositoryFile(
                        relativePath,
                        child.getName(),
                        "",
                        0,
                        true,
                        "DIRECTORY"
                ));

                scanDirectory(
                        rootDirectory,
                        child,
                        files);

            } else {

                String language =
                        languageDetectionService.detectLanguage(
                                child.getName()
                        );

                files.add(new RepositoryFile(
                        relativePath,
                        child.getName(),
                        getExtension(child.getName()),
                        child.length(),
                        false,
                        language
                ));
            }
        }
    }

    private String getExtension(String fileName) {

        int lastDot = fileName.lastIndexOf('.');

        if (lastDot <= 0 || lastDot == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDot + 1);
    }
}