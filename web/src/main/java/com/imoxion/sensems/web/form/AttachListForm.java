package com.imoxion.sensems.web.form;

import java.util.ArrayList;
import java.util.List;

import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.common.ImPage;


public class AttachListForm {

    private String searchText = "";

    private String sortOption = "00010000";

    /** 현재 페이지 */
    private String currentPage = "1";

    /** 페이지 사이즈 */
    private String pageSize = "15";

    /** 페이지 정보 */
    private ImPage imPage = new ImPage();

    private List<AttachBean> fileList = new ArrayList<AttachBean>();

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }


    public ImPage getImPage() { return imPage; }
    public void setImPage(ImPage imPage) { this.imPage = imPage; }

   public List<AttachBean> getFileList() {
        return fileList;
    }

   public void setFileList(List<AttachBean> fileList) {
        this.fileList = fileList;
    }

    public String getSortOption() {
        return sortOption;
    }

    public void setSortOption(String sortOption) {
        this.sortOption = sortOption;
    }

}
