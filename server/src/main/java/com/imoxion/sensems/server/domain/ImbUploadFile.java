package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class ImbUploadFile {
        private String fkey;

        private String filename;

        private String filepath;

        private Date regdate;

        private long filesize;
}
