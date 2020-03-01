package com.bochkov.wicket.component.select2;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.select2.*;

/**
 * The type Select 2 drop down behavior.
 */
public class Select2DropDownBehavior extends Behavior {

    private Settings settings = new Settings();

    /**
     * For single choice select 2 drop down behavior.
     *
     * @return the select 2 drop down behavior
     */
    public static Select2DropDownBehavior forSingleChoice() {
        return new Select2DropDownBehavior();
    }

    /**
     * For multi choice select 2 drop down behavior.
     *
     * @return the select 2 drop down behavior
     */
    public static Select2DropDownBehavior forMultiChoice() {
        Select2DropDownBehavior select2Behavior = new Select2DropDownBehavior();
        select2Behavior.getSettings().setMultiple(true);
        return select2Behavior;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        new Select2ResourcesBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);

                // render theme related resources if any
                if (settings.getTheme() != null) {
                    settings.getTheme().renderHead(component, response);
                }

                // include i18n resource file
                response.render(JavaScriptHeaderItem.forReference(
                        new Select2LanguageResourceReference(settings.getLanguage())));
            }
        }.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(JQuery.execute("$('#%s').select2(%s);",
                component.getMarkupId(), getSettings().toJson())));
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    /**
     * Gets settings.
     *
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Sets settings.
     *
     * @param settings the settings
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public CharSequence toJson() {
        return settings.toJson();
    }

    public Integer getMinimumInputLength() {
        return settings.getMinimumInputLength();
    }

    public Select2DropDownBehavior setMinimumInputLength(Integer minimumInputLength) {
        settings.setMinimumInputLength(minimumInputLength);
        return this;
    }

    public Integer getMinimumResultsForSearch() {
        return settings.getMinimumResultsForSearch();
    }

    public Select2DropDownBehavior setMinimumResultsForSearch(Integer minimumResultsForSearch) {
        settings.setMinimumResultsForSearch(minimumResultsForSearch);
        return this;
    }

    public Object getPlaceholder() {
        return settings.getPlaceholder();
    }

    public Select2DropDownBehavior setPlaceholder(Object placeholder) {
        settings.setPlaceholder(placeholder);
        return this;
    }

    public boolean getAllowClear() {
        return settings.getAllowClear();
    }

    public Select2DropDownBehavior setAllowClear(boolean allowClear) {
        settings.setAllowClear(allowClear);
        return this;
    }

    public boolean getMultiple() {
        return settings.getMultiple();
    }

    public Select2DropDownBehavior setMultiple(boolean multiple) {
        settings.setMultiple(multiple);
        return this;
    }

    public boolean getCloseOnSelect() {
        return settings.getCloseOnSelect();
    }

    public Select2DropDownBehavior setCloseOnSelect(boolean closeOnSelect) {
        settings.setCloseOnSelect(closeOnSelect);
        return this;
    }

    public String getId() {
        return settings.getId();
    }

    public Select2DropDownBehavior setId(String id) {
        settings.setId(id);
        return this;
    }

    public String getTemplateSelection() {
        return settings.getTemplateSelection();
    }

    public Select2DropDownBehavior setTemplateSelection(String templateSelection) {
        settings.setTemplateSelection(templateSelection);
        return this;
    }

    public String getTemplateResult() {
        return settings.getTemplateResult();
    }

    public Select2DropDownBehavior setTemplateResult(String templateResult) {
        settings.setTemplateResult(templateResult);
        return this;
    }

    public String getInitSelection() {
        return settings.getInitSelection();
    }

    public Select2DropDownBehavior setInitSelection(String initSelection) {
        settings.setInitSelection(initSelection);
        return this;
    }

    public String getQuery() {
        return settings.getQuery();
    }

    public Select2DropDownBehavior setQuery(String query) {
        settings.setQuery(query);
        return this;
    }

    public AjaxSettings getAjax() {
        return settings.getAjax();
    }

    public Select2DropDownBehavior setAjax(AjaxSettings ajax) {
        settings.setAjax(ajax);
        return this;
    }

    public AjaxSettings getAjax(boolean createIfNotSet) {
        return settings.getAjax(createIfNotSet);
    }

    public String getData() {
        return settings.getData();
    }

    public Select2DropDownBehavior setData(String data) {
        settings.setData(data);
        return this;
    }

    public boolean getTags() {
        return settings.getTags();
    }

    public Select2DropDownBehavior setTags(boolean tags) {
        settings.setTags(tags);
        return this;
    }

    public String getCreateTag() {
        return settings.getCreateTag();
    }

    public Select2DropDownBehavior setCreateTag(String createTag) {
        settings.setCreateTag(createTag);
        return this;
    }

    public Integer getMaximumSelectionLength() {
        return settings.getMaximumSelectionLength();
    }

    public Select2DropDownBehavior setMaximumSelectionLength(Integer maximumSelectionLength) {
        settings.setMaximumSelectionLength(maximumSelectionLength);
        return this;
    }

    public String getMatcher() {
        return settings.getMatcher();
    }

    public Select2DropDownBehavior setMatcher(String matcher) {
        settings.setMatcher(matcher);
        return this;
    }

    public String getTokenizer() {
        return settings.getTokenizer();
    }

    public Select2DropDownBehavior setTokenizer(String tokenizer) {
        settings.setTokenizer(tokenizer);
        return this;
    }

    public String getSorter() {
        return settings.getSorter();
    }

    public Select2DropDownBehavior setSorter(String sorter) {
        settings.setSorter(sorter);
        return this;
    }

    public String getEscapeMarkup() {
        return settings.getEscapeMarkup();
    }

    public Select2DropDownBehavior setEscapeMarkup(String escapeMarkup) {
        settings.setEscapeMarkup(escapeMarkup);
        return this;
    }

    public String getWidth() {
        return settings.getWidth();
    }

    public Select2DropDownBehavior setWidth(String width) {
        settings.setWidth(width);
        return this;
    }

    public ISelect2Theme getTheme() {
        return settings.getTheme();
    }

    public Select2DropDownBehavior setTheme(String theme) {
        settings.setTheme(theme);
        return this;
    }

    public Select2DropDownBehavior setTheme(ISelect2Theme theme) {
        settings.setTheme(theme);
        return this;
    }

    public String getContainerCss() {
        return settings.getContainerCss();
    }

    public Select2DropDownBehavior setContainerCss(String containerCss) {
        settings.setContainerCss(containerCss);
        return this;
    }

    public String getDropdownCss() {
        return settings.getDropdownCss();
    }

    public Select2DropDownBehavior setDropdownCss(String dropdownCss) {
        settings.setDropdownCss(dropdownCss);
        return this;
    }

    public String getContainerCssClass() {
        return settings.getContainerCssClass();
    }

    public Select2DropDownBehavior setContainerCssClass(String containerCssClass) {
        settings.setContainerCssClass(containerCssClass);
        return this;
    }

    public String getDropdownCssClass() {
        return settings.getDropdownCssClass();
    }

    public Select2DropDownBehavior setDropdownCssClass(String dropdownCssClass) {
        settings.setDropdownCssClass(dropdownCssClass);
        return this;
    }

    public String getDropdownParent() {
        return settings.getDropdownParent();
    }

    public Select2DropDownBehavior setDropdownParent(String dropdownParent) {
        settings.setDropdownParent(dropdownParent);
        return this;
    }

    public String getSeparator() {
        return settings.getSeparator();
    }

    public Select2DropDownBehavior setSeparator(String separator) {
        settings.setSeparator(separator);
        return this;
    }

    public String[] getTokenSeparators() {
        return settings.getTokenSeparators();
    }

    public Select2DropDownBehavior setTokenSeparators(String[] tokenSeparators) {
        settings.setTokenSeparators(tokenSeparators);
        return this;
    }

    public boolean getSelectOnClose() {
        return settings.getSelectOnClose();
    }

    public Select2DropDownBehavior setSelectOnClose(boolean selectOnClose) {
        settings.setSelectOnClose(selectOnClose);
        return this;
    }

    public boolean getDropdownAutoWidth() {
        return settings.getDropdownAutoWidth();
    }

    public Select2DropDownBehavior setDropdownAutoWidth(boolean dropdownAutoWidth) {
        settings.setDropdownAutoWidth(dropdownAutoWidth);
        return this;
    }

    public boolean isStateless() {
        return settings.isStateless();
    }

    public Select2DropDownBehavior setStateless(boolean stateless) {
        settings.setStateless(stateless);
        return this;
    }

    public String getMountPath() {
        return settings.getMountPath();
    }

    public Select2DropDownBehavior setMountPath(String mountPath) {
        settings.setMountPath(mountPath);
        return this;
    }

    public String getQueryParam() {
        return settings.getQueryParam();
    }

    public Select2DropDownBehavior setQueryParam(String queryParam) {
        settings.setQueryParam(queryParam);
        return this;
    }

    public String getLanguage() {
        return settings.getLanguage();
    }

    public Select2DropDownBehavior setLanguage(String language) {
        settings.setLanguage(language);
        return this;
    }
}
