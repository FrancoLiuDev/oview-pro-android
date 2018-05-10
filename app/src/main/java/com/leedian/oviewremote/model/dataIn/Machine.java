package com.leedian.oviewremote.model.dataIn;

/**
 * Created by francoliu on 2017/4/14.
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
        "le",
        "mo"
})
public class Machine implements Serializable {

    @JsonProperty("le")
    private Le le;
    @JsonProperty("mo")
    private Mo mo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("le")
    public Le getLe() {
        return le;
    }

    @JsonProperty("le")
    public void setLe(Le le) {
        this.le = le;
    }

    @JsonProperty("mo")
    public Mo getMo() {
        return mo;
    }

    @JsonProperty("mo")
    public void setMo(Mo mo) {
        this.mo = mo;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Machine(){
        le = new Le();
        mo = new  Mo();
    }

    public void syncMo(int time,int direction){

        mo.setDirection(direction);
        mo.setTimes(time);
        mo.setAngle(360/time);
    }

}