package net.lacnic.siselecciones.admin.web.commons;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class OnOffSwitch extends Panel {

	private static final long serialVersionUID = -1673297373093848215L;

	public OnOffSwitch(String id, IModel<Boolean> model) {
		super(id);
		AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("checkbox", model) {

			private static final long serialVersionUID = 8885764460555504063L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				accion();
			}
		};
		add(ajaxCheckBox);
		WebMarkupContainer l = new WebMarkupContainer("internal");
		add(l);
		l.add(new AttributeModifier("for", id));
		ajaxCheckBox.setMarkupId(id);
	}

	protected abstract void accion();

}
