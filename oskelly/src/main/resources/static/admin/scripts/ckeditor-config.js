CKEDITOR.editorConfig = function (config) {
	config.extraPlugins = 'uploadimage';
	config.uploadUrl = '/admin/files/images/upload/imageuploader';
	config.filebrowserImageUploadUrl = '/admin/files/images/upload/filebrowser';
};

/*
 Костыль(устанавливает минимальную ширину диалога при загрузке изображений)
 */
CKEDITOR.on('dialogDefinition', function (evt) {
	var dialog = evt.data;

	if (dialog.name == 'image2') {
		var definition = dialog.definition;
		definition.minWidth = 500;
		definition.minHeight = 400;
	}
});