/**
 * @class
 */

Widget = Class.extend({
    _eventListeners : null,
    /** @type {Function} */

    /**
     * @constructor
     * @param {Function} [createDisplayClbk] Callback for inner system usage. Creating display.
     * Used only with inheritance operations.
     */
    init : function(createDisplayClbk){
        this._eventListeners = {};
        this._createEvents();
        createDisplayClbk && createDisplayClbk();
    },
    /**
     * @return {jQuery}
     */
    _createDisplay : function(){
        return $();
    },
    /**
     * @param {String} eventName
     * @param {Function} listener
     */
    addEventListener : function(eventName, listener) {
        if (!this._eventListeners[eventName]) {
            this._eventListeners[eventName] = [];
        }
        this._eventListeners[eventName].push(listener);
    },
    /**
     * Call all binded event listeners
     * @param {String} eventName
     * @param {Array} [args]
     */
    _onEvent : function(eventName, args){
        var listeners = this._eventListeners[eventName] || [];

        for (var i = 0; i < listeners.length; i++) {
            var res = listeners[i].apply(this, args);
            if(res === false) return false;
        }
        return true;
    },
    _createEvents : function(){
        var curr = this;
        for (var fieldName in this) {
            if(/(^event_)/.test(fieldName)){
                (function(evName){
                    var currEvent = curr[evName];
                    curr[evName] = function(){
                        // converting arguments to array
                        var args = Array.prototype.slice.call(arguments);

                        if(curr._onEvent(evName, args)){
                            // if no one of binded event listeners returned false
                            return currEvent.apply(curr, args);
                        }
                    };
                })(fieldName)
            }
        }
    }
});





/** @class */

TimeRange = Widget.extend({
    /** @type {jQuery} */
    _jContainer : null,
    /** @type {TimeRange.Model} */
    _model : null,
    /** @type {TimeRange.Canvas} */
    _view : null,

    /**
     * @constructor
     * @param {jQuery} jContainer
     *
     * @param {Object} [opts] Optional parameters of TimeRange.
     * @param {String[]} opts.rowNames Names of TimeRange rows. It can be any length.
     * @param {String[]} opts.phaseNames Names of phases in a timeline of a TimeRange. It can be any length.
     * @param {Number} opts.cellsInRow Number of cells in every row
     * @param {Number} opts.beginTime Time in milliseconds of beginning of time span of TimeRange
     * @param {Number} opts.endTime Time in milliseconds of ending of time span of TimeRange
     * @param {Object[]} opts.occupiedRanges Objects, that presents structure of already occupied ranges.
     *          Every "range" object must have next structure:
     *          {
     *              begin : number of time in milliseconds
     *              end : number of time in milliseconds
     *          }
     * @param {Object[]} opts.readonlyRanges Objects, that presents structure of blocked ranges.
     *          Has same structure as opts.occupiedRanges
     * @param {Function} [createDisplayCallback] Callback for inner system usage. Creating display.
     *         Used only with inheritance operations.
     */
    init: function(jContainer, opts, createDisplayCallback){
        this._jContainer = jContainer;
        var curr = this;
        var curDate = new Date(); // current date
        var afterWeekDate = new Date(curDate.getTime());
        afterWeekDate.setDate(afterWeekDate.getDate() + 7);

        opts = $.extend({
            readonly : false,
            beginTime : curDate.getTime(),
            endTime : afterWeekDate.getTime(),
            rowNames : ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            phaseNames : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
            cellsInRow : 48,
            occupiedRanges : [],
            readonlyRanges : [],
            editableRanges : []
        }, opts);

        this._super(createDisplayCallback || function(){curr._createDisplay(opts)});
    },
    event_onChange : function(){},

    _paintCell : function(idx, toFill){
        this._model.resolveCell(idx);
        var cellModel = this._model[toFill ? 'fillCell' : 'cleanCell'](idx);
        this._view.updateCell(cellModel);
    },
    _createDisplay : function(opts){
        var curr = this;
        this._model = new TimeRange.Model(opts.beginTime, opts.endTime, opts.occupiedRanges, opts.readonlyRanges, opts.editableRanges, opts.cellsInRow * opts.rowNames.length);
        var modelData = this._model.getData();
        this._view = new TimeRange.NeatPainter(this._jContainer, opts.rowNames, opts.phaseNames, modelData.cells, opts.readonly);
        this._view.addEventListener('event_onChange', function(){return curr.event_onChange()});
        this._view.addEventListener('event_onPaintCell', function(idx, toFill){return curr._paintCell(idx, toFill)});
    },
    refresh : function(){
        this._view.update();
    },
    /**
     * @return {TimeRange.Model.Range[]}
     */
    getOccupiedRanges : function() {
        return this._model.getOccupiedRanges();
    }
});



