package com.nomadconnection.dapp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDto {

    private CommonCodeType title;

    private SurveyType answer;

    private String detail;

    public static SurveyDto from(Survey survey){
        return SurveyDto.builder()
            .title(survey.getTitle())
            .answer(survey.getAnswer())
            .detail(survey.getDetail().orElse("").toString())
            .build();
    }

    public static List<SurveyDto> from(List<Survey> survey){
        return survey.stream()
            .map(SurveyDto::from)
            .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyContents {
        private CommonCodeType key;
        private String title;
        private List<SurveyAnswer> answers;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SurveyAnswer {
            private SurveyType key;
            private String title;
            private SurveyType.DetailType type;
            @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
            private List<SurveyType.SelectBoxItem> items;
        }

    }

}
