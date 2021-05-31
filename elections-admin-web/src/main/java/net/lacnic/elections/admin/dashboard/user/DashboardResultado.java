package net.lacnic.elections.admin.dashboard.user;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.lacnic.elections.admin.app.AppContext;
import net.lacnic.elections.admin.dashboard.error.Error404;
import net.lacnic.elections.admin.dashboard.error.ErrorResultadosNoPublicos;
import net.lacnic.elections.admin.web.bases.DashboardPublicBasePage;
import net.lacnic.elections.admin.web.panel.elecciones.CodigosCandidatoPanel;
import net.lacnic.elections.admin.web.panel.elecciones.ResultadoEleccionPanel;
import net.lacnic.elections.domain.Election;

public class DashboardResultado extends DashboardPublicBasePage {

	private static final long serialVersionUID = 2304496268074384354L;

	private static final Logger appLogger = LogManager.getLogger("webAdminAppLogger");

	public DashboardResultado(PageParameters params) {
		super(params);
		try {
			add(new Label("titulo", getEleccion().getTitle(getIdioma())));
			Label desc = new Label("descripcion", getEleccion().getDescription(getIdioma()));
			desc.setEscapeModelStrings(false);
			add(desc);
			add(new ResultadoEleccionPanel("resultadoPanel", getEleccion().getElectionId()));
			add(new CodigosCandidatoPanel("codigosCandidatoPanel", getEleccion().getElectionId()));

		} catch (Exception e) {
			appLogger.error(e);
		}
	}

	@Override
	public Class validarToken(PageParameters params) {
		Election eleccion = AppContext.getInstance().getVoterBeanRemote().verificarAccesoResultado(getToken());
		if (eleccion == null) {
			AppContext.getInstance().getVoterBeanRemote().intentoFallidoIp(getIP());
			return Error404.class;
		} else {
			setEleccion(eleccion);
			if (!eleccion.isResultLinkAvailable()) {
				return ErrorResultadosNoPublicos.class;
			}
		}
		return null;
	}
}