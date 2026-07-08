package com.oxaira.airq.iotmonitoring.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorCommand {
    private boolean extractor;
    private boolean hepa;
    
    @JsonProperty("ac_cool")
    private boolean acCool;
    
    @JsonProperty("ac_dry")
    private boolean acDry;
    
    @JsonProperty("dampers_open")
    private boolean dampersOpen;
}
