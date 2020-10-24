package com.crawler.ecommerce.model;

import java.util.Date;

public class Queue {
    private int id;
    private String link;
    private int size;
    private String note;
    private Date createdDate;
    private String createdAgent;
    private Date updatedDate;
    private String updatedAgent;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedAgent() {
        return createdAgent;
    }

    public void setCreatedAgent(String createdAgent) {
        this.createdAgent = createdAgent;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedAgent() {
        return updatedAgent;
    }

    public void setUpdatedAgent(String updatedAgent) {
        this.updatedAgent = updatedAgent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
