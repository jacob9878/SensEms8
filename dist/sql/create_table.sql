
CREATE TABLE `imb_addrsel` (
   `msgid` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '메시지 아이디',
   `userid` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '사용자 아이디',
   `gkey` VARCHAR(60) NOT NULL DEFAULT '' COMMENT '주소록 그룹키',
   `gname` VARCHAR(60) NOT NULL DEFAULT '' COMMENT '주소록 그룹명',
   PRIMARY KEY (`msgid`, `userid`, `gkey`)
) COMMENT='메일 발송시 선택한 주소록 그룹';

CREATE TABLE IF NOT EXISTS `imb_block_email` (
    `email` varchar(30) NOT NULL COMMENT '차단이메일',
    `description` varchar(100) DEFAULT NULL COMMENT '설명',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`email`)
    ) COMMENT='차단이메일';

CREATE TABLE IF NOT EXISTS `imb_block_ip` (
    `ip` varchar(50) NOT NULL COMMENT '대역으로 지정(0.0.0.0 : 모두 차단, CIDR 가능)',
    `memo` varchar(100) DEFAULT NULL COMMENT '메모',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`ip`)
    ) COMMENT='smtp 차단 아이피';

CREATE TABLE IF NOT EXISTS `imb_category` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '고유키',
    `name` varchar(100) NOT NULL DEFAULT '' COMMENT '분류 이름',
    `userid` varchar(30) NOT NULL DEFAULT '' COMMENT '사용자 아이디',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`ukey`,`name`)
    ) COMMENT='발송 분류';

CREATE TABLE IF NOT EXISTS `imb_dbinfo` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '고유키',
    `dbname` varchar(255) NOT NULL DEFAULT '' COMMENT '데이타베이스 이름',
    `dbtype` varchar(50) NOT NULL DEFAULT '' COMMENT 'DB종류',
    `userid` varchar(30) DEFAULT NULL COMMENT '사용자아이디',
    `dbhost` varchar(100) DEFAULT NULL COMMENT 'DB 서버 주소',
    `dbuser` varchar(50) DEFAULT NULL COMMENT 'DB 접속 계정',
    `dbpasswd` varchar(50) DEFAULT NULL COMMENT 'DB 접속 비번',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `dbcharset` varchar(100) DEFAULT '' COMMENT 'DB 캐릭터셋',
    `datacharset` varchar(100) DEFAULT '' COMMENT 'DB 데이터 캐릭터셋',
    `address` varchar(255) DEFAULT NULL COMMENT 'JDBC URL',
    `dbport` varchar(20) DEFAULT '' COMMENT 'DB 포트번호',
    PRIMARY KEY (`ukey`,`dbname`,`dbtype`)
    ) COMMENT='수신자 추출용 데이터베이스 정보';

CREATE TABLE IF NOT EXISTS `imb_dkim` (
    `domain` varchar(100) NOT NULL COMMENT '도메인명',
    `selector` varchar(20) DEFAULT NULL COMMENT '셀렉터',
    `filename` varchar(255) DEFAULT NULL COMMENT '파일명',
    `public_key` varchar(512) DEFAULT NULL COMMENT '공개키',
    `private_key` varbinary(1024) DEFAULT NULL COMMENT '비밀키',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `use_sign` char(1) DEFAULT '0' COMMENT 'sign사용여부',
    PRIMARY KEY (`domain`)
    ) COMMENT='도메인 DKIM 설정';

CREATE TABLE IF NOT EXISTS `imb_emsattach` (
    `ekey` varchar(24) NOT NULL DEFAULT '' COMMENT '고유키',
    `msgid` varchar(24) DEFAULT '' COMMENT '메시지아이디',
    `file_name` varchar(100) DEFAULT '' COMMENT '파일명',
    `file_size` varchar(20) DEFAULT NULL COMMENT '파일크기',
    `file_path` varchar(255) DEFAULT NULL COMMENT '파일경로(YYYY/MM/DD/EKEY)',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `expire_date` datetime DEFAULT NULL COMMENT '만료일',
    `down_count` int(5) DEFAULT 0 COMMENT '다운로드 횟수',
    `disk_space` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`ekey`),
    KEY `idx_msgid` (`msgid`),
    KEY `idx_regdate` (`regdate`)
    ) COMMENT='첨부파일 정보';

