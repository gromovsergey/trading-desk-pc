<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>GeoLocation Map</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="http://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
    <script type="text/javascript">
        function querystring(key) {
            var re=new RegExp('(?:\\?|&)'+key+'=(.*?)(?=&|$)','gi');
            var r=[], m;
            while ((m=re.exec(document.location.search)) != null) r.push(m[1]);
            return r;
        }

        ymaps.ready(init);

        function init(){
            var lat = querystring('lat');
            var lon = querystring('lon');

            var myMap = new ymaps.Map("map", {
                center: [lat, lon],
                zoom: 12
            });

            var myPlacemark = new ymaps.Placemark([lat, lon], {
                hintContent: lat + ' ' + lon,
                iconContent: lat + ' ' + lon
            },  {preset: 'islands#blueStretchyIcon'});

            myMap.geoObjects.add(myPlacemark);
        }
    </script>
</head>

<body>
<div id="map" style="width: 800px; height: 600px"></div>
</body>

</html>