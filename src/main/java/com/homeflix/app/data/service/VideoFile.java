package com.homeflix.app.data.service;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VideoFile {
    private String identifier;
    private String name;
    private long size;
    private boolean file;
    private String fullPath;
    private String posterLink;
}