CREATE TABLE IF NOT EXISTS `imb_emsmain` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메시지아이디',
    `categoryid` varchar(24) DEFAULT '' COMMENT '발송분류아이디',
    `userid` varchar(30) DEFAULT NULL COMMENT '사용자아이디',
    `mail_from` varchar(200) DEFAULT '' COMMENT '발신주소',
    `replyto` varchar(100) DEFAULT '' COMMENT '회신주소',
    `recid` varchar(255) DEFAULT NULL COMMENT '수신그룹아이디',
    `recname` varchar(255) DEFAULT NULL COMMENT '수신그룹이름',
    `templateid` varchar(24) DEFAULT '' COMMENT '템플릿아이디',
    `rectype` char(2) DEFAULT '1' COMMENT '1:주소록, 3:수신그룹, 4:재발신',
    `dbkey` varchar(24) DEFAULT NULL,
    `query` text DEFAULT NULL COMMENT 'rectype=4인경우(재발신)',
    `msg_name` varchar(255) DEFAULT NULL COMMENT '메일제목',
    `msg_path` varchar(255) DEFAULT NULL COMMENT '메일파일경로(YYYY/MM/DD/MSGID.eml_',
    `reserv_time` varchar(14) DEFAULT NULL COMMENT '예약시간(YYYYMMDDHHmm)',
    `regdate` varchar(14) DEFAULT NULL COMMENT '등록일(YYYYMMDDHHmmss)',
    `resp_time` varchar(14) DEFAULT NULL COMMENT '반응분석종료일(YYYYMMDDHHmm)',
    `total_send` int(11) DEFAULT 0 COMMENT '총발송건수',
    `cur_send` int(11) DEFAULT 0 COMMENT '현재발송수',
    `start_time` varchar(14) DEFAULT NULL COMMENT '발송시작시간(YYYYMMDDHHmmss)',
    `end_time` varchar(14) DEFAULT NULL COMMENT '발송종료시간(YYYYMMDDHHmm)',
    `stop_time` varchar(14) DEFAULT NULL COMMENT '발송중지시간(YYYYMMDDHHmmss)',
    `send_start_time` bigint(20) DEFAULT 0 COMMENT '발송재시작시간UNIX_TIMESTAMP',
    `state` char(3) DEFAULT '' COMMENT '발송상태',
    `parentid` varchar(24) DEFAULT NULL COMMENT '부모키',
    `resend_num` int(11) DEFAULT 0 COMMENT '재발송번호',
    `resend_step` int(11) DEFAULT 0 COMMENT '재발송단계',
    `isattach` char(1) NOT NULL DEFAULT '0' COMMENT '첨부유무',
    `islink` char(1) DEFAULT '0' COMMENT '링크추적여부',
    `charset` varchar(20) DEFAULT '' COMMENT '메일 캐릭터셋',
    `extended` varchar(20) DEFAULT NULL COMMENT '기타(발송중지등에 사용)',
    `temp_campid` varchar(30) DEFAULT NULL COMMENT '시스템연동(imb_temp_main의 campid)',
    `temp_mailid` varchar(30) DEFAULT NULL COMMENT '시스템연동(imb_temp_main의 mailid)',
    `ishtml` tinyint(4) DEFAULT 1 COMMENT 'html메일 여부',
    `is_same_email` tinyint(4) DEFAULT 0 COMMENT '중복이메일 허용 여부',
    `error_resend` tinyint(4) DEFAULT 0 COMMENT '에러 재발송 횟수 설정',
    `cur_resend` tinyint(4) DEFAULT 0 COMMENT '현재 재발송 횟수',
    `send_interval` varchar(5) DEFAULT '0' COMMENT '통당 발송 간격(초)',
    `priority` char(2) DEFAULT NULL COMMENT '중요도(1:높음,3:보통,5:낮음)',
    `isstop` char(1) DEFAULT '0' COMMENT '1: 발송중지(대기중일때도 중지가능), 0:중지아님',
    PRIMARY KEY (`msgid`),
    KEY `idx_complex` (`categoryid`,`userid`,`regdate`,`state`,`parentid`,`resend_num`)
    ) COMMENT='대량 메일 발송 정보';

CREATE TABLE IF NOT EXISTS `imb_error_count` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메시지아이디',
    `unknownhost` int(11) DEFAULT 0 COMMENT '901 HOST UNKNOWN',
    `connect_error` int(11) DEFAULT 0 COMMENT '902 연결에러',
    `dns_error` int(11) DEFAULT 0 COMMENT '903 DNS 에러',
    `network_error` int(11) DEFAULT 0 COMMENT '904 네트워크 에러',
    `system_error` int(11) DEFAULT 0 COMMENT '905 시스템 에러',
    `server_error` int(11) DEFAULT 0 COMMENT '906 서버에러',
    `syntax_error` int(11) DEFAULT 0 COMMENT '907 명령어 에러',
    `userunknown` int(11) DEFAULT 0 COMMENT '908 USER UNKNOWN',
    `mboxfull` int(11) DEFAULT 0 COMMENT '909 메일박스 FULL',
    `etc_error` int(11) DEFAULT 0 COMMENT '910 기타에러',
    `emailaddr_error` int(11) DEFAULT 0 COMMENT '이메일형식 에러',
    `reject_error` int(11) DEFAULT 0 COMMENT '수신거부',
    `repeat_error` int(11) DEFAULT 0 COMMENT '중복에러',
    `domain_error` int(11) DEFAULT 0 COMMENT '차단도메인(imb_filter_domain)',
    `blankemail_error` int(11) DEFAULT 0 COMMENT '이메일주소 공백',
    `extended` varchar(20) DEFAULT NULL COMMENT '기타',
    PRIMARY KEY (`msgid`)
    ) COMMENT='에러 종류별 에러 카운트';

