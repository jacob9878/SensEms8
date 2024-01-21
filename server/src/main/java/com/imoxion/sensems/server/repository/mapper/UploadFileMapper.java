package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbUploadFile;

import java.util.List;

public interface UploadFileMapper {
    List<ImbUploadFile> getUploadFileListToDelete();
    void deleteUploadFileLazy();
}
