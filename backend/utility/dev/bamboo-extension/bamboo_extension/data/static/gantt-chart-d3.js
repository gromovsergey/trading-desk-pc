/**
 * @author Dimitry Kudrayvtsev
 * @version 1.0
 */

d3.gantt = function(root) {
    
    var margin = {
	top : 20,
	right : 40,
	bottom : 20,
	left : 40
    };
    var timeDomainStart = d3.time.day.offset(new Date(),-3);
    var timeDomainEnd = d3.time.hour.offset(new Date(),+3);
    var timeDomainMode = "fit";// fixed or fit
    var taskTypes = [];
    var taskStatus = [];
    var height = document.body.clientHeight - margin.top - margin.bottom-5;
    var width = document.body.clientWidth - margin.right - margin.left-5;
    
    function gantt(tasks) {
	
	if(timeDomainMode === "fit"){
    	    tasks.sort(function(a, b) {
		return a.endDate - b.endDate;
	    });
	     timeDomainEnd = tasks[tasks.length - 1].endDate;
	    tasks.sort(function(a, b) {
		return a.startDate - b.startDate;
	    });
	     timeDomainStart = tasks[0].startDate;
	}
	
    var x = null;
	
	var domain_x = d3.time.scale()
        .domain([ timeDomainStart, timeDomainEnd ])
        .range([ 0, width])
        .clamp(false);	

    var domain_y = d3.scale.ordinal()
        .domain(taskTypes)
        .rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);;

    var updateTimeDomain = function(min_date, max_date) {
        x = d3.time.scale()
            .domain([ min_date, max_date ])
            .range([ 0, width])
            .clamp(true);
    }

    updateTimeDomain(timeDomainStart, timeDomainEnd);

    var renderAxises = function(min_date, max_date) {
        var tickFormat = '%H:%M';
        var tickInterval = Math.round(Date.minutesBetween(min_date, max_date) * 50 / width);
        var tickMeasure = d3.time.minutes;
        if ((60 % tickInterval !== 0) && (tickInterval < 60)) {
            tickInterval += Math.floor((60 % tickInterval) / Math.floor(60 / tickInterval));
        } else if (tickInterval > 60) {
            tickInterval = Math.ceil(tickInterval / 60);
            tickMeasure = d3.time.hours;
            if ((24 % tickInterval !== 0) && (tickInterval < 24)) {
                tickInterval += Math.floor((24 % tickInterval) / Math.floor(24 / tickInterval));
            }
            if (tickInterval >= 24) {
                tickInterval = Math.ceil(tickInterval / 24);
                tickMeasure = d3.time.days;
                tickFormat = '%d.%m';
            }
        }

        var xAxis = d3.svg.axis().scale(x)
            .orient('bottom')
            .ticks(tickMeasure, tickInterval)
            .tickFormat(d3.time.format(tickFormat))
            .tickSize(-height)
            .tickPadding(8);

               
        var yAxis = d3.svg.axis().scale(domain_y)
            .orient("right");

        var svg = d3.select(".tasks");
        svg.selectAll(".axis").remove();

        var gxAxis = svg.append('g')
            .attr('class', 'x axis')
            .attr('transform', 'translate(0, ' + (height - margin.top - margin.bottom) + ')')
            .call(xAxis);

        if (tickMeasure !== d3.time.days) {
            var dxAxis = d3.svg.axis().scale(x)
                .orient('bottom')
                .ticks(d3.time.days, 1)
                .tickFormat(d3.time.format("%d.%m"))
                .tickSize(18)
                .tickPadding(12);

            var dgxAxis = svg.append('g')
                .attr('class', 'x axis')
                .attr('transform', 'translate(0, ' + (height - margin.top - margin.bottom) + ')')
                .call(dxAxis);
        }
     
        var gyAxis = svg.append('g').attr('class', 'y axis').call(yAxis);
    }

    var redraw = function() {
        translate=d3.event.translate;
        scale=d3.event.scale;
        var min_date = domain_x.invert(-translate[0] / scale);
        var max_date = domain_x.invert((width - translate[0]) / scale); 
        updateTimeDomain(min_date, max_date);
        renderAxises(min_date, max_date);
        d3.selectAll(".task").attr("transform", function(d) {
            return "translate(" + x(d.startDate) + "," + domain_y(d.taskName) + ")";
        }).attr('width', function(d) { 
            return (x(d.endDate) - x(d.startDate)); 
        });
    }
    

	var svg = d3.select(root)
    .html("")
	.append('svg')
	.attr('class', 'chart')
	.attr('width', width + margin.left + margin.right)
	.attr('height', height + margin.top + margin.bottom)
	.append('g')
    .call(d3.behavior.zoom().on("zoom", redraw))
    .attr("class", "tasks")
	.attr('width', width + margin.left + margin.right)
	.attr('height', height + margin.top + margin.bottom)
	.attr('transform', 'translate(' + margin.left + ', ' + margin.top + ')');

    var tooltip = d3.select(root).append("div").attr("class", "gantt-tooltip");

    var index = {i:0};
	var chart = svg.selectAll('.chart')
	 .data(tasks).enter()
	 .append('rect')
	 .attr("rx", 5)
         .attr("ry", 5)
	 .attr('class', function(d){ 
	     if(taskStatus[d.status] == null){ return 'task bar';}
	     return "task " + taskStatus[d.status];
	     }) 
	 .attr("y", 0)
	 .attr("transform", function(d) { return "translate(" + x(d.startDate) + "," + domain_y(d.taskName) + ")"; })
	 .attr('height', function(d) { return domain_y.rangeBand(); })
	 .attr('width', function(d) { 
	     return (x(d.endDate) - x(d.startDate)); 
	  })
     .on("mouseover", function(d) {
        tooltip.transition()        
            .duration(200)      
            .style("opacity", .9);      
        tooltip.html(d.fullName + '<br/>' + d.status + '<br/>' + Date.minutesBetween(d.startDate, d.endDate) + ' min')  
            .style("left", (d3.event.pageX) + "px")     
            .style("top", (d3.event.pageY - 28) + "px"); 
      })
     .on("mouseout", function(d){
        tooltip.transition()        
            .duration(500)      
            .style("opacity", 0);
      })
     .on("click", function(d){
        window.open(d.taskLink, "_blank");
      })
     .sort(function(a,b){return (a.index < b.index) ? 1 : -1;})
     .each(function(d) { 
        index.i += 1;
        if (!index[d.taskName]) {
            index[d.taskName] = 1;
            d3.select(".tasks")
                .insert('rect', ':first-child')
                .attr("class", "odd-even")
                .attr("y", domain_y(d.taskName))
                .attr("x", 0)
                .attr("height", domain_y.rangeBand())
                .attr("width", width);
        }
     });

     renderAxises(timeDomainStart, timeDomainEnd);

    };
    
    
    gantt.margin = function(value) {
	if (!arguments.length)
	    return margin;
	margin = +value;
	return gantt;
    };

    gantt.timeDomain = function(value) {
	if (!arguments.length)
	    return [ timeDomainStart, timeDomainEnd ];
	timeDomainStart = value[0], timeDomainEnd = value[1];
	return gantt;
    };

    gantt.timeDomainMode = function(value) {
	if (!arguments.length)
	    return timeDomainMode;
        timeDomainMode = value;
        return gantt;

    };

    gantt.taskTypes = function(value) {
	if (!arguments.length)
	    return taskTypes;
	taskTypes = value;
	return gantt;
    };
    
    gantt.taskStatus = function(value) {
	if (!arguments.length)
	    return taskStatus;
	taskStatus = value;
	return gantt;
    };

    gantt.width = function(value) {
	if (!arguments.length)
	    return width;
	width = +value;
	return gantt;
    };

    gantt.height = function(value) {
	if (!arguments.length)
	    return height;
	height = +value;
	return gantt;
    };
    
    return gantt;
};