/**
 * @class
 * @augments Widget
 */

TimeRange.Canvas = Widget.extend(
    {
        /** @type {TimeRange.Canvas.Cell[]} */
        _cells : null,
        /** @type {jQuery} */
        _canvas : null,

        /**
         * @param {jQuery} jParent
         * @param {string[]} rowNames
         * @param {string[]} phaseNames
         * @param {TimeRange.Model.CellModel[]} modelCells
         */
        init: function(jParent, rowNames, phaseNames, modelCells){
            this._cells = [];
            var curr = this;
            var createDisplayClbk = function(){curr._createDisplay(jParent, rowNames, phaseNames, modelCells)};
            this._super(createDisplayClbk);
        },
        event_onChange : function(){},

        _setHandlers : function(){
            var curr = this;

            this._canvas.mouseup(function(){
                curr.event_onChange();
            });
        },
        _createDisplay : function(jParent, rowNames, phaseNames, modelCells){
            this._canvas = $('<div/>')
                .addClass('timeRange')
                .appendTo(jParent);

            var rowsData = this._generateRows(modelCells, rowNames, phaseNames);
            this._generateCols(phaseNames, rowsData.rowWidth);
            this._setHandlers();
        },
        /**
         * @param {TimeRange.Model.CellModel[]} modelCells
         * @param {String[]} rowNames
         * @param {String[]} phases
         */
        _generateRows : function(modelCells, rowNames, phases) {
            var cellsInRow = modelCells.length / rowNames.length;
            var toColorCells = !(cellsInRow % phases.length);
            var cellsInCol = toColorCells ? cellsInRow / phases.length : 0;
            var row = null;
            /** @type {TimeRange.Canvas.Cell} */
            var cell = null;
            /** @type {TimeRange.Model.CellModel} */
            var modelCell = null;
            var cssClass = '';

            for (var i = 0; i < modelCells.length; i++) {
                modelCell = modelCells[i];
                if (!(i % cellsInRow)) {
                    row = $('<div class="cellsRow"/>')
                        .append('<div class="rowLabel">' + rowNames[Math.floor(i / cellsInRow)] + '</div>')
                        .appendTo(this._canvas);
                }
                if (toColorCells) {
                    cssClass = !(Math.floor(i / cellsInCol) % 2) ? 'evenZone' : '';
                }
                cell = this._appendCell(row, i, modelCell.getOccupied(), modelCell.getReadonly(), modelCell.getConflicted(), cssClass);
                this._cells.push(cell);
            }

            // setting width of row
            var cellWidth = this._getWidth($('.cell:eq(0)', row));
            var rowWidth = cellWidth * cellsInRow;
            var labelWidth = this._getWidth($('.rowLabel:eq(0)', row));
            $('.cellsRow', this._canvas).append('<div class="fixing"/>')
                .width(rowWidth + labelWidth);

            return {rowWidth : rowWidth};
        },
        /**
         * @param {jQuery} jObj
         */
        _getWidth : function(jObj){
            return (parseFloat(jObj.css('width')) || 0) + 
                (parseFloat(jObj.css('borderLeftWidth')) || 0) + 
                (parseFloat(jObj.css('borderRightWidth')) || 0) + 
                (parseFloat(jObj.css('marginLeft')) || 0) + 
                (parseFloat(jObj.css('marginRight')) || 0);
        },
        _generateCols : function(phases, rowWidth) {
            var jTimeZones = $('<div class="phases"/>').prependTo(this._canvas);
            for (var i = 0; i < phases.length; i++) {
                $('<div class="phase">')
                    .appendTo(jTimeZones)
                    .text(phases[i])
                    .addClass(i % 2 ? 'evenZone' : '');
            }
            jTimeZones.append('<div class="fixing"/>');
            $('.phase', jTimeZones).width(rowWidth / phases.length);
        },
        _appendCell : function(jParent, idx, isOccupied, isBlocked, isConflicted, cssClass){
            return new TimeRange.Canvas.Cell(jParent, idx, isOccupied, isBlocked, isConflicted, cssClass);
        },
        /**
         * @param {TimeRange.Model.CellModel} cellModel
         */
        updateCell : function(cellModel){
            /** @type {TimeRange.Canvas.Cell} */
            var cell = this._cells[cellModel.getIdx()];

            cell.update(cellModel.getOccupied(), cellModel.getReadonly(), cellModel.getConflicted());
        }
    }
);




