package imt.cicd.data;

import java.io.File;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;

@Slf4j
public class CloneRepository {

    @Builder
    @Getter
    public static class CloneRepositoryResult {

        private final String folder;
        private final Boolean status;
    }

    public static CloneRepositoryResult run(String repoUrl) {
        var newFolder =
            "./temp/repositories/" + repoUrl.replace("https://", "");
        try {
            File localPath = new File(newFolder);

            if (localPath.exists()) FileUtils.delete(
                localPath,
                FileUtils.RECURSIVE | FileUtils.RETRY
            );

            UsernamePasswordCredentialsProvider credentials =
                new UsernamePasswordCredentialsProvider(
                    "token",
                    RetrieveCurrentGithubToken.run()
                );

            Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localPath)
                .setCredentialsProvider(credentials)
                .call();

            log.info(
                "Cloned {} with branch {}",
                repoUrl,
                git.getRepository().getBranch()
            );
            git.close();

            return CloneRepositoryResult.builder()
                .folder(newFolder)
                .status(true)
                .build();
        } catch (Exception e) {
            log.info("Error cloning {}", repoUrl, e);
            return CloneRepositoryResult.builder()
                .folder(newFolder)
                .status(false)
                .build();
        }
    }
}
