function setupResolveDuplicate(cmpId, messageId, url, message) {
    const e = $('#' + cmpId).closest('.form-group');
    e.append("<div class='invalid-feedback d-block' data-type='duplicate'>" + message + ". Для загрузки нажмите <i id='" + messageId + "' class='fa fa-pencil'/></div>");
    $('#' + messageId).click(function () {
        resolveDuplicate(url);
    })
}

function clearFedbackMessages(cmpId) {
    $('#' + cmpId).closest('.form-group').remove('.invalid-feedback[data-type="duplicate"]');
}

function resolveDuplicate(url) {
    Wicket.Ajax.get({'u': url})
}