CREATE TABLE IF NOT EXISTS `imb_ext_mail` (
    `ukey` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '자동증가 키',
    `userid` varchar(30) DEFAULT '' COMMENT '사용자아이디',
    `f_key` varchar(50) NOT NULL DEFAULT '0' COMMENT '외부연동 0, 테스트발송 1, 개별재발신 2, 릴레이 3',
    `ext_key` varchar(50) DEFAULT NULL COMMENT '외부연동시 넘어온 고유키값',
    `tempid` varchar(24) DEFAULT '' COMMENT '템플릿아이디',
    `from_email` varchar(100) DEFAULT NULL COMMENT '발신메일주소',
    `to_email` varchar(100) DEFAULT NULL COMMENT '수신메일주소',
    `subject` varchar(255) DEFAULT '' COMMENT '제목',
    `field1` varchar(100) DEFAULT NULL COMMENT '개인화 필드1(수신자이메일)',
    `field2` varchar(100) DEFAULT NULL COMMENT '개인화 필드2',
    `field3` varchar(100) DEFAULT NULL COMMENT '개인화 필드3',
    `field4` varchar(100) DEFAULT '' COMMENT '개인화 필드4',
    `field5` varchar(100) DEFAULT '' COMMENT '개인화 필드5',
    `success` char(1) DEFAULT '2' COMMENT '성공여부(실패0, 성공1, 대기중2)',
    `errcode` char(3) DEFAULT '0' COMMENT '에러코드(기본0)',
    `err_exp` varchar(200) DEFAULT '' COMMENT '에러사유',
    `send_time` varchar(14) DEFAULT NULL COMMENT '보낸시간(연월일시분초)',
    `recv_time` varchar(14) DEFAULT NULL COMMENT '수신확인시간(연월일시분초)',
    `recv_count` int(11) DEFAULT 0 COMMENT '수신확인횟수',
    `state` char(1) NOT NULL DEFAULT '0' COMMENT '메일 발송 상태(기본0, 진행중1, 완료2)',
    `charset` varchar(20) DEFAULT NULL COMMENT '메일캐릭터셋',
    `ishtml` tinyint(4) DEFAULT 1 COMMENT 'html메일여부(html 1, text 0)',
    `utf_data` char(1) DEFAULT '1' COMMENT 'utf8 데이터여부(기본 1)',
    `isattach` char(1) DEFAULT '0' COMMENT '첨부포함 여부(없음 0, 포함 1)',
    `regdate` varchar(14) DEFAULT NULL COMMENT '등록일(현재시간)',
    `error_resend` int(11) DEFAULT 0 COMMENT '에러재발신 횟수 설정',
    `cur_resend` int(11) DEFAULT -1 COMMENT '현재 에러 재발신 횟수',
    PRIMARY KEY (`ukey`),
    KEY `idx_fkey` (`f_key`),
    KEY `idx_sendtime` (`send_time`),
    KEY `idx_state` (`state`),
    KEY `idx_regdate` (`regdate`),
    KEY `idx_complex` (`ukey`,`userid`,`f_key`,`send_time`)
    ) ENGINE=InnoDB AUTO_INCREMENT=272 DEFAULT CHARSET=utf8 COMMENT='시스템 연동 및 개별발송';

CREATE TABLE IF NOT EXISTS `imb_ext_mail_attach` (
    `ekey` varchar(24) NOT NULL DEFAULT '' COMMENT '첨부파일 고유키',
    `ukey` bigint(20) NOT NULL DEFAULT 0 COMMENT 'imb_ext_mail 의 ukey',
    `file_name` varchar(200) DEFAULT '' COMMENT '파일명',
    `file_size` varchar(20) DEFAULT NULL COMMENT '파일크기',
    `file_path` varchar(255) DEFAULT NULL COMMENT '파일경로(YYYY/MM/DD/EKEY)',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `expire_date` datetime DEFAULT NULL COMMENT '다운로드 만료일',
    PRIMARY KEY (`ekey`),
    KEY `idx_regdate` (`regdate`),
    KEY `idx_mailkey` (`ukey`)
    ) COMMENT='시스템 연동 개별발송 첨부 ';

