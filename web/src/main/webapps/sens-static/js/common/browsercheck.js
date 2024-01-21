/**
 * 브라우저의 종류 및 버젼을 체크한다.
 */
var browser = {
	type:0,
	version:0,
	IE:0,
	FF:1,
	CHROME:2,
	OPERA:3,
	SAFARI:4,
	ETC:5
};

(function(){ // 외부 라이브러리와 충돌을 막고자 모듈화.
    // 브라우저 및 버전을 구하기 위한 변수들.
    'use strict';
    var agent = navigator.userAgent.toLowerCase();
	var name = navigator.appName;

    // MS 계열 브라우저를 구분하기 위함.
    if(name === 'Microsoft Internet Explorer' || agent.indexOf('trident') > -1 || agent.indexOf('edge/') > -1) {
        browser.type = browser.IE;
        if(name === 'Microsoft Internet Explorer') { // IE old version (IE 10 or Lower)
            agent = /msie ([0-9]{1,}[\.0-9]{0,})/.exec(agent);
            browser.version = parseInt(agent[1]);
        } else { // IE 11+
            if(agent.indexOf('trident') > -1) { // IE 11
                browser.version = 11;
            } else if(agent.indexOf('edge/') > -1) { // Edge
                browser.version = 'edge';
			}
        }
    } else if(agent.indexOf('safari') > -1) { // Chrome or Safari
        if(agent.indexOf('opr') > -1) { // Opera
            browser.type = browser.OPERA;
        } else if(agent.indexOf('chrome') > -1) { // Chrome
            browser.type = browser.CHROME;
        } else { // Safari
            browser.type = browser.SAFARI;
        }
    } else if(agent.indexOf('firefox') > -1) { // Firefox
        browser.type = browser.FF;
    }
}());