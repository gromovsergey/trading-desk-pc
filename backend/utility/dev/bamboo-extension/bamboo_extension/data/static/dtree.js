if (!dtree) {
    var i = 0;
    var dtree = {
        dtree: function(key) {
            dtree.load(key);
        },
        layout: {
            height: 1700,
            width: 1000,
            root: null,
            duration: 500,
            height_multiplier: 70
        },
        children: function(key, data, callback) {
            d3.json("/api/plan/" + key + "/children", function(error, json) {
                if (error) {
                    common.http_error(error.status, JSON.parse(error.response).data);
                    return;
                }
                callback(json.data.key, json.data.children, data);
            });
        },
        load: function(key) {
            common.progress.show(50);
            var result = {};
            var queue = {};

            function loadtree(data) {
                common.progress.update(100);
                dtree.layout.root  = data;

                var width = $("#chart").width() + 160;
                var height = dtree.utils.tree_depth(dtree.layout.root) * dtree.layout.height_multiplier + 160;

                dtree.layout.width = width;
                dtree.layout.height = height;

                dtree.view();
                dtree.update(root = data);
            }

            function callback(key, children, data) {
                data.key = key;
                data.id = key;
                data.children = [];
                for (var i in children) {
                    queue[children[i]] = {};
                    data.children.push({});
                    dtree.children(children[i], data.children[data.children.length - 1], callback);

                }
                $.getJSON("/api/plan/" + key, function(value) {
                    data.name = value.data.name;
                    data.short_name = value.data.short_name;

                    delete queue[key];
                    if (Object.keys(queue).length == 0) {
                        loadtree(result);
                    }
                });
            }

            queue[key] = {};
            dtree.children(key, result, callback);
        },
        utils: {
            position: function(node) {
                return [node.x, node.y];
            },
            tree_depth: function(node) {
                depth = 1;
                child_depth = 0;
                for (var i = 0; i < node.children.length; ++i) {
                    child_depth = Math.max(child_depth, dtree.utils.tree_depth(node.children[i]));
                }
                return depth + child_depth;
            },
            /*node_count: function(node) {
                var count = 1;
                for (var i = 0; i < node.children.length; ++i) {
                    count += dtree.utils.node_count(node.children[i]);
                }
                return count;
            },*/
            wordwrap: function(str, width, brk, cut) {
                brk = brk || '\n';
                width = width || 75;
                cut = cut || false;

                if (!str) { return str; }

                var regex = '.{1,' +width+ '}(\\s|$)' + (cut ? '|.{' +width+ '}|.+$' : '|\\S+?(\\s|$)');

                return str.match( RegExp(regex, 'g') ).join( brk );
            },
            diagonal: d3.svg.diagonal().projection(function(d) {
                return dtree.utils.position(d);
            }),
            tree: null,
            vis: null
        },
        view: function() {
            dtree.utils.tree = d3.layout.tree().size([dtree.layout.width - 160, dtree.layout.height - 160]);
            dtree.utils.vis = d3.select("#chart").html("").append("svg:svg")
                .attr("width", dtree.layout.width)
                .attr("height", dtree.layout.height)
                .append("svg:g")
                .attr("transform", "translate(0,20)")
                .style("overflow", "visible");
            d3.select(self.frameElement).style("height", dtree.layout.height + "px");
        },
        update: function(source) {
            var nodes = dtree.utils.tree.nodes(dtree.layout.root).reverse();
            var node = dtree.utils.vis.selectAll("g.node")
                .data(nodes, function(d) { return d.id || (d.id = ++i); });
            var nodeEnter = node.enter().append("svg:g")
                .attr("class", "node")
                .attr("transform", function(d) {
                    return "translate(" + source.x0 + "," + source.y0 + ")";
                });
            nodeEnter.append("svg:circle")
                .attr("r", 5)
                .on("dblclick", dtree.node_dblclick);
            nodeEnter.append("svg:text")
                .attr("x", 8)
                .attr("y", -5).each(function(d, i) {
                    var parts = dtree.utils.wordwrap(d.name, 17, "#").split("#");
                    var textNode = d3.select(this);
                    for (var j = 0; j < parts.length; ++j) {
                        textNode.append("svg:tspan").attr("x", 8).attr("dy", 9).text(parts[j]);
                    }
                });
            nodeEnter.append("svg:text").attr("x", 8).attr("y", -7).classed("relative", true).text("");
            nodeEnter.transition()
                .duration(dtree.layout.duration)
                .attr("transform", function(d) {
                    var pos = dtree.utils.position(d);
                    return "translate(" + pos[0] + "," + pos[1] + ")";
                })
                .style("opacity", 1);
            node.transition()
                .duration(dtree.layout.duration)
                .attr("transform", function(d) {
                    var pos = dtree.utils.position(d);
                    return "translate(" + pos[0] + "," + pos[1] + ")";
                })
                .style("opacity", 1);
            node.exit().transition()
                .duration(dtree.layout.duration)
                .attr("transform", function(d) {
                    return "translate(" + source.x + "," + source.y + ")";
                })
                .style("opacity", 1e-6)
                .remove();

            var link = dtree.utils.vis.selectAll("path.link")
                .data(dtree.utils.tree.links(nodes), function(d) {
                    return d.target.id;
                });
            link.enter().insert("svg:path", "g")
                .attr("class", "link")
                .attr("d", function(d) {
                    var o = {x: source.x0, y: source.y0};
                    return dtree.utils.diagonal({source: o, target: o});
                })
                .transition()
                .duration(dtree.layout.duration)
                .attr("d", dtree.utils.diagonal);
            link.transition()
                .duration(dtree.layout.duration)
                .attr("d", dtree.utils.diagonal);
            link.exit().transition()
                .duration(dtree.layout.duration)
                .attr("d", function(d) {
                    var o = {x: source.x, y: source.y};
                    return dtree.utils.diagonal({source: o, target: o});
                })
                .remove();

            nodes.forEach(function(d) {
                d.x0 = d.x;
                d.y0 = d.y;
            });

            dtree.update_state()
        },
        node_dblclick: function(node) {
            if (node.children) {
                node._children = node.children;
                node.children = null;
            } else {
                node.children = node._children;
                node._children = null;
            }
            dtree.update(node);
        },
        node_click: function(node) {
            window.open(node.data.link, "_blank");
        },
        update_state: function() {
            if (!dtree.utils.vis) {
                return
            }
            dtree.layout.current_count = 0;
            dtree.utils.vis.selectAll("g.node").each(function(d) {
                var node = d3.select(this);
                common.progress.show(0);
                $.getJSON("/api/plan/" + d.id + "/status", function(data) {
                    if (data.data.is_building) {
                        node.select("circle").attr("class", "building");
                    }
                    else if (data.data.is_active) {
                        node.select("circle").attr("class", "queued");
                    }
                    else if (data.data.state === "successful") {
                        node.select("circle").attr("class", "successful");
                    }
                    else if (data.data.state === "failed") {
                        node.select("circle").attr("class", "failed");
                    }
                    else if (!data.data.enabled) {
                        node.select("circle").attr("class", "");
                    }
                    if (data.data.hours_ago > 1) {
                        node.select("text.relative").text(data.data.hours_ago + " hours ago");
                    }
                    else {
                        node.select("text.relative").text();
                    }
                    var minutes_ago = Date.minutesBetween(new Date(data.data.starttime), new Date());
                    var opacity = 1.0;
                    node.select("text.relative").text(Math.round(minutes_ago) + " minutes ago");
                    if (minutes_ago > 60) {
                        var opacity = (96 - (minutes_ago / 60)) / 72;
                        node.select("text.relative").text(Math.round(minutes_ago / 60) + " hours ago");
                    }
                    if (minutes_ago > 1440) {
                        node.select("text.relative").text(Math.round(minutes_ago / 1440) + " days ago");
                    }
                    if (opacity < 0.2) {
                        opacity = 0.2;
                    }
                    node.style("opacity", opacity).on("mouseover", function(e) {
                        d3.select(this).style("opacity", 1.0);
                    }).on("mouseout", function(e){
                        d3.select(this).style("opacity", opacity);
                    });
                    node.select("text").on("click", function(e) {
                        window.open(data.data.link, "_blank");
                    }).style("cursor", "pointer");
                }).fail(common.fail)
                  .always(function() {
                    dtree.layout.current_count++;
                    common.progress.update(dtree.layout.current_count / dtree.utils.vis.selectAll("circle")[0].length * 100); });
            });
        }
    }
}