CREATE TABLE IF NOT EXISTS `imb_ext_mail_body` (
    `ukey` bigint(20) NOT NULL COMMENT 'imb_ext_mail의 ukey',
    `body` mediumtext NOT NULL DEFAULT '' COMMENT '메일 본문',
    PRIMARY KEY (`ukey`)
    ) COMMENT='시스템 연동 개별발송 메일 본문';

CREATE TABLE IF NOT EXISTS `imb_file_ext_limit` (
    `ext` varchar(10) NOT NULL DEFAULT ''
    ) COMMENT='첨부파일 확장자 제한';

CREATE TABLE IF NOT EXISTS `imb_filterdomain` (
    `hostname` varchar(100) NOT NULL DEFAULT '' COMMENT '발송차단 도메인',
    PRIMARY KEY (`hostname`)
    ) COMMENT='발송 차단 도메인';

CREATE TABLE IF NOT EXISTS `imb_hostinfo` (
    `hostname` varchar(100) NOT NULL DEFAULT '' COMMENT 'sender 서버 아이피',
    `port` int(11) NOT NULL DEFAULT 0 COMMENT '9090, 9091, 9092…등',
    `target` int(11) DEFAULT 0 COMMENT '0: 대량+개별, 1:개별',
    `isactive` int(11) DEFAULT 1 COMMENT '1: 사용, 0:사용안함',
    PRIMARY KEY (`hostname`,`port`)
    ) COMMENT='발송 sender 서버 정보';

CREATE TABLE IF NOT EXISTS `imb_image` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '고유키',
    `userid` varchar(30) DEFAULT '' COMMENT '사용자 아이디',
    `image_name` varchar(100) DEFAULT '' COMMENT '이미지명',
    `image_width` int(11) DEFAULT 0 COMMENT '이미지 가로',
    `image_height` int(11) DEFAULT 0 COMMENT '이미지 세로',
    `image_path` varchar(200) DEFAULT '' COMMENT '이미지 url',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `flag` char(2) DEFAULT '02' COMMENT '01:공용, 02:개인',
    PRIMARY KEY (`ukey`)
    ) COMMENT='이미지 저장 정보';

CREATE TABLE IF NOT EXISTS `imb_limit_info` (
    `limit_type` varchar(3) NOT NULL COMMENT '제한타입',
    `limit_value` varchar(200) DEFAULT NULL COMMENT '제한값',
    `descript` varchar(100) DEFAULT NULL COMMENT '설명',
    PRIMARY KEY (`limit_type`)
    ) COMMENT='송신제한정보';

CREATE TABLE IF NOT EXISTS `imb_link_count` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메시지아이디',
    `linkid` int(11) NOT NULL DEFAULT 0 COMMENT '링크아이디',
    `link_count` int(11) DEFAULT 0 COMMENT '클릭 횟수',
    PRIMARY KEY (`msgid`,`linkid`)
    ) COMMENT='링크별 클릭 카운트 저장';

CREATE TABLE IF NOT EXISTS `imb_link_info` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메시지아이디',
    `linkid` int(11) NOT NULL,
    `link_name` varchar(100) DEFAULT NULL COMMENT '링크 이름',
    `link_url` varchar(1000) DEFAULT NULL COMMENT '링크 url',
    `link_img` varchar(1000) DEFAULT NULL COMMENT '링크 이미지',
    PRIMARY KEY (`msgid`,`linkid`)
    ) COMMENT='링크 정보';

CREATE TABLE IF NOT EXISTS `imb_msg_info` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메일메시지아이디',
    `contents` mediumtext DEFAULT NULL COMMENT '메일 본문',
    PRIMARY KEY (`msgid`)
    ) COMMENT='대량 메일 본문 저장';

CREATE TABLE IF NOT EXISTS `imb_receipt_count` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메일메시지아이디',
    `recv_count` int(11) DEFAULT 0 COMMENT '수신확인 횟수',
    PRIMARY KEY (`msgid`)
    ) COMMENT='날짜별 수신확인 카운트';

CREATE TABLE IF NOT EXISTS `imb_receiver` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '수신그룹 키',
    `userid` varchar(30) NOT NULL DEFAULT '' COMMENT '사용자 아이디',
    `recv_name` varchar(100) DEFAULT NULL COMMENT '수신그룹 이름',
    `dbkey` varchar(24) DEFAULT NULL COMMENT 'imb_dbinfo의 ukey',
    `query` text DEFAULT NULL COMMENT '수신그룸 추출 쿼리',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `extended` varchar(50) DEFAULT NULL COMMENT '기타',
    PRIMARY KEY (`ukey`,`userid`)
    ) COMMENT='수신그룹 정보';

CREATE TABLE IF NOT EXISTS `imb_reject` (
    `email` varchar(100) NOT NULL DEFAULT '' COMMENT '이메일주소',
    `msgid` varchar(24) DEFAULT NULL,
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`email`)
    ) COMMENT='수신거부 정보';

