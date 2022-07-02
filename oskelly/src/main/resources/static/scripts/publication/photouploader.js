photoDrop1.onmouseover = dropzoneMouseOver;
photoDrop2.onmouseover = dropzoneMouseOver;
photoDrop3.onmouseover = dropzoneMouseOver;
photoDrop4.onmouseover = dropzoneMouseOver;

photoDrop1.onmouseout = dropzoneMouseOut;
photoDrop2.onmouseout = dropzoneMouseOut;
photoDrop3.onmouseout = dropzoneMouseOut;
photoDrop4.onmouseout = dropzoneMouseOut;

photoDrop1.addEventListener('DOMNodeInserted', nodeInserted);
photoDrop2.addEventListener('DOMNodeInserted', nodeInserted);
photoDrop3.addEventListener('DOMNodeInserted', nodeInserted);
photoDrop4.addEventListener('DOMNodeInserted', nodeInserted);

photoDrop1.addEventListener('DOMNodeRemoved', nodeRemoved);
photoDrop2.addEventListener('DOMNodeRemoved', nodeRemoved);
photoDrop3.addEventListener('DOMNodeRemoved', nodeRemoved);
photoDrop4.addEventListener('DOMNodeRemoved', nodeRemoved);

function dropzoneMouseOver(event) {
    var parent = event.target.parentNode.parentNode;
    var deleteElement = $(parent.getElementsByClassName('dz-details'));
    deleteElement.show();
}

function dropzoneMouseOut(event) {
    if (event.target.className === 'dz-details') {
        var element = $(event.target);
        element.hide();
    }
}

function nodeInserted(mutation) {
    var element = mutation.target;
    if (element.querySelector('.dz-image') != undefined) {
        $(element.parentNode.querySelector('.dz-message')).hide();
    }
}

function nodeRemoved(mutation) {
    var element = mutation.target;
    if (element.querySelector('.dz-image') != undefined) {
        $(element.parentNode.querySelector('.dz-message')).show();
    }
}

photoDrop1.addEventListener('DOMNodeInserted', nodeInserted);
photoDrop1.addEventListener('DOMNodeRemoved', nodeRemoved);

var dropZoneSetup = {
    paramName: "image",
    acceptedFiles: "image/*",
    maxFiles:1,
    thumbnailWidth: 240,
    thumbnailHeight: 320,
    thumbnailMethod: 'contain',
    previewTemplate: document.querySelector('#dztemplate').innerHTML,

    init: function () {
        var dz = this;
        dz.handleFiles = function(files) {
            var _this5 = this;
            var files_array = [];

            for (var i = 0; i < files.length; i++) {
                files_array.push(files[i]);
            }

            return files_array.map(function (file) {
                return _this5.addFile(file);
            });
        };

        dz.currentFile= null;

        dz.on("addedfile", function (file) {
            if(dz.currentFile){
                dz.removeFile(dz.currentFile);
            }
            dz.currentFile = file;
            window.loadImage(
                file,
                function (img) {
                    dz.emit("thumbnail", file, img.toDataURL());
                },
                {
                    maxWidth: 240,
                    maxHeight: 320,
                    orientation: true
                }
            );
        });

        if(dz.element.attributes['data-tmb-src']){
            var rawUrl = dz.element.attributes['data-tmb-src'].value;
            var splitedUrl = rawUrl? rawUrl.split('/') : undefined;
            var nameFromUrl = splitedUrl[rawUrl.length - 1];
            var thumb = {
                name: nameFromUrl,
                size: 0,
                dataURL: rawUrl
            };
            dz.emit("addedfile", thumb);

            dz.createThumbnailFromUrl(thumb,
                dz.options.thumbnailWidth, dz.options.thumbnailHeight,
                dz.options.thumbnailMethod, true, function (thumbnail) {
                    dz.emit("thumbnail", thumb, thumbnail);
                });

            dz.emit('complete', thumb);
        }
    }
};

Dropzone.options.photoDrop1 = dropZoneSetup;

Dropzone.options.photoDrop2 = dropZoneSetup;

Dropzone.options.photoDrop3 = dropZoneSetup;

Dropzone.options.photoDrop4 = dropZoneSetup;