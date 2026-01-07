package imt.cicd.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Slf4j
public class CloneRepository {

    @Builder
    @Getter
    public static class CloneRepositoryResult implements HasStatus {

        private final String folder;
        private final Boolean status;
    }

    public static CloneRepositoryResult run(String repoUrl) {
        String folderName = repoUrl.replace("https://", "").replace(".git", "");
        String pathString = "./temp/repositories/" + folderName;
        File localPath = new File(pathString);

        String token = System.getenv("GITHUB_CICD_TOKEN");

        try {
            if (localPath.exists()) {
                log.info(
                    "Dossier existant détecté. Suppression de {}...",
                    pathString
                );
                deleteDirectory(localPath.toPath());
            }

            log.info("Début du clonage de {}...", repoUrl);

            var gitCommand = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localPath)
                .setBranch("main");

            if (token != null && !token.isEmpty()) {
                gitCommand.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(token, "")
                );
                log.debug("Authentification par token activée.");
            } else {
                log.warn(
                    "Aucun token détecté : clonage anonyme (risque d'échec si repo privé)."
                );
            }

            try (Git git = gitCommand.call()) {
                log.info(
                    "Clonage réussi sur la branche : {}",
                    git.getRepository().getBranch()
                );
            }

            return CloneRepositoryResult.builder()
                .folder(pathString)
                .status(true)
                .build();
        } catch (Exception e) {
            log.error("Echec du clonage pour {} : {}", repoUrl, e.getMessage());

            return CloneRepositoryResult.builder()
                .folder(pathString)
                .status(false)
                .build();
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }
    }
}
