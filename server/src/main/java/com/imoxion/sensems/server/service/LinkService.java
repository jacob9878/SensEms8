package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.domain.ImbLinkInfo;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LinkService {
    private Logger logger = LoggerFactory.getLogger( LinkService.class );

    private static final LinkService linkService = new LinkService();
    public static LinkService getInstance() {
        return linkService;
    }
    private LinkService() {}
    ///////////////////


    public List<ImbLinkInfo> getLinkList(String msgid){
        LinkRepository linkRepository = LinkRepository.getInstance();
        List<ImbLinkInfo> listLink = null;
        try {
            listLink = linkRepository.getLinkList(msgid);
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - insertErrorCount error", errorId);
        }

        return listLink;
    }

    public void insertLinkInfo(ImbLinkInfo linkInfo) {
        try {
            LinkRepository linkRepository = LinkRepository.getInstance();

            linkRepository.insertLinkInfo(linkInfo);
            linkRepository.insertLinkCountInfo(linkInfo.getMsgid(), linkInfo.getLinkid(), 0);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - insertLinkInfo error : {}", errorId, e.getMessage());
        }
    }

    public void createLinkLogTable(String msgid) throws Exception{
        LinkRepository linkRepository = LinkRepository.getInstance();

        linkRepository.createLinkLogTable(msgid);
    }


}
