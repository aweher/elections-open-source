package net.lacnic.elections.adminweb.ui.admin.election.joint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import net.lacnic.elections.adminweb.app.AppContext;
import net.lacnic.elections.adminweb.ui.components.ButtonDeleteWithConfirmation;
import net.lacnic.elections.domain.JointElection;


public class JointElectionsListPanel extends Panel {

	private static final long serialVersionUID = -3823908256532916047L;

	private static final Logger appLogger = LogManager.getLogger("webAdminAppLogger");


	public JointElectionsListPanel(String id) {
		super(id);

		List<JointElection> jointElectionsList = AppContext.getInstance().getManagerBeanRemote().getJointElectionsAll();
		if(jointElectionsList == null)
			jointElectionsList = new ArrayList<>();
		init(jointElectionsList);
	}


	private void init(List<JointElection> jointElectionsList) {
		try {
			final ListView<JointElection> jointElectionsListView = new ListView<JointElection>("jointElectionsList", jointElectionsList) {
				private static final long serialVersionUID = 1786359392545666490L;

				@Override
				protected void populateItem(ListItem<JointElection> item) {
					final JointElection actual = item.getModelObject();
					try {
						item.add(new Label("electionA", AppContext.getInstance().getManagerBeanRemote().getElection(actual.getIdElectionA()).getTitleSpanish()));
						item.add(new Label("electionB", AppContext.getInstance().getManagerBeanRemote().getElection(actual.getIdElectionB()).getTitleSpanish()));

						ButtonDeleteWithConfirmation buttonDelete = new ButtonDeleteWithConfirmation("remove", item.getIndex()) {
							private static final long serialVersionUID = 6986190296016629836L;

							@Override
							public void onConfirm() {
								try {
									AppContext.getInstance().getManagerBeanRemote().removeJointElection(actual);
									getSession().info(getString("unitedElecListExitoDel"));
									setResponsePage(JointElectionsDashboard.class);
								} catch (Exception e) {
									appLogger.error(e);
								}
							}
						};
						item.add(buttonDelete);
					} catch (Exception e) {
						appLogger.error(e);
					}
				}
			};
			add(jointElectionsListView);
		} catch (Exception e) {
			error(e.getMessage());
		}
	}

}