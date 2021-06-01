package net.lacnic.elections.admin.dashboard.admin;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.lacnic.elections.admin.app.AppContext;
import net.lacnic.elections.admin.app.SecurityUtils;
import net.lacnic.elections.admin.web.bases.DashboardAdminBasePage;
import net.lacnic.elections.admin.web.commons.GestionEleccionStatusPanel;
import net.lacnic.elections.admin.web.panel.usuariopadron.CamposUsuarioPadronPanel;
import net.lacnic.elections.admin.web.panel.usuariopadron.GestionListaUsuariosPadronPanel;
import net.lacnic.elections.admin.web.panel.usuariopadron.SubirArchivoUsuarioPadronPanel;
import net.lacnic.elections.admin.wicket.util.UtilsParameters;
import net.lacnic.elections.domain.Election;
import net.lacnic.elections.domain.UserVoter;
import net.lacnic.elections.exception.CensusValidationException;

@AuthorizeInstantiation("siselecciones-only-one")
public class DashboardGestionPadron extends DashboardAdminBasePage {

	private static final long serialVersionUID = 1L;
	private Election eleccion;
	private UserVoter up = new UserVoter();

	public DashboardGestionPadron(PageParameters params) {
		super(params);
		setEleccion(AppContext.getInstance().getManagerBeanRemote().getElection(UtilsParameters.getIdAsLong(params)));
		add(new FeedbackPanel("feedback"));
		add(new GestionEleccionStatusPanel("tabPadron", eleccion));
		add(new PadronEleccionForm("eleccionPadronForm", eleccion));

		Form<Void> formUsuario = new Form<>("formUsuario");
		formUsuario.add(new CamposUsuarioPadronPanel("camposUsuarioPadronPanel", up));
		add(formUsuario);

		formUsuario.add(new Button("agregar") {
			private static final long serialVersionUID = -5396612472447055621L;

			@Override
			public void onSubmit() {
				super.onSubmit();
				try {
					AppContext.getInstance().getManagerBeanRemote().addUserVoter(getEleccion().getElectionId(), getUp(), SecurityUtils.getAdminId(), SecurityUtils.getIPClient());
					getSession().info(getString("censusManagementExito"));
					setResponsePage(DashboardGestionPadron.class, UtilsParameters.getId(getEleccion().getElectionId()));
				} catch (CensusValidationException e) {
					error(getString(e.getMessage()));
				}
			}
		});

		add(new AjaxLazyLoadPanel<GestionListaUsuariosPadronPanel>("gestionUsuarioPadron") {
			private static final long serialVersionUID = 8165854022863553760L;

			@Override
			public GestionListaUsuariosPadronPanel getLazyLoadComponent(String markupId) {
				return new GestionListaUsuariosPadronPanel(markupId, eleccion);
			}
		});

	}

	public final class PadronEleccionForm extends Form<Void> {

		private static final long serialVersionUID = 2351447413365706203L;

		public PadronEleccionForm(String id, final Election eleccion) {
			super(id);

			SubirArchivoUsuarioPadronPanel camposEleccionPadronPanel = new SubirArchivoUsuarioPadronPanel("subirArchivoUsuarioPadronPanel", eleccion, DashboardGestionPadron.class);
			camposEleccionPadronPanel.setOutputMarkupId(true);
			add(camposEleccionPadronPanel);

			Button siguiente = new Button("siguiente") {
				private static final long serialVersionUID = 1073607359256986749L;

				@Override
				public void onSubmit() {
					try {
						setResponsePage(DashboardGestionCandidatos.class, UtilsParameters.getId(eleccion.getElectionId()));
					} catch (Exception e) {
						error(e.getMessage());
					}
				}
			};
			siguiente.setEnabled(getEleccion().isElectorsSet());
			add(siguiente);

			Link<Void> saltar = new Link<Void>("saltar") {

				private static final long serialVersionUID = 1073607359256986749L;

				@Override
				public void onClick() {
					try {
						setResponsePage(DashboardGestionCandidatos.class, UtilsParameters.getId(eleccion.getElectionId()));
					} catch (Exception e) {
						error(e.getMessage());
					}
				}
			};
			add(saltar);

			Link<Void> atras = new Link<Void>("atras") {

				private static final long serialVersionUID = 1073607359256986749L;

				@Override
				public void onClick() {
					try {
						setResponsePage(DashboardGestionEleccion.class, UtilsParameters.getId(eleccion.getElectionId()));
					} catch (Exception e) {
						error(e.getMessage());
					}
				}
			};
			add(atras);

		}
	}

	public Election getEleccion() {
		return eleccion;
	}

	public void setEleccion(Election eleccion) {
		this.eleccion = eleccion;
	}

	public UserVoter getUp() {
		return up;
	}

	public void setUp(UserVoter up) {
		this.up = up;
	}
}