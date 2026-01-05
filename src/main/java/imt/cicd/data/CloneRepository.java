package imt.cicd.data;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;

@Slf4j
public class CloneRepository {

    public static boolean run(String repoUrl) {
        try {
            File localPath = new File(
                "./temp/repositories/" + repoUrl.replace("https://", "")
            );

            Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localPath)
                .call();

			log.info("Cloned {} with branch {}", repoUrl, git.getRepository().getBranch());
            git.close();

            return true;
        } catch (Exception e) {
            log.info("Error cloning {}", repoUrl, e);
            return false;
        }
    }
}