/**
 * @class
 * @augments TimeRange.Canvas
 */

TimeRange.Painter = TimeRange.Canvas.extend(
    {
        _paintMethod : 0,
        _mousePressed : false,
        _lastPaintedIdx : 0,
        _readonly : false,

        init : function(jParent, rowNames, phaseNames, modelCells, readonly){
            this._readonly = readonly;
            this._super(jParent, rowNames, phaseNames, modelCells);
        },
        event_onPaintCell : function(idx, toFill){},
        event_onMouseEnter : function(e, idx) {
            this._mousePressed && this._paintCell(idx);
        },
        event_onCellDown : function(e, idx, shiftPressed) {
            this._mousePressed = true;
            var cell = this._cells[idx];
            var toFill = !cell.getOccupied();

            if (!shiftPressed) {
                this._paintMethod = this._static.PaintMethods[toFill ? 'fill' : 'clean'];
                this._paintCell(idx);
            } else {
                this._shiftPaint(idx);
            }
        },

        _setHandlers : function() {
            var curr = this;

            this._canvas.mousedown(function(){
                curr._mousePressed = true;
                return false;
            });

            if(this._readonly) return false;

            for (var i = 0; i < this._cells.length; i++) {
                var cell = this._cells[i];
                cell.addEventListener('event_onCellDown', function(e, idx, shiftPressed) {
                    return curr.event_onCellDown(e, idx, shiftPressed)
                });
                cell.addEventListener('event_onMouseEnter', function(e, idx) {
                    return curr.event_onMouseEnter(e, idx)
                });
            }

            $(document).mouseup(function(){
                if(curr._mousePressed){
                    curr.event_onChange();
                }
                curr._mousePressed = false;
            });
        },
        _paintCell : function(idx){
            this._lastPaintedIdx = idx;
            if (this._paintMethod == this._static.PaintMethods.fill) {
                this.event_onPaintCell(idx, true);
            } else if (this._paintMethod == this._static.PaintMethods.clean) {
                this.event_onPaintCell(idx, false);
            }
        },
        _shiftPaint : function(idx){
            var min = Math.min(this._lastPaintedIdx, idx);
            var max = Math.max(this._lastPaintedIdx, idx);
            for (var i = min; i <= max; i++) {
                this._paintCell(i);
            }
        }
    },

    // ----------------- Static Fields -------------------------------------------------------- //

    {
        PaintMethods : {
            none : 0,
            fill : 1,
            clean : 2
        }
    }
);




/**
 * @class
 * @augments TimeRange.Painter
 */

TimeRange.NeatPainter = TimeRange.Painter.extend(
    {
        _lastEnteredIdx : null,
        _lastCursorPos : null,
        _canvasOffset : null,

        event_onMouseEnter : function(e, idx) {
            if(!this._mousePressed) return;

            this._paintCell(idx);

            var cursorOffset = this._getCursorOffset(e.pageX, e.pageY);
            var cursorPos = new TimeRange.NeatPainter.Point(cursorOffset.left, cursorOffset.top);

            if(this._lastCursorPos){
                var cursorTrack = new TimeRange.NeatPainter.Line(this._lastCursorPos, cursorPos);
                this._paintCellsBetween(idx, cursorTrack);
            }

            this._lastCursorPos = cursorPos;
            this._lastEnteredIdx = idx;
        },
        event_onCellDown : function(e, idx, shiftPressed){
            this._canvasOffset = this._canvas.offset();
            var cursorOffset = this._getCursorOffset(e.pageX, e.pageY);

            this._lastEnteredIdx = idx;
            this._lastCursorPos = new TimeRange.NeatPainter.Point(cursorOffset.left, cursorOffset.top);
            this._super(e, idx, shiftPressed);
        },

        _createDisplay : function(jParent, rowNames, phaseNames, modelCells){
            this._super(jParent, rowNames, phaseNames, modelCells);
            this.update();
        },
        update : function(){
            this._canvasOffset = this._canvas.offset();
            for (var i = 0; i < this._cells.length; i++) {
                this._cells[i].updatePos(this._canvasOffset);
            }
        },
        _getCursorOffset : function(cursorLeft, cursorTop){
            return {
                left : cursorLeft - this._canvasOffset.left,
                top : cursorTop - this._canvasOffset.top
            }
        },
        _paintCellsBetween : function(currIdx, cursorTrack){
            var minIdx = Math.min(this._lastEnteredIdx, currIdx);
            var maxIdx = Math.max(this._lastEnteredIdx, currIdx);

            for (var i = minIdx + 1; i < maxIdx; i++) {
                if(this._cells[i].isCrossedThrough(cursorTrack)){
                    this._paintCell(i);
                }
            }
        },
        _appendCell : function(jParent, idx, isOccupied, isBlocked, isConflicted, cssClass){
            return new TimeRange.Canvas.AdvancedCell(jParent, idx, isOccupied, isBlocked, isConflicted, cssClass);
        }
    },

    // ----------------- Static Fields -------------------------------------------------------- //

    {
        /**
         * @param {Number} x
         * @param {Number} y
         */
        Point : function(x, y){
            this.x = x;
            this.y = y;
        },

        /**
         * @param {Point} start
         * @param {Point} end
         */
        Line : function(start, end){
            this.start = start;
            this.end = end;
        }
    }
);





