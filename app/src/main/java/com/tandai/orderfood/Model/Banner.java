package com.tandai.orderfood.Model;

public class Banner {
    private String id, idCountry,image;

    public Banner() {
    }

    public Banner(String id, String idCountry, String image) {
        this.id = id;
        this.idCountry = idCountry;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
