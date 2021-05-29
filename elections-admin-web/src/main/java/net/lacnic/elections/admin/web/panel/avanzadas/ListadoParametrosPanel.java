package net.lacnic.elections.admin.web.panel.avanzadas;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import net.lacnic.elections.admin.app.AppContext;
import net.lacnic.elections.admin.app.SecurityUtils;
import net.lacnic.elections.admin.dashboard.admin.DashboardEditarParametrosPage;
import net.lacnic.elections.admin.dashboard.admin.DashboardParametros;
import net.lacnic.elections.admin.web.commons.BotonConConfirmacionEliminar;
import net.lacnic.elections.admin.wicket.util.UtilsParameters;
import net.lacnic.elections.domain.Parameter;

public class ListadoParametrosPanel extends Panel {

	private static final long serialVersionUID = -8554113800494186242L;

	private static final Logger appLogger = LogManager.getLogger("webAdminAppLogger");

	private List<Parameter> listadoParametros;

	public ListadoParametrosPanel(String id) {
		super("listadoParametro");

		listadoParametros = AppContext.getInstance().getManagerBeanRemote().obtenerListadoParamteros();

		final ListView<Parameter> listadoParams = new ListView<Parameter>("listadoParametros", listadoParametros) {

			private static final long serialVersionUID = 2146073776795909288L;

			@Override
			protected void populateItem(final ListItem<Parameter> item) {
				try {
					final Parameter p = item.getModelObject();

					item.add(new Label("claveParametro", p.getKey()));
					item.add(new Label("valorParametro", p.getValue()));

					BookmarkablePageLink<Void> editarParametro = new BookmarkablePageLink<>("editarParametro", DashboardEditarParametrosPage.class, UtilsParameters.getClaveId(p.getKey()));
					editarParametro.setMarkupId("editarParametro" + item.getIndex());
					item.add(editarParametro);

					BotonConConfirmacionEliminar borrar = new BotonConConfirmacionEliminar("borrar", item.getIndex()) {

						private static final long serialVersionUID = -7529344711667630816L;

						@Override
						public void onConfirmar() {					
							AppContext.getInstance().getManagerBeanRemote().borrarParametro(p.getKey(), SecurityUtils.getAdminId(), SecurityUtils.getIPClient());
							getSession().info(getString("advParameterExito"));
							setResponsePage(DashboardParametros.class); 
						}
					};
					item.add(borrar);					

				} catch (Exception e) {
					appLogger.error(e);
				}
			}
		};
		add(listadoParams);
	}

}
