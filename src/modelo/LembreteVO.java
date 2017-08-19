package modelo;
import java.util.*;
import java.io.Serializable;

/**@author Willamys Araujo
**Generate for Jacroid**/

public class LembreteVO implements Serializable{

	public String assunto;
	public String localizacao;
	public String descricao;
	public String foto;

	public String getAssunto() {
		 return assunto;
	}
	public void setAssunto(String assunto) {
		 this.assunto = assunto;
	}
	public String getLocalizacao() {
		 return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		 this.localizacao = localizacao;
	}
	public String getDescricao() {
		 return descricao;
	}
	public void setDescricao(String descricao) {
		 this.descricao = descricao;
	}
	public String getFoto() {
		 return foto;
	}
	public void setFoto(String foto) {
		 this.foto = foto;
	}
 	public Map<String, Object> toMap(){
		HashMap<String, Object> result =  new HashMap<>();
		result.put("assunto", getAssunto());
		result.put("localizacao", getLocalizacao());
		result.put("descricao", getDescricao());
		result.put("foto", getFoto());
	return result;
  	}
}