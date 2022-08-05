(function(w, $){
    $.widget('custom.collapsible', {
        '_create':  function(){
            var self = this;
            this._jMainChBox        = this.element.find(':checkbox.main:eq(0)');
            this._enabledHdn        = this._jMainChBox.next();
            this._alwaysEnable      = this.element.find('.alwaysEnable');
            this._jButtsExpand      = $('.collapseButt.expand', this.element);
            this._jButtsCollapse    = $('.collapseButt.collapse', this.element);
            this._isExpandable      = true;
            
            this.element.filter(':has(.persistent)').addClass('hasPersistent');

            $.each(this._jButtsExpand, function () {
                $(this)
                    .data({'initText':    $(this).text()})
                    .text('[+] ' + $(this).data('initText'))
                    .on('click', function(e){
                        e.preventDefault();
                        self._isExpandable && self.expand();
                    })
            });

            this._jButtsCollapse
                .data({initText : this._jButtsCollapse.text()})
                .text('[-] ' + this._jButtsCollapse.data('initText'))
                .on('click', function(e){
                    e.preventDefault();
                    self.collapse();
                });
            
            if (this._jMainChBox.length){
                this._jMainChBox.on('change', function(){
                    self.toggleDisabled();
                });
                this.toggleDisabled();
            }
        },
        'toggleDisabled':   function(){
            var toDisable       = !this._jMainChBox.prop('checked'),
                jSetToDisable   = this.element.find(':input').not(this._jMainChBox).not(this._alwaysEnable);
            
            jSetToDisable.prop({disabled : toDisable});
            jSetToDisable.each(function(){
                if (toDisable && $(this).attr('name')) {
                    $('<input class="disabledBackup" type="hidden" />').attr('name', $(this).attr('name')).val($(this).val()).appendTo(this.parentNode);
                }
            });

            if (toDisable){
                this.collapse();
                this._jButtsExpand.text(this._jButtsExpand.data('initText'));
                this._isExpandable = false;
            } else {
                this._jButtsExpand.text('[+] ' + this._jButtsExpand.data('initText'));
                this._isExpandable = true;
                this.element.find('.disabledBackup').remove();
                if (this.element.hasClass('expanded_default')) {
                    this._jButtsExpand.trigger('click');
                }
            }
            this._enabledHdn.val(this._jMainChBox.prop('checked').toString());
        },
        'expand':   function(){
            this.element.removeClass('collapsed').addClass('expanded');
        },
        'collapse': function() {
            this.element.removeClass('expanded').addClass('collapsed');
        }
    });
})(window, jQuery);