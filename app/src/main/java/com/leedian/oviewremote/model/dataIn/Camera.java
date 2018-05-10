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
        "iso",
        "aperture",
        "shutter",
        "focal"
})
public class Camera implements Serializable {

    @JsonProperty("iso")
    private String iso;
    @JsonProperty("aperture")
    private String aperture;
    @JsonProperty("shutter")
    private String shutter;
    @JsonProperty("focal")
    private String focal;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("iso")
    public String getIso() {
        return iso;
    }

    @JsonProperty("iso")
    public void setIso(String iso) {
        this.iso = iso;
    }

    @JsonProperty("aperture")
    public String getAperture() {
        return aperture;
    }

    @JsonProperty("aperture")
    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    @JsonProperty("shutter")
    public String getShutter() {
        return shutter;
    }

    @JsonProperty("shutter")
    public void setShutter(String shutter) {
        this.shutter = shutter;
    }

    @JsonProperty("focal")
    public String getFocal() {
        return focal;
    }

    @JsonProperty("focal")
    public void setFocal(String focal) {
        this.focal = focal;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}


