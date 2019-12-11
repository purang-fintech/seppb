package com.pr.sepp.utils.jenkins.model;

import com.offbytwo.jenkins.model.BaseModel;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ParameterDefinition extends BaseModel {
    private String description;
    private String name;
    private String type;
    private DefaultParameterValue defaultParameterValue;
    private List<?> choices;

    public static Map<String, String> parameterDefinitionListToMap(List<ParameterDefinition> parameterDefinitions) {
        return parameterDefinitions.stream()
                .map(ParameterDefinition::getDefaultParameterValue)
                .collect(Collectors.toMap(DefaultParameterValue::getName, DefaultParameterValue::getValue));
    }

    public static class DefaultParameterValue extends BaseModel {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
