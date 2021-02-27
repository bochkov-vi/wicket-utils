function scrollToAnchor(aid) {
    const aTag = $("[name='" + aid + "']");
    const top = aTag.offset().top - 100;
    $('html,body').animate({scrollTop: top}, 'slow');
}