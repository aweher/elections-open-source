package net.lacnic.elections.ejb.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import net.lacnic.elections.dao.ElectionsDaoFactory;
import net.lacnic.elections.dao.UserVoterDao;
import net.lacnic.elections.data.DetalleResultadoData;
import net.lacnic.elections.data.ResultadoEleccionesData;
import net.lacnic.elections.domain.IpAccess;
import net.lacnic.elections.domain.Activity;
import net.lacnic.elections.domain.Auditor;
import net.lacnic.elections.domain.Candidate;
import net.lacnic.elections.domain.Election;
import net.lacnic.elections.domain.JointElection;
import net.lacnic.elections.domain.ElectionEmailTemplate;
import net.lacnic.elections.domain.ActivityType;
import net.lacnic.elections.domain.UserVoter;
import net.lacnic.elections.domain.Vote;
import net.lacnic.elections.ejb.VotanteEleccionesEJB;
import net.lacnic.elections.exception.OperationNotPermittedException;
import net.lacnic.elections.utils.Constants;
import net.lacnic.elections.utils.EJBFactory;
import net.lacnic.elections.utils.FileUtils;
import net.lacnic.elections.utils.StringUtils;
import net.lacnic.elections.utils.UtilsFiles;

@Stateless
@Remote(VotanteEleccionesEJB.class)
public class VotanteEleccionesEJBBean implements VotanteEleccionesEJB {

	private static final Logger appLogger = LogManager.getLogger("ejbAppLogger");
	
	@PersistenceContext(unitName = "elections-pu")
	private EntityManager em;

	String templatesPath = System.getProperty("jboss.server.config.dir").concat("/templates/");

	@Resource
	private SessionContext context;

	public VotanteEleccionesEJBBean() {
		//Constructor vacío
	}

	@Override
	public List<Object[]> obtenerCodigosdeVotacion(long idEleccion) {
		return ElectionsDaoFactory.createVotoDao(em).getElectionVotesCandidateAndCode(idEleccion);
	}

	@Override
	public void votar(List<Candidate> candidatos, UserVoter up, String ip) throws OperationNotPermittedException {
		UserVoter usP = ElectionsDaoFactory.createUsuarioPadronDao(em).getUserVoter(up.getUserVoterId());
		if (usP.isVoted()) {
			throw new OperationNotPermittedException("Operación no permitida");
		}
		if (candidatos.size() > usP.getElection().getMaxCandidates() || usP.isVoted() || !usP.getElection().isEnabledToVote()) {
			throw new OperationNotPermittedException("Operación no permitida");
		}

		ArrayList<Vote> votos = new ArrayList<>();
		Date fechaVoto = new Date();
		for (Candidate c : candidatos) {
			for (int i = 0; i < usP.getVoteAmount(); i++) {
				Vote v = new Vote();
				v.setCode(StringUtils.createSmallToken());
				v.setIp(ip);
				v.setCandidate(c);
				v.setVoteDate(fechaVoto);
				v.setUserVoter(usP);
				v.setElection(usP.getElection());
				em.persist(v);
				votos.add(v);
			}
		}
		usP.setVoted(true);
		usP.setVoteDate(fechaVoto);
		ElectionEmailTemplate t = EJBFactory.getInstance().getManagerEleccionesEJB().obtenerTemplate(Constants.TemplateTypeVOTE_CODES, usP.getElection().getElectionId());
		EJBFactory.getInstance().getEnvioMailsEJB().encolarEnvioIndividual(t, usP, null, usP.getElection(), votos);
		em.persist(usP);
	}

