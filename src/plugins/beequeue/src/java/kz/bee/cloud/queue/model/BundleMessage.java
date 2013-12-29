package kz.bee.cloud.queue.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import kz.bee.cloud.queue.model.User.Language;

@Entity
@Table(name="WX_BUNDLEMESSAGE")
public class BundleMessage {
	
	@Id @GeneratedValue
	@Column(name="ID_")
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="LANG_")
	private Language lang = Language.RU;
	
	@Column(name="KEY_")
	private String key;
	
	@Column(name="VALUE_",length=1000)
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Language getLang() {
		return lang;
	}

	public void setLang(Language lang) {
		this.lang = lang;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