CREATE TABLE IF NOT EXISTS `imb_relay_ip` (
    `ip` varchar(50) NOT NULL COMMENT '대역으로 지정(0.0.0.0 : 모두 허용, CIDR 가능)',
    `memo` varchar(100) DEFAULT NULL COMMENT '메모',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`ip`)
    ) COMMENT='smtp 릴레이아이피';

CREATE TABLE IF NOT EXISTS `imb_rotmain` (
    `msgid` varchar(24) NOT NULL DEFAULT '' COMMENT '메시지아이디',
    `categoryid` varchar(24) NOT NULL DEFAULT '' COMMENT '발송분류 키값',
    `userid` varchar(30) DEFAULT '' COMMENT '사용자아이디',
    `recid` varchar(24) DEFAULT '' COMMENT '수신그룹아이디',
    `mail_from` varchar(255) DEFAULT '' COMMENT '발신주소',
    `replyto` varchar(255) DEFAULT '' COMMENT '회신주소',
    `templateid` varchar(24) DEFAULT '' COMMENT '템플릿아이디',
    `msg_name` varchar(255) DEFAULT '' COMMENT '제목',
    `regdate` varchar(14) DEFAULT '' COMMENT '등록일',
    `start_time` varchar(8) DEFAULT '' COMMENT '정기발송 시작일(yyyymmdd)',
    `end_time` varchar(8) DEFAULT '' COMMENT '정기발송 종료일(yyyymmdd)',
    `rot_flag` tinyint(4) DEFAULT 1 COMMENT '반복주기(0:매일,1:매주,2:매월)',
    `rot_point` varchar(100) DEFAULT '' COMMENT '반복날짜(요일: 1(일)~7(토), 월: 1월~12월)',
    `send_time` varchar(4) DEFAULT '' COMMENT '발송시간(hhmm)',
    `last_send` varchar(4) DEFAULT '' COMMENT '최종발송날짜(mmdd)',
    `charset` varchar(20) DEFAULT '' COMMENT '메일 캐릭터셋',
    `islink` char(1) DEFAULT '0' COMMENT '링크추적여부',
    `ishtml` tinyint(4) DEFAULT 1 COMMENT 'html메일 여부',
    `extended` varchar(100) DEFAULT '' COMMENT '기타',
    PRIMARY KEY (`msgid`)
    ) COMMENT='정기 예약 메일';

CREATE TABLE IF NOT EXISTS `imb_smtp_account` (
    `sid` varchar(30) NOT NULL COMMENT '아이디',
    `passwd` varchar(100) DEFAULT NULL COMMENT '비밀번호',
    `pwd_type` varchar(10) DEFAULT 'sha-256' COMMENT '암호화 타입',
    `st_data` varchar(100) DEFAULT NULL COMMENT 'salt값',
    `description` varchar(100) DEFAULT NULL COMMENT '설명',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    PRIMARY KEY (`sid`)
    ) COMMENT='SMTP인증용 계정관리';

CREATE TABLE IF NOT EXISTS `imb_smtp_temp_main` (
    `mainkey` varchar(24) NOT NULL COMMENT '메일의 고유키',
    `subject` varchar(255) NOT NULL COMMENT '메일제목',
    `mailfrom` varchar(255) NOT NULL COMMENT '발신자',
    `group_key` varchar(50) DEFAULT NULL COMMENT '발신쪽에서 보관하는 발송메일의 고유키(옵션)',
    `regdate` datetime NOT NULL COMMENT '등록일',
    `ip` varchar(20) NOT NULL COMMENT '발신아이피',
    `body` mediumtext NOT NULL COMMENT '메일본문(html)',
    `send_type` char(1) DEFAULT 'T' COMMENT 'T: 테스트메일, D: 외부에서 DB연동(WEB API), A: Smtp 인증, R: 릴레이연동, C: 건별재발신',
    PRIMARY KEY (`mainkey`)
    ) COMMENT='개별발송 연동용임시 메인 테이블';

