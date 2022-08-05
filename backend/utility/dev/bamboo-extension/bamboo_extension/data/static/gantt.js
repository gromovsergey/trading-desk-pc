
if (!gantt) {
    var gantt = {
        gantt: function(tasks, task_names, min_date, max_date) {
            gantt.layout.data = gantt.utils.filter(tasks, task_names, min_date, max_date);
            gantt.render();
        },
        render: function(domain) {
            if ((!domain) || (!domain.length)) {
                domain = [gantt.layout.data.min_date, gantt.layout.data.max_date];
            }
            if (gantt.layout.data) {
                var diagram = d3.gantt("#chart")
                    .taskTypes(gantt.layout.data.names)
                    .taskStatus(gantt.layout.status)
                    .timeDomainMode("fixed")
                    .timeDomain(domain)
                    .height(gantt.layout.data.names.length * gantt.layout.height_multiplier + 35);

                diagram(gantt.layout.data.tasks);
            }
        },
        layout: {
            height_multiplier: 30,
            data: null,
            status: {
                "successful": "successful",
                "failed": "failed"
            }
        },
        utils: {
            filter: function(tasks, names, minDate, maxDate) {
                result = {tasks: [], names: {}}
                for (var index = 0; index < tasks.length; ++index) {
                    var task = tasks[index];
                    //if ((task.startDate <= maxDate) && (task.endDate >= minDate)) {
                        result.tasks.push(task);
                        result.names[task.taskName] = 1;
                    //}
                }
                var names = []
                for(var k in result.names) {
                    names.push(k);
                }
                result.names = names;
                result.min_date = minDate;
                result.max_date = maxDate;
                return result;
            },
        }
    }
}

$().ready(function() {
    common.progress.show(50);
    $.getJSON("/api/plan", function(data) {
        $("#select").removeClass("disabled");
        var names = [];
        var name2key = {};
        var key2name = {};
        for (var i = 0; i < data.data.length; ++i) {
            name2key[data.data[i].name] = data.data[i].key;
            key2name[data.data[i].key] = data.data[i].name;
            $("#left").append('<option title="' + data.data[i].name + '" value="' + data.data[i].key + '">' + data.data[i].name + '</option>');
        }

        $('#left option').sort(function(a, b){return (a.innerHTML > b.innerHTML) ? 1 : -1;}).appendTo('#left');

        $(".add").on("click", function() {
            $("#left option:selected").appendTo("#right");
        });

        $(".remove").on("click", function(){
            $("#right option:selected").appendTo("#left");
            $('#left option').sort(function(a, b){return (a.innerHTML > b.innerHTML) ? 1 : -1;}).appendTo('#left');
        });

        $("#render").on("click", function(){
            var tasks = [];
            var taskNames = [];
            var count = {count: $("#right option").size(), current:0, index:0};
            common.progress.show(0);
            $('#modal').modal("hide");
            $("#right option").each(function(){
                var key = $(this).val();
                var name = $(this).text();
                taskNames.push(name.substr(0, 30) + "...");
                $.getJSON("/api/plan/" + key + "/result", function(data) {
                    for (var index = 0; index < data.data.length; ++index) {
                        count.index += 1
                        var task = {
                            startDate: new Date(data.data[index].starttime),
                            endDate: new Date(data.data[index].stoptime),
                            taskName: name,
                            fullName: name,
                            taskLink: data.data[index].link,
                            status: data.data[index].state,
                            index: count.index
                        };
                        tasks.push(task);
                    }
                }).fail(common.fail)
                  .always(function() {
                        count.current++;
                        common.progress.update(count.current / count.count * 100);
                        if (count.current === count.count) {
                            var minDate = d3.time.hour.offset(new Date(),-24);
                            var maxDate = new Date();
                            gantt.gantt(tasks, taskNames, minDate, maxDate);
                        }
                  });
            });
        });

        $("#select").on("click", function(){
            $('#modal').modal();
        });

        $('#modal').modal();
    }).fail(common.fail)
      .always(function() {common.progress.update(100); });

    $(window).resize(function() {
        if(this.resizeTO) {
            clearTimeout(this.resizeTO);
        }
        this.resizeTO = setTimeout(function() {
            $(this).trigger('resizeEnd');
        }, 300);
    });

    $(window).bind('resizeEnd', function(e) {
        var width = $("#chart").width() + 160;
        gantt.render();
    });

});
