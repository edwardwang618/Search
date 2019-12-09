package com.solrtest.bean;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Date;

@Data
public class Products implements Serializable {

    private static final long serialVersionUID = 2L;
    
    @Field("id")
    private String pid;

    @Field("prod_pname")
    private String pname;

    private Integer catalog;

    @Field("prod_catalog_name")
    private String catalogName;

    @Field("prod_price")
    private Double price;

    private Integer number;

    @Field("prod_picture")
    private String picture;

    private Date releaseTime;

    @Field("prod_description")
    private String description;
    
    @Override
    public String toString() {
        return "Products{" +
                "pid='" + pid + '\'' +
                ", pname='" + pname + '\'' +
                ", catalog=" + catalog +
                ", catalogName='" + catalogName + '\'' +
                ", price=" + price +
                ", number=" + number +
                ", picture='" + picture + '\'' +
                ", releaseTime=" + releaseTime +
                ", description='" + description + '\'' +
                '}';
    }
}