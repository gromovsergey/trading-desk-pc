<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript">
        var url = location.href.match(/url=(.+)/)[1];
        url = /^https?\%3A\%2F\%2F/i.test(url) ? decodeURIComponent(url) : url;
        setTimeout(function(){location.replace(url)}, 3000);
    </script>
    <style type="text/css">
        html, body {
            height: 100%;
        }
        body {
            margin: 0;
        }
        table {
            width:100%;
            height:100%;
            text-align:center;
            vertical-align:middle;
        }
        table td {
            padding:10%;
        }
    </style>
</head>
<body>
    <table>
        <tr>
            <td>
                <h2>Успешное перенаправление после клика. Это сообщение не будет показано при живых рекламных кликах. Вы будете перенаправлены на оригинальный Клик URL в течение 3-х секунд.</h2>
            </td>
        </tr>
    </table>
</body>
</html>