CREATE TABLE IF NOT EXISTS `imb_smtp_temp_rcpt` (
    `idx` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '자동증가 값',
    `mainkey` varchar(24) NOT NULL COMMENT 'imp_temp_main의 mainkey 값',
    `rcptto` varchar(255) NOT NULL COMMENT '수신자주소',
    `rcpt_key` varchar(50) DEFAULT NULL COMMENT '발신쪽에서 보관하는 수신자의 고유키(옵션)',
    PRIMARY KEY (`idx`),
    KEY `idx_mainkey` (`mainkey`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='개별발송 연동용 임시 수신자 테이블';

CREATE TABLE IF NOT EXISTS `imb_template` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '템플릿키',
    `userid` varchar(30) DEFAULT '' COMMENT '사용자아이디',
    `temp_name` varchar(100) DEFAULT '' COMMENT '템플릿 제목',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `contents` mediumtext DEFAULT NULL COMMENT '본문',
    `image_path` varchar(100) DEFAULT '' COMMENT '썸네일 이미지 정보',
    `flag` varchar(2) DEFAULT '02' COMMENT '용도(01:공요, 02:개인)',
    `extended` varchar(20) DEFAULT '' COMMENT '기타',
    PRIMARY KEY (`ukey`)
    ) COMMENT='템플릿 정보';

CREATE TABLE IF NOT EXISTS `imb_temp_attach` (
    `idx` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `campid` varchar(30) NOT NULL DEFAULT '',
    `file_name` varchar(100) DEFAULT '',
    `file_size` varchar(20) DEFAULT NULL,
    `file_path` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`idx`),
    KEY `idx_campid` (`campid`)
    ) COMMENT='연동용 임시 첨부 저장 테이블';

CREATE TABLE IF NOT EXISTS `imb_temp_main` (
    `idx` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `campid` varchar(30) NOT NULL DEFAULT '',
    `userid` varchar(30) DEFAULT NULL,
    `mail_from` varchar(200) DEFAULT '',
    `recname` varchar(200) DEFAULT NULL,
    `templateid` varchar(24) DEFAULT '',
    `title` varchar(255) DEFAULT NULL,
    `body` text DEFAULT NULL,
    `reserve_date` varchar(12) DEFAULT NULL,
    `regdate` datetime DEFAULT NULL,
    `resp_time` varchar(12) DEFAULT NULL,
    `isattach` char(1) DEFAULT '0',
    `islink` char(1) DEFAULT '1',
    `charset` varchar(10) DEFAULT 'utf-8',
    `ishtml` tinyint(4) DEFAULT 1,
    `error_resend` tinyint(4) DEFAULT 0,
    PRIMARY KEY (`idx`),
    KEY `idx_campid` (`campid`)
    ) COMMENT='연동용 임시 저장 메일 테이블';

CREATE TABLE IF NOT EXISTS `imb_temp_rcpt` (
    `idx` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `campid` varchar(30) NOT NULL DEFAULT '',
    `field1` varchar(200) DEFAULT NULL,
    `field2` varchar(200) DEFAULT NULL,
    `field3` varchar(200) DEFAULT NULL,
    `field4` varchar(200) DEFAULT NULL,
    `field5` varchar(200) DEFAULT NULL,
    `field6` varchar(200) DEFAULT NULL,
    `field7` varchar(200) DEFAULT NULL,
    `field8` varchar(200) DEFAULT NULL,
    `field9` varchar(200) DEFAULT NULL,
    `field10` varchar(200) DEFAULT NULL,
    PRIMARY KEY (`idx`),
    KEY `idx_campid` (`campid`)
    ) COMMENT='연동용 수신자 임시 저장 테이블';

CREATE TABLE IF NOT EXISTS `imb_test_account` (
    `ukey` varchar(24) NOT NULL DEFAULT '' COMMENT '고유키',
    `userid` varchar(30) NOT NULL DEFAULT '' COMMENT '사용자 아이디',
    `email` varchar(100) NOT NULL DEFAULT '' COMMENT '테스트 이메일주소',
    `flag` char(1) NOT NULL DEFAULT '0' COMMENT '기본 테스트메일 여부',
    PRIMARY KEY (`ukey`,`email`)
    ) COMMENT='테스트 발송 계정 정보';

