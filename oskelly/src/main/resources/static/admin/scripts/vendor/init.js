
(function init() {

    $(function() {

        $(document).foundation();

        initMFP();

    });

    function initMFP() {
        $.extend(true, $.magnificPopup.defaults, {
            tClose: 'Закрыть', // Alt text on close button
            tLoading: 'Загрузка…', // Text that is displayed during loading. Can contain %curr% and %total% keys
            fixedContentPos: true,
            gallery: {
                tPrev: 'Назад', // Alt text on left arrow
                tNext: 'Далее', // Alt text on right arrow
                tCounter: '%curr% из %total%' // Markup for "1 of 7" counter
            },
            image: {
                tError: 'Не получилось загрузить <a href="%url%">изображение</a>.' // Error message when image could not be loaded
            },
            ajax: {
                tError: '<a href="%url%">Данные</a> загрузить не получилось.' // Error message when ajax request failed
            },
            removalDelay: 500,
        });

        $('[data-mfp-gallery]').each((i, el) => {
            $(el).magnificPopup({
                delegate: 'a',
                type: 'image',
                gallery: {
                    enabled: true
                },
            })
        });

        $('[data-mfp-src]').magnificPopup()
    }

})();

