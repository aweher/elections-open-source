package net.lacnic.siselecciones.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Personalizacion implements Serializable {

	private static final long serialVersionUID = 3371727214644352916L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personalizacion_seq")
	@SequenceGenerator(name = "personalizacion_seq", sequenceName = "personalizacion_seq", allocationSize = 1)
	@Column(name = "id_personalizacion")
	private long idPersonalizacion;

	@Column(name = "pic_small_logo", nullable = false, length = 255)
	private String picSmallLogo;

	@Column(name = "pic_big_logo", nullable = false, length = 255)
	private String picBigLogo;

	@Column(name = "pic_simbolo",nullable = false, length = 255)
	private String picSimbolo;

	@Column(name = "cont_pic_small_logo", nullable = true)
	private byte[] contPicSmallLogo;

	@Column(name = "cont_pic_big_logo", nullable = false)
	private byte[] contPicBigLogo;

	@Column(name = "cont_pic_simbolo", nullable = false)
	private byte[] contPicSimbolo;
	
	@Column(name = "titulo_sitio", nullable = false, length = 255)
	private String tituloSitio;
	
	@Column(name = "titulo_login", nullable = false, length = 255)
	private String tituloLogin;
	
	@Column(name = "show_home", nullable = true)
	private boolean showHome;
		
	@Column(name = "home_html", nullable = true, columnDefinition = "TEXT")
	private String homeHtml;


	public Personalizacion() { }


	public long getIdPersonalizacion() {
		return idPersonalizacion;
	}

	public void setIdPersonalizacion(long idPersonalizacion) {
		this.idPersonalizacion = idPersonalizacion;
	}

	public String getPicSmallLogo() {
		return picSmallLogo;
	}

	public void setPicSmallLogo(String picSmallLogo) {
		this.picSmallLogo = picSmallLogo;
	}

	public String getPicBigLogo() {
		return picBigLogo;
	}

	public void setPicBigLogo(String picBigLogo) {
		this.picBigLogo = picBigLogo;
	}

	public String getPicSimbolo() {
		return picSimbolo;
	}

	public void setPicSimbolo(String picSimbolo) {
		this.picSimbolo = picSimbolo;
	}

	public byte[] getContPicSmallLogo() {
		return contPicSmallLogo;
	}

	public byte[] getContPicBigLogo() {
		return contPicBigLogo;
	}

	public byte[] getContPicSimbolo() {
		return contPicSimbolo;
	}

	public void setContPicSmallLogo(byte[] contPicSmallLogo) {
		this.contPicSmallLogo = contPicSmallLogo;
	}

	public void setContPicBigLogo(byte[] contPicBigLogo) {
		this.contPicBigLogo = contPicBigLogo;
	}

	public void setContPicSimbolo(byte[] contPicSimbolo) {
		this.contPicSimbolo = contPicSimbolo;
	}
	
	public String getTituloSitio() {
		return tituloSitio;
	}

	public void setTituloSitio(String tituloSitio) {
		this.tituloSitio = tituloSitio;
	}
	
	public String getTituloLogin() {
		return tituloLogin;
	}

	public void setTituloLogin(String tituloLogin) {
		this.tituloLogin = tituloLogin;
	}


	public boolean isShowHome() {
		return showHome;
	}


	public void setShowHome(boolean showHome) {
		this.showHome = showHome;
	}


	public String getHomeHtml() {
		return homeHtml;
	}


	public void setHomeHtml(String homeHtml) {
		this.homeHtml = homeHtml;
	}
	


}