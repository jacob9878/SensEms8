<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var calendarEl = document.getElementById('calendar');

        var calendar = new FullCalendar.Calendar(calendarEl, {
            height: 'parent', // 높이 결정
            customButtons :{
                addSchedule : {
                    text : '일정등록',
                    click:function () {
                        alert("일정등록");
                        //calendar.changeView('timeGridDay');
                    }
                }
            },
            headerToolbar: {
                left: 'dayGridMonth,timeGridWeek,timeGridDay,addSchedule',
                center: 'title',
                right: 'today,prev,next'
            }, // 기본 툴바 위치 설정
            //initialDate: '2021-03-26', // 기본 날짜 세팅, 값 없을 시 Default로 오늘
            navLinks: true, // 달력상의 날짜를 클릭할 수 있는지 여부
            selectable: true, // 날짜를 클릭 또는 드래그 하여 강조 표시
            // businessHours: {
            //     daysOfWeek: [ 1, 2, 3, 4, 5 ], // 월 ~ 금
            //     startTime: '9:00', // 시작 시간
            //     endTime: '18:00', // 끝나는 시간
            // }, //업무시간 제외 나머지 시간 회색으로 처리
            selectMirror: true,
            locale:'${UserInfo.language}', //언어 설정
            editable: false,//이벤트 드래그, 리사이징 등의 편집 여부 결정
            dayMaxEvents: true, // 칸에 담을 수 있는 이벤트 갯수가 많아질 경우 +로 표기
            select: function(arg) { //날짜 클릭 시 이벤트 처리
                var title = prompt('Event Title:');
                if (title) {
                    calendar.addEvent({
                        title: title,
                        start: arg.start,
                        end: arg.end,
                        allDay: arg.allDay
                    })
                }
                calendar.unselect()
            },
            eventClick: function(arg) { // 등록된 일정을 클릭 시 이벤트 처리
                if (confirm('Are you sure you want to delete this event?')) {
                    arg.event.remove()
                }
            },
            events: [ //TODO url 통해서 표시될 데이터 획득해야한다.
                {
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },
                {
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },{
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },
                {
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Repeating Event',
                    start: '2021-03-09T16:00:00'
                },

                {
                    groupId: 999, //이벤트 드래그 / 리사이징 할 경우 동일한 그룹 이벤트들은 자동으로 같이 수정됨.
                    title: 'Event',
                    start: '2021-03-05T16:00:00'
                },
                {
                    title: 'Click for Google',
                    url: 'http://google.com/',
                    start: '2021-03-28'
                }
            ]
        });

        calendar.render();
    });

</script>
<style>

</style>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>

<div class="schedule">
    <!-- section start -->
    <!-- top area start -->
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0518" text="발송일정관리"/></h1>
                <p>설명글을 적어주세요.</p>
            </div>
        </div>
    </div>
    <!-- top area end -->



    <div class="pd_l30" style="position: absolute;
    top: 80px;
    left: 195px;
    right: 0;
    bottom: 10px;
    overflow: hidden;
    overflow-y: scroll;
    overflow-x: hidden;
	padding-right:13px;" id="calendar">

    </div>
    <!-- section end -->
</div>