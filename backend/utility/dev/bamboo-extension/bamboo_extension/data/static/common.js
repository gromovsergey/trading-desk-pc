Date.minutesBetween = function(date1, date2) {
  var minute=1000 * 60;

  var date1_ms = date1.getTime();
  var date2_ms = date2.getTime();
  var difference_ms = date2_ms - date1_ms;

  return Math.round(difference_ms/minute);
}

if (!common) {
    var common = {
        error: function (title, message) {
            $("#error_list").append('<div class="alert alert-warning alert-dismissable"></div>').children().last()
                .append('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>')
                .append('<strong>' + title + '</strong> ' + message);
        },
        http_error: function (status_code, message) {
            common.error("Status " + status_code, message);
        },

        fail: function(data) {
            var answer = JSON.parse(data.responseText);
            if (answer.redirect) {
                window.location.pathname = answer.redirect;
            }
            else {
                common.http_error(data.status, answer.data);
 	    }
        },
        progress: {
            update: function(persentage) {
                if (persentage >= 100) {
                    common.progress.hide();
                }
                else {
                    $("#progress > .progress-bar").css("width", persentage + "%");
                    $("#progress > .progress-bar").text("completed " + Math.round(persentage) + "%")
                }
            },
            show: function(persentage) {
                $("#progress > .progress-bar").css("width", persentage + "%");
                $("#progress > .progress-bar").text("completed " + Math.round(persentage) + "%")
                $("#nav-progress").removeClass("hide");
            },
            hide: function() {
                if (!$("#nav-progress").hasClass("hide")) {
                    $("#nav-progress").addClass("hide");
                }
            }
        }
    }
}
