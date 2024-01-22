/*발송차단 설정 등록일 추가*/
alter table imb_filterdomain add `regdate` datetime DEFAULT NULL COMMENT '등록일';

/*사용자 활동로그 C104 - 재발송 로그에서 재발신으로 변경 TODO 추후 insert 쿼리 작성 필요*/
