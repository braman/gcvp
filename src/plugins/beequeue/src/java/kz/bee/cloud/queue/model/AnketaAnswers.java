package kz.bee.cloud.queue.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.CollectionNode;
import org.jivesoftware.openfire.pubsub.LeafNode;

@Entity
@Table(name = "Q_AnketaAnswers")
public class AnketaAnswers implements Serializable {

	private static final long serialVersionUID = 1635501999608866625L;
	
	@Id @GeneratedValue @Column(name = "ID_")
	private Long id;
	
	@Column(name = "NAME_", length=1000)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name = "answers") 
	private AnketaQuestions question;
	
	@Column(name = "Score")
	private int score = 0;
	
	public AnketaAnswers(){
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public AnketaQuestions getQuestion() {
		return question;
	}

	public void setQuestion(AnketaQuestions question) {
		this.question = question;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public AnketaAnswers(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnketaAnswers other = (AnketaAnswers) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