CREATE TABLE IF NOT EXISTS `imb_transmit_data` (
    `traceid` varchar(50) NOT NULL COMMENT 'smtp traceid',
    `serverid` varchar(10) NOT NULL DEFAULT '0' COMMENT '서버 ID',
    `logdate` datetime NOT NULL COMMENT '수신 시간',
    `authid` varchar(30) DEFAULT NULL COMMENT 'smtp 인증 아이디',
    `group_key` varchar(50) DEFAULT NULL COMMENT '동보발송의 그룹키(발송메일의 헤더에 포함)',
    `rcpt_key` varchar(50) DEFAULT NULL COMMENT '수신자별 메일 고유키(발송메일의 헤더에 포함)',
    `subject` varchar(255) DEFAULT NULL COMMENT '제목',
    `mailfrom` varchar(100) DEFAULT NULL COMMENT '송신자 이메일',
    `rcptto` varchar(100) NOT NULL COMMENT '수신자 이메일',
    `from_domain` varchar(100) DEFAULT NULL COMMENT '송신자 도메인',
    `rcpt_domain` varchar(100) DEFAULT NULL COMMENT '수신자 도메인',
    `ip` varchar(50) DEFAULT NULL COMMENT '송신자 IP',
    `mailsize` bigint(20) DEFAULT 0 COMMENT '메일 크기',
    `transmit_fl` char(1) DEFAULT NULL COMMENT '송/수신/내부간 구분',
    `local_fl` char(1) DEFAULT NULL COMMENT '내부메일-1, 외부-2 (미사용)',
    `result` char(1) DEFAULT NULL COMMENT '메일 발송 성공 여부, 0-실패,1-성공,2-처리중',
    `description` varchar(100) DEFAULT NULL COMMENT '설명',
    `errcode` int(11) DEFAULT NULL COMMENT '오류 코드',
    `errmsg` varchar(200) DEFAULT NULL COMMENT '오류 메시지',
    `etc` varchar(200) DEFAULT NULL COMMENT '기타',
    `org_traceid` varchar(50) DEFAULT NULL COMMENT 'original traceid',
    `logdate_ymd` varchar(10) GENERATED ALWAYS AS (cast(`logdate` as date)) STORED,
    `readdate` datetime DEFAULT NULL COMMENT '수신확인시간',
    `readcount` int(11) DEFAULT 0 COMMENT '수신확인횟수',
    `send_type` char(1) DEFAULT 'T' COMMENT 'T: 테스트메일, D: 외부에서 DB연동(WEB API), A: Smtp 인증, R: 릴레이연동',
    PRIMARY KEY (`traceid`,`serverid`,`rcptto`),
    KEY `idx_complex` (`logdate`,`mailfrom`,`rcptto`,`result`),
    KEY `idx_logdate_ymd` (`logdate_ymd`),
    KEY `idx_group_key` (`group_key`),
    KEY `idx_rcpt_key` (`rcpt_key`),
    KEY `idx_send_type` (`send_type`)
    ) COMMENT='개별발송 결과';

CREATE TABLE IF NOT EXISTS `imb_upload_file` (
    `fkey` varchar(24) NOT NULL COMMENT '파일키',
    `filename` varchar(255) DEFAULT NULL COMMENT '파일명',
    `filepath` varchar(255) DEFAULT NULL COMMENT '파일경로',
    `regdate` datetime DEFAULT NULL COMMENT '등록일자',
    `filesize` bigint(11) DEFAULT NULL COMMENT '파일사이즈',
    PRIMARY KEY (`fkey`)
    ) COMMENT='업로드 파일';

CREATE TABLE IF NOT EXISTS `imb_userinfo` (
    `userid` varchar(30) NOT NULL COMMENT '사용자 아이디',
    `passwd` varchar(100) DEFAULT NULL COMMENT '비밀번호',
    `uname` varchar(50) DEFAULT NULL COMMENT '이름',
    `dept` varchar(30) DEFAULT NULL COMMENT '부서',
    `grade` varchar(20) DEFAULT NULL COMMENT '직위',
    `email` varchar(100) DEFAULT NULL COMMENT '메일주소',
    `mobile` varchar(100) DEFAULT NULL COMMENT '휴대폰번호',
    `tel` varchar(100) DEFAULT NULL COMMENT '전화번호',
    `permission` char(2) DEFAULT NULL COMMENT '권한(A:관리자U:사용자)',
    `regdate` datetime DEFAULT NULL COMMENT '등록일',
    `isstop` char(1) DEFAULT NULL COMMENT '중지여부(0:사용중 1:중지)',
    `approve_email` varchar(200) DEFAULT NULL COMMENT '승인메일 주소',
    `access_ip` varchar(2000) DEFAULT NULL COMMENT '접근아이피',
    `pwd_type` varchar(10) DEFAULT NULL COMMENT '비밀번호 암호화 타입',
    `pwd_date` datetime DEFAULT NULL COMMENT '비밀번호 변경일',
    `st_data` varchar(100) DEFAULT NULL COMMENT '암호 salt 값',
    `fail_login_time` datetime DEFAULT NULL COMMENT '계정잠금시각',
    `fail_login` int(2) DEFAULT 0 COMMENT '실패 로그인 횟수',
    `use_smtp` varchar(4) NOT NULL DEFAULT '0',
    PRIMARY KEY (`userid`)
    ) COMMENT='사용자 정보';

CREATE TABLE IF NOT EXISTS `imb_user_action_log` (
    `log_key` varchar(24) NOT NULL COMMENT '로그키',
    `log_date` datetime NOT NULL COMMENT '로그시간',
    `ip` varchar(30) NOT NULL COMMENT 'ip',
    `userid` varchar(100) NOT NULL COMMENT '사용자 아이디',
    `menu_key` varchar(10) NOT NULL COMMENT '메뉴키',
    `param` text DEFAULT NULL COMMENT '상세내용(url 파라미터)',
    PRIMARY KEY (`log_key`),
    KEY `idx_date` (`log_date`)
    ) COMMENT='사용자 활동 로그';

