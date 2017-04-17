package com.riya.marvel.db;

import java.io.Serializable;

/**
 * Created by RiyaSharma on 16-04-2017.
 */
public class Comic  implements Serializable {

    private int id;
    private int marvelId;
    private String name;
    private String description;
    private String URLDetail;
    private String landscapeSmallImageUrl;
    private String standardXLargeImageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarvelId() {
        return marvelId;
    }

    public void setMarvelId(int marvelId) {
        this.marvelId = marvelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getURLDetail() {
        return URLDetail;
    }

    public void setURLDetail(String URLDetail) {
        this.URLDetail = URLDetail;
    }

    public String getLandscapeSmallImageUrl() {
        return landscapeSmallImageUrl;
    }

    public void setLandscapeSmallImageUrl(String landscapeSmallImageUrl) {
        this.landscapeSmallImageUrl = landscapeSmallImageUrl;
    }

    public String getStandardXLargeImageUrl() {
        return standardXLargeImageUrl;
    }

    public void setStandardXLargeImageUrl(String standardXLargeImageUrl) {
        this.standardXLargeImageUrl = standardXLargeImageUrl;
    }

}