/**
 * @class
 * @augments Widget
 */

TimeRange.Canvas.Cell = Widget.extend({
    _IDX : 0,
    _occupied : false,
    _readonly : false,
    _conflicted : false,
    /** @type {jQuery} */
    _jDisplay : null,

    /**
     * @constructor
     * @param {jQuery} jParent
     * @param {Number} idx
     * @param {Boolean} occupied
     * @param {String} cssClass
     */
    init: function(jParent, idx, occupied, readonly, conflicted, cssClass){
        this._IDX = idx;
        this._occupied = occupied;
        this._readonly = readonly;
        this._conflicted = conflicted;

        var curr = this;
        var createDisplayClbk = function(){curr._createDisplay(jParent, cssClass)};
        this._super(createDisplayClbk);
    },
    event_onCellDown : function(e, idx, shiftPressed){},
    event_onMouseEnter : function(e, idx){},
    /**
     * @param {String} cssClass
     * @return {jQuery}
     */
    _createDisplay : function(jParent, cssClass){
        var curr = this;
        this._jDisplay = $('<div class="cell"/>')
            .appendTo(jParent)
            .addClass(cssClass)
            .addClass(this._occupied ? 'occupied' : 'empty')
            .addClass(this._readonly ? 'readonly' : '')
            .addClass(this._conflicted ? 'conflicted' : '')
            .mouseenter(function(e){
                curr.event_onMouseEnter(e, curr._IDX);
            })
            .mousedown(function(e) {
                curr.event_onCellDown(e, curr._IDX, e.shiftKey);
            });
        return this._jDisplay;
    },
    getIdx : function() {
        return this._IDX
    },
    fill : function() {
        this._occupied = true;
        this._jDisplay
            .removeClass('empty')
            .addClass('occupied');
    },
    clean : function() {
        this._occupied = false;
        this._jDisplay
            .removeClass('occupied')
            .addClass('empty');
    },
    _setReadonly : function(readonly){
        this._readonly = readonly;
        this._jDisplay[readonly ? 'addClass' : 'removeClass']('readonly');
    },
    _setConflicted : function(conflicted){
        this._conflicted = conflicted;
        this._jDisplay[conflicted ? 'addClass' : 'removeClass']('conflicted');
    },
    getOccupied : function(){
        return this._occupied;
    },
    update : function(occupied, readonly, conflicted){
        occupied ? this.fill() : this.clean();
        this._setReadonly(readonly);
        this._setConflicted(conflicted);
    }
});




