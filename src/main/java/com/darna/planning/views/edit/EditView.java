package com.darna.planning.views.edit;

import com.darna.planning.data.entity.Payement;
import com.darna.planning.data.service.PayementService;
import com.darna.planning.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Edit")
@Route(value = "edit/:payementID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class EditView extends Div implements BeforeEnterObserver {

    private final String PAYEMENT_ID = "payementID";
    private final String PAYEMENT_EDIT_ROUTE_TEMPLATE = "edit/%s/edit";

    private Grid<Payement> grid = new Grid<>(Payement.class, false);

    private TextField source;
    private TextField target;
    private TextField amount;
    private TextField goal;
    private DatePicker date;
    private TextField remaining;
    private Checkbox reached;

    private Button cancel = new Button("Annuler");
    private Button save = new Button("Enregistrer");

    private BeanValidationBinder<Payement> binder;

    private Payement payement;

    private final PayementService payementService;

    @Autowired
    public EditView(PayementService payementService) {
        this.payementService = payementService;
        addClassNames("edit-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("source").setHeader("Source").setAutoWidth(true);
        grid.addColumn("target").setHeader("Cible").setAutoWidth(true);
        grid.addColumn("amount").setHeader("Montant").setAutoWidth(true);
        grid.addColumn("goal").setHeader("Objectif").setAutoWidth(true);
        grid.addColumn("date").setHeader("Date de payement").setAutoWidth(true);
        grid.addColumn("remaining").setHeader("Le reste du totale").setAutoWidth(true);
        LitRenderer<Payement> reachedRenderer = LitRenderer.<Payement>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", reached -> reached.isReached() ? "check" : "minus").withProperty("color",
                        reached -> reached.isReached()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(reachedRenderer).setHeader("Abouti").setAutoWidth(true);

        grid.setItems(query -> payementService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PAYEMENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EditView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Payement.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.payement == null) {
                    this.payement = new Payement();
                }
                binder.writeBean(this.payement);
                payementService.update(this.payement);
                clearForm();
                refreshGrid();
                Notification.show("Payement bien effectué.");
                UI.getCurrent().navigate(EditView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the payement details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> payementId = event.getRouteParameters().get(PAYEMENT_ID).map(UUID::fromString);
        if (payementId.isPresent()) {
            Optional<Payement> payementFromBackend = payementService.get(payementId.get());
            if (payementFromBackend.isPresent()) {
                populateForm(payementFromBackend.get());
            } else {
                Notification.show(
                        String.format("Le payement demandé n'est pas trouvé, ID = %s", payementId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EditView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        source = new TextField("Source");
        target = new TextField("Cible");
        amount = new TextField("Montant");
        goal = new TextField("Objectif");
        date = new DatePicker("Date de payement");
        remaining = new TextField("Le reste du totale");
        reached = new Checkbox("Abouti");
        Component[] fields = new Component[] { source, target, amount, goal, date, remaining, reached };

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Payement value) {
        this.payement = value;
        binder.readBean(this.payement);

    }
}
