package kz.bee.cloud.queue.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "WX_PROPERTIES")
public class Properties implements Serializable {

	private static final long serialVersionUID = 149374299418938962L;

	@Id @Column(name = "NAME_", length=100)
	private String name;
	
	@Column(name = "TYPE_", length=1)
	private String type = "0";
	
	@ElementCollection(fetch=FetchType.EAGER) @JoinTable(name = "WX_PROPERTY", joinColumns = @JoinColumn(name = "PROPERTIES_", columnDefinition="varchar(100)")) 
	@Column(name = "VALUE_")
	private Map<String, String> entries = new HashMap<String, String>();

	public Properties() {
		this.name = UUID.randomUUID().toString();
		this.type = "U";
	}

	public Properties(String name) {
		this.name = name;
		this.type = "N";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getEntries() {
		return entries;
	}
	public void setEntries(Map<String, String> entries) {
		this.entries = entries;
	}	
	
	@Transient
	public String get(String key){
		return this.getEntries().get(key);
	}

	@Transient
	public String put(String key, String value) {
		return this.entries.put(key, value);
	}
	
	@Transient
	public String remove(String key){
		return this.entries.remove(key);
	}
	
	@Transient
	public List<Entry<String,String>> entryList(){
		List<Entry<String,String>> entryList = new ArrayList<Entry<String,String>>();
		entryList.addAll(this.entries.entrySet());
		return entryList;
	}
	
	@Override
	public String toString(){
		String str = "Properties."+getName()+"(";
		for(Entry<String, String> entry : entries.entrySet()){
			str+="{"+entry.getKey()+":"+entry.getValue()+"}";
		}
		return str+")";
	}
}
