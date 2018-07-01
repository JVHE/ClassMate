package com.example.jvhe.videoproject;

import java.io.Serializable;

/**
 * Created by 이정배 on 2018-04-20.
 */

public class VideoItem implements Serializable {

    /*
        비디오 아이템 클래스.
        썸네일 이미지 링크, 비디오 링크, 제목, 내용, 작성자, 재생 시간의 정보를 갖고 있다.
     */

    // 썸네일 이미지 링크, 비디오 링크, 제목, 내용, 작성자, 카테고리
    private String link_thumbnail, link_video, title, contents, writer;
    // 재생 시간, 고유 id
    private int play_time, video_id;
    // 날짜
    private String date;

    // 공개 유무
    private boolean is_public;

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public VideoItem(int video_id, String link_thumbnail, String link_video, String title, String contents, int play_time, String date, String writer, boolean is_public) {
        this.video_id = video_id;

        this.link_thumbnail = link_thumbnail;
        this.link_video = link_video;
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.play_time = play_time;
        this.date = date;
        this.is_public = is_public;
    }


    public int getPlay_time() {
        return play_time;
    }

    public void setPlay_time(int play_time) {
        this.play_time = play_time;
    }

    public String getLink_thumbnail() {
        return link_thumbnail;
    }

    public void setLink_thumbnail(String link_thumbnail) {
        this.link_thumbnail = link_thumbnail;
    }

    public String getLink_video() {
        return link_video;
    }

    public void setLink_video(String link_video) {
        this.link_video = link_video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIs_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
}
