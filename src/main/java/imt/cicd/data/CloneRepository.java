package imt.cicd.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Slf4j
public class CloneRepository {

    public static boolean run(String repoUrl) {
        String token = System.getenv("GITHUB_CICD_TOKEN");
        
        try {
            String folderName = repoUrl.replace("https://", "").replace(".git", "");
            File localPath = new File("./temp/repositories/" + folderName);

            if (localPath.exists()) {
                log.info("Nettoyage du dossier existant...");
                deleteDirectory(localPath.toPath());
            }

            log.info("Clonage de {}...", repoUrl);

            var gitCommand = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localPath)
                .setBranch("main");

            if (token != null && !token.isEmpty()) {
                log.info("Utilisation du GITHUB_CICD_TOKEN pour l'authentification.");
                gitCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""));
            } else {
                log.warn("Aucun GITHUB_CICD_TOKEN trouvé : Tentative de clonage anonyme (Public uniquement).");
            }

            try (Git git = gitCommand.call()) {
                log.info("Clonage réussi dans {}", localPath.getAbsolutePath());
            }

            return true;

        } catch (Exception e) {
            log.error("Erreur critique lors du clonage : " + e.getMessage(), e);
            return false;
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }
    }
}