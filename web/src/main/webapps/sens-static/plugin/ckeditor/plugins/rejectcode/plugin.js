/**
 * @license Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.plugins.add( 'rejectcode', {
	lang: 'en,ja,ko,zh,zh-cn', // %REMOVE_LINE_CORE%
	icons: 'rejectcode', // %REMOVE_LINE_CORE%
	init: function( editor ) {

		editor.addCommand( 'rejectcode', {
			exec: function( editor ) {
				//수신거부 코드 삽입
				EditorUtil.rejectCodeInsert();
			}
		});

		editor.ui.addButton && editor.ui.addButton( 'RejectCode', {
			label: editor.lang.rejectcode.toolbar,
			command: 'rejectcode',
			toolbar: 'inserts,'+ 9
		});

	}

});

