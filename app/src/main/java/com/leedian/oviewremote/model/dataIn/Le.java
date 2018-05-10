package com.leedian.oviewremote.model.dataIn;

/**
 * Created by francoliu on 2017/4/14.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K"
})
public class Le implements Serializable {

    @JsonProperty("A")
    private List<Integer> a = null;
    @JsonProperty("B")
    private List<Integer> b = null;
    @JsonProperty("C")
    private List<Integer> c = null;
    @JsonProperty("D")
    private List<Integer> d = null;
    @JsonProperty("E")
    private List<Integer> e = null;
    @JsonProperty("F")
    private List<Integer> f = null;
    @JsonProperty("G")
    private List<Integer> g = null;
    @JsonProperty("H")
    private List<Integer> h = null;
    @JsonProperty("I")
    private List<Integer> i = null;
    @JsonProperty("J")
    private List<Integer> j = null;
    @JsonProperty("K")
    private List<Integer> k = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("A")
    public List<Integer> getA() {
        return a;
    }

    @JsonProperty("A")
    public void setA(List<Integer> a) {
        this.a = a;
    }

    @JsonProperty("B")
    public List<Integer> getB() {
        return b;
    }

    @JsonProperty("B")
    public void setB(List<Integer> b) {
        this.b = b;
    }

    @JsonProperty("C")
    public List<Integer> getC() {
        return c;
    }

    @JsonProperty("C")
    public void setC(List<Integer> c) {
        this.c = c;
    }

    @JsonProperty("D")
    public List<Integer> getD() {
        return d;
    }

    @JsonProperty("D")
    public void setD(List<Integer> d) {
        this.d = d;
    }

    @JsonProperty("E")
    public List<Integer> getE() {
        return e;
    }

    @JsonProperty("E")
    public void setE(List<Integer> e) {
        this.e = e;
    }

    @JsonProperty("F")
    public List<Integer> getF() {
        return f;
    }

    @JsonProperty("F")
    public void setF(List<Integer> f) {
        this.f = f;
    }

    @JsonProperty("G")
    public List<Integer> getG() {
        return g;
    }

    @JsonProperty("G")
    public void setG(List<Integer> g) {
        this.g = g;
    }

    @JsonProperty("H")
    public List<Integer> getH() {
        return h;
    }

    @JsonProperty("H")
    public void setH(List<Integer> h) {
        this.h = h;
    }

    @JsonProperty("I")
    public List<Integer> getI() {
        return i;
    }

    @JsonProperty("I")
    public void setI(List<Integer> i) {
        this.i = i;
    }

    @JsonProperty("J")
    public List<Integer> getJ() {
        return j;
    }

    @JsonProperty("J")
    public void setJ(List<Integer> j) {
        this.j = j;
    }

    @JsonProperty("K")
    public List<Integer> getK() {
        return k;
    }

    @JsonProperty("K")
    public void setK(List<Integer> k) {
        this.k = k;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Le(){
        a= setValue(0,0,0);
        b= setValue(0,0,0);
        c= setValue(0,0,0);
        d= setValue(0,0,0);
        e= setValue(0,0,0);
        f= setValue(0,0,0);
        g= setValue(0,0,0);
        h= setValue(0,0,0);
        i= setValue(0,0,0);
        j= setValue(0,0,0);
        k= setValue(0,0,0);
    }

    private List<Integer> setValue(int a,int b,int c) {

        List<Integer> val = new ArrayList<>();

        val.add(a);
        val.add(b);
        val.add(c);

        return val;
    }
}