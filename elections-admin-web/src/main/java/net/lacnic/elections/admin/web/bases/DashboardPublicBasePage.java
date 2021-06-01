package net.lacnic.elections.admin.web.bases;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.lacnic.elections.admin.app.SecurityUtils;
import net.lacnic.elections.admin.web.commons.TopBarPublic;
import net.lacnic.elections.admin.wicket.util.UtilsParameters;
import net.lacnic.elections.domain.Election;

public abstract class DashboardPublicBasePage extends WebPage {

	private static final long serialVersionUID = 6861984885215804314L;

	private Election eleccion;
	private Election[] elecciones;
	private String token;

	public DashboardPublicBasePage(PageParameters params) {
		setToken(UtilsParameters.getToken(params));
		Class classError = validarToken(params);
		if (classError != null) {
			setResponsePage(classError);
		}
		add(new TopBarPublic("topBarPublic"));
	}

	public String getIP() {
		WebClientInfo info = (WebClientInfo) getSession().getClientInfo();
		return info.getProperties().getRemoteAddress();
	}

	protected abstract Class validarToken(PageParameters params);

	public Election getEleccion() {
		return eleccion;
	}

	public void setEleccion(Election eleccion) {
		this.eleccion = eleccion;
	}

	public String getToken() {
		return token;
	}

	public String getIdioma() {
		return SecurityUtils.getLocale().getDisplayName();
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Election[] getElecciones() {
		return elecciones;
	}

	public void setElecciones(Election[] elecciones) {
		this.elecciones = elecciones;
	}

}