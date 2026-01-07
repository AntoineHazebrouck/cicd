package imt.cicd.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildHistory {

    private static final Path PATH = Paths.get(
        "temp",
        "data",
        "build-history.json"
    );

    @Builder
    @Getter
    public static class BuildRecap {

        private final String status;
        private final String imageId;
        private final String imageName;
        private final String imageTag;
        private final String containerId;
        private final String containerName;
        private final LocalDateTime time;
    }

    @Data
    private static class BuildRecapDto {

        private String status;
        private String imageId;
        private String imageName;
        private String imageTag;
        private String containerId;
        private String containerName;
        private String time;
    }

    private static BuildRecap map(BuildRecapDto d) {
        if (d == null) return null;
        return BuildRecap.builder()
            .status(d.getStatus())
            .imageId(d.getImageId())
            .imageName(d.getImageName())
            .imageTag(d.getImageTag())
            .containerId(d.getContainerId())
            .containerName(d.getContainerName())
            .time(LocalDateTime.parse(d.getTime()))
            .build();
    }

    private static List<BuildRecap> map(List<BuildRecapDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        return dtos.stream().map(BuildHistory::map).toList();
    }

    private static BuildRecapDto toDto(BuildRecap r) {
        if (r == null) return null;
        var d = new BuildRecapDto();
        d.setStatus(r.getStatus());
        d.setImageId(r.getImageId());
        d.setImageName(r.getImageName());
        d.setImageTag(r.getImageTag());
        d.setContainerId(r.getContainerId());
        d.setContainerName(r.getContainerName());
        d.setTime(r.getTime().toString());
        return d;
    }

    private static List<BuildRecapDto> toDto(List<BuildRecap> recaps) {
        if (recaps == null || recaps.isEmpty()) return List.of();
        return recaps.stream().map(BuildHistory::toDto).toList();
    }

    public static List<BuildRecap> history() {
        try {
            if (!Files.exists(PATH)) {
                log.info("file was not found {}", PATH.toString());
                Files.createDirectories(PATH.getParent());
                Files.write(PATH, "[]".getBytes());

                log.info("create file {}", PATH.toString());
            }

            byte[] bytes = Files.readAllBytes(PATH);
            var json = new ObjectMapper();
            var data = json.readValue(
                bytes,
                new TypeReference<List<BuildRecapDto>>() {}
            );
            return map(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read build history", e);
        }
    }

    public static List<BuildRecap> add(BuildRecap recap) {
        var current = Stream.concat(
            history().stream(),
            Stream.of(recap)
        ).toList();

        try {
            Files.createDirectories(PATH.getParent());
            var json = new ObjectMapper();
            byte[] bytes = json.writeValueAsBytes(toDto(current));
            Files.write(PATH, bytes);
            return history();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write build history", e);
        }
    }
}