	@Override
	public UserVoter verificarAccesoUP(String token) {
		try {
			return ElectionsDaoFactory.createUsuarioPadronDao(em).getUserVoterByToken(token);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public UserVoter[] verificarAccesoUPEleccionJunta(String token) {
		try {
			UserVoter up1 = ElectionsDaoFactory.createUsuarioPadronDao(em).getUserVoterByToken(token);
			UserVoter up2 = null;
			JointElection supraEleccion = ElectionsDaoFactory.createEleccionDao(em).getJointElectionForElection(up1.getElection().getElectionId());
			long eleccionOtra = 0;

			if (up1.getElection().getElectionId() == supraEleccion.getIdElectionB())
				eleccionOtra = supraEleccion.getIdElectionA();
			else
				eleccionOtra = supraEleccion.getIdElectionB();

			for (UserVoter usu : ElectionsDaoFactory.createEleccionDao(em).getElection(eleccionOtra).getUserVoters()) {
				if (usu.getMail().toUpperCase().equalsIgnoreCase(up1.getMail())) {
					up2 = usu;
					break;
				}
			}

			return new UserVoter[] { up1, up2 };
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Election verificarAccesoResultado(String token) {
		try {
			return ElectionsDaoFactory.createEleccionDao(em).getElectionByResultToken(token);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Auditor verificarAccesoResultadoAuditor(String token) {
		try {
			return ElectionsDaoFactory.createAuditorDao(em).getAuditorByResultToken(token);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void intentoFallidoIp(String remoteAddress) {
		IpAccess ip = ElectionsDaoFactory.createIpAccessDao(em).getIP(remoteAddress);
		if (ip == null) {
			ip = new IpAccess();
			ip.setLastAttemptDate(new Date());
			ip.setFirstAttemptDate(new Date());
			ip.setIp(remoteAddress);
			ip.setAttemptCount(1);
			em.persist(ip);
		} else {
			ip.setLastAttemptDate(new Date());
			ip.setAttemptCount(ip.getAttemptCount() + 1);
			em.merge(ip);
		}

	}

	@Override
	public List<Candidate> obtenerCandidatosEleccionOrdenados(long idEleccion) throws Exception {
		return EJBFactory.getInstance().getManagerEleccionesEJB().obtenerCandidatosEleccionOrdenados(idEleccion);
	}

	@Override
	public List<Candidate> obtenerCandidatosEleccionConVotos(long idEleccion) throws Exception {
		return ElectionsDaoFactory.createCandidateDao(em).getElectionCandidates(idEleccion);
	}

	@Override
	public long obtenerVotosCandidato(long idCandidato) throws Exception {
		return ElectionsDaoFactory.createCandidateDao(em).getCandidateVotesAmount(idCandidato);
	}

	@Override
	public long obtenerCantidadVotantesQueVotaronEleccion(long idEleccion) throws Exception {
		UserVoterDao padronDao = ElectionsDaoFactory.createUsuarioPadronDao(em);
		return padronDao.getElectionUserVotersVotedAmount(idEleccion);
	}

	@Override
	public void confirmarEleccionAuditor(long idAuditor) {
		try {
			Auditor a = em.find(Auditor.class, idAuditor);
			a.setAgreedConformity(true);
			em.persist(a);
			ElectionEmailTemplate t = EJBFactory.getInstance().getManagerEleccionesEJB().obtenerTemplate(Constants.TemplateTypeAUDITOR_AGREEMENT, a.getElection().getElectionId());
			EJBFactory.getInstance().getEnvioMailsEJB().encolarEnvioIndividual(t, null, a, a.getElection(), new ArrayList<>());
		} catch (Exception e) {
			appLogger.error(e);
		}

	}

	@Override
	public void habilitarRevisionEleccionAuditor(long idAuditor, String ip) {
		try {
			Auditor a = em.find(Auditor.class, idAuditor);
			a.setRevisionAvailable(true);
			em.persist(a);
			ElectionEmailTemplate t = EJBFactory.getInstance().getManagerEleccionesEJB().obtenerTemplate(Constants.TemplateTypeAUDITOR_REVISION, a.getElection().getElectionId());
			EJBFactory.getInstance().getEnvioMailsEJB().encolarEnvioIndividual(t, null, a, a.getElection(), new ArrayList<>());

			String descripcion = a.getAuditorId() + " - " + a.getName() + " autorizó la revisión de la elección: " + a.getElection().getTitleSpanish();
			persistirActividad(a.getName(), ActivityType.ELECTION_REVISION_YES, descripcion, ip, a.getElection().getElectionId());
		} catch (Exception e) {
			appLogger.error(e);
		}

	}

	@Override
	public boolean yaVoto(long idUsuarioPadron) throws Exception {
		UserVoter u = ElectionsDaoFactory.createUsuarioPadronDao(em).getUserVoter(idUsuarioPadron);
		return u.isVoted();
	}

	@Override
	public long obtenerTotalVotosEleccion(long idEleccion) throws Exception {
		return ElectionsDaoFactory.createVotoDao(em).getElectionVotesAmount(idEleccion);
	}

	@Override
	public byte[] obtenerAuditoriaAquaIt() {
		try {
			return FileUtils.getBytesFromFile(templatesPath + "AQUAIT-LACNIC-sistema-votacion.pdf");
		} catch (Exception e) {
			appLogger.error(e);
		}
		return "".getBytes();
	}

	@Override
	public byte[] obtenerRequerimientosV1() {
		try {

			return FileUtils.getBytesFromFile(templatesPath + "RequerimientosSistemaEleccionesV10.pdf");
		} catch (Exception e) {
			appLogger.error(e);
		}
		return "".getBytes();
	}

	@Override
	public byte[] obtenerEleccionesRolesFuncionamientoRevision() {
		try {

			return FileUtils.getBytesFromFile(templatesPath + "EleccionesRolesFuncionamientoRevision.pdf");
		} catch (Exception e) {
			appLogger.error(e);
		}
		return "".getBytes();
	}
	
	@Override
	public byte[] obtenerEleccionesRolesFuncionamientoRevision(String filePath) {
		try {			
			return FileUtils.getBytesFromFile(filePath + "/static/EleccionesRolesFuncionamientoRevision.pdf");
		} catch (Exception e) {
			appLogger.error(e);
		}
		return "".getBytes();
	}
	
	@Override
	public File getEleccionesRolesFuncionamientoRevision(String filePath) {					
			return UtilsFiles.getEleccionesRolesFuncionamientoRevision(filePath);			
	}

	@Override
	public ResultadoEleccionesData obtenerResultadoEleccionesData(long idEleccion) throws Exception {
		Election eleccion = em.find(Election.class, idEleccion);
		int max = eleccion.getMaxCandidates();

		ResultadoEleccionesData resultadoData = new ResultadoEleccionesData(max);
		ArrayList<DetalleResultadoData> listaDetalleResultadoData = new ArrayList<>();
		List<Integer> pesosDistintos = ElectionsDaoFactory.createUsuarioPadronDao(em).getElectionUserVotersDistinctVoteAmounts(idEleccion);

		for (Integer pesos : pesosDistintos) {
			Long habilidado = obtenerCantidadVotantesPorPesoPorEleccion(idEleccion, pesos);
			Long participantes = obtenerCantidadVotantesQueVotaronEleccionPorPeso(idEleccion, pesos);
			DetalleResultadoData detalle = new DetalleResultadoData(habilidado, participantes, pesos);
			listaDetalleResultadoData.add(detalle);
		}
		resultadoData.setDetalleResultadoData(listaDetalleResultadoData);
		resultadoData.calcularTotales();
		return resultadoData;
	}

	private Long obtenerCantidadVotantesPorPesoPorEleccion(long idEleccion, Integer pesos) {
		UserVoterDao padronDao = ElectionsDaoFactory.createUsuarioPadronDao(em);
		return padronDao.getElectionUserVotersAmountByVoteAmount(idEleccion, pesos);
	}

	private Long obtenerCantidadVotantesQueVotaronEleccionPorPeso(long idEleccion, Integer pesos) {
		UserVoterDao padronDao = ElectionsDaoFactory.createUsuarioPadronDao(em);
		return padronDao.getElectionUserVotersAmountByVoteAmountAndVoted(idEleccion, pesos);
	}

	@Override
	public List<Object[]> obtenerCandidatosVotacion(long idUsuarioPadron, long idEleccion) {
		return ElectionsDaoFactory.createVotoDao(em).getElectionVotesCandidateForUserVoter(idUsuarioPadron, idEleccion);
	}

	@Override
	public List<Object> obtenerDataEvolucionVotosEleccion(long idEleccion) {
		List<UserVoter> usuPadVotaronEleccion = ElectionsDaoFactory.createUsuarioPadronDao(em).getElectionsUserVotersVoted(idEleccion);

		List<String> dias = new ArrayList<>();
		List<Integer> votosPorDia = new ArrayList<>();
		List<Integer> votosAcumulados = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {

			Date dia1 = usuPadVotaronEleccion.get(0).getVoteDate();
			Date ultimoDia = usuPadVotaronEleccion.get(usuPadVotaronEleccion.size() - 1).getVoteDate();

			List<String> allDates = getDatesBetween(dia1, ultimoDia);
			List<String> votosDates = new ArrayList<>();

			for (UserVoter usuPad : usuPadVotaronEleccion) {
				votosDates.add(sdf.format(usuPad.getVoteDate()));
			}

			for (String date : allDates) {
				dias.add(date);
				votosPorDia.add(Collections.frequency(votosDates, date));
			}

			int sum = 0;
			for (int votosDia : votosPorDia) {
				sum = sum + votosDia;
				votosAcumulados.add(sum);
			}

			data.add(dias);
			data.add(votosPorDia);
			data.add(votosAcumulados);

			return data;
		} catch (Exception e) {

			return null;
		}
	}

	public static List<String> getDatesBetween(Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<String> datesInRange = new ArrayList<>();
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);

		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		while (calendar.before(endCalendar)) {
			Date result = calendar.getTime();
			String dia = sdf.format(result);
			if (!datesInRange.contains(dia))
				datesInRange.add(dia);
			calendar.add(Calendar.DATE, 1);
		}
		String dia = sdf.format(endDate);
		if (!datesInRange.contains(dia))
			datesInRange.add(dia);

		return datesInRange;
	}

	private void persistirActividad(String nomAdmin, ActivityType tipoActividad, String descripcion, String ip, Long idEleccion) {
		Activity a = new Activity();
		a.setIp(ip);
		a.setElectionId(idEleccion);
		a.setTimestamp(new Date());
		a.setUserName(nomAdmin);
		a.setActivityType(tipoActividad);
		a.setDescription(descripcion);
		em.persist(a);
	}

	@Override
	public Election obtenerEleccion(long idEleccion) {
		return ElectionsDaoFactory.createEleccionDao(em).getElection(idEleccion);
	}

	@Override
	public boolean isEleccionSimple(long idEleccion) {
		return ElectionsDaoFactory.createEleccionDao(em).isEleccionSimple(idEleccion);
	}

	@Override
	public JointElection obtenerSupraEleccion(long idEleccion) {
		return ElectionsDaoFactory.createEleccionDao(em).getJointElectionForElection(idEleccion);
	}

}