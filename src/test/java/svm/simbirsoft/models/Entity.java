package svm.simbirsoft.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class Entity {
    private String id;
    private Addition addition;
    private List<Integer> important_numbers;
    private String title;
    private boolean verified;

    @Data
    @Builder
    @Jacksonized
    public static class Addition {
        private String additional_info;
        private int additional_number;
        private Integer id;
    }
}