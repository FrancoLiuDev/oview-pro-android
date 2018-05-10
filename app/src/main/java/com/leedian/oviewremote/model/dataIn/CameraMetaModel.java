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
        "name",
        "about",
        "size",
        "machine",
        "camera"
})
public class CameraMetaModel implements Serializable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("about")
    private String about;
    @JsonProperty("size")
    private String size;

    @JsonProperty("machine")
    private Machine machine;

    @JsonProperty("camera")
    private Camera camera;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setCamera(String name) {
        this.name = name;
    }

    @JsonProperty("about")
    public String getAbout() {
        return about;
    }

    @JsonProperty("about")
    public void setAbout(String about) {
        this.about = about;
    }

    @JsonProperty("size")
    public String getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(String size) {
        this.size = size;
    }


    @JsonProperty("machine")
    public Machine getMachine() {
        return machine;
    }

    @JsonProperty("machine")
    public void setMachine(Machine machine) {
        this.machine = machine;
    }



    @JsonProperty("camera")
    public Camera getCamera() {
        return camera;
    }

    @JsonProperty("camera")
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CameraMetaModel(){

        machine = new Machine();
        camera =new Camera();
    }

    public void syncCameraParam(String iso ,String shutter,String aperture){

        camera.setAperture(aperture);
        camera.setIso(iso);
        camera.setShutter(shutter);
    }
    public void syncCameraFocus(String focus){

        camera.setFocal(focus);
    }

    public void syncMo(int times){

        machine.syncMo(times,1);
    }
}




