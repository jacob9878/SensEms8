am4core.useTheme(am4themes_animated);

var chart = am4core.create("chartdiv", am4charts.PieChart);

chart.data = [{
    "country": "Lithuania",
    "litres": 1.9
}, {
    "country": "Czech Republic",
    "litres": 2.9
}, {
    "country": "Ireland",
    "litres": 3.1
}, {
    "country": "Germany",
    "litres": 4.8
}, {
    "country": "Australia",
    "litres": 5.9
}, {
    "country": "Austria",
    "litres": 6.3
}, {
    "country": "UK",
    "litres": 7
}, {
    "country": "Belgium",
    "litres": 8
}, {
    "country": "The Netherlands",
    "litres": 9
}];

var series = chart.series.push(new am4charts.PieSeries());
series.dataFields.value = "litres";
series.dataFields.category = "country";

// this creates initial animation
series.hiddenState.properties.opacity = 1;
series.hiddenState.properties.endAngle = -90;
series.hiddenState.properties.startAngle = -90;

chart.legend = new am4charts.Legend();