package com.bochkov.wicket.jpa.crud;

import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Accessors(chain = true)
public abstract class CrudEditPanel<T extends Persistable<ID>, ID extends Serializable> extends CrudPanel<T, T, ID> {

    static {
        PropertyUtils.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        /*ConvertUtils.register(new Converter() {
            @Override
            public <T> T convert(Class<T> type, Object value) {
                return (T) Sets.newHashSet((Collection) value);
            }
        }, Set.class);*/
    }

    @SpringBean
    EntityManager entityManager;

    WebMarkupContainer container = new WebMarkupContainer("container");

    Form<T> form = new Form<>("form");

    @Getter
    @Setter
    SerializableSupplier<T> entityInstanceCreator = this::newEntityInstance;

    public CrudEditPanel(String id, Class<T> entityClass) {
        super(id, entityClass);

    }

    public CrudEditPanel(String id, Class<T> entityClass, IModel<T> model) {
        super(id, entityClass, model);
    }


    private Button createSaveButton(String id) {
        Button button = null;
        if (ajax) {
            button = createAjaxSaveButton(id);
        } else {
            button = createSimpleSaveButton(id);
        }
        return button;
    }

    private Button createAjaxSaveButton(String id) {
        return new AjaxButton(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                onSave(Optional.of(target), CrudEditPanel.this.getModel());
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                onSaveError(Optional.of(target), CrudEditPanel.this.getModel());
            }
        };
    }

    private Button createSimpleSaveButton(String id) {
        return new Button(id) {
            @Override
            public void onSubmit() {
                onSave(Optional.empty(), CrudEditPanel.this.getModel());
            }
        };
    }


    public void onSave(Optional<AjaxRequestTarget> target, IModel<T> model) {
        try {
            internalSave(model.getObject());
            String message = new StringResourceModel("save.success", form, model).setParameters(model.getObject()).getObject();
            Session.get().success(message);
            onAfterSave(target, model);
        } catch (NestedRuntimeException ex) {
            String message = new StringResourceModel("save.error", form, model).setParameters(model.getObject()).getObject();
            Session.get().error(message);
            Session.get().fatal(((NestedRuntimeException) ex).getMostSpecificCause());
            log.error(message, ex);
        } catch (CrudIteruptException ex) {
        } catch (Exception ex) {
            String message = new StringResourceModel("save.error", form, model).setParameters(model.getObject()).getObject();
            Session.get().error(message);
            Session.get().fatal(ex);
            log.error(message, ex);
        }
        target.ifPresent(t -> t.add(feedback));
    }

    public final T internalSave(T entity) {
        T saved = save(entity);
        setModelObject(saved);
        return saved;
    }

    public T save(T entity) {
        return getRepository().save(entity);
    }

    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<T> model) {
        if (getBackPage() != null) {
            setResponsePage(getBackPage());
        }
    }

    public void onSaveError(Optional<AjaxRequestTarget> target, IModel<T> model) {
        error(getString("save.error.unknown"));
        target.ifPresent(t -> t.add(feedback));
    }

    public void onClone(Optional<AjaxRequestTarget> target, IModel<T> model) {
        ID id = model.map(Persistable::getId).getObject();
        SerializableSupplier<T> entityCreator = () -> {
            T clone = newEntityInstance();
            Optional.ofNullable(id).flatMap(getRepository()::findById).ifPresent(original -> copyDataForClone(original, clone));
            return clone;
        };

        if (target.isPresent()) {
            setModelObject(null);
            setEntityInstanceCreator(entityCreator);
            target.get().add(this);
        } else {
            IModel<T> newModel = createModelForNewRow(entityCreator);
            Page editPage = BeanUtils.instantiateClass(getPage().getClass());
            editPage.setDefaultModel(newModel);
            setBackPageMeta(getPage(), editPage);
            setResponsePage(editPage);
        }

    }

    public void onAddRow(Optional<AjaxRequestTarget> target) {
        Page page = BeanUtils.instantiateClass(getPage().getClass());
        setBackPageMeta(getPage(), page);
        setResponsePage(page);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        IModel<T> model = getModel();
        if (model == null) {
            model = (IModel<T>) getPage().getDefaultModel();
            setModel(model);
        }
        if (model == null) {
            setModel(createModelForNewRow());
        }
        getEntityFromPageParam().ifPresent(this::setModelObject);

        form.setModel(new CompoundPropertyModel<>(getModel()));
        Button saveButton = createSaveButton("btn-save");
        form.add(saveButton);

        AbstractLink cloneButton = createCloneButton("btn-clone", getModel());
        form.add(cloneButton);

        Component addButton = createAddRowButton("btn-new");
        form.add(addButton);

        Component deleteButton = createDeleteButton("btn-delete", getModel());
        form.add(deleteButton);
        form.add(createBackButton("btn-back", ajax));
        form.add(createInputPanel("input-panel", getModel()));
        form.setDefaultButton(saveButton);
        container.add(form);
        container.setOutputMarkupId(true);
        add(container);
    }

    protected abstract Component createInputPanel(String id, IModel<T> model);


    public T newEntityInstance() {
        return BeanUtils.instantiateClass(getEntityClass());
    }

    final protected IModel<T> createModelForNewRow() {
        IModel<T> model = PersistableModel.of(id -> getRepository().findById(id), () -> this.entityInstanceCreator.get());
        return model;
    }

    final protected IModel<T> createModelForNewRow(SerializableSupplier<T> newInstanceCreator) {
        IModel<T> model = PersistableModel.of(id -> getRepository().findById(id), newInstanceCreator);
        return model;
    }

    protected AbstractLink createCloneButton(String id, IModel<T> model) {
        AbstractLink button = null;
        if (ajax) {
            button = createAjaxCloneButton(id, model);
        } else {
            button = createSimpleCloneButton(id, model);
        }
        button.add(new DisabledAttributeBehavior());
        return button;
    }

    public T copyDataForClone(final T src, final T dst) {
        try {
            entityManager.detach(dst);
            org.apache.commons.beanutils.BeanUtils.copyProperties(dst, src);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return dst;
    }

    public AbstractLink createAjaxCloneButton(String id, IModel<T> model) {
        AbstractLink button = new AjaxLink<T>(id, model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onClone(Optional.of(target), CrudEditPanel.this.getModel());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                T object = getModelObject();
                setEnabled(object != null && !object.isNew());
            }
        };
        button.setEnabled(false).setVisible(false);
        return button;

    }

    public AbstractLink createSimpleCloneButton(String id, IModel<T> model) {
        AbstractLink button = new Link<T>(id, model) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                T object = getModelObject();
                setEnabled(object != null && !object.isNew());
            }

            @Override
            public void onClick() {
                onClone(Optional.empty(), CrudEditPanel.this.getModel());
            }
        };
        button.setVisible(false).setEnabled(false);
        return button;
    }


    public Component createAddRowAjaxButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAddRow(Optional.of(target));
            }
        };
    }

    public Component createAddRowSimpleButton(String id) {
        return new Link<Void>(id) {
            @Override
            public void onClick() {
                onAddRow(Optional.empty());
            }
        };
    }

    public Component createAddRowButton(String id) {
        if (ajax) {
            return createAddRowAjaxButton(id);
        } else {
            return createAddRowSimpleButton(id);
        }
    }

    @Override
    public void onAfterDelete(AjaxRequestTarget target) {
        super.onAfterDelete(target);
        if (getBackPage() != null) {
            setResponsePage(getBackPage());
        }
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
