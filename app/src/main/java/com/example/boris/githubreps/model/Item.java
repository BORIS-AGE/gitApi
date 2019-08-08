package com.example.boris.githubreps.model;

import com.google.gson.annotations.Expose;

public class Item {

    @Expose
    private String name;

    @Expose
    private String html_url;

    @Expose
    private Owner owner;

    public Item(String name, String html_url, String avatar_url, Owner owner) {
        this.name = name;
        this.html_url = html_url;
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public class Owner{
        @Expose
        public String avatar_url;

        public Owner(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }
    }

}
