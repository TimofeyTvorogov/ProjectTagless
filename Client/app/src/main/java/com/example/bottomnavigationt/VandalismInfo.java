package com.example.bottomnavigationt;



public class VandalismInfo {
    private Long id;
    private Double lat;
    private Double lon;
    private String address;
    private String type;
    private String object;
    private Long votes;
    private Boolean cleaned;
    private String imageName;

    public VandalismInfo(Double lat,
                         Double lon,
                         String address,
                         String type,
                         String object,
                         String imageName) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.type = type;
        this.object = object;
        this.imageName = imageName;
        cleaned = false;
        votes = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public Boolean getCleaned() {
        return cleaned;
    }

    public void setCleaned(Boolean cleaned) {
        this.cleaned = cleaned;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "VandalismInfo{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", object='" + object + '\'' +
                ", votes=" + votes +
                ", cleaned=" + cleaned +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}
