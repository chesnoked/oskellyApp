(function($, window, document) {
    $(function() {

        var currentPage = 1;
        var MODERATION_PRODUCTS_API = "/admin/api/v1/moderation/products";
        var totalPages = $("[data-total-pages]").attr("data-total-pages");

        let productsContainer = $("#products");

        function applyFilters() {
            productsContainer.fadeTo(300, 0.2);
            currentPage = 1;
            reloadData(currentPage)
                .done(function (renderedProducts) {
                    productsContainer.html(renderedProducts.content);
                    totalPages = renderedProducts.totalPages;
                })
                .fail(function () {
                    alert("Произошла ошибка при загрузке данных");
                })
                .always(function () {
                    productsContainer.fadeTo(300, 1);
                });
        }

        $('input[type=radio][name=state]').change(function() {
            applyFilters();
        });

        $('input[type=checkbox][name=pro]').change(function() {
            applyFilters();
        });

        $('input[type=checkbox][name=vip]').change(function() {
            applyFilters();
        });

        function reloadData (currentPage) {
            let currentState = $('input[type=radio][name=state]:checked').val();
            return jQuery.ajax({
                url: MODERATION_PRODUCTS_API,
                type: "GET",
                data: {
                    state: currentState === 'ALL' ? null : currentState,
                    page: currentPage,
                    pro: document.getElementById('pro').checked ? true : null,
                    vip: document.getElementById('vip').checked ? true : null
                }
            })
        }

        (function initInfiniteScroll() {

            if (totalPages < 2) { return; }

            let scrollIsBlocked = false;
            //$(window).on("touchmove", handleScroll);
            $(window).scroll(function(){
                    if (scrollIsBlocked) { return; }
                    let scrollHeight = document.getElementById("scrollContainer").scrollHeight;
                    let currentScrollPosition = this.scrollY;
                    let screenHeight = document.documentElement.clientHeight;

                    if (currentScrollPosition + screenHeight < scrollHeight * 2 / 3) { return; }

                    if (currentPage >= totalPages) {
                        return;
                    }

                    ++currentPage;
                    scrollIsBlocked = true;
                    reloadData(currentPage)
                        .done(function (renderedProducts) {
                            productsContainer.append(renderedProducts.content);
                            totalPages = renderedProducts.totalPages;
                            scrollIsBlocked = false;
                        })
                        .fail(function () {
                            alert("Произошла ошибка при загрузке данных");
                        });
                }

            );




        })();
    });

})(window.jQuery, window, document);

