package com.whf.demolist.common.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */
@DatabaseTable(tableName = "tb_gank")
public class GankEntry {
    @DatabaseField(id = true, uniqueIndex = true)
    private Integer id;
    @DatabaseField
    private String createAt;
    @DatabaseField
    private String desc;
    @DatabaseField
    private String publishedAt;
    @DatabaseField
    private String source;
    @DatabaseField
    private String type;
    @DatabaseField
    private String url;
    @DatabaseField
    private String images;
    @DatabaseField
    private Boolean used;
    @DatabaseField
    private String who;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "GankEntry{" +
                "id='" + id + '\'' +
                ", createAt='" + createAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", images='" + images + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
    }
}