TimeRange.Canvas.AdvancedCell = TimeRange.Canvas.Cell.extend(
    {
        /** @type {TimeRange.NeatPainter.Line[]} */
        _shapeLines : null,
        /**
         * @param {TimeRange.NeatPainter.Line} cursorTrack
         */
        isCrossedThrough : function(cursorTrack){
            var shapeLines = this._shapeLines;

            for (var i = 0, line; i < shapeLines.length; i++) {
                line = shapeLines[i];
                if(this._static._isLinesCrossed(cursorTrack, line)){
                    return true;
                }
            }
            return false;
        },
        updatePos : function(canvasOffset){
            this._shapeLines = this._getShapeLines(canvasOffset);
        },
        _getShapeLines : function(canvasOffset){
            var canvasLeft = canvasOffset.left;
            var canvasTop = canvasOffset.top;

            var cellOffset = this._jDisplay.offset();

            var cellLeft = cellOffset.left - canvasLeft;
            var cellRight = cellLeft + this._jDisplay.width();
            var cellTop = cellOffset.top - canvasTop;
            var cellBottom = cellTop + this._jDisplay.height();

            var leftTop = new TimeRange.NeatPainter.Point(cellLeft, cellTop);
            var rightTop = new TimeRange.NeatPainter.Point(cellRight, cellTop);
            var leftBottom = new TimeRange.NeatPainter.Point(cellLeft, cellBottom);
            var rightBottom = new TimeRange.NeatPainter.Point(cellRight, cellBottom);

            return [
                new TimeRange.NeatPainter.Line(leftTop, rightBottom),
                new TimeRange.NeatPainter.Line(rightTop, leftBottom)
            ]
        }
    },

    // ----------------- Static Fields -------------------------------------------------------- //

    {
        /**
         * @param {TimeRange.NeatPainter.Line} line1
         * @param {TimeRange.NeatPainter.Line} line2
         */
        _isLinesCrossed : function(line1, line2){
            var start1 = line1.start;
            var start2 = line2.start;
            var end1 = line1.end;
            var end2 = line2.end;

            var ua_t = (end2.x - start2.x) * (start1.y - start2.y) - (end2.y - start2.y) * (start1.x - start2.x);
            var ub_t = (end1.x - start1.x) * (start1.y - start2.y) - (end1.y - start1.y) * (start1.x - start2.x);
            var u_b  = (end2.y - start2.y) * (end1.x - start1.x) - (end2.x - start2.x) * (end1.y - start1.y);

            if ( u_b != 0 ) {
                var ua = ua_t / u_b;
                var ub = ub_t / u_b;

                if ( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
                    return true;
                }
            } else if (ua_t == 0 || ub_t == 0) {
                return true;
            }

            return false;
        }
    }
);




/** @class */

TimeRange.Model = Class.extend({
    /** @type {TimeRange.Model.CellModel[]} */
    _cells : null,
    /** @type {TimeRange.Model.Range[]} */
    _occupiedRanges : null,
    /** @type {TimeRange.Model.Range[]} */
    _readonlyRanges : null,
    /** @type {TimeRange.Model.Range[]} */
    _conflictedRanges : null,

    /**
     * @constructor
     * @param {Number} beginTime
     * @param {Number} endTime
     * @param {TimeRange.Model.Range[]} occupiedRanges
     * @param {TimeRange.Model.Range[]} readonlyRanges
     * @param {TimeRange.Model.Range[]} editableRanges
     * @param {Number} numOfCells
     */
    init: function(beginTime, endTime, occupiedRanges, readonlyRanges, editableRanges, numOfCells){
        this._occupiedRanges = TimeRange.Model.Range.mergeRanges(occupiedRanges);
        this._readonlyRanges = TimeRange.Model.Range.mergeRanges(readonlyRanges);
        editableRanges = TimeRange.Model.Range.mergeRanges(editableRanges);
        var notEditableRanges = TimeRange.Model.Range._invertRanges(beginTime, endTime, editableRanges);
        this._conflictedRanges = TimeRange.Model.Range.getIntersections(this._occupiedRanges, notEditableRanges);
        this._readonlyRanges = TimeRange.Model.Range.mergeRanges(this._readonlyRanges.concat(notEditableRanges));
        this._cells = [];

        this._createCells(beginTime, endTime, numOfCells);
    },
    _createCells : function(begin, end, numOfCells) {
        var timeLength = end - begin;
        var celDuration = timeLength / numOfCells;
        var beginTime = begin;
        var endTime = beginTime + celDuration;
        var occupied = false;
        var readonly = false;
        var conflicted = false;
        var cell = null;

        for (var i = 0; i < numOfCells; i++) {
            occupied = new TimeRange.Model.Range(beginTime, endTime).isIntersected(this._occupiedRanges);
            readonly = new TimeRange.Model.Range(beginTime, endTime).isIntersected(this._readonlyRanges);
            conflicted = new TimeRange.Model.Range(beginTime, endTime).isIntersected(this._conflictedRanges);
            cell = new TimeRange.Model.CellModel(beginTime, endTime, occupied, readonly, conflicted, i);
            this._cells.push(cell);
            beginTime = endTime;
            endTime += celDuration;
        }
    },
    /**
     * @param {Number} idx
     * @return {TimeRange.Model.CellModel}
     */
    fillCell : function(idx){
        /** @type {TimeRange.Model.CellModel} */
        var cell = this._cells[idx];
        if(!cell.getReadonly()){
            cell.fill();
        }
        return cell;
    },
    /**
     * @param {Number} idx
     * @return {TimeRange.Model.CellModel}
     */
    cleanCell : function(idx){
        /** @type {TimeRange.Model.CellModel} */
        var cell = this._cells[idx];
        if(!cell.getReadonly()){
            cell.clean();
        }
        return cell;
    },
    resolveCell : function(idx){
        /** @type {TimeRange.Model.CellModel} */
        var cell = this._cells[idx];
        if(cell.getConflicted()){
            cell.setConflicted(false);
            cell.getOccupied() && cell.clean();
        }
        return cell;
    },
    /**
     * @return {TimeRange.Model.Range[]}
     */
    getOccupiedRanges : function() {
        var allRanges = [].concat(this._occupiedRanges);
        var range = null;
        for (var i = 0; i < this._cells.length; i++) {
            range = this._cells[i].getRange();
            range && allRanges.push(range);
        }
        return TimeRange.Model.Range.mergeRanges(allRanges);
    },
    getData : function() {
        return {
            cells : this._cells
        }
    }
});




