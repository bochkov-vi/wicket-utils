function createInputGroup(cmp) {
    const input = $(cmp);
    const id = input.attr('id');
    const inputGroup = `<div class="input-group date" id="${id}" data-target-input="nearest"></div>`;
    const button = `<div class="input-group-append" data-target="#${id}" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-calendar"></i></div>
                    </div>`
    input.wrapAll(inputGroup);
    input.after(button);
}
