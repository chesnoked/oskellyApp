var leftArrow = document.getElementsByClassName('profilePage-scroll-menu-arrowLeft')[0];
var rightArrow = document.getElementsByClassName('profilePage-scroll-menu-arrowRight')[0];
var menu = document.getElementsByClassName('profilePage-scroll-menu')[0];

menu.onscroll = function() {

    if (this.scrollLeft == 0) {
        leftArrow.classList.add ("hide");
    }
    else if (this.scrollLeft + this.clientWidth == this.scrollWidth) {
        rightArrow.classList.add ("hide");
    }
    else {
        leftArrow.classList.remove ("hide");
        rightArrow.classList.remove ("hide");
    }

    var rightArrowStyle = getComputedStyle(rightArrow);
    var rightArrowWidth = parseInt(rightArrowStyle.width);

    leftArrow.style.left = menu.scrollLeft + 'px';
    rightArrow.style.left = (menu.scrollLeft + menu.clientWidth - rightArrowWidth) + 'px';
};