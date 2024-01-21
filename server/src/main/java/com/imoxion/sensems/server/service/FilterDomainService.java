package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.domain.ImbFilterDomain;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.FilterDomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FilterDomainService {
    private Logger logger = LoggerFactory.getLogger( ReceiverService.class );

    private static final FilterDomainService filterDomainService = new FilterDomainService();
    public static FilterDomainService getInstance() {
        return filterDomainService;
    }
    private FilterDomainService() {}
    ///////////////////

    public List<String> getListFilter() {
        FilterDomainRepository filterDomainRepository = FilterDomainRepository.getInstance();

        List<String> listFilter = new ArrayList<>();
        try {
            listFilter = filterDomainRepository.getFilterList();
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - getRejectList error", errorId);
        }

        return listFilter;
    }
}