CREATE TABLE IF NOT EXISTS `imb_user_action_menu` (
    `menu_key` varchar(10) NOT NULL COMMENT '고유키',
    `menu` varchar(100) DEFAULT NULL COMMENT '메뉴명',
    PRIMARY KEY (`menu_key`)
    );


INSERT INTO `imb_limit_info` (`limit_type`, `limit_value`, `descript`) VALUES
   ('004', '1', '대용량큐로 전환되는 건당 메일 크기(MB)'),
   ('005', '500', '대용량큐로 전환되는 From의 시간당 메일 건수'),
   ('006', '500', '대용량큐로 전환되는 From의 시간당 메일 총 용량(MB)'),
   ('012', '1500', '1회 발송 시 최대 동보 수신자 수'),
   ('016', '20', '1회 발송 시 최대 메일 크기(MB)'),
   ('017', '2000', '1회 발송 시 최대 총 메일 크기(수신자수 * 메일크기, MB)');

INSERT INTO `imb_relay_ip` (`ip`, `memo`, `regdate`) VALUES
    ('127.0.0.1', 'localhost(필수)', now());

INSERT INTO `imb_userinfo` (`userid`, `passwd`, `uname`, `dept`, `grade`, `email`, `mobile`, `tel`, `permission`, `regdate`, `isstop`, `approve_email`, `access_ip`, `pwd_type`, `pwd_date`, `st_data`, `fail_login_time`, `fail_login`, `use_smtp`) VALUES
     ('admin', '2LwQyrg88T5oo4ieRKy7DMHbBW3IANInkA6xGAz2hv4=', '관리자', '', '', 'qsL2S8tJckdKPdAftrRWtXQUpv7d7v6/8xOJ76R6j4k=', '', '', 'A', now(), '0', '', '0.0.0.0', 'SHA-256', now(), NULL, NULL, 0, '0');


CREATE TABLE `imb_addrgrp_admin` (
     `gkey` INT(11) NOT NULL AUTO_INCREMENT COMMENT '그룹키',
     `gname` VARCHAR(100) NULL DEFAULT NULL COMMENT '그룹 명',
     `memo` VARCHAR(255) NULL DEFAULT NULL COMMENT '메모(설명)',
     `grpcount` INT(11) NULL DEFAULT '0' COMMENT '그룹 카운트',
     PRIMARY KEY (`gkey`)
) COMMENT='사용자별 주소록 그룹';

CREATE TABLE `imb_addr_admin` (
      `ukey` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '주소록 키',
      `gkey` INT(11) NOT NULL DEFAULT '0' COMMENT '그룹 키',
      `name` VARCHAR(50) NOT NULL COMMENT '이름',
      `email` VARCHAR(200) NULL DEFAULT NULL COMMENT '이메일',
      `company` VARCHAR(80) NULL DEFAULT NULL COMMENT '회사',
      `dept` VARCHAR(80) NULL DEFAULT NULL COMMENT '부서',
      `grade` VARCHAR(80) NULL DEFAULT NULL COMMENT '직책',
      `office_tel` VARCHAR(50) NULL DEFAULT NULL COMMENT '회사 전화번호',
      `mobile` VARCHAR(50) NULL DEFAULT NULL COMMENT '휴대폰 번호',
      `etc1` VARCHAR(200) NULL DEFAULT NULL COMMENT '기타정보1',
      `etc2` VARCHAR(200) NULL DEFAULT NULL COMMENT '기타정보2',
      `regdate` DATETIME NULL DEFAULT NULL COMMENT '등록일',
      PRIMARY KEY (`ukey`),
      INDEX `idx_gkey` (`gkey`)
) COMMENT='사용자별 주소록';

CREATE TRIGGER trg_grp_insert_admin AFTER INSERT ON imb_addr_admin
    FOR EACH ROW
BEGIN
    UPDATE imb_addrgrp_admin SET grpcount = grpcount + 1 WHERE gkey = NEW.gkey;
END;

CREATE TRIGGER trg_grp_update_admin AFTER UPDATE ON imb_addr_admin
    FOR EACH ROW
BEGIN
    IF OLD.gkey != NEW.gkey THEN
        UPDATE imb_addrgrp_admin SET grpcount = grpcount - 1 WHERE gkey = OLD.gkey;
        UPDATE imb_addrgrp_admin SET grpcount = grpcount + 1 WHERE gkey = NEW.gkey;
    END IF;
END;

CREATE TRIGGER trg_grp_delete_admin AFTER DELETE ON imb_addr_admin
    FOR EACH ROW
BEGIN
    UPDATE imb_addrgrp_admin SET grpcount = grpcount - 1 WHERE gkey = OLD.gkey;
END;
