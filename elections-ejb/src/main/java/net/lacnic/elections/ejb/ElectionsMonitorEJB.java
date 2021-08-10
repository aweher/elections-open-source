package net.lacnic.elections.ejb;

import java.util.List;

import net.lacnic.elections.data.HealthCheck;
import net.lacnic.elections.data.Participation;
import net.lacnic.elections.data.TablesReportData;
import net.lacnic.elections.domain.ElectionLight;
import net.lacnic.elections.domain.IpAccess;
import net.lacnic.elections.domain.services.ActivityReportTable;
import net.lacnic.elections.domain.services.AuditorReportTable;
import net.lacnic.elections.domain.services.CandidateReportTable;
import net.lacnic.elections.domain.services.CommissionerReportTable;
import net.lacnic.elections.domain.services.ElectionReportTable;
import net.ripe.ipresource.IpResourceSet;


public interface ElectionsMonitorEJB {

	public HealthCheck getHealthCheckData();

	public HealthCheck updateHealthCheckData();

	public List<Participation> getOrganizationParticipations(String org);

	public List<ElectionLight> getElectionsLightAllOrderStartDateDesc();

	public String getWsAuthToken();

	public String getWsAuthMethod();

	public String getWsLacnicAuthUrl();

	public IpResourceSet getWsAuthorizedIps();

	public List<TablesReportData> getActivitiesBasicData();

	public ActivityReportTable getActivityTableReport(Long activityId);
	
	public List<TablesReportData> getAuditorsBasicData();

	public AuditorReportTable getAuditorTableReport(Long auditorId);

	public List<TablesReportData> getCandidatesBasicData();

	public CandidateReportTable getCandidateTableReport(Long candidateId);

	public List<TablesReportData> getCommissionersBasicData();

	public CommissionerReportTable getCommissionerTableReport(Long commissionerId);

	public List<TablesReportData> getElectionsBasicData();

	public ElectionReportTable getElectionTableReport(Long electionId);

	public List<TablesReportData> getIpAccessesBasicData();

	public IpAccess getIpAccessTableReport(Long ipAccessId);

}