/** @class */

TimeRange.Model.Range = Class.extend(
    {
        _beginBoundary : null,
        _endBoundary : null,

        /**
         * @constructor
         * @param {Number} begin
         * @param {Number} end
         * @param {Boolean} inverted
         */
        init: function(begin, end, inverted){
            this._beginBoundary = new TimeRange.Model.Range._RangeBoundary(begin, !inverted);
            this._endBoundary = new TimeRange.Model.Range._RangeBoundary(end, !!inverted);
        },
        getBegin : function() {
            return this._beginBoundary.getTime();
        },
        getEnd : function() {
            return this._endBoundary.getTime();
        },
        /**
         * @return {TimeRange.Model.Range._RangeBoundary}
         */
        getBeginBoundary : function(){
            return this._beginBoundary;
        },
        /**
         * @return {TimeRange.Model.Range._RangeBoundary}
         */
        getEndBoundary : function(){
            return this._endBoundary;
        },
        /**
         * @param {Boolean} filled
         */
        setFilled : function(filled){
            if(filled){
                this._beginBoundary.setOpening(true);
                this._endBoundary.setOpening(false);
            }else{
                this._beginBoundary.setOpening(false);
                this._endBoundary.setOpening(true);
            }
        },
        /**
         * @param {TimeRange.Model.Range[]} ranges
         */
        isIntersected : function(ranges){
            var range = ranges[0];
            for (var i = 0; i < ranges.length; i++) {
                range = ranges[i];
                if (this.getEnd() > range.getBegin() && this.getBegin() < range.getEnd()) {
                    return true;
                }
            }
            return false;
        },
        /**
         * @param {TimeRange.Model.Range[]} ranges
         * @return {TimeRange.Model.Range[]}
         */
        _getIntersections : function(ranges){
            var intersections = [];

            for (var i = 0, intersection; i < ranges.length; i++) {
                intersection = this._getIntersection(ranges[i]);
                intersection && intersections.push(intersection);
            }
            return this._static.mergeRanges(intersections);
        },
        /**
         * @param {TimeRange.Model.Range} range
         * @return {TimeRange.Model.Range}
         */
        _getIntersection : function(range){
            if (!this.isIntersected([range])) return null;

            var begin = Math.max(this.getBegin(), range.getBegin());
            var end = Math.min(this.getEnd(), range.getEnd());

            return new TimeRange.Model.Range(begin, end);
        }
    },

    // ----------------- Static Fields -------------------------------------------------------- //

    {
        /**
         * @param {Array} ranges
         */
        mergeRanges : function(ranges){
            var allBoundaries = this._getAllBoundaries(ranges);
            return this._getRanges(allBoundaries);
        },
        /**
         * @param {TimeRange.Model.Range._RangeBoundary[]} boundaries
         */
        _getRanges : function(boundaries){
            var filteredBoundaries = this._filterBoundaries(boundaries);
            var beginBoundary = filteredBoundaries[0];
            var endBoundary = filteredBoundaries[0];
            var ranges = [];

            for (var i = 0; i < filteredBoundaries.length; i += 2) {
                beginBoundary = filteredBoundaries[i];
                endBoundary = filteredBoundaries[i + 1];
                ranges.push(new TimeRange.Model.Range(beginBoundary.getTime(), endBoundary.getTime()));
            }

            return ranges;
        },
        /**
         * @param {TimeRange.Model.Range._RangeBoundary[]} allBoundaries
         */
        _filterBoundaries : function(allBoundaries){
            var boundary = allBoundaries[0];
            var balance = 0;
            var isOpening = false;
            var filteredBoundaries = [];
            var curTime = 0;
            var prevTime = 0;
            var lastFilteredBndry = null;

            allBoundaries.sort(function(a, b){
                return a.getTime() > b.getTime() ? 1 : a.getTime() < b.getTime() ? -1 : 0;
            });

            for (var i = 0; i < allBoundaries.length; i++) {
                boundary = allBoundaries[i];
                lastFilteredBndry = filteredBoundaries[filteredBoundaries.length - 1];
                isOpening = boundary.isOpening();
                curTime = boundary.getTime();
                prevTime = lastFilteredBndry && lastFilteredBndry.getTime();
                balance += isOpening ? 1 : -1;
                if(balance == 1 && isOpening){
                    if(curTime != prevTime){
                        filteredBoundaries.push(new this._RangeBoundary(curTime, true));
                    }else{
                        filteredBoundaries.pop();
                    }
                }else if(balance == 0 && !isOpening){
                    if(curTime != prevTime){
                        filteredBoundaries.push(new this._RangeBoundary(curTime, false));
                    }else{
                        filteredBoundaries.pop();
                    }
                }
            }

            return filteredBoundaries;
        },
        /**
         * @param {TimeRange.Model.Range[]} ranges
         */
        _getAllBoundaries : function(ranges){
            var boundaries = [];
            var range = ranges[0];
            for (var i = 0; i < ranges.length; i++) {
                range = ranges[i];
                boundaries.push(range.getBeginBoundary(), range.getEndBoundary());
            }
            return boundaries;
        },
        _invertRanges : function(beginTime, endTime, ranges){
            var newRanges = [];

            for(var i = 0, range=null; i<ranges.length; i++){
                range = ranges[i];
                newRanges.push(new TimeRange.Model.Range(beginTime, range.getBegin()));
                beginTime = range.getEnd();

                if(i == ranges.length - 1){
                    newRanges.push(new TimeRange.Model.Range(beginTime, endTime));
                }
            }

            return newRanges;
        },
        getIntersections : function(ranges1, ranges2){
            var intersections = [];

            for (var i = 0, range; i < ranges1.length; i++) {
                range = ranges1[i];
                intersections = intersections.concat(range._getIntersections(ranges2));
            }
            return intersections;
        }
    }
);












