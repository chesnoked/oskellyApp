let CheckboxProductFilter = function () {};

CheckboxProductFilter.prototype.getActualRequestParameter = function() {
    const selectedValues = this._filterElement.find("input:checked")
        .map(function() { return this.value; })
        .get().join();

    if (selectedValues) {
        let actualRequestParameter = {};
        actualRequestParameter[this._name] = selectedValues;

        return actualRequestParameter;
    }

    return undefined;
};

let SizeFilter = function (trigger) {

    let filterElement = $("[data-filter-size]");
    const sizeTypeSelectElement = filterElement.find("select");

    filterElement.on("change", "input:checkbox, select", trigger);

    this.getActualRequestParameter = function() {
        const selectedValues = filterElement.find("input:checked")
            .map(function() { return this.value; })
            .get().join();

        let actualRequestParameters = {};
        if (sizeTypeSelectElement.val()) {
            actualRequestParameters["sizeType"] = sizeTypeSelectElement.val();
        }

        if (selectedValues) {
            actualRequestParameters["size"] = selectedValues;
        }
        return actualRequestParameters;
    };
};

SizeFilter.prototype.__proto__ = CheckboxProductFilter.prototype;

let BrandFilter = function (trigger) {
    let filterElement = $("[data-filter-brand]");
    let brands = filterElement.find("input:checkbox + label");

    filterElement.on("change", "input:checkbox", trigger);

    this._filterElement = filterElement;
    this._name = "brand";

    let brandSearchElement = filterElement.find("input[type=text]");

    let brandSearchLabel = brandSearchElement.next();
    let brandResetSearchLabel = brandSearchLabel.next();

    brandSearchElement.on("input", search);
    brandSearchElement.on("input", showOrHideResetSearchElement);
    brandResetSearchLabel.click(resetSearch);

    function search() {
        const interestingBrand = $(this).val().toLowerCase();

        brands.each(function() {
            const currentBrand = $(this);
            const brandName = $(currentBrand).text();
            const currentBrandLooksLikeInterestingBrand =
                (brandName.toLowerCase().indexOf(interestingBrand) === 0);

            if (currentBrandLooksLikeInterestingBrand) {
                currentBrand.show();
            }
            else { currentBrand.hide(); }
        });
    }

    function resetSearch() {
        brandSearchElement.val("");
        brandSearchElement.triggerHandler("input");
    }

    function showOrHideResetSearchElement() {
        if (brandSearchElement.val() === "") {
            brandResetSearchLabel.addClass("hide");
            brandSearchLabel.removeClass("hide");
        }
        else {
            brandResetSearchLabel.removeClass("hide");
            brandSearchLabel.addClass("hide");
        }
    }

};

BrandFilter.prototype.__proto__ = CheckboxProductFilter.prototype;

let TagFilter = function (trigger) {
    let filterElement = $("[data-filter-tag]");
    filterElement.change(trigger);

    this.getActualRequestParameter = function() {
        let actualRequestParameters = {};

        filterElement.find("input:checked")
            .each(function() { actualRequestParameters[this.name] = 1; });

        return actualRequestParameters;
    };
};

TagFilter.prototype.__proto__ = CheckboxProductFilter.prototype;

let AttributeFilter = function (trigger) {
    let filterElement = $("[data-filter-attribute]");
    filterElement.change(trigger);

    this._filterElement = filterElement;
    this._name = "filter";
};

AttributeFilter.prototype.__proto__ = CheckboxProductFilter.prototype;

let ProductSorting = function (trigger) {

    let $sortElement = $("[data-product-sort]").find("select");
    let currentSort = $sortElement.val();

    $sortElement.change(function() {
        currentSort = $(this).val();
        trigger();
    });

    this.getActualRequestParameter = function() {
        if (currentSort) { return { sort: currentSort }; }
    }
};

let ProductConditionFilter = function (trigger) {
    let filterElement = $("[data-filter-product-condition]");
    filterElement.on("change", "input:checkbox", trigger);

    this._filterElement = filterElement;
    this._name = "productCondition";
};

ProductConditionFilter.prototype.__proto__ = CheckboxProductFilter.prototype;