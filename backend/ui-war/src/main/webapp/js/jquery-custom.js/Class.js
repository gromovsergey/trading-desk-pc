(function() {
    var initializing = false, fnTest = /xyz/.test(function() {xyz;}) ? /\b_super\b/ : /.*/;

    // The base Class implementation (does nothing)
    this.Class = function() {};

    // Create a new Class that inherits from this class
    Class.extend = function(props, statics) {
        var _super = this.prototype;

        // Instantiate a base class (but only create the instance,
        // don't run the init constructor)
        initializing = true;
        var prototype = new this();
        initializing = false;

        // Copy the properties over onto the new prototype
        for (var name in props) {
            // Check if we're overwriting an existing function
            prototype[name] = typeof props[name] == "function" &&
                typeof _super[name] == "function" && fnTest.test(props[name]) ?
                (function(name, fn) {
                    return function() {
                        var tmp = this._super;

                        // Add a new ._super() method that is the same method
                        // but on the super-class
                        this._super = _super[name];

                        // The method only need to be bound temporarily, so we
                        // remove it when we're done executing
                        var ret = fn.apply(this, arguments);
                        this._super = tmp;

                        return ret;
                    };
                })(name, props[name]) :
                props[name];
        }

        prototype._static = Class;

        // --- Copy static methods --- //
        var defaultFunc = new Function();
        var parentClass = this;
        for (var name in statics) {
            // preventing to overwriting fields of default Function object
            if (name in defaultFunc) continue;

            Class[name] = typeof statics[name] == "function" &&
                typeof parentClass[name] == "function" && fnTest.test(statics[name]) ?
                (function(name, fn) {
                    return function() {
                        var tmp = this._super;

                        this._super = parentClass[name];

                        var ret = fn.apply(Class, arguments);
                        this._super = tmp;

                        return ret;
                    };
                })(name, statics[name]) :
                statics[name];
        }
        for (var name in parentClass) {
            Class[name] = (name in Class) ? Class[name] : parentClass[name];
        }
        // --- End copy static methods --- //

        // The dummy class constructor
        function Class() {
            // All construction is actually done in the init method
            if (!initializing && this.init)
                this.init.apply(this, arguments);
        }

        // Populate our constructed prototype object
        Class.prototype = prototype;

        // Enforce the constructor to be what we expect
        Class.prototype.constructor = Class;

        // And make this class extendable
        Class.extend = arguments.callee;

        return Class;
    };
})();