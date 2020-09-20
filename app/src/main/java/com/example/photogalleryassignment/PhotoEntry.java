package com.example.photogalleryassignment;


import androidx.annotation.Nullable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PhotoEntry {

    @Setter
    private String caption;
    private String filepath;
    private Date timestamp;
}