/** @class */

TimeRange.Model.Range._RangeBoundary = Class.extend({
    _time : 0,
    _isOpening : false,
    /**
     * @constructor
     * @param {Number} time
     * @param {Boolean} isOpening
     */
    init: function(time, isOpening){
        this._time = time;
        this._isOpening = isOpening;
    },
    getTime : function() {
        return this._time;
    },
    isOpening : function() {
        return this._isOpening;
    },
    /**
     * @param {Boolean} isBegin
     */
    setOpening : function(isBegin){
        this._isOpening = isBegin;
    }
});




/** @class */

TimeRange.Model.CellModel = Class.extend({
    _occupied : false,
    _readonly : false,
    _conflicted : false,
    _begin : 0,
    _end : 0,
    _idx : 0,
    /** @type {TimeRange.Model.Range} */
    _range : null,

    /**
     * @constructor
     * @param {Number} begin
     * @param {Number} end
     * @param {Boolean} occupied
     * @param {Number} idx
     */
    init: function(begin, end, occupied, readonly, conflicted, idx){
        this._occupied = occupied;
        this._readonly = readonly;
        this._conflicted = conflicted;
        this._begin = begin;
        this._end = end;
        this._idx = idx;
    },
    getOccupied : function() {
        return this._occupied;
    },
    getReadonly : function(){
        return this._readonly;
    },
    getConflicted : function(){
        return this._conflicted;
    },
    setConflicted : function(conflicted){
        this._conflicted = conflicted;
    },
    fill : function(){
        this._occupied = true;
        this._setRange(true);
    },
    clean : function(){
        this._occupied = false;
        this._setRange(false);
    },
    getIdx : function() {
        return this._idx;
    },
    /**
     * @param {Boolean} occupied
     */
    _setRange : function(occupied){
        if(this._range){
            this._range.setFilled(occupied);
        }else{
            this._range = new TimeRange.Model.Range(this._begin, this._end, !occupied);
        }
    },
    /**
     * @return {TimeRange.Model.Range}
     */
    getRange : function() {
        return this._range;
    }
});




/**
 * @param {String} name
 * @param {String} occupiedRanges
 * @param {String} editableRanges
 * @param {Boolean} [readonly]
 */
