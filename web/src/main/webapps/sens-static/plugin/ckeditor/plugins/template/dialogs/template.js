/**
 * @license Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.dialog.add( 'template', function( editor ) {
	return {
		CKEditor.popup('/Mail?act=MAIL_TEMPLATE&ispopup=1','720px', '600px','location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,resizable=yes,scrollbars=yes');
		/*
		title: editor.lang.smiley.title,
		minWidth: 270,
		minHeight: 120,
		contents: [
			{
			id: 'tab1',
			label: '',
			title: '',
			expand: true,
			padding: 0,
			elements: [
				smileySelector
				]
		}
		],
		buttons: [ CKEDITOR.dialog.cancelButton ]
		*/
	};
});
