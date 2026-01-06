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

public class BuildHistory {

    private static final Path PATH = Paths.get(
        "temp",
        "data",
        "build-history.json"
    );

    @Builder
    @Getter
    public static class BuildRecap {

        private final String status; // CLONE_FAIL, BUILD_FAIL, DEPLOY_FAIL, RUN_FAIL, SUCCESS
        private final String image;
        private final String imageTag;
        private final LocalDateTime time;
    }

    @Data
    private static class BuildRecapDto {

        private String status;
        private String image;
        private String imageTag;
        private String time;
    }

    private static BuildRecap map(BuildRecapDto d) {
        if (d == null) return null;
        return BuildRecap.builder()
            .status(d.getStatus())
            .image(d.getImage())
            .imageTag(d.getImageTag())
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
        d.setImage(r.getImage());
        d.setImageTag(r.getImageTag());
        d.setTime(r.getTime().toString());
        return d;
    }

    private static List<BuildRecapDto> toDto(List<BuildRecap> recaps) {
        if (recaps == null || recaps.isEmpty()) return List.of();
        return recaps.stream().map(BuildHistory::toDto).toList();
    }

    public static List<BuildRecap> history() {
        if (!Files.exists(PATH)) throw new IllegalStateException(
            PATH.toString() + " should exist"
        );

        try {
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
