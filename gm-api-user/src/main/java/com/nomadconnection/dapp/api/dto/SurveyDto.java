package com.nomadconnection.dapp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDto {

    private String title;

    private String answer;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detail;

    public static SurveyDto from(SurveyAnswer surveyAnswer){
        return SurveyDto.builder()
            .title(surveyAnswer.getTitle())
            .answer(surveyAnswer.getAnswer())
            .detail(surveyAnswer.getDetail().orElse("").toString())
            .build();
    }

    public static List<SurveyDto> from(List<SurveyAnswer> surveyAnswer){
        return surveyAnswer.stream()
            .map(SurveyDto::from)
            .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyContents {
        private String key;
        private String title;
        private List<SurveyAnswer> answers;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SurveyAnswer {
            private String key;
            private String title;
            private String subTitle;
            private String type;
            @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
            private List<String> items;
        }
    }

}
