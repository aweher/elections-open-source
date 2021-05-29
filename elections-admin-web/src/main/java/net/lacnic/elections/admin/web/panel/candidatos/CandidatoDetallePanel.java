package net.lacnic.elections.admin.web.panel.candidatos;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;

import net.lacnic.elections.admin.wicket.util.ImageResource;
import net.lacnic.elections.domain.Candidate;

public class CandidatoDetallePanel extends Panel {

	private static final long serialVersionUID = -2103342536982357758L;

	public CandidatoDetallePanel(String id, Candidate candidato) {
		super(id);

		add(new Label("nombreCandidato", candidato.getName()));
		Label bio = new Label("biografia", candidato.getBioSpanish());
		bio.setEscapeModelStrings(false);
		add(bio);
		add(new NonCachingImage("foto", new ImageResource(candidato.getPictureInfo(), candidato.getPictureExtension())));
	}
}
