package com.qiuyi.cn.orangemoduleNew.util.FileManager.bean1;

/**
 * Created by Administrator on 2018/3/21.
 */
public class MusicBean extends FileInfo{

    //音乐
    private String id;//歌曲id
    private String name;//歌曲名
    private String album;//专辑名
    private Long duration;//持续时间
    private Long size;//大小
    private Long date;//修改日期
    private String path;//路径
    private String artist;//歌手名

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public MusicBean(String id, String name, String album, Long duration, Long size, Long date, String path, String artist) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.duration = duration;
        this.size = size;
        this.date = date;
        this.path = path;
        this.artist = artist;
    }

    public MusicBean() {
    }
}
