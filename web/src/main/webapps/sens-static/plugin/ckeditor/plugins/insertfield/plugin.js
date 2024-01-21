/**
 * @license Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.plugins.add( 'insertfield', {
	requires:  ['richcombo'],
	lang: 'en,ja,ko,zh,zh-cn', // %REMOVE_LINE_CORE%
	icons: 'template', // %REMOVE_LINE_CORE%

	init: function( editor ) {
		editor.ui.addButton && editor.ui.addButton( 'InsertField', {
			label: editor.lang.insertfield.toolbar,
			command: 'insertfield',
			toolbar: 'inserts,'+ 8
		});


		editor.ui.addRichCombo('InsertField',
			{
				label: editor.lang.insertfield.toolbar,
				title: editor.lang.insertfield.toolbar,
				voiceLabel: "voiceLabel",
				multiSelect: false,
				panel:
					{
						css: [ CKEDITOR.skin.getPath( 'editor' ) ].concat( editor.config.contentsCss ),
						multiSelect: false,
						attributes: { 'aria-label': '필드추가' }
					},

				init: function () {
					var rebuildList = CKEDITOR.tools.bind(buildList, this);
					rebuildList();
					$(editor).bind('rebuildList', rebuildList);
				},
				onClick: function (value) {

					editor.focus();
					editor.fire('saveSnapshot');
					editor.insertText(value);
					editor.fire('saveSnapshot');

				},
				onOpen:function(){
					//필드삽입 눌러서 아래 레이어가 뜰 때 마다 초기화!
					var rebuildList = CKEDITOR.tools.bind(buildList, this);
					rebuildList();
					$(editor).bind('rebuildList', rebuildList);
				}

			});

		var buildListHasRunOnce = 0;
		var buildList = function () {
			if (buildListHasRunOnce) {
				// Remove the old unordered list from the dom.
				// This is just to cleanup the old list within the iframe
				// Note that this removes all uls... if there are more than one editor on page, we have to be more specific on what to remove. In my production ready code, I target one of the lis, and find its ul parent and remove that instead of shotgunning all uls like in this example
				$(this._.panel._.iframe.$).contents().find("ul").remove();
				// reset list
				this._.items = {};
				this._.list._.items = {};
			}

			if(receiverFieldArray != undefined && receiverFieldArray.length > 0 ){
				for( var i = 0 ; i < receiverFieldArray.length ; i++ ){
					this.add( receiverFieldArray[i].key, receiverFieldArray[i].value );
				}
			}
			if (buildListHasRunOnce) {
				// Force CKEditor to commit the html it generates through this.add
				this._.committed = 0; // We have to set to false in order to trigger a complete commit()
				this.commit();
			}
			buildListHasRunOnce = 1;
		};

	}
});