$().ready(function(){
    common.progress.show(50);
    $.getJSON("/api/plan", function(data) {
        var names = [];
        var name2key = {};
        var key2name = {};
        for (var i = 0; i < data.data.length; ++i) {
            names.push(data.data[i].name);
            name2key[data.data[i].name] = data.data[i].key;
            key2name[data.data[i].key] = data.data[i].name;
            $("#left").append('<option title="' + data.data[i].name + '" value="' + data.data[i].key + '">' + data.data[i].name + '</option>');
        }

        $('#left option').sort(function(a, b){return (a.innerHTML > b.innerHTML) ? 1 : -1;}).appendTo('#left');

	$('#render').on("click", function(){
	    var key = $("#left option:selected").val();
	    if (key) {
		$('#modal').modal("hide");
	        dtree.dtree(key);
		window.location.hash = "#" + key;
	    }
	    return false;
	});

	if (window.location.hash) {
	    var key = window.location.hash.substr(1);
	    if (key2name[key]) {
		dtree.dtree(key);
	    }
	    else {
		window.location.hash = "";
	    }
	}
	else {
            $('#modal').modal();
	}

    }).fail(common.fail)
      .always(function() {
        if (window.location.hash) {
            common.progress.update(75)
        }
        else {
            common.progress.update(100)
        };
    });

    function sec() {
        if (dtree.layout.root) {
            dtree.update_state();
        }
    }
    setInterval(sec, 60000);

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
        if (dtree.layout.root) {
            dtree.layout.width = width;
            dtree.view();
            dtree.update(dtree.layout.root);
        }
    });
});