$.fn.WeekRange = function(name ,occupiedRanges, editableRanges, readonly) {
    this.empty();
    var weekRange = new WeekRange(this, name, occupiedRanges, editableRanges, readonly);
    this.data({weekRange : weekRange});
};




WeekRange = TimeRange.extend(
    {
        /** @type jQuery */
        _jHidden : null,
        /**
         * @param {jQuery} jContainer
         * @param {String} name
         * @param {String} occupiedRangesStr
         * @param {String} editableRangesStr
         * @param {Boolean} [readonly]
         */
        init: function(jContainer, name, occupiedRangesStr, editableRangesStr, readonly){
            var occupiedRanges = this._static._strToRanges(occupiedRangesStr + '');
            var editableRanges = this._static._strToRanges(editableRangesStr + '');
            var begNextWeek = this._static._getBeginNextWeek();
            var MILSEC_IN_WEEK = 7 * 24 * 60 * 60 * 1000;
            var endNextWeek = new Date(begNextWeek.getTime() + MILSEC_IN_WEEK);

            this._super(jContainer, {
                readonly : readonly,
                beginTime : begNextWeek.getTime(),
                endTime : endNextWeek.getTime(),
                occupiedRanges : this._static._convRangesToFull(occupiedRanges),
                readonlyRanges : [],
                rowNames: [
                    $.localize('dayOfWeek.short.1'),
                    $.localize('dayOfWeek.short.2'),
                    $.localize('dayOfWeek.short.3'),
                    $.localize('dayOfWeek.short.4'),
                    $.localize('dayOfWeek.short.5'),
                    $.localize('dayOfWeek.short.6'),
                    $.localize('dayOfWeek.short.7')
                ],
                editableRanges : this._static._convRangesToFull(editableRanges)
            });

            this._jHidden = $('<input type="hidden"/>').attr({name : name}).appendTo(jContainer);
            this._updateHidden();
        },
        event_onChange : function(){
            this._updateHidden();
            this._super();
        },

        _updateHidden : function(){
            this._jHidden.val(this.getOccupiedRanges());
        },
        getOccupiedRanges : function(){
            var ranges = this._super();
            return this._static._rangesToStr(this._static._convRangesToSimple(ranges));
        }
    },

    // ----------------- Static Fields -------------------------------------------------------- //

    {
        /**
         * @return {Date}
         */
        _getBeginNextWeek : function(){
            var currDate = new Date();
            var deltaDay = 7 - currDate.getDay() + 1;
            var newDate = new Date(currDate.getTime());
            newDate.setDate(currDate.getDate() + deltaDay);

            return new Date(newDate.getFullYear(), newDate.getMonth(), newDate.getDate());
        },
        _convRangesToFull : function(occupiedRanges){
            return this._convertRanges(occupiedRanges, true);
        },
        _convRangesToSimple : function(occupiedRanges){
            return this._convertRanges(occupiedRanges, false);
        },
        _convertRanges : function(occupiedRanges, toFull){
            var begNextWeek = this._getBeginNextWeek();
            var nextWeekTime = begNextWeek.getTime();
            var MSEC_IN_MIN = 60000;
            var newRanges = [];
            /** @type {TimeRange.Model.Range} */
            var range = null;

            for (var i = 0; i < occupiedRanges.length; i++) {
                range = occupiedRanges[i];
                newRanges.push(toFull
                    ? new TimeRange.Model.Range(
                    range.getBegin() * MSEC_IN_MIN + nextWeekTime,
                    (range.getEnd() + 1) * MSEC_IN_MIN + nextWeekTime
                )
                    : new TimeRange.Model.Range(
                    Math.floor((range.getBegin() - nextWeekTime) / MSEC_IN_MIN),
                    Math.floor((range.getEnd() - nextWeekTime) / MSEC_IN_MIN) - 1
                )
                );
            }
            return newRanges;
        },
        _rangesToStr : function(ranges){
            var range = ranges[0];
            var str = '';
            for (var i = 0; i < ranges.length; i++) {
                if (i) str += ',';
                range = ranges[i];
                str += range.getBegin() + ':' + range.getEnd();
            }
            return str;
        },
        _strToRanges : function(str){
            var arr = str.split(',');
            var substr = arr[0];
            var matches = [];
            var ranges = [];

            for (var i = 0; i < arr.length; i++) {
                substr = arr[i];
                matches = substr.match(/(\d+)\s*:\s*(\d+)/);
                if(matches && matches[1] && matches[2]){
                    ranges.push(new TimeRange.Model.Range(+matches[1], +matches[2]))
                }
            }
            return ranges;
        }
    }
);
