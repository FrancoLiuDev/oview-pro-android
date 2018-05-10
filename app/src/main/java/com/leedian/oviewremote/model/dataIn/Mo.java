package com.leedian.oviewremote.model.dataIn;

/**
 * Created by francoliu on 2017/4/12.
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "times",
        "angle",
        "sleep",
        "direction"
})
public class Mo implements Serializable {

    @JsonProperty("times")
    private Integer times;
    @JsonProperty("angle")
    private Integer angle;
    @JsonProperty("sleep")
    private Integer sleep;
    @JsonProperty("direction")
    private Integer direction;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("times")
    public Integer getTimes() {
        return times;
    }

    @JsonProperty("times")
    public void setTimes(Integer times) {
        this.times = times;
    }

    @JsonProperty("angle")
    public Integer getAngle() {
        return angle;
    }

    @JsonProperty("angle")
    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    @JsonProperty("sleep")
    public Integer getSleep() {
        return sleep;
    }

    @JsonProperty("sleep")
    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }

    @JsonProperty("direction")
    public Integer getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Mo(){

        times = 0;
        angle = 0;
        sleep = 0;
        direction =0;

    }
}